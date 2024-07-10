package kr.adnetwork.controllers.org;

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
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgRadRegion;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;

/**
 * 지도의 원 반경 지역 컨트롤러
 */
@Controller("org-rad-region-controller")
@RequestMapping(value="/org/radregion")
public class OrgRadRegionController {

	private static final Logger logger = LoggerFactory.getLogger(OrgRadRegionController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 지도의 원 반경 지역 페이지
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
    	model.addAttribute("pageTitle", "지도의 원 반경 지역");
    	
    	

    	
        return "org/radregion";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = orgService.getRadRegionList(request);
    		
    		for(Object obj : result.getData()) {
    			OrgRadRegion radRegion = (OrgRadRegion) obj;
    			
    			radRegion.setCenter(SolUtil.reverseGCRegion2(String.valueOf(radRegion.getLat()), String.valueOf(radRegion.getLng())));
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 좌표 값에 대한 geo 정보
	 */
    @RequestMapping(value = "/readGeo", method = RequestMethod.POST)
    public @ResponseBody String readGeoInfo(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	Double lat = (Double)model.get("lat");
    	Double lng = (Double)model.get("lng");
    	
    	String info = SolUtil.reverseGCRegion2(String.valueOf(lat), String.valueOf(lng));
    	
    	return Util.isValid(info) ? info : "[등록 정보 없음]";
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String name = (String)model.get("name");
    	
    	Double lat = (Double)model.get("lat");
    	Double lng = (Double)model.get("lng");
    	
    	Integer radius = (Integer)model.get("radius");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || lat == null || lng == null || radius == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	OrgRadRegion target = new OrgRadRegion(medium, name, lat.doubleValue(), lng.doubleValue(), 
    			radius.intValue(), activeStatus, session);

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
    	
    	Double lat = (Double)model.get("lat");
    	Double lng = (Double)model.get("lng");
    	
    	Integer radius = (Integer)model.get("radius");
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(name) || lat == null || lng == null || radius == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	OrgRadRegion target = orgService.getRadRegion((int)model.get("id"));
    	if (target != null) {
        	
            target.setName(name);
            target.setLat(lat.doubleValue());
            target.setLng(lng.doubleValue());
            target.setRadius(radius.intValue());
            target.setActiveStatus(activeStatus);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(OrgRadRegion target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveOrUpdate(target);
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
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<OrgRadRegion> radRegions = new ArrayList<OrgRadRegion>();

    	for (Object id : objs) {
    		
    		// 이 모바일 타겟팅 지역이 설정된 타겟팅이 존재하는가?
    		int childCnt = adcService.getMobTargetCountByMobTypeTgtId("CR", (int)id);
    		if (childCnt > 0) {
    			throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_AD);
    		}
    		
    		OrgRadRegion radRegion = new OrgRadRegion();
    		
    		radRegion.setId((int)id);
    		
    		radRegions.add(radRegion);
    	}
    	
    	try {
        	orgService.deleteRadRegions(radRegions);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
}
