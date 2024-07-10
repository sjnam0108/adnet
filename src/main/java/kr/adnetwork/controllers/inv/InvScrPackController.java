package kr.adnetwork.controllers.inv;

import java.util.ArrayList;
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
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcAdTarget;
import kr.adnetwork.models.adc.AdcCreatTarget;
import kr.adnetwork.models.inv.InvScrPack;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.utils.Util;

/**
 * 화면 묶음 컨트롤러
 */
@Controller("inv-scr-pack-controller")
@RequestMapping(value="/inv/scrpack")
public class InvScrPackController {

	private static final Logger logger = LoggerFactory.getLogger(InvScrPackController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 화면 묶음 페이지
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
    	model.addAttribute("pageTitle", "화면 묶음");

    	

    	
        return "inv/scrpack";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = invService.getScrPackList(request);
    		
    		/*
    		HashMap<String, Long> countMap = new HashMap<String, Long>();
    		List<Tuple> countList = invService.getSiteCountGroupByMediumSiteCondId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			countMap.put("K" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		
    		countList = invService.getScreenCountGroupByMediumSiteCondId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			countMap.put("L" + String.valueOf((Integer) tuple.get(0)), (Long) tuple.get(1));
    		}
    		*/
    		
    		for(Object obj : result.getData()) {
    			InvScrPack scrPack = (InvScrPack)obj;
    			
    			scrPack.setScreenCount(invService.getScrPackItemCountByScrPackId(scrPack.getId()));
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	InvScrPack target = new InvScrPack(medium, name, memo, activeStatus, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	InvScrPack target = invService.getScrPack((int)model.get("id"));
    	if (target != null) {
        	
            target.setName(name);
            target.setActiveStatus(activeStatus);
    		target.setMemo(memo);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(InvScrPack target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            invService.saveOrUpdate(target);
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
    	
    	int mediumId = Util.getSessionMediumId(session);
    	
    	ArrayList<Integer> tgtScrPackIds = new ArrayList<Integer>();

		List<AdcAdTarget> list1 = adcService.getAdTargetListByMediumId(mediumId);
		for(AdcAdTarget adTarget : list1) {
			if (adTarget.getInvenType().equals("SP")) {
				List<Integer> scrPackIdList = Util.getIntegerList(adTarget.getTgtValue());
				for (Integer id : scrPackIdList) {
					if (!tgtScrPackIds.contains(id)) {
						tgtScrPackIds.add(id);
					}
				}
			}
		}
		
		List<AdcCreatTarget> list2 = adcService.getCreatTargetListByMediumId(mediumId);
		for(AdcCreatTarget creatTarget : list2) {
			if (creatTarget.getInvenType().equals("SP")) {
				List<Integer> scrPackIdList = Util.getIntegerList(creatTarget.getTgtValue());
				for (Integer id : scrPackIdList) {
					if (!tgtScrPackIds.contains(id)) {
						tgtScrPackIds.add(id);
					}
				}
			}
		}
    	
		
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<InvScrPack> items = new ArrayList<InvScrPack>();

    	for (Object id : objs) {
    		int scrPackId = (int)id;
    		if (tgtScrPackIds.contains(scrPackId)) {
    			throw new ServerOperationForbiddenException(
    					"삭제 대상의 자료는 광고 또는 광고 소재의 인벤토리 타겟팅에 사용되고 있기 때문에 삭제할 수 없습니다.");
    		}
    		InvScrPack item = new InvScrPack();
    		
    		item.setId(scrPackId);
    		items.add(item);
    	}
    	
    	try {
        	invService.deleteScrPacks(items);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
