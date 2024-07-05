package net.doohad.controllers.adc;

import java.util.ArrayList;
import java.util.Collections;
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

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceRequest.FilterDescriptor;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.fnd.FndState;
import net.doohad.models.inv.InvScrPack;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.org.OrgSiteCond;
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
import net.doohad.viewmodels.adc.AdcAdTargetOrderItem;
import net.doohad.viewmodels.sys.SysAuditTrailValueItem;

/**
 * 캠페인 컨트롤러(인벤토리 타겟팅)
 */
@Controller("adc-campaign-inv-target-controller")
@RequestMapping(value="/adc/campaign/invtargets")
public class AdcCampaignInvTargetController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignInvTargetController.class);

	
    @Autowired 
    private InvService invService;
	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private SysService sysService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 캠페인 컨트롤러(인벤토리 타겟팅)
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
    	AdcAd ad = adcService.getAd(currAdId);		// ad가 null일 수도 있음

		// 광고의 인벤 타겟팅 여부 및 상태카드 설정
    	SolUtil.setAdInvenTargeted(ad);
		SolUtil.setAdStatusCard(ad);
		SolUtil.setAdResolutions(ad);
		SolUtil.setAdFixedResolution(ad);
		SolUtil.setAdMediumImpTypes(ad);

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcAd");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고");

    	model.addAttribute("Campaign", campaign);
    	model.addAttribute("Ad", ad);

    	

    	
        return "adc/campaign/camp-invtarget";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		// 카운팅 계산을 먼저 수행
    		invService.getTargetScreenCountByCreativeId(request.getReqIntValue1());
    		
    		return adcService.getAdTargetList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 광고 인벤 타겟팅 순서 변경용
	 */
    @RequestMapping(value = "/readOrdered", method = RequestMethod.POST)
    public @ResponseBody List<AdcAdTargetOrderItem> readOrdered(@RequestBody Map<String, Object> model, 
    		HttpSession session) {
    	
    	List<AdcAdTarget> list = adcService.getAdTargetListByAdId((int)model.get("adId"));
    	
    	ArrayList<AdcAdTargetOrderItem> retList = new ArrayList<AdcAdTargetOrderItem>();
    	
    	int idx = 1;
    	for (AdcAdTarget adTarget : list) {
    		retList.add(new AdcAdTargetOrderItem(adTarget, idx++));
    	}
    	
    	return retList;
    }
    
    
	/**
	 * 순서 변경 액션
	 */
    @RequestMapping(value = "/reorder", method = RequestMethod.POST)
    public @ResponseBody String reorder(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	String items = (String)model.get("items");
    	if (Util.isValid(items)) {

    		//
    		// 감사 추적: Case EA6
    		//
    		ArrayList<String> seqItems = new ArrayList<String>();
    		AdcAd ad = null;
    		
    		List<String> list = Util.tokenizeValidStr(items);
    		
    		try {
    			int idx = 0;
        		for (String idStr : list) {
        			idx ++;
        			
        			AdcAdTarget adTarget = adcService.getAdTarget(Util.parseInt(idStr));
        			if (adTarget != null) {
        				int seq = idx * 10;
        				if (adTarget.getSiblingSeq() != seq) {
        					adTarget.setSiblingSeq(seq);
        					adTarget.touchWho(session);
            				
        					adcService.saveOrUpdate(adTarget);
        				}
        				
        				if (ad == null) {
        					ad = adTarget.getAd();
        				}
        				seqItems.add(adTarget.getInvenType() + adTarget.getTgtDisplay());
        			}
        		}
        		
        		
        		if (seqItems.size() > 0 && ad != null) {
                	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Inven", "F", session);
                	auditTrail.setTgtName("순서 변경");
                	auditTrail.setTgtValue("");
                    sysService.saveOrUpdate(auditTrail);
                	
                    for(String s : seqItems) {
                		if (s.length() > 2) {
                    		String type = s.substring(0, 2);
                    		String name = s.substring(2);
                        	
                    		SysAuditTrailValueItem item = new SysAuditTrailValueItem("타겟팅 순서", "TargetOrder");
                    		item.setNewText(name);
                    		item.setNewValue(type);
                    		
                    		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                		}
                    }
        		}
    		} catch (Exception e) {
        		logger.error("reorder", e);
            	throw new ServerOperationForbiddenException("OperationError");
    		}
    	}
    	
    	return "OK";
    }
    
    
	/**
	 * 변경 액션 - 인벤 타겟팅 And/Or 토글 처리
	 */
	@RequestMapping(value = "/updateFilterType", method = RequestMethod.POST)
    public @ResponseBody String updateFilterType(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String filterType = (String)model.get("filterType");

    	AdcAdTarget target = adcService.getAdTarget((int)model.get("id"));
    	
    	// 파라미터 검증
    	if (target == null || Util.isNotValid(filterType)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
		//
		// 감사 추적: Case EA5
		//
		// - 01: 타겟팅 연산
    	//
    	String oFilterType = target.getFilterType();
    	
    	ArrayList<SysAuditTrailValueItem> editItems = new ArrayList<SysAuditTrailValueItem>();
    	
    	if (!oFilterType.equals(filterType)) {
    		editItems.add(new SysAuditTrailValueItem("타겟팅 연산", oFilterType, filterType));
    	}
    	
    	
    	target.setFilterType(filterType);
    	
    	target.touchWho(session);

    	
    	try {
    		adcService.saveOrUpdate(target);

    		if (editItems.size() > 0) {
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(target.getAd(), "E", "Inven", "F", session);
            	auditTrail.setTgtName(target.getTgtDisplay());
            	auditTrail.setTgtValue(target.getInvenType());
                sysService.saveOrUpdate(auditTrail);
            	
                for(SysAuditTrailValueItem item : editItems) {
                	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                }
    		}
    		
    	} catch (Exception e) {
    		logger.error("updateFilterType", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 현재의 타겟팅에 대한 대상 화면 갯수 계산
	 */
	@RequestMapping(value = "/calcScrCount", method = RequestMethod.POST)
    public @ResponseBody int calcScreenCount(@RequestBody Map<String, Object> model, Locale locale) {
    	
    	return invService.getTargetScreenCountByAdId((int)model.get("adId"));
	}

	
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<AdcAdTarget> adTargets = new ArrayList<AdcAdTarget>();

    	ArrayList<String> delItems = new ArrayList<String>();
    	AdcAd ad = null;

    	for (Object id : objs) {
    		AdcAdTarget adTarget = adcService.getAdTarget((int)id);
    		if (adTarget != null) {
    			if (ad == null) {
    				ad = adTarget.getAd();
    			}
    			delItems.add(adTarget.getInvenType() + adTarget.getTgtDisplay());
    			
    			adTargets.add(adTarget);
    		}
    	}
    	
    	try {
        	adcService.deleteAdTargets(adTargets);
        	
        	
    		// 감사 추적: Case UA4
            
        	if (ad != null) {
            	for(String s : delItems) {
            		if (s.length() > 2) {
                		String type = s.substring(0, 2);
                		String name = s.substring(2);
                    	
                    	SysAuditTrail auditTrail = new SysAuditTrail(ad, "U", "Inven", "F", session);
                    	auditTrail.setTgtName(name);
                    	auditTrail.setTgtValue(type);
                    	
                        sysService.saveOrUpdate(auditTrail);
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
	 * 읽기 액션 - Kendo AutoComplete 용 시/군/구 정보
	 */
    @RequestMapping(value = "/readACRegion", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplRegions(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<FndRegion> regionList = fndService.getRegionListByNameLike(userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(FndRegion region : regionList) {
			list.add(new DropDownListItem(region.getName(), region.getCode()));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 화면 정보
	 */
    @RequestMapping(value = "/readACScreen", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplScreens(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvScreen> screenList = invService.getMonitScreenListByMediumNameLike(Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(InvScreen screen : screenList) {
			list.add(new DropDownListItem(screen.getName(), String.valueOf(screen.getId())));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 광역시/도 정보
	 */
    @RequestMapping(value = "/readACState", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplStates(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<FndState> stateList = fndService.getStateListByNameLike(userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(FndState state : stateList) {
			list.add(new DropDownListItem(state.getName(), state.getCode()));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 사이트 정보
	 */
    @RequestMapping(value = "/readACSite", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplSites(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvSite> siteList = invService.getMonitSiteListByMediumNameLike(Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(InvSite site : siteList) {
			list.add(new DropDownListItem(site.getName(), String.valueOf(site.getId())));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 입지 유형 정보
	 */
    @RequestMapping(value = "/readACSiteCond", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplSiteConds(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<OrgSiteCond> siteCondList = orgService.getSiteCondListByMediumIdNameLike(
				Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(OrgSiteCond siteCond : siteCondList) {
			list.add(new DropDownListItem(siteCond.getName(), siteCond.getCode()));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 화면 묶음 정보
	 */
    @RequestMapping(value = "/readACScrPack", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAutoComplScrPacks(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvScrPack> scrPackList = invService.getScrPackListByMediumIdNameLike(
				Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(InvScrPack scrPack : scrPackList) {
			list.add(new DropDownListItem(scrPack.getName(), String.valueOf(scrPack.getId())));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
    
    
	/**
	 * 저장 액션 - 시/군/구
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveRegion", method = RequestMethod.POST)
    public @ResponseBody String saveRegion(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> regionCodes = (ArrayList<Object>) model.get("regionCodes");
		ArrayList<Object> regionTexts = (ArrayList<Object>) model.get("regionTexts");
    	AdcAd ad = adcService.getAd((int)model.get("ad"));

    	// 파라미터 검증
    	if (ad == null || regionCodes.size() == 0 || regionTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumRegionCodeIn(ad.getMedium().getId(), 
    			Util.getStringList(regionCodes.toString()));
    	
    	
    	SysAuditTrailValueItem editItem = null;
    	
    	AdcAdTarget target = adcService.getAdTarget((int)model.get("id"));
    	if (target == null) {
        	target = new AdcAdTarget(ad, "RG", regionCodes.size(), regionCodes.toString(), 
        			regionTexts.toString(), scrCnt, 1000, session);
    	} else {
    		String oTgtDisplay = target.getTgtDisplay();
    		if (!oTgtDisplay.equals(regionTexts.toString())) {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", oTgtDisplay, regionTexts.toString());
    		} else {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", "NoChg", "NoChg");
    		}
    		
        	target.setTgtCount(regionCodes.size());
        	target.setTgtValue(regionCodes.toString());
        	target.setTgtDisplay(regionTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		adcService.saveAndReorderAdTarget(target);
        	
        	
    		if (editItem == null) {
    			
        		// 감사 추적: Case SA5
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Inven", "F", session);
            	auditTrail.setTgtName(regionTexts.toString());
            	auditTrail.setTgtValue("RG");
            	
                sysService.saveOrUpdate(auditTrail);
                
    		} else if (!editItem.getOldValue().equals("NoChg")) {
    			
    	    	// 감사 추적: Case EA7
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Inven", "F", session);
            	auditTrail.setTgtName(regionTexts.toString());
            	auditTrail.setTgtValue("RG");
            	
                sysService.saveOrUpdate(auditTrail);
                
                sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, editItem));
                
    		} else {
    			// 값이 변경으로 등록되었으나, 이전과 동일값이기 때문에 추적 등록하지 않음
    		}
    	} catch (Exception e) {
    		logger.error("saveRegion", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 매체 화면
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveScreen", method = RequestMethod.POST)
    public @ResponseBody String saveScreen(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> screenIds = (ArrayList<Object>) model.get("screenIds");
		ArrayList<Object> screenTexts = (ArrayList<Object>) model.get("screenTexts");
    	AdcAd ad = adcService.getAd((int)model.get("ad"));

    	// 파라미터 검증
    	if (ad == null || screenIds.size() == 0 || screenTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumScreenIdIn(ad.getMedium().getId(), 
    			Util.getIntegerList(screenIds.toString()));
    	
    	
    	SysAuditTrailValueItem editItem = null;
    	
    	AdcAdTarget target = adcService.getAdTarget((int)model.get("id"));
    	if (target == null) {
        	target = new AdcAdTarget(ad, "SC", screenIds.size(), screenIds.toString(), 
        			screenTexts.toString(), scrCnt, 1000, session);
    	} else {
    		String oTgtDisplay = target.getTgtDisplay();
    		if (!oTgtDisplay.equals(screenTexts.toString())) {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", oTgtDisplay, screenTexts.toString());
    		} else {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", "NoChg", "NoChg");
    		}
    		
        	target.setTgtCount(screenIds.size());
        	target.setTgtValue(screenIds.toString());
        	target.setTgtDisplay(screenTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		adcService.saveAndReorderAdTarget(target);
        	
        	
    		if (editItem == null) {
    			
        		// 감사 추적: Case SA5
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Inven", "F", session);
            	auditTrail.setTgtName(screenTexts.toString());
            	auditTrail.setTgtValue("SC");
            	
                sysService.saveOrUpdate(auditTrail);
                
    		} else if (!editItem.getOldValue().equals("NoChg")) {
    			
    	    	// 감사 추적: Case EA7
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Inven", "F", session);
            	auditTrail.setTgtName(screenTexts.toString());
            	auditTrail.setTgtValue("SC");
            	
                sysService.saveOrUpdate(auditTrail);
                
                sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, editItem));
                
    		} else {
    			// 값이 변경으로 등록되었으나, 이전과 동일값이기 때문에 추적 등록하지 않음
    		}
    	} catch (Exception e) {
    		logger.error("saveScreen", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 광역시/도
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveState", method = RequestMethod.POST)
    public @ResponseBody String saveState(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> stateCodes = (ArrayList<Object>) model.get("stateCodes");
		ArrayList<Object> stateTexts = (ArrayList<Object>) model.get("stateTexts");
    	AdcAd ad = adcService.getAd((int)model.get("ad"));

    	// 파라미터 검증
    	if (ad == null || stateCodes.size() == 0 || stateTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumStateCodeIn(ad.getMedium().getId(), 
    			Util.getStringList(stateCodes.toString()));
    	
    	
    	SysAuditTrailValueItem editItem = null;
    	
    	AdcAdTarget target = adcService.getAdTarget((int)model.get("id"));
    	if (target == null) {
        	target = new AdcAdTarget(ad, "CT", stateCodes.size(), stateCodes.toString(), 
        			stateTexts.toString(), scrCnt, 1000, session);
    	} else {
    		String oTgtDisplay = target.getTgtDisplay();
    		if (!oTgtDisplay.equals(stateTexts.toString())) {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", oTgtDisplay, stateTexts.toString());
    		} else {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", "NoChg", "NoChg");
    		}
    		
        	target.setTgtCount(stateCodes.size());
        	target.setTgtValue(stateCodes.toString());
        	target.setTgtDisplay(stateTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		adcService.saveAndReorderAdTarget(target);
        	
        	
    		if (editItem == null) {
    			
        		// 감사 추적: Case SA5
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Inven", "F", session);
            	auditTrail.setTgtName(stateTexts.toString());
            	auditTrail.setTgtValue("CT");
            	
                sysService.saveOrUpdate(auditTrail);
                
    		} else if (!editItem.getOldValue().equals("NoChg")) {
    			
    	    	// 감사 추적: Case EA7
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Inven", "F", session);
            	auditTrail.setTgtName(stateTexts.toString());
            	auditTrail.setTgtValue("CT");
            	
                sysService.saveOrUpdate(auditTrail);
                
                sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, editItem));
                
    		} else {
    			// 값이 변경으로 등록되었으나, 이전과 동일값이기 때문에 추적 등록하지 않음
    		}
    	} catch (Exception e) {
    		logger.error("saveState", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 사이트
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveSite", method = RequestMethod.POST)
    public @ResponseBody String saveSite(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> siteIds = (ArrayList<Object>) model.get("siteIds");
		ArrayList<Object> siteTexts = (ArrayList<Object>) model.get("siteTexts");
    	AdcAd ad = adcService.getAd((int)model.get("ad"));

    	// 파라미터 검증
    	if (ad == null || siteIds.size() == 0 || siteTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumSiteIdIn(ad.getMedium().getId(), 
    			Util.getIntegerList(siteIds.toString()));
    	
    	
    	SysAuditTrailValueItem editItem = null;
    	
    	AdcAdTarget target = adcService.getAdTarget((int)model.get("id"));
    	if (target == null) {
        	target = new AdcAdTarget(ad, "ST", siteIds.size(), siteIds.toString(), 
        			siteTexts.toString(), scrCnt, 1000, session);
    	} else {
    		String oTgtDisplay = target.getTgtDisplay();
    		if (!oTgtDisplay.equals(siteTexts.toString())) {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", oTgtDisplay, siteTexts.toString());
    		} else {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", "NoChg", "NoChg");
    		}
    		
        	target.setTgtCount(siteIds.size());
        	target.setTgtValue(siteIds.toString());
        	target.setTgtDisplay(siteTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		adcService.saveAndReorderAdTarget(target);
        	
        	
    		if (editItem == null) {
    			
        		// 감사 추적: Case SA5
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Inven", "F", session);
            	auditTrail.setTgtName(siteTexts.toString());
            	auditTrail.setTgtValue("ST");
            	
                sysService.saveOrUpdate(auditTrail);
                
    		} else if (!editItem.getOldValue().equals("NoChg")) {
    			
    	    	// 감사 추적: Case EA7
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Inven", "F", session);
            	auditTrail.setTgtName(siteTexts.toString());
            	auditTrail.setTgtValue("ST");
            	
                sysService.saveOrUpdate(auditTrail);
                
                sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, editItem));
                
    		} else {
    			// 값이 변경으로 등록되었으나, 이전과 동일값이기 때문에 추적 등록하지 않음
    		}
    	} catch (Exception e) {
    		logger.error("saveSite", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 입지 유형
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveSiteCond", method = RequestMethod.POST)
    public @ResponseBody String saveSiteCond(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> siteCondCodes = (ArrayList<Object>) model.get("siteCondCodes");
		ArrayList<Object> siteCondTexts = (ArrayList<Object>) model.get("siteCondTexts");
    	AdcAd ad = adcService.getAd((int)model.get("ad"));

    	// 파라미터 검증
    	if (ad == null || siteCondCodes.size() == 0 || siteCondTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumSiteCondCodeIn(ad.getMedium().getId(), 
    			Util.getStringList(siteCondCodes.toString()));
    	
    	
    	SysAuditTrailValueItem editItem = null;
    	
    	AdcAdTarget target = adcService.getAdTarget((int)model.get("id"));
    	if (target == null) {
        	target = new AdcAdTarget(ad, "CD", siteCondCodes.size(), siteCondCodes.toString(), 
        			siteCondTexts.toString(), scrCnt, 1000, session);
    	} else {
    		String oTgtDisplay = target.getTgtDisplay();
    		if (!oTgtDisplay.equals(siteCondTexts.toString())) {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", oTgtDisplay, siteCondTexts.toString());
    		} else {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", "NoChg", "NoChg");
    		}
    		
        	target.setTgtCount(siteCondCodes.size());
        	target.setTgtValue(siteCondCodes.toString());
        	target.setTgtDisplay(siteCondTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		adcService.saveAndReorderAdTarget(target);
        	
        	
    		if (editItem == null) {
    			
        		// 감사 추적: Case SA5
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Inven", "F", session);
            	auditTrail.setTgtName(siteCondTexts.toString());
            	auditTrail.setTgtValue("CD");
            	
                sysService.saveOrUpdate(auditTrail);
                
    		} else if (!editItem.getOldValue().equals("NoChg")) {
    			
    	    	// 감사 추적: Case EA7
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Inven", "F", session);
            	auditTrail.setTgtName(siteCondTexts.toString());
            	auditTrail.setTgtValue("CD");
            	
                sysService.saveOrUpdate(auditTrail);
                
                sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, editItem));
                
    		} else {
    			// 값이 변경으로 등록되었으나, 이전과 동일값이기 때문에 추적 등록하지 않음
    		}
    	} catch (Exception e) {
    		logger.error("saveSite", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }
    
    
	/**
	 * 저장 액션 - 화면 묶음
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/saveScrPack", method = RequestMethod.POST)
    public @ResponseBody String saveScrPack(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		ArrayList<Object> scrPackIds = (ArrayList<Object>) model.get("scrPackIds");
		ArrayList<Object> scrPackTexts = (ArrayList<Object>) model.get("scrPackTexts");
    	AdcAd ad = adcService.getAd((int)model.get("ad"));

    	// 파라미터 검증
    	if (ad == null || scrPackIds.size() == 0 || scrPackTexts.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	int scrCnt = invService.getMonitScreenCountByMediumScrPackIdIn(ad.getMedium().getId(), 
    			Util.getIntegerList(scrPackIds.toString()));
    	
    	
    	SysAuditTrailValueItem editItem = null;
    	
    	AdcAdTarget target = adcService.getAdTarget((int)model.get("id"));
    	if (target == null) {
        	target = new AdcAdTarget(ad, "SP", scrPackIds.size(), scrPackIds.toString(), 
        			scrPackTexts.toString(), scrCnt, 1000, session);
    	} else {
    		String oTgtDisplay = target.getTgtDisplay();
    		if (!oTgtDisplay.equals(scrPackTexts.toString())) {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", oTgtDisplay, scrPackTexts.toString());
    		} else {
    			editItem = new SysAuditTrailValueItem("타겟팅 변경", "NoChg", "NoChg");
    		}
    		
        	target.setTgtCount(scrPackIds.size());
        	target.setTgtValue(scrPackIds.toString());
        	target.setTgtDisplay(scrPackTexts.toString());
        	target.setTgtScrCount(scrCnt);
        	
        	target.touchWho(session);
    	}
    	
    	try {
    		adcService.saveAndReorderAdTarget(target);
        	
        	
    		if (editItem == null) {
    			
        		// 감사 추적: Case SA5
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Inven", "F", session);
            	auditTrail.setTgtName(scrPackTexts.toString());
            	auditTrail.setTgtValue("SP");
            	
                sysService.saveOrUpdate(auditTrail);
                
    		} else if (!editItem.getOldValue().equals("NoChg")) {
    			
    	    	// 감사 추적: Case EA7
            	
            	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Inven", "F", session);
            	auditTrail.setTgtName(scrPackTexts.toString());
            	auditTrail.setTgtValue("SP");
            	
                sysService.saveOrUpdate(auditTrail);
                
                sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, editItem));
                
    		} else {
    			// 값이 변경으로 등록되었으나, 이전과 동일값이기 때문에 추적 등록하지 않음
    		}
    	} catch (Exception e) {
    		logger.error("saveScrPack", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }

}
