package net.doohad.controllers.org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysOpt;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.inv.InvSimpleScreenItem;

/**
 * 일반 설정 화면 컨트롤러
 */
@Controller("org-medium-opt-screen-controller")
@RequestMapping(value="")
public class OrgMediumOptScreenController {

	private static final Logger logger = LoggerFactory.getLogger(OrgMediumOptScreenController.class);

	
	@Autowired
	private SysService sysService;

    @Autowired 
    private KnlService knlService;
	
    @Autowired 
    private InvService invService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 일반 설정 화면 페이지
	 */
    @RequestMapping(value = {"/org/mediumopt/screen/{optID}", "/org/mediumopt/screen/{optID}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	String optID = pathMap.get("optID");
    	if (Util.isNotValid(optID)) {
    		return "forward:/org/mediumopt";
    	}
    	
    	
    	ArrayList<DropDownListItem> currOpts = new ArrayList<DropDownListItem>();
    	String dbOptID = "", dbOptName = "";
    	boolean isFound = false;
    	
    	String opts = SolUtil.getOptValue(Util.getSessionMediumId(session), "opt.list");
    	if (Util.isValid(opts)) {
    		List<String> optList = Util.tokenizeValidStr(opts);
    		for(String opt : optList) {
    			List<String> optPair = Util.tokenizeValidStr(opt, ",");
    			if (optPair.size() == 2) {
    				String ID = optPair.get(0);
    				String name = optPair.get(1);
    				
    				currOpts.add(new DropDownListItem("fa-sliders-simple", name, ID, ID));
    				if (ID.equals(optID)) {
    					isFound = true;
    					dbOptID = ID;
    					dbOptName = name;
    				}
    			}
    		}
    	}
    	if (currOpts.size() == 0 || !isFound) {
    		return "forward:/org/mediumopt";
    	}
    	
		Collections.sort(currOpts, CustomComparator.DropDownListItemTextComparator);
    	
    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "OrgMediumOpt");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "일반 설정");

    	
    	model.addAttribute("optID", dbOptID);
    	model.addAttribute("optName", dbOptName);
    	model.addAttribute("currOpts", currOpts);
    	
    	
        return "org/mediumopt/mediumopt-screen";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/org/mediumopt/screen/read", method = RequestMethod.POST)
    public @ResponseBody List<InvSimpleScreenItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
        	
        	ArrayList<InvSimpleScreenItem> retList = new ArrayList<InvSimpleScreenItem>();
        	
        	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
        	String optID = request.getReqStrValue1();
        	if (medium != null && Util.isValid(optID)) {
        		List<Integer> ids = getScreenIdsByKey("opt." + optID + "." + medium.getShortName());
        		for(Integer i : ids) {
    				InvScreen screen = invService.getScreen(i.intValue());
    				if (screen != null) {
    					retList.add(new InvSimpleScreenItem(screen.getId(), screen));
    				}
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
    
    
    private ArrayList<Integer> getScreenIdsByKey(String optKey) {
    	
    	ArrayList<Integer> retIds = new ArrayList<Integer>();
    	
    	if (Util.isValid(optKey)) {
    		SysOpt sysOpt = sysService.getOpt(optKey);
    		if (sysOpt != null && Util.isValid(sysOpt.getValue())) {
    			List<String> ids = Util.tokenizeValidStr(sysOpt.getValue());
    			for(String id : ids) {
    				Integer i = Util.parseInt(id);
    				if (!retIds.contains(i)) {
    					retIds.add(i);
    				}
    			}
    		}
    	}
    	
    	return retIds;
    }
    
    
    private String getValueFromList(List<Integer> ids) {
    	
    	String ret = "";
    	for(Integer i : ids) {
    		if (Util.isValid(ret)) {
    			ret += "|";
    		}
    		ret += String.valueOf(i);
    	}
    	
    	return ret;
    }
    
    
	/**
	 * 추가 액션 - 화면ID로
	 */
    @RequestMapping(value = "/org/mediumopt/screen/createWithShortNames", method = RequestMethod.POST)
    public @ResponseBody String createWithShortNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	String optID = (String) model.get("opt");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(optID)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	String optKey = "opt." + optID + "." + medium.getShortName();
    	ids = getScreenIdsByKey(optKey);
    	
    	SysOpt sysOpt = sysService.getOpt(optKey);

    	
    	String list = (String)model.get("list");
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String shortName : scrList) {
    		InvScreen screen = invService.getScreen(medium, shortName);
    		
    		if (screen == null) {
    			errList.add(shortName);
    		} else {
    			boolean saveReq = false;
    			if (ids.contains(screen.getId())) {
    				//logger.error(">> 기등록: " + shortName);
    			} else {
    				//logger.error(">> 신규 등록: " + shortName);
    				ids.add(screen.getId());
    				saveReq = true;
    			}
    			
    			if (saveReq) {
    				if (sysOpt == null) {
    					sysOpt = new SysOpt(optKey, "");
    				}
    				sysOpt.setValue(getValueFromList(ids));
    				sysOpt.setDate(new Date());
    				
    				sysService.saveOrUpdate(sysOpt);
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
    @RequestMapping(value = "/org/mediumopt/screen/createWithNames", method = RequestMethod.POST)
    public @ResponseBody String createWithNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	String optID = (String) model.get("opt");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(optID)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	String optKey = "opt." + optID + "." + medium.getShortName();
    	ids = getScreenIdsByKey(optKey);
    	
    	SysOpt sysOpt = sysService.getOpt(optKey);

    	
    	String list = (String)model.get("list");
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String itemName : scrList) {
    		InvScreen screen = invService.getScreenByName(medium, itemName);
    		
    		if (screen == null) {
    			errList.add(itemName);
    		} else {
    			boolean saveReq = false;
    			if (ids.contains(screen.getId())) {
    				//logger.error(">> 기등록: " + shortName);
    			} else {
    				//logger.error(">> 신규 등록: " + shortName);
    				ids.add(screen.getId());
    				saveReq = true;
    			}
    			
    			if (saveReq) {
    				if (sysOpt == null) {
    					sysOpt = new SysOpt(optKey, "");
    				}
    				sysOpt.setValue(getValueFromList(ids));
    				sysOpt.setDate(new Date());
    				
    				sysService.saveOrUpdate(sysOpt);
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
    @RequestMapping(value = "/org/mediumopt/screen/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	String optID = (String) model.get("opt");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(optID)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	String optKey = "opt." + optID + "." + medium.getShortName();
    	ids = getScreenIdsByKey(optKey);
    	
    	SysOpt sysOpt = sysService.getOpt(optKey);
    	if (sysOpt == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");

    	try {

        	boolean saveReq = false;
        	for (Object id : objs) {
        		Integer i = (Integer) id;
        		if (ids.contains(i)) {
        			ids.remove(i);
        			saveReq = true;
        		}
        		
        		if (saveReq) {
    				sysOpt.setValue(getValueFromList(ids));
    				sysOpt.setDate(new Date());
    				
    				sysService.saveOrUpdate(sysOpt);
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

}
