package kr.adnetwork.controllers.adc;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.knl.KnlUser;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.SysService;
import kr.adnetwork.models.sys.SysAuditTrail;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.adc.AdcCreatDragItem;

/**
 * 캠페인 컨트롤러(광고 상세)
 */
@Controller("adc-campaign-detail-controller")
@RequestMapping(value="/adc/campaign/detail")
public class AdcCampaignDetailController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignDetailController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private SysService sysService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 캠페인 컨트롤러(광고 상세)
	 */
    @RequestMapping(value = {"/{campId}", "/{campId}/", "/{campId}/{adId}", "/{campId}/{adId}/"}, method = RequestMethod.GET)
    public String index1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	AdcCampaign campaign = adcService.getCampaign(Util.parseInt(pathMap.get("campId")));
    	if (campaign == null || campaign.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/campaign";
    	}

    	// "현재" 광고 선택 변경의 경우
    	int adId = Util.parseInt(pathMap.get("adId"));
    	if (adId > 0) {
    		AdcAd ad = adcService.getAd(adId);
    		if (ad == null || ad.getCampaign().getId() != campaign.getId()) {
    			adId = -1;
    		}
    	}
    	
		
		// 캠페인의 상태카드 설정
    	SolUtil.setCampaignStatusCard(campaign);
		
		// 쿠키에 있는 "현재" 광고 정보 등을 확인하고, 최종적으로 session에 currAdId, currAds 이름으로 정보를 설정한다.
		int currAdId = SolUtil.saveCurrAdsToSession(request, response, session, campaign.getId(), adId);
    	AdcAd ad = adcService.getAd(currAdId);

		// 광고의 인벤 타겟팅 여부 및 상태카드 설정
    	SolUtil.setAdInvenTargeted(ad);
		SolUtil.setAdStatusCard(ad);
		SolUtil.setAdResolutions(ad);
		SolUtil.setAdFixedResolution(ad);
		SolUtil.setAdMediumImpTypes(ad);
		
		// 오늘 목표
		if (ad != null && ad.getTgtToday() > 0) {
			if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
					(ad.getStatus().equals("A") || ad.getStatus().equals("R")) &&
					(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
					(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
				
				String tgtTodayDisp = String.format("<small>%s<span class='text-muted'>는</span></small>",
						ad.getStatus().equals("A") ? "하루 목표" : "오늘 목표");
				tgtTodayDisp += String.format("<small class='pl-2'>%s</small><span class='px-2'>%s</span>",
						ad.getGoalType().equals("A") ? "광고 예산" : "노출량",
						new DecimalFormat("###,###,##0").format(ad.getTgtToday()));
				tgtTodayDisp += String.format("<small>%s<span class='text-muted'>입니다</span></small>", 
						ad.getGoalType().equals("A") ? "원" : "회");
				
				ad.setTgtTodayDisp(tgtTodayDisp);
			}
		}
		//-
		

    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcAd");
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

    	model.addAttribute("Campaign", campaign);
    	model.addAttribute("Ad", ad);

    	model.addAttribute("mediumFreqCapAd", mediumFreqCapAd);
    	model.addAttribute("mediumDailyScrCap", mediumDailyScrCap);
    	
    	// 노출량 계산기 전달용
    	model.addAttribute("mediumActiveScrCnt", activeScrCnt);
    	
		model.addAttribute("ViewTypes", getViewTypeDropDownList(mediumId));
    	
    	
        return "adc/campaign/camp-detail";
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
	 * 읽기 액션 - 감사 추적
	 */
    @RequestMapping(value = "/readAuditTrail", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readAuditTrail(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = sysService.getAuditTrailList(request, "A", (int)request.getReqIntValue1());

			for(Object obj : result.getData()) {
				SysAuditTrail auditTrail = (SysAuditTrail) obj;
    			KnlUser actedByUser = knlService.getUser(auditTrail.getWhoCreatedBy());
    			
    			auditTrail.setActedByShortName(actedByUser == null ? "-" : actedByUser.getShortName());
    			
    			if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("P")) {
        			auditTrail.setTarget("광고 속성");
    			} else if ((auditTrail.getActType().equals("S") || auditTrail.getActType().equals("U") || auditTrail.getActType().equals("E")) 
    					&& auditTrail.getTgtType().equals("Creat")) {
        			auditTrail.setTarget("{IconAC}" + auditTrail.getTgtName());
    			} else if (auditTrail.getActType().equals("S") && auditTrail.getTgtType().equals("CPack")) {
	        		auditTrail.setTarget(Util.tokenizeValidStr(auditTrail.getTgtValue()).size() + " 광고 소재");
    			} else if (auditTrail.getActType().equals("S") && auditTrail.getTgtType().equals("Time")) {
        			auditTrail.setTarget("시간 타겟팅 - {TagSmallO}총 " + auditTrail.getTgtName() + " 시간{TagSmallC}");
    			} else if (auditTrail.getActType().equals("U") && auditTrail.getTgtType().equals("Time")) {
        			auditTrail.setTarget("시간 타겟팅");
    			} else if ((auditTrail.getActType().equals("S") || auditTrail.getActType().equals("U")) 
    					&& auditTrail.getTgtType().equals("Mobil")) {
    				auditTrail.setTarget("모바일 타겟팅 - {TagSmallO}{IconMob" + auditTrail.getTgtValue() + "}" + auditTrail.getTgtName() + "{TagSmallC}");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Sts")) {
	        		auditTrail.setTarget("광고 상태");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Mobil") 
    					&& Util.isValid(auditTrail.getTgtValue())) {
    				auditTrail.setTarget("모바일 타겟팅 - {TagSmallO}{IconMob" + auditTrail.getTgtValue() + "}" + auditTrail.getTgtName() + "{TagSmallC}");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Mobil") 
    					&& Util.isNotValid(auditTrail.getTgtValue())) {
    				auditTrail.setTarget("모바일 타겟팅 - {TagSmallO}" + auditTrail.getTgtName() + "{TagSmallC}");
    			} else if ((auditTrail.getActType().equals("S") || auditTrail.getActType().equals("U")) 
    					&& auditTrail.getTgtType().equals("Inven")) {
    				auditTrail.setTarget("인벤토리 타겟팅 - {TagSmallO}{IconInv" + auditTrail.getTgtValue() + "}" + auditTrail.getTgtName() + "{TagSmallC}");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Inven") 
    					&& Util.isValid(auditTrail.getTgtValue())) {
    				auditTrail.setTarget("인벤토리 타겟팅 - {TagSmallO}{IconInv" + auditTrail.getTgtValue() + "}" + auditTrail.getTgtName() + "{TagSmallC}");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Inven") 
    					&& Util.isNotValid(auditTrail.getTgtValue())) {
    				auditTrail.setTarget("인벤토리 타겟팅 - {TagSmallO}" + auditTrail.getTgtName() + "{TagSmallC}");
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readAuditTrail", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 감사 추적 항목 값
	 */
    @RequestMapping(value = "/readAuditTrailValue", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readAuditTrailValue(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		return sysService.getAuditTrailValueList(request, (int)request.getReqIntValue1());
    	} catch (Exception e) {
    		logger.error("readAuditTrailValue", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 광고 소재 파일
	 */
    @RequestMapping(value = "/readCreatFiles", method = RequestMethod.POST)
    public @ResponseBody List<AdcCreatDragItem> readCreatFiles(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	try {
    		ArrayList<AdcCreatDragItem> retItems = new ArrayList<AdcCreatDragItem>();
    		
        	String idStr = (String)model.get("ids");
        	List<String> idList = Util.tokenizeValidStr(idStr);
        	ArrayList<Integer> ids = new ArrayList<Integer>();
        	for(String idS : idList) {
        		ids.add(Util.parseInt(idS));
        	}
        	

        	HashMap<String, AdcCreatDragItem> map = new HashMap<String, AdcCreatDragItem>();
        	List<AdcCreatFile> list = adcService.getCreatFileListIn(ids);
        	for(AdcCreatFile creatFile : list) {
        		map.put("I" + creatFile.getId(), new AdcCreatDragItem(creatFile));
        	}
        	for(String idS : idList) {
        		AdcCreatDragItem item = map.get("I" + idS);
        		if (item != null) {
        			retItems.add(item);
        		}
        	}
        	
    		return retItems;
    	} catch (Exception e) {
    		logger.error("readCreatFiles", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

}
