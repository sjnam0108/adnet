package net.doohad.controllers.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSyncPack;
import net.doohad.models.inv.InvSyncPackItem;
import net.doohad.models.service.InvService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.inv.InvSimpleScreenItem;

/**
 * 동기화 화면 묶음 컨트롤러(화면)
 */
@Controller("inv-sync-pack-screen-controller")
@RequestMapping(value="")
public class InvSyncPackScreenController {

	private static final Logger logger = LoggerFactory.getLogger(InvSyncPackScreenController.class);

	
    @Autowired 
    private InvService invService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 동기화 화면 묶음(화면) 페이지
	 */
    @RequestMapping(value = {"/inv/syncpack/{packId}", "/inv/syncpack/{packId}/", 
    		"/inv/syncpack/screen/{packId}", "/inv/syncpack/screen/{packId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	InvSyncPack syncPack = invService.getSyncPack(Util.parseInt(pathMap.get("packId")));
    	if (syncPack == null || syncPack.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/inv/syncpack";
    	}

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "InvSyncPack");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "동기화 화면 묶음");

    	model.addAttribute("SyncPack", syncPack);

    	
        return "inv/syncpack/syncpack-screen";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/inv/syncpack/screen/read", method = RequestMethod.POST)
    public @ResponseBody List<InvSimpleScreenItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
        	
        	ArrayList<InvSimpleScreenItem> retList = new ArrayList<InvSimpleScreenItem>();
        	
    		DataSourceResult result = invService.getSyncPackItemList(request, (int)request.getReqIntValue1());
    		
    		for(Object obj : result.getData()) {
    			InvSyncPackItem syncPackItem = (InvSyncPackItem) obj;
    			InvScreen screen = invService.getScreen(syncPackItem.getScreenId());
    			if (screen != null) {
    				InvSimpleScreenItem item = new InvSimpleScreenItem(syncPackItem.getId(), screen);
    				item.setLaneId(syncPackItem.getLaneId());
    				retList.add(item);
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
    @RequestMapping(value = "/inv/syncpack/screen/createWithShortNames", method = RequestMethod.POST)
    public @ResponseBody String createWithShortNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	InvSyncPack syncPack = invService.getSyncPack((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (syncPack == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String shortName : scrList) {
    		//invService.getScreenBy
    		InvScreen screen = invService.getScreen(syncPack.getMedium(), shortName);
    		
    		if (screen == null) {
    			errList.add(shortName);
    		} else {
    			InvSyncPackItem item = invService.getSyncPackItemByScreenId(screen.getId());
    			if (item == null) {
    				invService.saveAndReorderSyncPackItem(new InvSyncPackItem(syncPack, screen.getId(), 1000, session));
    			} else {
        			errList.add(shortName);
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
    @RequestMapping(value = "/inv/syncpack/screen/createWithNames", method = RequestMethod.POST)
    public @ResponseBody String createWithNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	InvSyncPack syncPack = invService.getSyncPack((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (syncPack == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String itemName : scrList) {
    		InvScreen screen = invService.getScreenByName(syncPack.getMedium(), itemName);
    		
    		if (screen == null) {
    			errList.add(itemName);
    		} else {
    			InvSyncPackItem item = invService.getSyncPackItemByScreenId(screen.getId());
    			if (item == null) {
    				invService.saveAndReorderSyncPackItem(new InvSyncPackItem(syncPack, screen.getId(), 1000, session));
    			} else {
        			errList.add(itemName);
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
    @RequestMapping(value = "/inv/syncpack/screen/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<InvSyncPackItem> items = new ArrayList<InvSyncPackItem>();
    	int syncPackId = 0;

    	for (Object id : objs) {
    		InvSyncPackItem item = new InvSyncPackItem();
    		
    		item.setId((int)id);
    		
    		items.add(item);
    		
    		if (syncPackId == 0) {
    			InvSyncPackItem target = invService.getSyncPackItem((int)id);
    			if (target != null) {
    				syncPackId = target.getSyncPack().getId();
    			}
    		}
    	}

    	try {
        	invService.deleteSyncPackItems(items);
        	if (items.size() > 0 && syncPackId > 0) {
            	invService.reorderSyncPackItem(syncPackId);
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
    /**
	 * 이름순으로 정렬 액션
	 */
    @RequestMapping(value = "/inv/syncpack/screen/sort", method = RequestMethod.POST)
    public @ResponseBody String sort(@RequestBody Map<String, Object> model) {

    	int syncPackId = (int)model.get("id");
    	
    	// 파라미터 검증
    	if (syncPackId < 1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	ArrayList<InvSimpleScreenItem> items = new ArrayList<InvSimpleScreenItem>();
    	List<InvSyncPackItem> list = invService.getSyncPackItemListBySyncPackId(syncPackId);
    	for(InvSyncPackItem item : list) {
    		InvScreen screen = invService.getScreen(item.getScreenId());
    		if (screen != null) {
    			items.add(new InvSimpleScreenItem(item.getId(), screen));
    		}
    	}
		
		Collections.sort(items, new Comparator<InvSimpleScreenItem>() {
	    	public int compare(InvSimpleScreenItem item1, InvSimpleScreenItem item2) {
	    		return item1.getName().toLowerCase().compareTo(item2.getName().toLowerCase());
	    	}
	    });

		
    	try {
    		
    		int cnt = 1;
    		for(InvSimpleScreenItem item : items) {
    			InvSyncPackItem syncPackItem = invService.getSyncPackItem(item.getId());
    			if (syncPackItem != null) {
    				syncPackItem.setLaneId(cnt++);
    				invService.saveOrUpdate(syncPackItem);
    			}
    		}
    		
    		invService.reorderSyncPackItem(syncPackId);
    	} catch (Exception e) {
    		logger.error("sort", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "Ok";
    }

}
