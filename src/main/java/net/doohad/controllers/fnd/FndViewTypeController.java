package net.doohad.controllers.fnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.doohad.models.DataSourceRequest.FilterDescriptor;
import net.doohad.models.fnd.FndViewType;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.FndService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysOpt;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.knl.KnlMediumItem;

/**
 * 게시 유형 컨트롤러
 */
@Controller("fnd-view-type-controller")
@RequestMapping(value="/fnd/viewtype")
public class FndViewTypeController {

	private static final Logger logger = LoggerFactory.getLogger(FndViewTypeController.class);


    @Autowired 
    private FndService fndService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private SysService sysService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 게시 유형 페이지
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
    	model.addAttribute("pageTitle", "게시 유형");
    	
		model.addAttribute("CustomResos", getCustomResoDropDownList());
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "fnd/viewtype";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
            return fndService.getViewTypeList(request);
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
    	
    	String code = (String)model.get("code");
    	String name = (String)model.get("name");
    	String resolution = (String)model.get("resolution");
    	
    	Boolean adPackUsed = (Boolean)model.get("adPackUsed");
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> destMedia = (ArrayList<Object>) model.get("destMedia");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(code) || Util.isNotValid(name) || Util.isNotValid(resolution) || 
    			adPackUsed == null || destMedia == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	String media = "";
		for(Object obj : destMedia) {
			if (Util.isValid(media)) {
				media += "|" + (String)obj;
			} else {
				media = (String)obj;
			}
		}
    	
    	
    	FndViewType target = new FndViewType(code, name, resolution, media, adPackUsed.booleanValue(), session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String code = (String)model.get("code");
    	String name = (String)model.get("name");
    	String resolution = (String)model.get("resolution");
    	
    	Boolean adPackUsed = (Boolean)model.get("adPackUsed");
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> destMedia = (ArrayList<Object>) model.get("destMedia");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(code) || Util.isNotValid(name) || Util.isNotValid(resolution) || 
    			adPackUsed == null || destMedia == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	String media = "";
		for(Object obj : destMedia) {
			if (Util.isValid(media)) {
				media += "|" + (String)obj;
			} else {
				media = (String)obj;
			}
		}
		
		FndViewType target = fndService.getViewType((int)model.get("id"));
    	if (target != null) {
        	
    		target.setCode(code);
            target.setName(name);
            target.setResolution(resolution);
            target.setDestMedia(media);
            
            target.setAdPackUsed(adPackUsed.booleanValue());

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(FndViewType target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	String ukErrorMsg = "동일한 [이름] 혹은 [유형ID, 게시 크기]의 자료가 이미 등록되어 있습니다.";
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            fndService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(ukErrorMsg);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(ukErrorMsg);
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
    	
    	List<FndViewType> viewTypes = new ArrayList<FndViewType>();

    	for (Object id : objs) {
    		FndViewType viewType = new FndViewType();
    		
    		viewType.setId((int)id);
    		
    		viewTypes.add(viewType);
    	}
    	
    	try {
        	fndService.deleteViewTypes(viewTypes);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
	
    
	/**
	 * 읽기 액션 - Kendo AutoComplete 용 매체 정보
	 */
    @RequestMapping(value = "/readACMedia", method = RequestMethod.POST)
    public @ResponseBody List<KnlMediumItem> readAutoComplMedia(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<KnlMediumItem> list = new ArrayList<KnlMediumItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<KnlMedium> mediumList = knlService.getMediumListByShortNameLike(userInput);
		
		if (mediumList.size() <= 50) {
    		for(KnlMedium medium : mediumList) {
    			list.add(new KnlMediumItem(medium.getId(), medium.getShortName(), medium.getName()));
    		}
    		
    		Collections.sort(list, new Comparator<KnlMediumItem>() {
    	    	public int compare(KnlMediumItem item1, KnlMediumItem item2) {
    	    		return item1.getShortName().toLowerCase().compareTo(item2.getShortName().toLowerCase());
    	    	}
    	    });
		}

    	return list;
    }
    
    
	/**
	 * 커스텀 해상도 정보 획득
	 */
    private List<DropDownListItem> getCustomResoDropDownList() {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	SysOpt sysOpt = sysService.getOpt("sol.resos");
    	if (sysOpt != null) {
    		List<String> resos = Util.tokenizeValidStr(sysOpt.getValue());
    		for(String reso : resos) {
    			String text = reso.replace("x", " x ");
    			retList.add(new DropDownListItem(text, reso));
    		}
    	}
    	
    	return retList;
    }

}
