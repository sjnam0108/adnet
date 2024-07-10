package kr.adnetwork.controllers.adc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.SysService;
import kr.adnetwork.models.sys.SysAuditTrail;
import kr.adnetwork.models.sys.SysAuditTrailValue;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.adc.AdcCreatDragItem;
import kr.adnetwork.viewmodels.sys.SysAuditTrailValueItem;

/**
 * 캠페인 컨트롤러(광고 소재)
 */
@Controller("adc-campaign-creative-controller")
@RequestMapping(value="/adc/campaign/creatives")
public class AdcCampaignCreativeController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignCreativeController.class);

	
    @Autowired 
    private AdcService adcService;

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
	 * 캠페인 컨트롤러(광고 소재)
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
		
		// 묶음 광고 모드 확인
		boolean isPackedAdMode = false;
		if (ad != null && Util.isValid(ad.getViewTypeCode()) && Util.isValid(ad.getFixedResolution())) {
			FndViewType viewType = fndService.getViewType(ad.getViewTypeCode(), ad.getFixedResolution());
			isPackedAdMode = viewType != null && viewType.isAdPackUsed();
		}

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcAd");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고");

    	model.addAttribute("Campaign", campaign);
    	model.addAttribute("Ad", ad);
    	
		model.addAttribute("Creatives", getCreativeListByAd(ad));
		model.addAttribute("isPackedAdMode", isPackedAdMode);
    	
    	
        return "adc/campaign/camp-creative";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = adcService.getAdCreativeList(request);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getCreatTargetCountGroupByMediumCreativeId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
    		
    		for(Object obj : result.getData()) {
    			AdcAdCreative adCreate = (AdcAdCreative)obj;
    			List<AdcCreatFile> fileList = adcService.getCreatFileListByCreativeId(adCreate.getCreative().getId());
    			
    			String resolutions = "";
    			
    			// 이 값이 유효하다는 것: 게시 유형이 지정되어 있고, 유효한 게시 크기(해상도) 존재
    			String fixedReso = "";
    			if (Util.isValid(adCreate.getAd().getViewTypeCode())) {
    				fixedReso = fndService.getViewTypeResoByCode(adCreate.getAd().getViewTypeCode());
    			}
    			for(AdcCreatFile creatFile : fileList) {
        			
        			// 20% 범위로 적합도 판정
    				int fitness = Util.isValid(fixedReso) ?
    						SolUtil.measureResolutionWith(creatFile.getResolution(), fixedReso, 20) :
    						adcService.measureResolutionWithMedium(
    	            				creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    				
    				if (Util.isValid(resolutions)) {
    					resolutions += "|" + String.valueOf(fitness) + ":" + creatFile.getResolution();
    				} else {
    					resolutions = String.valueOf(fitness) + ":" + creatFile.getResolution();
    				}
    			}
    			
    			adCreate.getCreative().setFileResolutions(resolutions);
    			
    			if (targetIds.contains(adCreate.getCreative().getId())) {
    				adCreate.getCreative().setInvenTargeted(true);
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 광고 소재 목록 획득
	 */
    private List<DropDownListItem> getCreativeListByAd(AdcAd ad) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		if (ad != null) {
			List<AdcCreative> creativeList = adcService.getCreativeListByAdvertiserIdViewTypeCode(
							ad.getCampaign().getAdvertiser().getId(), ad.getViewTypeCode());
			for (AdcCreative creative : creativeList) {
				// 대체 광고는 제외
				if (creative.getType().equals("F") || creative.getStatus().equals("V")) {
					continue;
				}
				
				list.add(new DropDownListItem(creative.getName(), String.valueOf(creative.getId())));
			}
			
			Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		}

		if (list.size() == 0) {
			list.add(0, new DropDownListItem("", "-1"));
		}
		
		return list;
    }
    
    
	/**
	 * 소재와 연결 액션 - Ad & Creative
	 */
    @RequestMapping(value = "/link", method = RequestMethod.POST)
    public @ResponseBody String link(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	int weight = (int)model.get("weight");
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	AdcCreative creative = adcService.getCreative((int)model.get("creative"));
    	AdcAd ad = adcService.getAd((int)model.get("ad"));
    	
    	// 파라미터 검증
    	if (creative == null || ad == null || startDate == null || endDate == null || weight < 1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	AdcAdCreative target = new AdcAdCreative(ad, creative, weight, startDate, endDate, session);
    	
        saveOrUpdate(target, locale, session);
        
        
		//
		// 감사 추적: Case SA1
		//
		// - 01: 시작일
		// - 02: 종료일
		// - 03: 광고 소재간 가중치
        
		ArrayList<SysAuditTrailValueItem> editItems = new ArrayList<SysAuditTrailValueItem>();
		
		editItems.add(new SysAuditTrailValueItem("시작일", "[-]", Util.toSimpleString(startDate, "yyyy-MM-dd")));
		editItems.add(new SysAuditTrailValueItem("종료일", "[-]", Util.toSimpleString(endDate, "yyyy-MM-dd")));
		editItems.add(new SysAuditTrailValueItem("광고 소재간 가중치", "[-]", String.valueOf(weight)));
    	

    	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Creat", "F", session);
    	auditTrail.setTgtName(creative.getName());
    	auditTrail.setTgtValue(String.valueOf(creative.getId()));
    	
        sysService.saveOrUpdate(auditTrail);
    	
        for(SysAuditTrailValueItem item : editItems) {
        	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
        }
        

        return "Ok";
    }

    
    /**
	 * 연결 해제 액션
	 */
    @RequestMapping(value = "/unlink", method = RequestMethod.POST)
    public @ResponseBody String unlink(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	
    	// 감사 추적: Case UA1
    	ArrayList<SysAuditTrail> auditTrailList = new ArrayList<SysAuditTrail>();
    	
    	
    	List<AdcAdCreative> adCreatives = new ArrayList<AdcAdCreative>();

    	for (Object id : objs) {
    		AdcAdCreative adCreative = adcService.getAdCreative((int)id);
    		if (adCreative != null) {
        		adCreatives.add(adCreative);
        		
            	SysAuditTrail auditTrail = new SysAuditTrail(adCreative.getAd(), "U", "Creat", "F", session);
            	auditTrail.setTgtName(adCreative.getCreative().getName());
            	auditTrail.setTgtValue(String.valueOf(adCreative.getCreative().getId()));
            	
            	auditTrailList.add(auditTrail);
    		}
    	}

    	
    	try {
        	adcService.deleteAdCreatives(adCreatives);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("destroy", dive);
        	throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_AD_SELECT);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}
    	
    	
    	for(SysAuditTrail auditTrail : auditTrailList) {
            sysService.saveOrUpdate(auditTrail);
    	}

        return "Ok";
    }
    
    
	/**
	 * 소재와 연결 변경 액션 - Ad & Creative
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	int weight = (int)model.get("weight");
    	
    	Date startDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("startDate")));
    	Date endDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("endDate")));
    	
    	AdcCreative creative = adcService.getCreative((int)model.get("creative"));
    	AdcAd ad = adcService.getAd((int)model.get("ad"));
    	
    	// 파라미터 검증
    	if (creative == null || ad == null || startDate == null || endDate == null || weight < 1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
		//
		// 감사 추적: Case EA2
		//
		// - 01: 시작일
		// - 02: 종료일
		// - 03: 광고 소재간 가중치
		
		ArrayList<SysAuditTrailValueItem> editItems = new ArrayList<SysAuditTrailValueItem>();
        
        
    	AdcAdCreative target = adcService.getAdCreative((int)model.get("id"));
    	if (target != null) {
    		
    		Date oStartDate = target.getStartDate();
    		Date oEndDate = target.getEndDate();
    		int oWeight = target.getWeight();
    		if (oStartDate.compareTo(startDate) != 0) {
    			editItems.add(new SysAuditTrailValueItem("시작일", 
    					Util.toSimpleString(oStartDate, "yyyy-MM-dd"), Util.toSimpleString(startDate, "yyyy-MM-dd")));
    		}
    		if (oEndDate.compareTo(endDate) != 0) {
    			editItems.add(new SysAuditTrailValueItem("종료일", 
    					Util.toSimpleString(oEndDate, "yyyy-MM-dd"), Util.toSimpleString(endDate, "yyyy-MM-dd")));
    		}
    		if (oWeight != weight) {
    			editItems.add(new SysAuditTrailValueItem("광고 소재간 가중치", String.valueOf(oWeight), String.valueOf(weight)));
    		}
    		
    		target.setStartDate(startDate);
    		target.setEndDate(endDate);
    		target.setWeight(weight);
    		
    		target.touchWho(session);
    	}
    	
        saveOrUpdate(target, locale, session);

        
        if (editItems.size() > 0) {
        	
        	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Creat", "F", session);
        	auditTrail.setTgtName(creative.getName());
        	auditTrail.setTgtValue(String.valueOf(creative.getId()));
            sysService.saveOrUpdate(auditTrail);
        	
            for(SysAuditTrailValueItem item : editItems) {
            	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
            }
        }

        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcAdCreative target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {

        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(target);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
    
	/**
	 * 읽기 액션 - 가능한 광고 소재 전체 
	 */
    @RequestMapping(value = "/readCreats", method = RequestMethod.POST)
    public @ResponseBody List<AdcCreatDragItem> readCreats(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	try {
    		ArrayList<AdcCreatDragItem> dragItems = new ArrayList<AdcCreatDragItem>();
    		
    		String reso = (String)model.get("resolution");
    		AdcAd ad = adcService.getAd((int)model.get("id"));
        	
        	// 파라미터 검증
        	if (ad == null || Util.isNotValid(reso)) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
            }
        	

        	// 동일 게시 유형 && 해상도 && 광고주
        	List<AdcCreative> creatList = adcService.getCreativeListByAdvertiserIdViewTypeCode(
        			ad.getCampaign().getAdvertiser().getId(), ad.getViewTypeCode());
        	for(AdcCreative creat : creatList) {
        		if (creat.getViewTypeCode().equals(ad.getViewTypeCode())) {
        			AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(creat.getId(), reso);
        			if (creatFile != null) {
                		dragItems.add(new AdcCreatDragItem(creatFile));
        			}
        		}
        	}
        	
        	for(AdcCreatDragItem dragItem : dragItems) {
        		List<AdcAdCreative> list = adcService.getAdCreativeListByCreativeId(dragItem.getCreatId());
        		dragItem.setValid(list.size() == 0);
        	}
        	
    		Collections.sort(dragItems, new Comparator<AdcCreatDragItem>() {
    	    	public int compare(AdcCreatDragItem item1, AdcCreatDragItem item2) {
    	    		return item1.getOrderCode().compareTo(item2.getOrderCode());
    	    	}
    	    });
    		
    		return dragItems;
    	} catch (Exception e) {
    		logger.error("readCreats", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 현재 광고 소재
	 */
    @RequestMapping(value = "/readCurrCreats", method = RequestMethod.POST)
    public @ResponseBody List<AdcCreatDragItem> readCurrCreats(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	try {
    		ArrayList<AdcCreatDragItem> dragItems = new ArrayList<AdcCreatDragItem>();
    		
    		String reso = (String)model.get("resolution");
    		AdcAd ad = adcService.getAd((int)model.get("id"));
        	
        	// 파라미터 검증
        	if (ad == null || Util.isNotValid(reso)) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
            }
        	

        	List<String> idList = Util.tokenizeValidStr(ad.getAdPackIds());
        	for(String idStr : idList) {
        		AdcCreative creat = adcService.getCreative(Util.parseInt(idStr));
        		if (creat.getViewTypeCode().equals(ad.getViewTypeCode())) {
        			AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(creat.getId(), reso);
        			if (creatFile != null) {
                		dragItems.add(new AdcCreatDragItem(creatFile));
        			}
        		}
        	}
        	
    		return dragItems;
    	} catch (Exception e) {
    		logger.error("readCurrCreats", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 변경 액션 - 묶음 광고 소재
	 */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/editPack", method = RequestMethod.POST)
    public @ResponseBody String editPack(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcAd ad = adcService.getAd((int)model.get("ad"));
    	
    	String ids = (String)model.get("ids");
    	
    	List<String> idList = Util.tokenizeValidStr(ids);
    	
    	// 파라미터 검증
    	if (ad == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (!ad.getAdPackIds().equals(ids)) {
        	ArrayList<Integer> creatIds = new ArrayList<Integer>();
        	for(String id : idList) {
        		int i = Util.parseInt(id);
        		if (i > 0 && !creatIds.contains(i)) {
        			creatIds.add(i);
        		}
        	}
        	
        	ArrayList<Integer> currIds = new ArrayList<Integer>();
        	ArrayList<AdcAdCreative> delList = new ArrayList<AdcAdCreative>();
        	List<AdcAdCreative> adCreatList = adcService.getAdCreativeListByAdId(ad.getId());
        	for(AdcAdCreative adCreat : adCreatList) {
        		if (creatIds.contains(adCreat.getCreative().getId())) {
        			currIds.add(adCreat.getCreative().getId());
        		} else {
        			delList.add(adCreat);
        		}
        	}
        	
    		ArrayList<Integer> newIds = (ArrayList<Integer>) creatIds.clone();
    		newIds.removeAll(currIds);
    		
    		
    		for(Integer i : newIds) {
    			AdcCreative creative = adcService.getCreative(i);
    			if (creative != null) {
    				AdcAdCreative target = new AdcAdCreative(ad, creative, 1, ad.getStartDate(), ad.getEndDate(), session);
    				adcService.saveOrUpdate(target);
    			}
    		}
    		
    		adcService.deleteAdCreatives(delList);
    		
    		ad.setAdPackIds(ids);
    		ad.touchWho(session);
    		
    		adcService.saveOrUpdate(ad);
        	
        	
        	
    		//
    		// 감사 추적: Case SA2
    		//
        	
        	//
        	// 묶음 광고 모드 = 게시유형이 지정 && 게시유형의 속성 중 묶음 광고 모드 체크
        	//
        	String creatFileIds = "";
        	String fixedReso = fndService.getViewTypeResoByCode(ad.getViewTypeCode());
        	if (Util.isValid(fixedReso)) {
            	for(String id : idList) {
            		AdcCreative creat = adcService.getCreative(Util.parseInt(id));
            		if (creat.getViewTypeCode().equals(ad.getViewTypeCode())) {
            			AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(creat.getId(), fixedReso);
            			if (creatFile != null) {
            				creatFileIds += String.valueOf(creatFile.getId()) + "|";
            			}
            		}
            		
            	}
        	}
        	
        	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "CPack", "F", session);
        	auditTrail.setTgtName("");
        	auditTrail.setTgtValue(creatFileIds);
        	
            sysService.saveOrUpdate(auditTrail);
    	}
    	
        
        return "Ok";
    }
}
