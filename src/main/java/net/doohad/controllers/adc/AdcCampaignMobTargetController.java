package net.doohad.controllers.adc;

import java.util.ArrayList;
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
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcMobTarget;
import net.doohad.models.fnd.FndMobRegion;
import net.doohad.models.org.OrgRadRegion;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysAuditTrail;
import net.doohad.models.sys.SysAuditTrailValue;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.adc.AdcMobTargetOrderItem;
import net.doohad.viewmodels.sys.SysAuditTrailValueItem;

/**
 * 캠페인 컨트롤러(모바일 타겟팅)
 */
@Controller("adc-campaign-mob-target-controller")
@RequestMapping(value="/adc/campaign/mobtargets")
public class AdcCampaignMobTargetController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignInvTargetController.class);

	
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
	 * 캠페인 컨트롤러(모바일 타겟팅)
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

    	

    	
        return "adc/campaign/camp-mobtarget";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		return adcService.getMobTargetViewList(request);
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 변경 액션 - 인벤 타겟팅 And/Or 토글 처리
	 */
	@RequestMapping(value = "/updateFilterType", method = RequestMethod.POST)
    public @ResponseBody String updateFilterType(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String filterType = (String)model.get("filterType");

    	AdcMobTarget target = adcService.getMobTarget((int)model.get("id"));
    	
    	// 파라미터 검증
    	if (target == null || Util.isNotValid(filterType)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
		//
		// 감사 추적: Case EA3
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
            	
    			String name = "";
    			if (target.getMobType().equals("CR")) {
    				OrgRadRegion radRegion = orgService.getRadRegion(target.getTgtId());
    				if (radRegion != null) {
    					name = radRegion.getName();
    				}
    			} else if (target.getMobType().equals("RG")) {
    				FndMobRegion mobRegion = fndService.getMobRegion(target.getTgtId());
    				if (mobRegion != null) {
    					name = mobRegion.getName();
    				}
    			}

    			if (Util.isValid(name)) {
                	SysAuditTrail auditTrail = new SysAuditTrail(target.getAd(), "E", "Mobil", "F", session);
                	auditTrail.setTgtName(name);
                	auditTrail.setTgtValue(target.getMobType());
                    sysService.saveOrUpdate(auditTrail);
                	
                    for(SysAuditTrailValueItem item : editItems) {
                    	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                    }
    			}
    			
    		}
    		
    	} catch (Exception e) {
    		logger.error("updateFilterType", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }

	
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<AdcMobTarget> mobTargets = new ArrayList<AdcMobTarget>();

    	ArrayList<String> delItems = new ArrayList<String>();
    	AdcAd ad = null;
    	for (Object id : objs) {
    		AdcMobTarget mobTarget = adcService.getMobTarget((int)id);
    		if (mobTarget != null) {
    			if (ad == null) {
    				ad = mobTarget.getAd();
    			}
    			if (mobTarget.getMobType().equals("CR")) {
    				OrgRadRegion radRegion = orgService.getRadRegion(mobTarget.getTgtId());
    				if (radRegion != null) {
    					delItems.add(mobTarget.getMobType() + radRegion.getName());
    				}
    			} else if (mobTarget.getMobType().equals("RG")) {
    				FndMobRegion mobRegion = fndService.getMobRegion(mobTarget.getTgtId());
    				if (mobRegion != null) {
    					delItems.add(mobTarget.getMobType() + mobRegion.getName());
    				}
    			}
    			mobTargets.add(mobTarget);
    		}
    		/*
    		// 감사 추적 목적으로 삭제 대상의 정보가 필요하기 때문에 변경
    		AdcMobTarget mobTarget = new AdcMobTarget();
    		
    		mobTarget.setId((int)id);
    		
    		mobTargets.add(mobTarget);
    		*/
    	}
    	
    	try {
        	adcService.deleteMobTargets(mobTargets);
        	
        	
    		// 감사 추적: Case UA3
            
        	if (ad != null) {
            	for(String s : delItems) {
            		if (s.length() > 2) {
                		String type = s.substring(0, 2);
                		String name = s.substring(2);
                    	
                    	SysAuditTrail auditTrail = new SysAuditTrail(ad, "U", "Mobil", "F", session);
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
	 * 읽기 액션 - 광고 모바일 타겟팅 순서 변경용
	 */
    @RequestMapping(value = "/readOrdered", method = RequestMethod.POST)
    public @ResponseBody List<AdcMobTargetOrderItem> readOrdered(@RequestBody Map<String, Object> model, 
    		HttpSession session) {
    	
    	List<AdcMobTarget> list = adcService.getMobTargetListByAdId((int)model.get("adId"));
    	
    	ArrayList<AdcMobTargetOrderItem> retList = new ArrayList<AdcMobTargetOrderItem>();
    	
    	int idx = 1;
    	for (AdcMobTarget mobTarget : list) {
    		String title = "";
    		if (mobTarget.getMobType().equals("RG")) {
    			FndMobRegion mobRegion = fndService.getMobRegion(mobTarget.getTgtId());
    			if (mobRegion != null) {
    				title = mobRegion.getName();
    			}
    		} else if (mobTarget.getMobType().equals("CR")) {
    			OrgRadRegion radRegion = orgService.getRadRegion(mobTarget.getTgtId());
    			if (radRegion != null) {
    				title = radRegion.getName();
    			}
    		}
    		
    		retList.add(new AdcMobTargetOrderItem(mobTarget, title, idx++));
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
    		// 감사 추적: Case EA4
    		//
    		ArrayList<String> seqItems = new ArrayList<String>();
    		AdcAd ad = null;
    		
    		List<String> list = Util.tokenizeValidStr(items);
    		
    		try {
    			int idx = 0;
        		for (String idStr : list) {
        			idx ++;
        			
        			AdcMobTarget mobTarget = adcService.getMobTarget(Util.parseInt(idStr));
        			if (mobTarget != null) {
        				int seq = idx * 10;
        				if (mobTarget.getSiblingSeq() != seq) {
        					mobTarget.setSiblingSeq(seq);
        					mobTarget.touchWho(session);
            				
        					adcService.saveOrUpdate(mobTarget);
        				}
        				
        				if (ad == null) {
        					ad = mobTarget.getAd();
        				}
        	    		if (mobTarget.getMobType().equals("RG")) {
        	    			FndMobRegion mobRegion = fndService.getMobRegion(mobTarget.getTgtId());
        	    			if (mobRegion != null) {
        	    				seqItems.add(mobTarget.getMobType() + mobRegion.getName());
        	    			}
        	    		} else if (mobTarget.getMobType().equals("CR")) {
        	    			OrgRadRegion radRegion = orgService.getRadRegion(mobTarget.getTgtId());
        	    			if (radRegion != null) {
        	    				seqItems.add(mobTarget.getMobType() + radRegion.getName());
        	    			}
        	    		}
        			}
        		}
        		
        		
        		if (seqItems.size() > 0 && ad != null) {
                	SysAuditTrail auditTrail = new SysAuditTrail(ad, "E", "Mobil", "F", session);
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
	 * 읽기 액션 - 모바일 타겟팅 지역
	 */
    @RequestMapping(value = "/readMobRgn", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readMobRgn(@RequestBody DataSourceRequest request) {
    	try {
            return fndService.getActiveMobRegionList(request);
    	} catch (Exception e) {
    		logger.error("readMobRgn", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 저장 액션 - 모바일 타겟팅 지역
	 */
	@RequestMapping(value = "/saveMobRgn", method = RequestMethod.POST)
    public @ResponseBody String saveMobRgn(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcAd ad = adcService.getAd((int)model.get("ad"));
    	FndMobRegion mobRegion = fndService.getMobRegion((int)model.get("id"));

    	// 파라미터 검증
    	if (ad == null || mobRegion == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	AdcMobTarget target = new AdcMobTarget(ad, "RG", mobRegion.getId(), 1000, session);

    	try {
    		adcService.saveAndReorderMobTarget(target);
        	
        	
    		// 감사 추적: Case SA4
        	
        	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Mobil", "F", session);
        	auditTrail.setTgtName(mobRegion.getName());
        	auditTrail.setTgtValue("RG");
        	
            sysService.saveOrUpdate(auditTrail);
    	} catch (Exception e) {
    		logger.error("saveMobRgn", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 지도의 원 반경 지역
	 */
    @RequestMapping(value = "/readRadRgn", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readRadRgn(@RequestBody DataSourceRequest request) {
    	try {
            return orgService.getActiveRadRegionList(request);
    	} catch (Exception e) {
    		logger.error("readRadRgn", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 저장 액션 - 지도의 원 반경 지역
	 */
	@RequestMapping(value = "/saveRadRgn", method = RequestMethod.POST)
    public @ResponseBody String saveRadRgn(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	AdcAd ad = adcService.getAd((int)model.get("ad"));
    	OrgRadRegion radRegion = orgService.getRadRegion((int)model.get("id"));

    	// 파라미터 검증
    	if (ad == null || radRegion == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	AdcMobTarget target = new AdcMobTarget(ad, "CR", radRegion.getId(), 1000, session);

    	try {
    		adcService.saveAndReorderMobTarget(target);
        	
        	
    		// 감사 추적: Case SA4
        	
        	SysAuditTrail auditTrail = new SysAuditTrail(ad, "S", "Mobil", "F", session);
        	auditTrail.setTgtName(radRegion.getName());
        	auditTrail.setTgtValue("RG");
        	
            sysService.saveOrUpdate(auditTrail);
    	} catch (Exception e) {
    		logger.error("saveMobRgn", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }

        return "Ok";
    }

}
