package kr.adnetwork.controllers.inv;

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

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.info.StringInfo;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.inv.InvScrPack;
import kr.adnetwork.models.inv.InvScrPackItem;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.inv.InvSimpleScreenItem;

/**
 * 화면 묶음 컨트롤러(화면)
 */
@Controller("inv-scr-pack-screen-controller")
@RequestMapping(value="")
public class InvScrPackScreenController {

	private static final Logger logger = LoggerFactory.getLogger(InvScrPackScreenController.class);

	
    @Autowired 
    private InvService invService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 화면 묶음(화면) 페이지
	 */
    @RequestMapping(value = {"/inv/scrpack/{packId}", "/inv/scrpack/{packId}/", 
    		"/inv/scrpack/screen/{packId}", "/inv/scrpack/screen/{packId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	InvScrPack scrPack = invService.getScrPack(Util.parseInt(pathMap.get("packId")));
    	if (scrPack == null || scrPack.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/inv/scrpack";
    	}

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "InvScrPack");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "화면 묶음");

    	model.addAttribute("ScrPack", scrPack);

    	
        return "inv/scrpack/scrpack-screen";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/inv/scrpack/screen/read", method = RequestMethod.POST)
    public @ResponseBody List<InvSimpleScreenItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
        	
        	ArrayList<InvSimpleScreenItem> retList = new ArrayList<InvSimpleScreenItem>();
        	
    		DataSourceResult result = invService.getScrPackItemList(request, (int)request.getReqIntValue1());
    		
    		for(Object obj : result.getData()) {
    			InvScrPackItem scrPackItem = (InvScrPackItem) obj;
    			InvScreen screen = invService.getScreen(scrPackItem.getScreenId());
    			if (screen != null) {
    				retList.add(new InvSimpleScreenItem(scrPackItem.getId(), screen));
    			}
    		}

    		return retList;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
    private String getFormattedScreenNames(List<String> list) {

    	if (list == null || list.size() == 0) {
    		return "";
    	}
    	
    	String ret = "";
		for (String s : list) {
			if (Util.isValid(ret)) {
				ret += ", ";
			}
			ret += "[" + s + "]";
		}
		
		return ret;
    }
    
	/**
	 * 추가 액션 - 화면ID로
	 */
    @RequestMapping(value = "/inv/scrpack/screen/createWithShortNames", method = RequestMethod.POST)
    public @ResponseBody String createWithShortNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	InvScrPack scrPack = invService.getScrPack((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (scrPack == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String shortName : scrList) {
    		//invService.getScreenBy
    		InvScreen screen = invService.getScreen(scrPack.getMedium(), shortName);
    		
    		if (screen == null) {
    			errList.add(shortName);
    		} else {
    			InvScrPackItem item = invService.getScrPackItem(scrPack, screen.getId());
    			if (item == null) {
    				//logger.error(">> 신규 등록: " + shortName);
    				invService.saveOrUpdate(new InvScrPackItem(scrPack, screen.getId(), session));
    			} else {
    				//logger.error(">> 기등록: " + shortName);
    			}
    		}
    	}

    	if (errList.size() == 0) {
    		return "Ok";
    	} else {
    		return getFormattedScreenNames(errList);
    	}
    }
    
	/**
	 * 추가 액션 - 화면명으로
	 */
    @RequestMapping(value = "/inv/scrpack/screen/createWithNames", method = RequestMethod.POST)
    public @ResponseBody String createWithNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	InvScrPack scrPack = invService.getScrPack((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (scrPack == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String itemName : scrList) {
    		//invService.getScreenBy
    		InvScreen screen = invService.getScreenByName(scrPack.getMedium(), itemName);
    		
    		if (screen == null) {
    			errList.add(itemName);
    		} else {
    			InvScrPackItem item = invService.getScrPackItem(scrPack, screen.getId());
    			if (item == null) {
    				//logger.error(">> 신규 등록: " + itemName);
    				invService.saveOrUpdate(new InvScrPackItem(scrPack, screen.getId(), session));
    			} else {
    				//logger.error(">> 기등록: " + itemName);
    			}
    		}
    	}

    	if (errList.size() == 0) {
    		return "Ok";
    	} else {
    		return getFormattedScreenNames(errList);
    	}
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/inv/scrpack/screen/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<InvScrPackItem> items = new ArrayList<InvScrPackItem>();

    	for (Object id : objs) {
    		InvScrPackItem item = new InvScrPackItem();
    		
    		item.setId((int)id);
    		
    		items.add(item);
    	}

    	try {
        	invService.deleteScrPackItems(items);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
