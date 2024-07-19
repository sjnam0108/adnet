package kr.adnetwork.controllers.org;

import java.util.ArrayList;
import java.util.Collections;
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
import kr.adnetwork.models.CustomComparator;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.org.OrgChanSub;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.inv.InvSimpleSyncPackItem;

/**
 * 광고 채널 컨트롤러(동기화 화면 묶음)
 */
@Controller("org-channel-sync-pack-controller")
@RequestMapping(value="/org/channel/syncpack")
public class OrgChannelSyncPackController {

	private static final Logger logger = LoggerFactory.getLogger(OrgChannelSyncPackController.class);

	
    @Autowired 
    private InvService invService;

    @Autowired 
    private OrgService orgService;

    @Autowired
    private FndService fndService;
    
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 광고 채널(동기화 화면 묶음) 페이지
	 */
    @RequestMapping(value = {"/{channelId}", "/{channelId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	OrgChannel channel = orgService.getChannel(Util.parseInt(pathMap.get("channelId")));
    	if (channel == null || channel.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/org/channel";
    	}

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "OrgChannel");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});
		
    	// 세션에 현재 채널 및 채널 목록 설정
    	setCurrChannelsToSession(request, response, session, channel.getId());
		

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고 채널");

		channel.setSubCount(
				orgService.getChanSubCountByChannelId(channel.getId()));
		
		if (channel.getAppendMode().equals("A")) {
			// A: 자율 광고선택
			if (channel.getReqScreenId() == null) {
				channel.setReqScreen("미지정");
			} else {
				InvScreen tmpScr = invService.getScreen(channel.getReqScreenId().intValue());
				if (tmpScr == null) {
					channel.setReqScreen("새로운 지정 필요");
				} else {
					channel.setReqScreen(tmpScr.getName() + "(" + tmpScr.getShortName() + ")");
				}
			}
		}
		
		// 묶음 광고 모드 확인
		boolean isPackedAdMode = false;
		if (Util.isValid(channel.getViewTypeCode())) {
			FndViewType viewType = fndService.getViewType(channel.getViewTypeCode(), channel.getResolution());
			isPackedAdMode = viewType != null && viewType.isAdPackUsed();
		}
		channel.setPackedAdMode(isPackedAdMode);

		
    	model.addAttribute("Channel", channel);

    	
        return "org/channel/channel-syncpack";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<InvSimpleSyncPackItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
        	
        	ArrayList<InvSimpleSyncPackItem> retList = new ArrayList<InvSimpleSyncPackItem>();
        	
    		DataSourceResult result = orgService.getChanSubList(request, "P", (int)request.getReqIntValue1());
    		
    		for(Object obj : result.getData()) {
    			OrgChanSub chanSub = (OrgChanSub) obj;
    			InvSyncPack syncPack = invService.getSyncPack(chanSub.getObjId());
    			if (syncPack != null) {
    				InvSimpleSyncPackItem item = new InvSimpleSyncPackItem(chanSub.getId(), syncPack);
    				item.setScreenCount(invService.getSyncPackItemCountBySyncPackId(syncPack.getId()));
    				
    				retList.add(item);
    			}
    		}

    		return retList;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
    private String getFormattedSyncPackNames(List<String> list) {

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
	 * 추가 액션 - 묶음ID로
	 */
    @RequestMapping(value = "/createWithShortNames", method = RequestMethod.POST)
    public @ResponseBody String createWithShortNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (channel == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> syncPackList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (syncPackList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String shortName : syncPackList) {
    		InvSyncPack syncPack = invService.getSyncPackByShortName(shortName);
    		
    		if (syncPack == null) {
    			errList.add(shortName);
    		} else {
    			OrgChanSub chanSub = orgService.getChanSub(channel, "P", syncPack.getId());
    			if (chanSub == null) {
    				//logger.error(">> 신규 등록: " + shortName);
    				orgService.saveOrUpdate(new OrgChanSub(channel, "P", syncPack.getId(), session));
    			} else {
    				//logger.error(">> 기등록: " + shortName);
    			}
    		}
    	}

    	if (errList.size() == 0) {
    		return "Ok";
    	} else {
    		return getFormattedSyncPackNames(errList);
    	}
    }
    
	/**
	 * 추가 액션 - 화면명으로
	 */
    @RequestMapping(value = "/createWithNames", method = RequestMethod.POST)
    public @ResponseBody String createWithNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (channel == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> syncPackList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (syncPackList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String itemName : syncPackList) {
    		InvSyncPack syncPack = invService.getSyncPack(channel.getMedium(), itemName);
    		
    		if (syncPack == null) {
    			errList.add(itemName);
    		} else {
    			OrgChanSub chanSub = orgService.getChanSub(channel, "P", syncPack.getId());
    			if (chanSub == null) {
    				//logger.error(">> 신규 등록: " + itemName);
    				orgService.saveOrUpdate(new OrgChanSub(channel, "P", syncPack.getId(), session));
    			} else {
    				//logger.error(">> 기등록: " + itemName);
    			}
    		}
    	}

    	if (errList.size() == 0) {
    		return "Ok";
    	} else {
    		return getFormattedSyncPackNames(errList);
    	}
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<OrgChanSub> chanSubs = new ArrayList<OrgChanSub>();

    	for (Object id : objs) {
    		OrgChanSub chanSub = new OrgChanSub();
    		
    		chanSub.setId((int)id);
    		
    		chanSubs.add(chanSub);
    	}

    	try {
        	orgService.deleteChanSubs(chanSubs);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
    private void setCurrChannelsToSession(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		int chanId) {
    	
		int currChanId = chanId;
		if (chanId < 0) {
			currChanId = Util.parseInt(Util.cookieValue(request, "currChanId"));
		}
    	
    	boolean goAhead = false;
    	List<OrgChannel> chanList = orgService.getChannelListByMediumId(Util.getSessionMediumId(session));
    	ArrayList<DropDownListItem> currChans = new ArrayList<DropDownListItem>();
    	for(OrgChannel chan : chanList) {
			String icon = chan.isActiveStatus() ? "fa-tower-cell" : "fa-circle-dashed";
			String text = chan.getShortName();
			String value = String.valueOf(chan.getId());
			String subIcon = chan.getName();
			
			currChans.add(new DropDownListItem(icon, subIcon, text, value));
    		if (chan.getId() == currChanId) {
    			goAhead = true;
    		}
    	}
		Collections.sort(currChans, CustomComparator.DropDownListItemTextComparator);
    	
    	if (!goAhead) {
    		if (chanList.size() == 0) {
    			currChanId = -1;
    		} else {
    			currChanId = Util.parseInt(currChans.get(0).getValue());
    		}
    	}
    	if ((goAhead && chanId > 0) || !goAhead) {
    		response.addCookie(Util.cookie("currChanId", String.valueOf(currChanId)));
    	}
    	
		session.setAttribute("currChanId", String.valueOf(currChanId));
		session.setAttribute("currChannels", currChans);
    }
}
