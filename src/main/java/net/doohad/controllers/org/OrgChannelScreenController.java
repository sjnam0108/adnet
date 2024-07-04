package net.doohad.controllers.org;

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

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.org.OrgChanSub;
import net.doohad.models.org.OrgChannel;
import net.doohad.models.service.InvService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.inv.InvSimpleScreenItem;

/**
 * 광고 채널 컨트롤러(화면)
 */
@Controller("org-channel-screen-controller")
@RequestMapping(value="")
public class OrgChannelScreenController {

	private static final Logger logger = LoggerFactory.getLogger(OrgChannelScreenController.class);

	
    @Autowired 
    private InvService invService;

    @Autowired 
    private OrgService orgService;
    
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 광고 채널(화면) 페이지
	 */
    @RequestMapping(value = {"/org/channel/{channelId}", "/org/channel/{channelId}/", 
    		"/org/channel/screen/{channelId}", "/org/channel/screen/{channelId}/"}, method = RequestMethod.GET)
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
		
    	model.addAttribute("Channel", channel);

    	
        return "org/channel/channel-screen";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/org/channel/screen/read", method = RequestMethod.POST)
    public @ResponseBody List<InvSimpleScreenItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
        	
        	ArrayList<InvSimpleScreenItem> retList = new ArrayList<InvSimpleScreenItem>();
        	
    		DataSourceResult result = orgService.getChanSubList(request, "S", (int)request.getReqIntValue1());
    		
    		for(Object obj : result.getData()) {
    			OrgChanSub chanSub = (OrgChanSub) obj;
    			InvScreen screen = invService.getScreen(chanSub.getObjId());
    			if (screen != null) {
    				retList.add(new InvSimpleScreenItem(chanSub.getId(), screen));
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
    @RequestMapping(value = "/org/channel/screen/createWithShortNames", method = RequestMethod.POST)
    public @ResponseBody String createWithShortNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (channel == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String shortName : scrList) {
    		InvScreen screen = invService.getScreen(channel.getMedium(), shortName);
    		
    		if (screen == null) {
    			errList.add(shortName);
    		} else {
    			OrgChanSub chanSub = orgService.getChanSub(channel, "S", screen.getId());
    			if (chanSub == null) {
    				//logger.error(">> 신규 등록: " + shortName);
    				orgService.saveOrUpdate(new OrgChanSub(channel, "S", screen.getId(), session));
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
    @RequestMapping(value = "/org/channel/screen/createWithNames", method = RequestMethod.POST)
    public @ResponseBody String createWithNames(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("id"));
    	
    	String list = (String)model.get("list");
    	
    	// 파라미터 검증
    	if (channel == null || Util.isNotValid(list)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	List<String> scrList = Util.tokenizeValidStr(list.replaceAll("[\\t\\n\\r]+", "|"));
    	if (scrList.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}


    	ArrayList<String> errList = new ArrayList<String>();
    	for(String itemName : scrList) {
    		InvScreen screen = invService.getScreenByName(channel.getMedium(), itemName);
    		
    		if (screen == null) {
    			errList.add(itemName);
    		} else {
    			OrgChanSub chanSub = orgService.getChanSub(channel, "S", screen.getId());
    			if (chanSub == null) {
    				//logger.error(">> 신규 등록: " + itemName);
    				orgService.saveOrUpdate(new OrgChanSub(channel, "S", screen.getId(), session));
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
    @RequestMapping(value = "/org/channel/screen/destroy", method = RequestMethod.POST)
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
