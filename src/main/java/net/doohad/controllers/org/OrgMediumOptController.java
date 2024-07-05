package net.doohad.controllers.org;

import java.util.ArrayList;
import java.util.HashMap;
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
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.models.org.OrgSiteCond;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysOpt;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * (매체) 일반 설정 컨트롤러
 */
@Controller("org-medium-opt-controller")
@RequestMapping(value="/org/mediumopt")
public class OrgMediumOptController {

	private static final Logger logger = LoggerFactory.getLogger(OrgMediumOptController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private SysService sysService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 일반 설정 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "일반 설정");
    	
    	

    	
        return "org/mediumopt";
    }
    
    
	/**
	 * 읽기 액션 - 광고주
	 */
    @RequestMapping(value = "/readAdv", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readAdv(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = orgService.getAdvertiserList(request);
    		
    		List<Tuple> countList = adcService.getCreativeCountGroupByMediumAdvertiserId(Util.getSessionMediumId(session));
    		HashMap<String, Long> countCreatMap = new HashMap<String, Long>();
    		for(Tuple tuple : countList) {
    			countCreatMap.put("K" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		countList = adcService.getCampaignCountGroupByMediumAdvertiserId(Util.getSessionMediumId(session));
    		HashMap<String, Long> countCampMap = new HashMap<String, Long>();
    		for(Tuple tuple : countList) {
    			countCampMap.put("K" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		for(Object obj : result.getData()) {
    			OrgAdvertiser advertiser = (OrgAdvertiser)obj;
    			
    			Long value = countCreatMap.get("K" + advertiser.getId());
    			if (value != null) {
    				advertiser.setCreativeCount(value.intValue());
    			}
    			
    			value = countCampMap.get("K" + advertiser.getId());
    			if (value != null) {
    				advertiser.setCampaignCount(value.intValue());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - 광고주
	 */
    @RequestMapping(value = "/createAdv", method = RequestMethod.POST)
    public @ResponseBody String createAdv(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String domainName = (String)model.get("domainName");
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || Util.isNotValid(domainName)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	OrgAdvertiser target = new OrgAdvertiser(medium, name, domainName, session);
    	
        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 광고주
	 */
    @RequestMapping(value = "/updateAdv", method = RequestMethod.POST)
    public @ResponseBody String updateAdv(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String domainName = (String)model.get("domainName");
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || Util.isNotValid(domainName)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	OrgAdvertiser target = orgService.getAdvertiser((int)model.get("id"));
    	if (target != null) {
    		
            target.setName(name);
            target.setDomainName(domainName);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장 - 광고주
	 */
    private void saveOrUpdate(OrgAdvertiser target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME_OR_DOMAIN_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_NAME_OR_DOMAIN_NAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션 - 광고주
	 */
    @RequestMapping(value = "/destroyAdv", method = RequestMethod.POST)
    public @ResponseBody String destroyAdv(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");

    	try {
        	for (Object id : objs) {
        		OrgAdvertiser advertiser = orgService.getAdvertiser((int)id);
        		if (advertiser != null) {
        			// 이용중인 캠페인 점검
        			int campCnt = adcService.getCampaignCountByAdvertiserId(advertiser.getId());
        			if (campCnt > 0) {
        				throw new DataIntegrityViolationException(StringInfo.DEL_ERROR_CHILD_AD);
        			}
        			
        			// 이용중인 광고 소재 점검
        			int creatCnt = adcService.getCreativeCountByAdvertiserId(advertiser.getId());
        			if (creatCnt > 0) {
        				throw new DataIntegrityViolationException(StringInfo.DEL_ERROR_CHILD_AD);
        			}

        			// 소프트 삭제 진행
        			orgService.deleteSoftAdvertiser(advertiser);
        		}
        	}
        } catch (DataIntegrityViolationException dive) {
    		logger.error("destroy", dive);
        	throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_INVENTORY);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 입지 유형
	 */
    @RequestMapping(value = "/readCond", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readCond(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = orgService.getSiteCondList(request);
    		
    		HashMap<String, Long> countMap = new HashMap<String, Long>();
    		List<Tuple> countList = invService.getSiteCountGroupByMediumSiteCondId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			countMap.put("K" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		countList = invService.getScreenCountGroupByMediumSiteCondId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			countMap.put("L" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		for(Object obj : result.getData()) {
    			OrgSiteCond siteCond = (OrgSiteCond)obj;
    			
    			Long value1 = countMap.get("K" + siteCond.getId());
    			if (value1 != null) {
    				siteCond.setSiteCount(value1.intValue());
    			}
    			
    			Long value2 = countMap.get("L" + siteCond.getId());
    			if (value2 != null) {
    				siteCond.setScreenCount(value2.intValue());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - 입지 유형
	 */
    @RequestMapping(value = "/createCond", method = RequestMethod.POST)
    public @ResponseBody String createCond(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String code = (String)model.get("code");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	OrgSiteCond target = new OrgSiteCond(medium, name, code, activeStatus, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 입지 유형
	 */
    @RequestMapping(value = "/updateCond", method = RequestMethod.POST)
    public @ResponseBody String updateCond(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String code = (String)model.get("code");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	OrgSiteCond target = orgService.getSiteCond((int)model.get("id"));
    	if (target != null) {
        	
    		target.setCode(code);
            target.setName(name);
            target.setActiveStatus(activeStatus);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장 - 입지 유형
	 */
    private void saveOrUpdate(OrgSiteCond target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션 - 입지 유형
	 */
    @RequestMapping(value = "/destroyCond", method = RequestMethod.POST)
    public @ResponseBody String destroyCond(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<OrgSiteCond> siteConds = new ArrayList<OrgSiteCond>();

    	for (Object id : objs) {
    		OrgSiteCond siteCond = new OrgSiteCond();
    		
    		siteCond.setId((int)id);
    		
    		siteConds.add(siteCond);
    	}
    	
    	try {
        	orgService.deleteSiteConds(siteConds);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("destroy", dive);
        	throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_INVENTORY);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 화면 옵션
	 */
    @RequestMapping(value = "/readOpt", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readOpt(HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
    	boolean hasTester = false;
    	String opts = SolUtil.getOptValue(Util.getSessionMediumId(session), "opt.list");
    	if (Util.isValid(opts)) {
    		List<String> optList = Util.tokenizeValidStr(opts);
    		for(String opt : optList) {
    			List<String> optPair = Util.tokenizeValidStr(opt, ",");
    			if (optPair.size() == 2) {
    				String ID = optPair.get(0);
    				String name = optPair.get(1);
    				
    				if (ID.equals("tester")) {
    					hasTester = true;
    				}
    				
    				DropDownListItem item = new DropDownListItem(ID, name);
    				int cnt = 0;

    				if (medium != null) {
        		    	SysOpt sysOpt = sysService.getOpt("opt." + ID + "." + medium.getShortName());
    					if (sysOpt != null) {
    						String value = sysOpt.getValue();
    						if (Util.isValid(value)) {
    							List<String> ids = Util.tokenizeValidStr(value);
    							cnt = ids.size();
    						}
    					}
    				}
    				item.setIcon(String.valueOf(cnt));
    				
    				list.add(item);
    			}
    		}
    	}
    	
    	if (!hasTester) {
    		list.add(new DropDownListItem("0", "tester", "테스터"));
    	}
    	
    	return list;
    }

}
