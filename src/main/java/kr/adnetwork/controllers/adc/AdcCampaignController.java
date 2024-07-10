package kr.adnetwork.controllers.adc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.info.StringInfo;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.CustomComparator;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgAdvertiser;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.SysService;
import kr.adnetwork.models.sys.SysAuditTrail;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;

/**
 * 캠페인 컨트롤러
 */
@Controller("adc-campaign-controller")
@RequestMapping(value="/adc/campaign")
public class AdcCampaignController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignController.class);


    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private SysService sysService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 캠페인 페이지
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
		
		// 동일 광고주 송출 금지 시간
		int freqCapAdv = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.advertiser"));
		String mediumFreqCapAdv = freqCapAdv <= 0 ? "설정 안함" : freqCapAdv + " 초";
		
		// 동일 광고 송출 금지 시간
		int freqCapAd = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.ad"));
		String mediumFreqCapAd = freqCapAd <= 0 ? "설정 안함" : freqCapAd + " 초";
		
		// 화면당 하루 노출한도
		int dailyScrCap = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.daily.screen"));
		String mediumDailyScrCap = dailyScrCap <= 0 ? "설정 안함" : dailyScrCap + " 회";
		
		// 매체의 화면 수
		int activeScrCnt = Util.parseInt(SolUtil.getOptValue(mediumId, "activeCount.screen"), 1);

		
    	// 페이지 제목
    	model.addAttribute("pageTitle", "캠페인");
    	
    	model.addAttribute("mediumFreqCapAdv", mediumFreqCapAdv);
    	model.addAttribute("mediumFreqCapAd", mediumFreqCapAd);
    	model.addAttribute("mediumDailyScrCap", mediumDailyScrCap);
    	
    	// 노출량 계산기 전달용
    	model.addAttribute("mediumActiveScrCnt", activeScrCnt);
    	
		model.addAttribute("ViewTypes", getViewTypeDropDownList(mediumId));

    	

    	
        return "adc/campaign";
    }
    
    
	/**
	 * 광고주 목록 획득
	 */
    public List<DropDownListItem> getAdvertiserListByMediumId(int mediumId) {
    	
		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();

		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(mediumId);
		
		retList.add(new DropDownListItem("", String.valueOf(-1)));
		
		for(OrgAdvertiser advertiser : list) {
			retList.add(new DropDownListItem(advertiser.getName(), 
					String.valueOf(advertiser.getId())));
		}
		
		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
    	
    	return retList;
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = adcService.getCampaignList(request);
    		
			int sysValuePct = Util.parseInt(SolUtil.getOptValue(Util.getSessionMediumId(session), "sysValue.pct"));
			
    		for(Object obj : result.getData()) {
    			AdcCampaign campaign = (AdcCampaign) obj;
    			
    			campaign.setAdCount(adcService.getAdCountByCampaignId(campaign.getId()));
    			
    			if (campaign.isSelfManaged() && campaign.getGoalType().equals("I") && campaign.getGoalValue() > 0 &&
    					campaign.getSysValue() == 0 && sysValuePct > 0) {
    				campaign.setProposedSysValue((int)Math.ceil((float)campaign.getGoalValue() * (float)sysValuePct / 100f));
    			}
    			
    			// 캠페인의 상태카드 설정
    	    	SolUtil.setCampaignStatusCard(campaign);
    			
    			// 오늘/하루 목표
    			if (campaign.getTgtToday() > 0 && campaign.isSelfManaged() && (campaign.getStatus().equals("U") || campaign.getStatus().equals("R")) &&
    					(campaign.getGoalType().equals("A") || campaign.getGoalType().equals("I"))) {
    					
					String tgtTodayDisp = String.format("%s: ", campaign.getStatus().equals("A") ? "하루 목표" : "오늘 목표");
					tgtTodayDisp += String.format("%s %s %s",
							campaign.getGoalType().equals("A") ? "광고 예산" : "노출량",
							new DecimalFormat("###,###,##0").format(campaign.getTgtToday()),
							campaign.getGoalType().equals("A") ? "원" : "회");
					
					campaign.setTgtTodayDisp(tgtTodayDisp);
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
	 * 추가 액션 - 캠페인
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody AdcCampaign create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	int advId = (int)model.get("advertiser");
    	
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");

    	String advName = (String)model.get("advName");
    	String advDomainName = (String)model.get("advDomainName");
    	
    	
    	boolean selfManaged = (Boolean)model.get("selfManaged");
    	
    	String goalType = (String)model.get("goalType");
    	
    	int budget = (int)model.get("budget");
    	int goalValue = (int)model.get("goalValue");
    	int sysValue = (int)model.get("sysValue");
    	
    	OrgAdvertiser advertiser = null;
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	if (advId == -1) {
    		// 광고주 신규 등록 모드
    		if (Util.isNotValid(advDomainName) || Util.isNotValid(advName)) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    		
    		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(Util.getSessionMediumId(session));
    		for (OrgAdvertiser a : list) {
    			if (a.getName().equals(advName) || a.getDomainName().equals(advDomainName)) {
            		throw new ServerOperationForbiddenException(
            				"기존에 동일한 이름 혹은 도메인명으로 등록된 광고주가 있습니다.");
    			}
    		}
    		
    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    		advertiser = new OrgAdvertiser(medium, advName, advDomainName, session);
    		orgService.saveOrUpdate(advertiser);
    	} else {
    		// 기존 광고주 선택 모드
    		advertiser = orgService.getAdvertiser(advId);
    		if (advertiser == null) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    	}
    	if (Util.isNotValid(goalType) || (!selfManaged && !goalType.equals("U"))) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	} else if (selfManaged) {
    		if (budget < 1 && goalValue < 1 && sysValue < 1) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    	}

    	
    	AdcCampaign target = new AdcCampaign(advertiser, name, 0, memo, session);

    	if (!selfManaged) {
    		goalType = "U";
    		budget = 0;
    		goalValue = 0;
    		sysValue = 0;
    	}
    	
    	target.setSelfManaged(selfManaged);
    	target.setGoalType(goalType);
    	target.setGoalValue(goalValue);
    	target.setBudget(budget);
    	target.setSysValue(sysValue);
    	
        saveOrUpdate(target, locale, session);
        
        
        // 캠페인의 오늘 목표치 재계산
        SolUtil.calcCampTodayTargetValue(target);

        return target;
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	String adAgency = (String)model.get("adAgency");
    	String mediaRep = (String)model.get("mediaRep");
    	
    	int freqCap = (int)model.get("freqCap");
    	
    	
    	boolean selfManaged = (Boolean)model.get("selfManaged");
    	
    	String goalType = (String)model.get("goalType");
    	
    	int budget = (int)model.get("budget");
    	int goalValue = (int)model.get("goalValue");
    	int sysValue = (int)model.get("sysValue");
    	
    	String impDailyType = (String)model.get("impDailyType");
    	String impHourlyType = (String)model.get("impHourlyType");
    	
    	// 광고주는 수정 불가
    	//OrgAdvertiser advertiser = orgService.getAdvertiser((int)model.get("advertiser"));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || freqCap < 0 || Util.isNotValid(impDailyType) || Util.isNotValid(impHourlyType)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	if (selfManaged) {
    		if (Util.isNotValid(goalType) || goalType.equals("U")) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        	} else if (budget < 1 && goalValue < 1 && sysValue < 1) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    	}
    	
    	AdcCampaign target = adcService.getCampaign((int)model.get("id"));
    	if (target != null) {
    		
            target.setName(name);
            target.setAdAgency(adAgency);
            target.setMediaRep(mediaRep);
            target.setMemo(memo);
            target.setFreqCap(freqCap);
            
        	if (!selfManaged) {
        		goalType = "U";
        		budget = 0;
        		goalValue = 0;
        		sysValue = 0;
        		impDailyType = "E";
        		impHourlyType = "E";
        	}

        	target.setSelfManaged(selfManaged);
        	target.setGoalType(goalType);
        	target.setGoalValue(goalValue);
        	target.setBudget(budget);
        	target.setSysValue(sysValue);
        	target.setImpDailyType(impDailyType);
        	target.setImpHourlyType(impHourlyType);
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
            
            
            // 캠페인의 오늘 목표치 재계산
            SolUtil.calcCampTodayTargetValue(target);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcCampaign target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        
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
        		AdcCampaign campaign = adcService.getCampaign((int)id);
        		if (campaign != null) {
        			// 소프트 삭제 진행
        			adcService.deleteSoftCampaign(campaign, session);
        			
        			// 광고도 함께 소프트 삭제 진행
        			List<AdcAd> ads = adcService.getAdListByCampaignId(campaign.getId());
        			for(AdcAd ad : ads) {
        				adcService.deleteSoftAd(ad, session);
        			}
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 추가 액션 - 광고
	 */
    @RequestMapping(value = "/createAd", method = RequestMethod.POST)
    public @ResponseBody String createAd(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	int advId = (int)model.get("advertiser");
    	OrgAdvertiser advertiser = null;
    	
    	String name = (String)model.get("name");
    	String purchType = (String)model.get("purchType");
    	String goalType = (String)model.get("goalType");
    	String memo = (String)model.get("memo");
    	
    	String impDailyType = (String)model.get("impDailyType");
    	String impHourlyType = (String)model.get("impHourlyType");
    	
    	String viewType = Util.parseString((String)model.get("viewType"));

    	String advName = (String)model.get("advName");
    	String advDomainName = (String)model.get("advDomainName");
    	
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
    	if (Util.isNotValid(name) || Util.isNotValid(purchType) || 
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
        

    	String msgError = "동일한 이름의 광고 자료가 이미 등록되어 있습니다.";
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	AdcCampaign prevCamp = adcService.getCampaign(medium, name);
    	if (prevCamp != null) {
    		throw new ServerOperationForbiddenException("동일한 이름의 캠페인 자료가 이미 등록되어 있습니다.");
    	}
    	AdcAd prevAd = adcService.getAd(medium, name);
    	if (prevAd != null) {
    		throw new ServerOperationForbiddenException(msgError);
    	}
    	
    	if (advId == -1) {
    		// 광고주 신규 등록 모드
    		if (Util.isNotValid(advDomainName) || Util.isNotValid(advName)) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    		
    		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(Util.getSessionMediumId(session));
    		for (OrgAdvertiser a : list) {
    			if (a.getName().equals(advName) || a.getDomainName().equals(advDomainName)) {
            		throw new ServerOperationForbiddenException(
            				"기존에 동일한 이름 혹은 도메인명으로 등록된 광고주가 있습니다.");
    			}
    		}
    		
    		advertiser = new OrgAdvertiser(medium, advName, advDomainName, session);
    		orgService.saveOrUpdate(advertiser);
    	} else {
    		// 기존 광고주 선택 모드
    		advertiser = orgService.getAdvertiser(advId);
    		if (advertiser == null) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    	}
    	
    	AdcCampaign target = new AdcCampaign(advertiser, name, 0, "", session);
    	saveOrUpdate(target, locale, session);

    	
    	AdcAd ad = new AdcAd(target, name, purchType, startDate, endDate, session);
        
        ad.setMemo(memo);
    	ad.setCpm(cpm);
    	
        ad.setPriority(priority);
        ad.setDuration(durSecs);
        ad.setFreqCap(freqCap);
    	
        ad.setGoalType(goalType);
        ad.setGoalValue(goalValue);
        ad.setDailyCap(dailyCap);
    	
        ad.setBudget(budget);
        ad.setDailyScrCap(dailyScrCap);
        ad.setSysValue(sysValue);
    	
    	ad.setImpAddRatio(impAddRatio);
    	ad.setImpDailyType(impDailyType);
    	ad.setImpHourlyType(impHourlyType);
    	
    	ad.setViewTypeCode(viewType);

        
        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(ad);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(msgError);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(msgError);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
        
        
        // 캠페인의 시작일/종료일/상태 재계산
        adcService.refreshCampaignInfoBasedAds(target.getId());
        
        // 감사 추적: Case NA3
        sysService.saveOrUpdate(new SysAuditTrail(ad, "N", "F", session));
        
    	
        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 상태 정보
	 */
    @RequestMapping(value = "/readStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-alarm-clock fa-fw", "시작전", "U"));
		list.add(new DropDownListItem("fa-regular fa-bolt-lightning fa-fw text-orange", "진행", "R"));
		list.add(new DropDownListItem("fa-regular fa-flag-checkered fa-fw", "완료", "C"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }
    
    
	/**
	 * 읽기 액션 - 광고주 정보
	 */
    @RequestMapping(value = "/readAdvertisers", method = RequestMethod.POST)
    public @ResponseBody String readAdvertisers(HttpSession session) {
    	
    	
		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();

		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(Util.getSessionMediumId(session));
		
		for(OrgAdvertiser advertiser : list) {
			retList.add(new DropDownListItem(advertiser.getName(), 
					String.valueOf(advertiser.getId())));
		}
		
		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
    	
    	return Util.getObjectToJson(retList, false);
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
	 * 읽기 액션 - 집행 방법 정보
	 */
    @RequestMapping(value = "/readGoalTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readGoalTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-sack-dollar fa-fw", "광고 예산", "A"));
		list.add(new DropDownListItem("fa-regular fa-eye fa-fw", "노출량", "I"));
		list.add(new DropDownListItem("fa-regular fa-infinity fa-fw", "무제한 노출", "U"));
		list.add(new DropDownListItem("fa-regular fa-question fa-fw", "여러 방법", "M"));
		
		return list;
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
	 * 일자별 목표 달성률 재계산 액션
	 */
    @RequestMapping(value = "/recalcDailyAchves", method = RequestMethod.POST)
    public @ResponseBody String recalcDailyAchves(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	AdcCampaign campaign = adcService.getCampaign((int)model.get("id"));
    	if (campaign == null) {
    		throw new ServerOperationForbiddenException("캠페인을 확인할 수 없습니다.");
    	} else {
    		if (!SolUtil.calcDailyAchvesByCampId(campaign.getId())) {
    			throw new ServerOperationForbiddenException("자체 목표 설정이 가능한 캠페인이 아닙니다.");
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 오늘 목표값 재계산 액션
	 */
    @RequestMapping(value = "/recalcTodayTarget", method = RequestMethod.POST)
    public @ResponseBody String recalcTodayTarget(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	AdcCampaign campaign = adcService.getCampaign((int)model.get("id"));
    	if (campaign == null) {
    		throw new ServerOperationForbiddenException("캠페인을 확인할 수 없습니다.");
    	} else {
    		if (campaign.isSelfManaged() && (campaign.getStatus().equals("U") || campaign.getStatus().equals("R")) &&
    				(campaign.getGoalType().equals("A") || campaign.getGoalType().equals("I"))) {
        		if (!SolUtil.calcCampTodayTargetValue(campaign)) {
        			throw new ServerOperationForbiddenException("OperationError");
        		}
    		} else {
    			throw new ServerOperationForbiddenException("자체 목표 설정이 가능한 캠페인이 아니거나, 목표값 재계산이 필요하지 않은 캠페인입니다.");
    		}
    	}

        return "Ok";
    }

}
