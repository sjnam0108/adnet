package net.doohad.controllers.fnd;

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

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.fnd.FndMobRegion;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.fnd.FndState;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.utils.Util;

/**
 * 지역 컨트롤러
 */
@Controller("fnd-region-controller")
@RequestMapping(value="/fnd/region")
public class FndRegionController {

	private static final Logger logger = LoggerFactory.getLogger(FndRegionController.class);


    @Autowired 
    private FndService fndService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 지역 페이지
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
    	model.addAttribute("pageTitle", "지역");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "fnd/region";
    }
    
    
	/**
	 * 읽기 액션 - 시/군/구
	 */
    @RequestMapping(value = "/readRgn", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readRgn(@RequestBody DataSourceRequest request) {
    	try {
            return fndService.getRegionList(request);
    	} catch (Exception e) {
    		logger.error("readRgn", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - 시/군/구
	 */
    @RequestMapping(value = "/createRgn", method = RequestMethod.POST)
    public @ResponseBody String createRgn(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String code = (String)model.get("code");
    	String name = (String)model.get("name");
    	
    	boolean listIncluded = (Boolean)model.get("listIncluded");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	FndRegion target = new FndRegion(code, name, listIncluded, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 시/군/구
	 */
    @RequestMapping(value = "/updateRgn", method = RequestMethod.POST)
    public @ResponseBody String updateRgn(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String code = (String)model.get("code");
    	String name = (String)model.get("name");
    	
    	boolean listIncluded = (Boolean)model.get("listIncluded");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	FndRegion target = fndService.getRegion((int)model.get("id"));
    	if (target != null) {
        	
    		target.setCode(code);
            target.setName(name);
            target.setListIncluded(listIncluded);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장 - 시/군/구
	 */
    private void saveOrUpdate(FndRegion target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            fndService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE_OR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE_OR_NAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션 - 시/군/구
	 */
    @RequestMapping(value = "/destroyRgn", method = RequestMethod.POST)
    public @ResponseBody String destroyRgn(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<FndRegion> regions = new ArrayList<FndRegion>();

    	for (Object id : objs) {
    		FndRegion region = new FndRegion();
    		
    		region.setId((int)id);
    		
    		regions.add(region);
    	}
    	
    	try {
        	fndService.deleteRegions(regions);
    	} catch (Exception e) {
    		logger.error("destroyRgn", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 광역시/도
	 */
    @RequestMapping(value = "/readSt", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readSt(@RequestBody DataSourceRequest request) {
    	try {
            return fndService.getStateList(request);
    	} catch (Exception e) {
    		logger.error("readSt", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - 광역시/도
	 */
    @RequestMapping(value = "/createSt", method = RequestMethod.POST)
    public @ResponseBody String createSt(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String code = (String)model.get("code");
    	String name = (String)model.get("name");
    	
    	boolean listIncluded = (Boolean)model.get("listIncluded");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	FndState target = new FndState(code, name, listIncluded, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 광역시/도
	 */
    @RequestMapping(value = "/updateSt", method = RequestMethod.POST)
    public @ResponseBody String updateSt(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String code = (String)model.get("code");
    	String name = (String)model.get("name");
    	
    	boolean listIncluded = (Boolean)model.get("listIncluded");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(code) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	FndState target = fndService.getState((int)model.get("id"));
    	if (target != null) {
        	
    		target.setCode(code);
            target.setName(name);
            target.setListIncluded(listIncluded);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장 - 광역시/도
	 */
    private void saveOrUpdate(FndState target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            fndService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE_OR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CODE_OR_NAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션 - 광역시/도
	 */
    @RequestMapping(value = "/destroySt", method = RequestMethod.POST)
    public @ResponseBody String destroySt(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<FndState> states = new ArrayList<FndState>();

    	for (Object id : objs) {
    		FndState state = new FndState();
    		
    		state.setId((int)id);
    		
    		states.add(state);
    	}
    	
    	try {
        	fndService.deleteStates(states);
    	} catch (Exception e) {
    		logger.error("destroySt", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 모바일 타겟팅 지역
	 */
    @RequestMapping(value = "/readMobRgn", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readMobRgn(@RequestBody DataSourceRequest request) {
    	try {
            return fndService.getMobRegionList(request);
    	} catch (Exception e) {
    		logger.error("readMobRgn", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - 모바일 타겟팅 지역
	 */
    @RequestMapping(value = "/createMobRgn", method = RequestMethod.POST)
    public @ResponseBody String createMobRgn(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String gcName = (String)model.get("gcName");
    	String name = (String)model.get("name");
    	String code = (String)model.get("code");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(gcName) || Util.isNotValid(name) || Util.isNotValid(code)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	FndMobRegion target = new FndMobRegion(name, gcName, code, activeStatus, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 모바일 타겟팅 지역
	 */
    @RequestMapping(value = "/updateMobRgn", method = RequestMethod.POST)
    public @ResponseBody String updateMobRgn(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String gcName = (String)model.get("gcName");
    	String name = (String)model.get("name");
    	String code = (String)model.get("code");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(gcName) || Util.isNotValid(name) || Util.isNotValid(code)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	FndMobRegion target = fndService.getMobRegion((int)model.get("id"));
    	if (target != null) {
        	
    		target.setGcName(gcName);
            target.setName(name);
            target.setCode(code);
            target.setActiveStatus(activeStatus);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장 - 모바일 타겟팅 지역
	 */
    private void saveOrUpdate(FndMobRegion target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            fndService.saveOrUpdate(target);
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
	 * 삭제 액션 - 모바일 타겟팅 지역
	 */
    @RequestMapping(value = "/destroyMobRgn", method = RequestMethod.POST)
    public @ResponseBody String destroyMobRgn(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<FndMobRegion> regions = new ArrayList<FndMobRegion>();

    	for (Object id : objs) {
    		
    		// 이 모바일 타겟팅 지역이 설정된 타겟팅이 존재하는가?
    		int childCnt = adcService.getMobTargetCountByMobTypeTgtId("RG", (int)id);
    		if (childCnt > 0) {
    			throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_AD);
    		}
    		
    		FndMobRegion region = new FndMobRegion();
    		
    		region.setId((int)id);
    		
    		regions.add(region);
    	}
    	
    	try {
        	fndService.deleteMobRegions(regions);
    	} catch (Exception e) {
    		logger.error("destroyMobRgn", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
