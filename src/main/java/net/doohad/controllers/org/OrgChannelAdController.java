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
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.org.OrgChannel;
import net.doohad.models.service.InvService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.RevService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 광고 채널 컨트롤러(채널 광고)
 */
@Controller("org-channel-ad-controller")
@RequestMapping(value="/org/channel/ad")
public class OrgChannelAdController {

	private static final Logger logger = LoggerFactory.getLogger(OrgChannelAdController.class);

	
    @Autowired 
    private InvService invService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private RevService revService;
    
    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 광고 채널(채널 광고) 페이지
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
		
    	model.addAttribute("Channel", channel);

    	
        return "org/channel/channel-ad";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		return revService.getChanAdList(request, (int)request.getReqIntValue1());
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
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
