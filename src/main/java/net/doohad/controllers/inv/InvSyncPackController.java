package net.doohad.controllers.inv;

import java.util.ArrayList;
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
import net.doohad.models.inv.InvSyncPack;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.Util;

/**
 * 동기화 화면 묶음 컨트롤러
 */
@Controller("inv-sync-pack-controller")
@RequestMapping(value="/inv/syncpack")
public class InvSyncPackController {

	private static final Logger logger = LoggerFactory.getLogger(InvSyncPackController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 동기화 화면 묶음 페이지
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
    	model.addAttribute("pageTitle", "동기화 화면 묶음");

    	

    	
        return "inv/syncpack";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = invService.getSyncPackList(request);
    		
    		for(Object obj : result.getData()) {
    			InvSyncPack syncPack = (InvSyncPack)obj;
    			
    			syncPack.setScreenCount(invService.getSyncPackItemCountBySyncPackId(syncPack.getId()));
    	    	
    			List<Tuple> tupleList = orgService.getChannelTupleListByTypeObjId("P", syncPack.getId());
    			String subChannels = "";
    			for(Tuple tuple : tupleList) {
    				// SELECT c.channel_id, c.name, c.active_status, c.view_type_code, c.priority, c.short_name
    				int priority = (Integer) tuple.get(4);
    				String shortName = (String) tuple.get(5);
    				
    				subChannels += String.valueOf(priority) + ". " + shortName + "|";
    			}
    			
    			syncPack.setSubChannels(subChannels);
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
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	InvSyncPack target = new InvSyncPack(medium, shortName, name, memo, activeStatus, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String memo = (String)model.get("memo");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	InvSyncPack target = invService.getSyncPack((int)model.get("id"));
    	if (target != null) {
        	
    		target.setShortName(shortName);
            target.setName(name);
    		target.setMemo(memo);
    		target.setActiveStatus(activeStatus);
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(InvSyncPack target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            invService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException("동일한 묶음ID 혹은 이름의 자료가 이미 등록되어 있습니다.");
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException("동일한 묶음ID 혹은 이름의 자료가 이미 등록되어 있습니다.");
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
        		InvSyncPack item = invService.getSyncPack((int)id);
        		if (item != null) {
        			orgService.deleteChanSubsBySyncPackId(item.getId());
        			invService.deleteSyncPack(item);
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
