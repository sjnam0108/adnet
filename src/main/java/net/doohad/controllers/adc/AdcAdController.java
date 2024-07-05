package net.doohad.controllers.adc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcMobTarget;
import net.doohad.models.fnd.FndMobRegion;
import net.doohad.models.fnd.FndViewType;
import net.doohad.models.org.OrgRadRegion;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysAuditTrail;
import net.doohad.models.sys.SysAuditTrailValue;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.sys.SysAuditTrailValueItem;

/**
 * 광고 컨트롤러
 */
@Controller("adc-ad-controller")
@RequestMapping(value="/adc/ad")
public class AdcAdController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignController.class);


    @Autowired 
    private AdcService adcService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private SysService sysService;

    @Autowired 
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 광고 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	int mediumId = Util.getSessionMediumId(session);
		
		// 동일 광고 송출 금지 시간
		int freqCapAd = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.ad"));
		String mediumFreqCapAd = freqCapAd <= 0 ? "설정 안함" : freqCapAd + " 초";
		
		// 화면당 하루 노출한도
		int dailyScrCap = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.daily.screen"));
		String mediumDailyScrCap = dailyScrCap <= 0 ? "설정 안함" : dailyScrCap + " 회";
		
		// 매체의 화면 수
		int activeScrCnt = Util.parseInt(SolUtil.getOptValue(mediumId, "activeCount.screen"), 1);
		
		
    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고");
    	
    	model.addAttribute("Campaigns", getCampaignDropDownListByMediumId(mediumId));
    	
    	model.addAttribute("mediumFreqCapAd", mediumFreqCapAd);
    	model.addAttribute("mediumDailyScrCap", mediumDailyScrCap);
    	
    	// 노출량 계산기 전달용
    	model.addAttribute("mediumActiveScrCnt", activeScrCnt);
    	
		model.addAttribute("ViewTypes", getViewTypeDropDownList(mediumId));

    	

    	
        return "adc/ad";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = adcService.getAdList(request);

    		int mediumId = Util.getSessionMediumId(session);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getAdTargetCountGroupByMediumAdId(mediumId);
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
			
    		
			int sysValuePct = Util.parseInt(SolUtil.getOptValue(mediumId, "sysValue.pct"));
			int freqCapAd = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.ad"));
			int dailyScrCap = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.daily.screen"));
    		
    		for(Object obj : result.getData()) {
    			AdcAd ad = (AdcAd) obj;
    			
    			ad.setCreativeCount(adcService.getAdCreativeCountByAdId(ad.getId()));
    			
    			if (targetIds.contains(ad.getId())) {
    				ad.setInvenTargeted(true);
    			}
    			
    			// 모바일 타겟팅 여부 설정
    			if (adcService.getMobTargetCountByAdId(ad.getId()) > 0) {
    				ad.setMobTargeted(true);
    			}
    			
    			if (ad.getGoalType().equals("I") && ad.getGoalValue() > 0 &&
    					ad.getSysValue() == 0 && sysValuePct > 0) {
    				ad.setProposedSysValue((int)Math.ceil((float)ad.getGoalValue() * (float)sysValuePct / 100f));
    			}
    			
    			if (freqCapAd > 0 && ad.getFreqCap() == 0) {
    				ad.setProposedFreqCap(freqCapAd);
    			}
    			if (dailyScrCap > 0 && ad.getDailyScrCap() == 0) {
    				ad.setProposedDailyScrCap(dailyScrCap);
    			}
    			
    			// 광고의 상태카드 설정
    			SolUtil.setAdStatusCard(ad);
    			
    			// 오늘/하루 목표
    			if (ad != null && ad.getTgtToday() > 0) {
    				if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
    						(ad.getStatus().equals("A") || ad.getStatus().equals("R")) &&
    						(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
    						(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
    					
    					String tgtTodayDisp = String.format("%s: ", ad.getStatus().equals("A") ? "하루 목표" : "오늘 목표");
    					tgtTodayDisp += String.format("%s %s %s",
    							ad.getGoalType().equals("A") ? "광고 예산" : "노출량",
    							new DecimalFormat("###,###,##0").format(ad.getTgtToday()),
    							ad.getGoalType().equals("A") ? "원" : "회");
    					
    					ad.setTgtTodayDisp(tgtTodayDisp);
    				}
    			}
    			//-
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 캠페인 목록 획득
	 */
    public List<DropDownListItem> getCampaignDropDownListByMediumId(int mediumId) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		List<AdcCampaign> campaignList = adcService.getCampaignListByMediumId(mediumId);
		for (AdcCampaign campaign : campaignList) {
			list.add(new DropDownListItem(campaign.getName(), String.valueOf(campaign.getId())));
		}

		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		
		list.add(0, new DropDownListItem("", "-1"));
		
		return list;
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcCampaign campaign = adcService.getCampaign((int)model.get("campaign"));
    	
    	String name = (String)model.get("name");
    	String purchType = (String)model.get("purchType");
    	String goalType = (String)model.get("goalType");
    	String memo = (String)model.get("memo");
    	
    	String impDailyType = (String)model.get("impDailyType");
    	String impHourlyType = (String)model.get("impHourlyType");
    	
    	String viewType = Util.parseString((String)model.get("viewType"));
    	
    	int cpm = (int)model.get("cpm");
    	int freqCap = (int)model.get("freqCap");
    	int goalValue = (int)model.get("goalValue");
    	int dailyCap = (int)model.get("dailyCap");

    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	int priority = (int)model.get("priority");
    	int durSecs = (int)model.get("durSecs");

    	int budget = (int)model.get("budget");
    	int dailyScrCap = (int)model.get("dailyScrCap");
    	int sysValue = (int)model.get("sysValue");
    	
    	int impAddRatio = (int)model.get("impAddRatio");
    	
    	// 파라미터 검증
    	if (campaign == null || Util.isNotValid(name) || Util.isNotValid(purchType) || 
    			Util.isNotValid(impDailyType) || Util.isNotValid(impHourlyType) ||
    			Util.isNotValid(goalType) || startDate == null || endDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	} else if (durSecs < 0 || (durSecs > 0 && durSecs < 5)) {
    		throw new ServerOperationForbiddenException(StringInfo.VAL_WRONG_DUR);
    	}
    	
    	
    	AdcAd target = new AdcAd(campaign, name, purchType, startDate, endDate, session);

    	target.setMemo(memo);
    	target.setCpm(cpm);
    	
        target.setPriority(priority);
        target.setDuration(durSecs);
    	target.setFreqCap(freqCap);
    	
    	target.setGoalType(goalType);
    	target.setGoalValue(goalValue);
    	target.setDailyCap(dailyCap);
    	
    	target.setBudget(budget);
    	target.setDailyScrCap(dailyScrCap);
    	target.setSysValue(sysValue);
    	
    	target.setImpAddRatio(impAddRatio);
    	target.setImpDailyType(impDailyType);
    	target.setImpHourlyType(impHourlyType);
    	
    	target.setViewTypeCode(viewType);

    	
        saveOrUpdate(target, locale, session);
        
        
        // 캠페인의 시작일/종료일/상태 재계산
        adcService.refreshCampaignInfoBasedAds(campaign.getId());

        // 광고의 오늘 목표치 재계산
        SolUtil.calcAdTodayTargetValue(target);
        
        
        // 감사 추적: Case NA1
        sysService.saveOrUpdate(new SysAuditTrail(target, "N", "F", session));
        

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcCampaign campaign = adcService.getCampaign((int)model.get("campaign"));
    	
    	String name = (String)model.get("name");
    	String purchType = (String)model.get("purchType");
    	String goalType = (String)model.get("goalType");
    	String memo = (String)model.get("memo");
    	
    	String impDailyType = (String)model.get("impDailyType");
    	String impHourlyType = (String)model.get("impHourlyType");

    	String viewType = Util.parseString((String)model.get("viewType"));

    	int cpm = (int)model.get("cpm");
    	int freqCap = (int)model.get("freqCap");
    	int goalValue = (int)model.get("goalValue");
    	int dailyCap = (int)model.get("dailyCap");
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	int priority = (int)model.get("priority");
    	int durSecs = (int)model.get("durSecs");

    	int budget = (int)model.get("budget");
    	int dailyScrCap = (int)model.get("dailyScrCap");
    	int sysValue = (int)model.get("sysValue");
    	
    	int impAddRatio = (int)model.get("impAddRatio");

    	// 파라미터 검증
    	if (campaign == null || Util.isNotValid(name) || Util.isNotValid(purchType) || 
    			Util.isNotValid(impDailyType) || Util.isNotValid(impHourlyType) ||
    			Util.isNotValid(goalType) || startDate == null || endDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	} else if (durSecs < 0 || (durSecs > 0 && durSecs < 5)) {
    		throw new ServerOperationForbiddenException(StringInfo.VAL_WRONG_DUR);
    	}

    	
    	AdcAd target = adcService.getAd((int)model.get("id"));
    	if (target != null) {
    		
    		// 캠페인 변경 불가
    		//target.setCampaign(campaign);
    		
    		
    		//
    		// 감사 추적: Case EA1
    		//
    		//   대상 항목(전 항목 NotNull 속성이기 때문에, null 비교는 하지 않음)
    		// - 01: 광고명
    		// - 02: 구매 유형
    		// - 03: 시작일
    		// - 04: 종료일
    		// - 05: 우선 순위
    		// - 06: 광고 예산
    		// - 07: 보장 노출량
    		// - 08: 목표 노출량
    		// - 09: 집행 방법
    		// - 10: 일별 광고 분산
    		// - 11: 1일 광고 분산
    		// - 12: 현재 노출량 추가 제어
    		// - 13: 게시 유형
    		// - 14: CPM
    		// - 15: 하루 노출한도
    		// - 16: 동일 광고 송출 금지 시간
    		// - 17: 화면당 하루 노출한도
    		// - 18: 재생 시간
    		//
    		String oName = target.getName();				// 01: 광고명
    		String oPurchType = target.getPurchType();		// 02: 구매 유형
    		Date oStartDate = target.getStartDate();		// 03: 시작일
    		Date oEndDate = target.getEndDate();			// 04: 종료일
    		int oPriority = target.getPriority();			// 05: 우선 순위
    		int oBudget = target.getBudget();				// 06: 광고 예산
    		int oGoalValue = target.getGoalValue();			// 07: 보장 노출량
    		int oSysValue = target.getSysValue();			// 08: 목표 노출량
    		String oGoalType = target.getGoalType();		// 09: 집행 방법
    		String oImpDailyType = target.getImpDailyType();	// 10: 일별 광고 분산
    		String oImpHourlyType = target.getImpHourlyType();	// 11: 1일 광고 분산
    		int oImpAddRatio = target.getImpAddRatio();			// 12: 현재 노출량 추가 제어
    		String oViewTypeCode = target.getViewTypeCode();	// 13: 게시 유형
    		int oCpm = target.getCpm();						// 14: CPM
    		int oDailyCap = target.getDailyCap();			// 15: 하루 노출한도
    		int oFreqCap = target.getFreqCap();				// 16: 동일 광고 송출 금지 시간
    		int oDailyScrCap = target.getDailyScrCap();		// 17: 화면당 하루 노출한도
    		int oDuration = target.getDuration();			// 18: 재생 시간
    		
    		ArrayList<SysAuditTrailValueItem> editItems = new ArrayList<SysAuditTrailValueItem>();
    		DecimalFormat dFormatter = new DecimalFormat("##,###,###,##0");
    		
    		if (!oName.equals(name)) {
    			editItems.add(new SysAuditTrailValueItem("광고명", oName, name));
    		}
    		if (!oPurchType.equals(purchType)) {
    			editItems.add(new SysAuditTrailValueItem("구매 유형", oPurchType, purchType));
    		}
    		if (oStartDate.compareTo(startDate) != 0) {
    			editItems.add(new SysAuditTrailValueItem("시작일", 
    					Util.toSimpleString(oStartDate, "yyyy-MM-dd"), Util.toSimpleString(startDate, "yyyy-MM-dd")));
    		}
    		if (oEndDate.compareTo(endDate) != 0) {
    			editItems.add(new SysAuditTrailValueItem("종료일", 
    					Util.toSimpleString(oEndDate, "yyyy-MM-dd"), Util.toSimpleString(endDate, "yyyy-MM-dd")));
    		}
    		if (oPriority != priority) {
    			editItems.add(new SysAuditTrailValueItem("우선 순위", String.valueOf(oPriority), String.valueOf(priority)));
    		}
    		if (oBudget != budget) {
    			editItems.add(new SysAuditTrailValueItem("광고 예산", dFormatter.format(oBudget), dFormatter.format(budget)));
    		}
    		if (oGoalValue != goalValue) {
    			editItems.add(new SysAuditTrailValueItem("보장 노출량", dFormatter.format(oGoalValue), dFormatter.format(goalValue)));
    		}
    		if (oSysValue != sysValue) {
    			editItems.add(new SysAuditTrailValueItem("목표 노출량", dFormatter.format(oSysValue), dFormatter.format(sysValue)));
    		}
    		if (!oGoalType.equals(goalType)) {
    			editItems.add(new SysAuditTrailValueItem("집행 방법", oGoalType, goalType));
    		}
    		if (!oImpDailyType.equals(impDailyType)) {
    			editItems.add(new SysAuditTrailValueItem("일별 광고 분산", oImpDailyType, impDailyType));
    		}
    		if (!oImpHourlyType.equals(impHourlyType)) {
    			editItems.add(new SysAuditTrailValueItem("1일 광고 분산", oImpHourlyType, impHourlyType));
    		}
    		if (oImpAddRatio != impAddRatio) {
    			editItems.add(new SysAuditTrailValueItem("현재 노출량 추가 제어", String.valueOf(oImpAddRatio), String.valueOf(impAddRatio)));
    		}
    		if (!oViewTypeCode.equals(viewType)) {
    			editItems.add(new SysAuditTrailValueItem("게시 유형",
    					Util.isValid(oViewTypeCode) ? oViewTypeCode : "[-]", Util.isValid(viewType) ? viewType : "[-]"));
    		}
    		if (oCpm != cpm) {
    			editItems.add(new SysAuditTrailValueItem("CPM", dFormatter.format(oCpm), dFormatter.format(cpm)));
    		}
    		if (oDailyCap != dailyCap) {
    			editItems.add(new SysAuditTrailValueItem("하루 노출한도", dFormatter.format(oDailyCap), dFormatter.format(dailyCap)));
    		}
    		if (oFreqCap != freqCap) {
    			editItems.add(new SysAuditTrailValueItem("동일 광고 송출 금지 시간", dFormatter.format(oFreqCap), dFormatter.format(freqCap)));
    		}
    		if (oDailyScrCap != dailyScrCap) {
    			editItems.add(new SysAuditTrailValueItem("화면당 하루 노출한도", dFormatter.format(oDailyScrCap), dFormatter.format(dailyScrCap)));
    		}
    		if (oDuration != durSecs) {
    			editItems.add(new SysAuditTrailValueItem("재생 시간", dFormatter.format(oDuration), dFormatter.format(durSecs)));
    		}
    		
    		
            target.setName(name);
            target.setPurchType(purchType);
            target.setStartDate(startDate);
            target.setEndDate(endDate);
            target.setCpm(cpm);
            target.setMemo(memo);
            
            target.setPriority(priority);
            target.setDuration(durSecs);
            target.setFreqCap(freqCap);
        	
        	target.setGoalType(goalType);
        	target.setGoalValue(goalValue);
        	target.setDailyCap(dailyCap);
        	
        	target.setBudget(budget);
        	target.setDailyScrCap(dailyScrCap);
        	target.setSysValue(sysValue);
        	
        	target.setImpAddRatio(impAddRatio);
        	target.setImpDailyType(impDailyType);
        	target.setImpHourlyType(impHourlyType);
        	
        	target.setViewTypeCode(viewType);
            
            // 현재 광고의 상태 확인
            if (target.getStatus().equals("A") || target.getStatus().equals("C") || 
            		target.getStatus().equals("R")) {
            	
                Date today = Util.removeTimeOfDate(new Date());
                if (today.before(startDate)) {
                	target.setStatus("A");
                } else if (today.after(endDate)) {
                	target.setStatus("C");
                } else {
                	target.setStatus("R");
                }
            }
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
            
            
            // 캠페인의 시작일/종료일/상태 재계산
            adcService.refreshCampaignInfoBasedAds(campaign.getId());
            
            // 연결된 광고 소재의 시작일/종료일 점검
            adcService.refreshAdCreativePeriodByAdDates(target, oStartDate, oEndDate);

            //광고의 오늘 목표치 재계산
            SolUtil.calcAdTodayTargetValue(target);

            
            if (editItems.size() > 0) {
            	
                // 감사 추적: Case EA1
            	SysAuditTrail auditTrail = new SysAuditTrail(target, "E", "P", "F", session);
                sysService.saveOrUpdate(auditTrail);
            	
                for(SysAuditTrailValueItem item : editItems) {
                	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                }
            }
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcAd target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {

        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	try {
        	for (Object id : objs) {
        		AdcAd ad = adcService.getAd((int)id);
        		if (ad != null) {
        			// 소프트 삭제 진행
        			adcService.deleteSoftAd(ad, session);
                    
                    // 캠페인의 시작일/종료일/상태 재계산
                    adcService.refreshCampaignInfoBasedAds(ad.getCampaign().getId());
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 상태 정보
	 */
    @RequestMapping(value = "/readStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-asterisk fa-fw", "준비", "D"));
		list.add(new DropDownListItem("fa-regular fa-square-question fa-fw", "승인대기", "P"));
		list.add(new DropDownListItem("fa-regular fa-do-not-enter fa-fw", "거절", "J"));
		list.add(new DropDownListItem("fa-regular fa-alarm-clock fa-fw", "예약", "A"));
		list.add(new DropDownListItem("fa-regular fa-bolt-lightning text-orange fa-fw", "진행", "R"));
		list.add(new DropDownListItem("fa-regular fa-flag-checkered fa-fw", "완료", "C"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 구매 유형 정보
	 */
    @RequestMapping(value = "/readPurchTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readPurchTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-hexagon-check text-blue fa-fw", "목표 보장", "G"));
		list.add(new DropDownListItem("fa-regular fa-hexagon-exclamation fa-fw", "목표 비보장", "N"));
		list.add(new DropDownListItem("fa-regular fa-house fa-fw", "하우스 광고", "H"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 집행 방법 정보
	 */
    @RequestMapping(value = "/readGoalTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readGoalTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-sack-dollar fa-fw", "광고 예산", "A"));
		list.add(new DropDownListItem("fa-regular fa-eye fa-fw", "노출량", "I"));
		list.add(new DropDownListItem("fa-regular fa-infinity fa-fw", "무제한 노출", "U"));
		
		return list;
    }

    
    /**
	 * 승인 액션
	 */
    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    public @ResponseBody String approve(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
		// 단일 항목 액션만 가능하도록
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 승인 처리는 준비(D), 승인대기(P), 거절(J), 보관(V)만 가능
    			if (ad.getStatus().equals("D") || ad.getStatus().equals("P") || ad.getStatus().equals("J") || 
    					ad.getStatus().equals("V")) {
    				
    				String oStatus = ad.getStatus();
    				
    				// 오늘 날짜 기준으로 예약A/진행R/완료C 처리
    				String status = "A";
    				Date today = Util.removeTimeOfDate(new Date());
    				if (today.before(ad.getStartDate())) {
    					// "A"
    				} else if (today.after(ad.getEndDate())) {
    					status = "C";
    				} else {
    					status = "R";
    				}
    				
    				ad.setStatus(status);
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);

                	
            		//
            		// 감사 추적: Case EA8
            		//
                	if (!oStatus.equals(status)) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, status);
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("A", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("A", "Status", status));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 거절 액션
	 */
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public @ResponseBody String reject(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
		// 단일 항목 액션만 가능하도록
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 거절 처리는 승인대기(P), 예약(A), 진행(R), 완료(C)만 가능
    			if (ad.getStatus().equals("P") || ad.getStatus().equals("A") || ad.getStatus().equals("R") || 
    					ad.getStatus().equals("C")) {
    				
    				String oStatus = ad.getStatus();
    				
    				ad.setStatus("J");
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);

                	
            		//
            		// 감사 추적: Case EA8
            		//
                	if (!oStatus.equals("J")) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "J");
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("A", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("A", "Status", "J"));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 보관 액션
	 */
    @RequestMapping(value = "/archive", method = RequestMethod.POST)
    public @ResponseBody String archive(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 보관 처리는 준비(D), 승인대기(P), 승인(A), 거절(J)만 가능
    			if (ad.getStatus().equals("D") || ad.getStatus().equals("P") ||
    					ad.getStatus().equals("A") || ad.getStatus().equals("R") ||
    					ad.getStatus().equals("C") || ad.getStatus().equals("J")) {
    				
    				String oStatus = ad.getStatus();
    				
    				ad.setStatus("V");
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);

                	
            		//
            		// 감사 추적: Case EA8
            		//
                	if (!oStatus.equals("V")) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "V");
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("A", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("A", "Status", "V"));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 보관 해제 액션
	 */
    @RequestMapping(value = "/unarchive", method = RequestMethod.POST)
    public @ResponseBody String unarchive(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 보관 해제 처리는 보관(V)만 가능
    			if (ad.getStatus().equals("V")) {
    				
    				String oStatus = ad.getStatus();
    				
    				ad.setStatus("D");
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);

                	
            		//
            		// 감사 추적: Case EA8
            		//
                	if (!oStatus.equals("D")) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "D");
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("A", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("A", "Status", "D"));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 잠시 멈춤 액션
	 */
    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    public @ResponseBody String pause(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 잠시 멈춤 처리는 잠시 멈춤이 아닌 항목만 가능
    			if (!ad.isPaused()) {
    				
    				ad.setPaused(true);
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 재개 액션
	 */
    @RequestMapping(value = "/resume", method = RequestMethod.POST)
    public @ResponseBody String resume(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcAd ad = adcService.getAd((int)id);
    		if (ad != null) {
    			// 재개 처리는 잠시 멈춤인 항목만 가능
    			if (ad.isPaused()) {
    				
    				ad.setPaused(false);
    				
    				ad.touchWho(session);
    				
                	adcService.saveOrUpdate(ad);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }
    
    
	/**
	 * 현재 매체에서 이용가능한 게시 유형 획득
	 */
    private List<DropDownListItem> getViewTypeDropDownList(int mediumId) {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	// 빈 행 추가
    	retList.add(new DropDownListItem("", ""));
    	
    	
    	List<FndViewType> viewTypeList = fndService.getViewTypeList();
    	
    	List<String> viewTypes = SolUtil.getViewTypeListByMediumId(mediumId);
    	for(String s : viewTypes) {
    		String text = s;
    		for(FndViewType viewType : viewTypeList) {
    			if (viewType.getCode().equals(s)) {
    				text = String.format("%s - %s", s, viewType.getName());
    			}
    		}
    		retList.add(new DropDownListItem(text, s));
    	}
    	
    	return retList;
    }
    
    
	/**
	 * 복사 추가 액션
	 */
    @RequestMapping(value = "/duplicate", method = RequestMethod.POST)
    public @ResponseBody String duplicate(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcAd ad = adcService.getAd((int)model.get("id"));
    	
    	String name = (String)model.get("name");

    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));

    	// 파라미터 검증
    	if (ad == null || Util.isNotValid(name) || startDate == null || endDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (startDate.after(endDate)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_END_DATE);
    	}

    	
    	AdcAd adTarget = new AdcAd(ad.getCampaign(), name, ad.getPurchType(), startDate, endDate, session);

    	adTarget.setMemo(ad.getMemo());
    	adTarget.setCpm(ad.getCpm());
    	
        adTarget.setPriority(ad.getPriority());
        adTarget.setDuration(ad.getDuration());
    	adTarget.setFreqCap(ad.getFreqCap());
    	
    	adTarget.setGoalType(ad.getGoalType());
    	adTarget.setGoalValue(ad.getGoalValue());
    	adTarget.setDailyCap(ad.getDailyCap());
    	
    	adTarget.setBudget(ad.getBudget());
    	adTarget.setDailyScrCap(ad.getDailyScrCap());
    	adTarget.setSysValue(ad.getSysValue());
    	
    	adTarget.setImpAddRatio(ad.getImpAddRatio());
    	adTarget.setImpDailyType(ad.getImpDailyType());
    	adTarget.setImpHourlyType(ad.getImpHourlyType());
    	
    	adTarget.setViewTypeCode(ad.getViewTypeCode());
    	
    	// 1. 시간 타겟팅 설정
    	adTarget.setExpHour(ad.getExpHour());

    	
        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(adTarget);
            
            
            // 감사 추적: Case NA2
            sysService.saveOrUpdate(new SysAuditTrail(adTarget, "N", "F", session));
            
            if (Util.isValid(ad.getExpHour())) {
            	
            	// 감사 추적: Case SA3
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(adTarget, "S", "Time", "F", session);
            	auditTrail.setTgtName(String.valueOf(SolUtil.getHourCnt(ad.getExpHour())));
            	auditTrail.setTgtValue(ad.getExpHour());
            	
                sysService.saveOrUpdate(auditTrail);
            }
        } catch (DataIntegrityViolationException dive) {
    		logger.error("duplicate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("duplicate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME);
        } catch (Exception e) {
    		logger.error("duplicate", e);
        	throw new ServerOperationForbiddenException("OperationError");
        }
        
        // 캠페인의 시작일/종료일/상태 재계산
        adcService.refreshCampaignInfoBasedAds(ad.getCampaign().getId());

        //광고의 오늘 목표치 재계산
        SolUtil.calcAdTodayTargetValue(adTarget);

        
        // 2. 광고 소재 복사
        //    - 광고의 시작/종료일과 일치하는 연결된 광고 소재만 등록(연결)
    	List<AdcAdCreative> adCreatList = adcService.getAdCreativeListByAdId(ad.getId());
    	for(AdcAdCreative adCreat : adCreatList) {
    		
    		// 이전에는 광고와 동일한 시작일/종료일의 광고 소재만 그 대상이 되었으나,
    		// 지금은 모든 광고 소재를 복사하고, 시작/종료일을 광고와 동일하게 설정
	        /*
    		boolean isRightData = Util.toSimpleString(adCreat.getStartDate()).equals(Util.toSimpleString(ad.getStartDate())) &&
    				Util.toSimpleString(adCreat.getEndDate()).equals(Util.toSimpleString(ad.getEndDate()));
    		if (isRightData) {
    		}
    		*/
			adcService.saveOrUpdate(
					new AdcAdCreative(adTarget, adCreat.getCreative(), adCreat.getWeight(), startDate, endDate, session));
	        
	        
			//
			// 감사 추적: Case SA1
			//
			// - 01: 시작일
			// - 02: 종료일
			// - 03: 광고 소재간 가중치
	        
			ArrayList<SysAuditTrailValueItem> editItems = new ArrayList<SysAuditTrailValueItem>();
			
			editItems.add(new SysAuditTrailValueItem("시작일", "[-]", Util.toSimpleString(startDate, "yyyy-MM-dd")));
			editItems.add(new SysAuditTrailValueItem("종료일", "[-]", Util.toSimpleString(endDate, "yyyy-MM-dd")));
			editItems.add(new SysAuditTrailValueItem("광고 소재간 가중치", "[-]", String.valueOf(adCreat.getWeight())));
	    	

	    	SysAuditTrail auditTrail = new SysAuditTrail(adTarget, "S", "Creat", "F", session);
	    	auditTrail.setTgtName(adCreat.getCreative().getName());
	    	auditTrail.setTgtValue(String.valueOf(adCreat.getCreative().getId()));
	    	
	        sysService.saveOrUpdate(auditTrail);
	    	
	        for(SysAuditTrailValueItem item : editItems) {
	        	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
	        }
    		
    	}
    	
    	
    	// 3. 인벤타겟팅 복사
    	List<Integer> currIds = new ArrayList<Integer>();
    	int tgtScrCount = 0;
    	List<AdcAdTarget> invTargets = adcService.getAdTargetListByAdId(ad.getId());
    	for(AdcAdTarget invTarget : invTargets) {
			if (invTarget.getInvenType().equals("RG")) {
				currIds = invService.getMonitScreenIdsByMediumRegionCodeIn(invTarget.getMedium().getId(), 
		    			Util.getStringList(invTarget.getTgtValue()));
			} else if (invTarget.getInvenType().equals("CT")) {
				currIds = invService.getMonitScreenIdsByMediumStateCodeIn(invTarget.getMedium().getId(), 
						Util.getStringList(invTarget.getTgtValue()));
			} else if (invTarget.getInvenType().equals("ST")) {
				currIds = invService.getMonitScreenIdsByMediumSiteIdIn(invTarget.getMedium().getId(), 
						Util.getIntegerList(invTarget.getTgtValue()));
			} else if (invTarget.getInvenType().equals("SC")) {
				currIds = invService.getMonitScreenIdsByMediumScreenIdIn(invTarget.getMedium().getId(), 
						Util.getIntegerList(invTarget.getTgtValue()));
			} else if (invTarget.getInvenType().equals("CD")) {
				currIds = invService.getMonitScreenIdsByMediumSiteCondCodeIn(invTarget.getMedium().getId(), 
						Util.getStringList(invTarget.getTgtValue()));
			} else if (invTarget.getInvenType().equals("SP")) {
				currIds = invService.getMonitScreenIdsByMediumScrPackIdIn(invTarget.getMedium().getId(), 
						Util.getIntegerList(invTarget.getTgtValue()));
			}
			if (invTarget.getTgtScrCount() != currIds.size()) {
				tgtScrCount = currIds.size();
			} else {
				tgtScrCount = invTarget.getTgtScrCount();
			}
			
			AdcAdTarget invTgtTarget = new AdcAdTarget(adTarget, invTarget.getInvenType(), invTarget.getTgtCount(),
					invTarget.getTgtValue(), invTarget.getTgtDisplay(), tgtScrCount, 1000, session);
			invTgtTarget.setFilterType(invTarget.getFilterType());
			
			adcService.saveAndReorderAdTarget(invTgtTarget);
	        
	        
			// 감사 추적: Case SA5
        	
        	SysAuditTrail auditTrail = new SysAuditTrail(adTarget, "S", "Inven", "F", session);
        	auditTrail.setTgtName(invTarget.getTgtDisplay());
        	auditTrail.setTgtValue(invTarget.getInvenType());
        	
            sysService.saveOrUpdate(auditTrail);
    	}
    	
    	
    	// 4. 모바일타겟팅 복사
    	List<AdcMobTarget> mobTargets = adcService.getMobTargetListByAdId(ad.getId());
    	for(AdcMobTarget mobTarget : mobTargets) {
    		
    		AdcMobTarget mobTgtTarget = new AdcMobTarget(adTarget, mobTarget.getMobType(), mobTarget.getTgtId(), 1000, session);
    		mobTgtTarget.setFilterType(mobTarget.getFilterType());
			
			adcService.saveAndReorderMobTarget(mobTgtTarget);
        	
        	
    		// 감사 추적: Case SA4

			String targetName = "";
			if (mobTarget.getMobType().equals("RG")) {
    			FndMobRegion mobRegion = fndService.getMobRegion(mobTarget.getTgtId());
    			if (mobRegion != null) {
    				targetName = mobRegion.getName();
    			}
			} else if (mobTarget.getMobType().equals("CR")) {
    			OrgRadRegion radRegion = orgService.getRadRegion(mobTarget.getTgtId());
    			if (radRegion != null) {
    				targetName = radRegion.getName();
    			}
    		}
			
			if (Util.isValid(targetName)) {
				
	        	SysAuditTrail auditTrail = new SysAuditTrail(adTarget, "S", "Mobil", "F", session);
	        	auditTrail.setTgtName(targetName);
	        	auditTrail.setTgtValue(mobTarget.getMobType());
	        	
	            sysService.saveOrUpdate(auditTrail);
			}

    	}
    	
    	
        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 일별 광고 분산 정책 정보
	 */
    @RequestMapping(value = "/readImpDailyTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readImpDailyTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-equals fa-fw", "모든 날짜 균등", "E"));
		list.add(new DropDownListItem("fa-regular fa-bars-staggered fa-fw", "통계 기반 요일별 차등", "W"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 하루 광고 분산 정책 정보
	 */
    @RequestMapping(value = "/readImpHourlyTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readImpHourlyTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-equals fa-fw", "모든 시간 균등", "E"));
		list.add(new DropDownListItem("fa-regular fa-sun fa-fw", "일과 시간 집중", "D"));
		
		return list;
    }

    
    /**
	 * 일자별 목표 달성률 재계산 액션
	 */
    @RequestMapping(value = "/recalcDailyAchves", method = RequestMethod.POST)
    public @ResponseBody String recalcDailyAchves(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	AdcAd ad = adcService.getAd((int)model.get("id"));
    	if (ad == null) {
    		throw new ServerOperationForbiddenException("광고를 확인할 수 없습니다.");
    	} else {
    		if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
    				(ad.getStatus().equals("A") || ad.getStatus().equals("R") || ad.getStatus().equals("C")) &&
    				(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
    				(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
        		if (!SolUtil.calcDailyAchvesByAdId(ad.getId())) {
        			throw new ServerOperationForbiddenException("OperationError");
        		}
    		} else {
    			throw new ServerOperationForbiddenException("일자별 목표 달성률 재계산이 필요하지 않은 광고입니다.");
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 오늘 목표값 재계산 액션
	 */
    @RequestMapping(value = "/recalcTodayTarget", method = RequestMethod.POST)
    public @ResponseBody String recalcTodayTarget(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	AdcAd ad = adcService.getAd((int)model.get("id"));
    	if (ad == null) {
    		throw new ServerOperationForbiddenException("광고를 확인할 수 없습니다.");
    	} else {
    		if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
    				(ad.getStatus().equals("A") || ad.getStatus().equals("R") || ad.getStatus().equals("C")) &&
    				(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
    				(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
        		if (!SolUtil.calcAdTodayTargetValue(ad)) {
        			throw new ServerOperationForbiddenException("OperationError");
        		}
    		} else {
    			throw new ServerOperationForbiddenException("목표값 재계산이 필요하지 않은 광고입니다.");
    		}
    	}

        return "Ok";
    }

}
