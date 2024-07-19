package kr.adnetwork.controllers.inv;

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

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSyncPackItem;
import kr.adnetwork.models.org.OrgChanSub;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.org.OrgRTChannel;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;

/**
 * 화면 컨트롤러(광고 채널)
 */
@Controller("inv-screen-chan-controller")
@RequestMapping(value="")
public class InvScreenChanController {

	private static final Logger logger = LoggerFactory.getLogger(InvScreenChanController.class);

	
    @Autowired 
    private InvService invService;

    @Autowired 
    private AdcService adcService;

    @Autowired
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 화면(광고 채널) 페이지
	 */
    @RequestMapping(value = {"/inv/screen/{screenId}", "/inv/screen/{screenId}/", 
    		"/inv/screen/chans/{screenId}", "/inv/screen/chans/{screenId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	InvScreen screen = invService.getScreen(Util.parseInt(pathMap.get("screenId")));
    	if (screen == null || screen.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/inv/screen";
    	}

    	
    	// 화면 상태 갱신
    	SolUtil.setScreenReqStatus(screen);
    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "InvScreen");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "화면");


    	InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
    	if (rtScreen != null) {
    		screen.setPlayerVer(rtScreen.getPlayerVer());
    	}
    	
    	List<String> typeNames = Util.tokenizeValidStr(SolUtil.getScreenPackTypeNamesByScreenId(screen.getId()));
    	for(String s : typeNames) {
    		if (Util.isNotValid(s) || s.length() < 2) {
    			continue;
    		}
    		
    		// S: 동기화 화면 묶음, P: 화면 묶음
    		// 자료 구조상 처음 S, 그다음 P 연속
    		if (s.startsWith("S")) {
    			screen.setSyncPackName(s.substring(1));
    		} else if (s.startsWith("P")) {
    			screen.setScrPackName(s.substring(1));
    			break;
    		}
    	}
    	if (Util.isNotValid(screen.getScrPackName()) && Util.isNotValid(screen.getSyncPackName())) {
    		screen.setScrPackName("-");
    	}
    	
    	model.addAttribute("Screen", screen);

    	
        return "inv/screen/screen-chan";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/inv/screen/chans/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	int objId = (int)request.getReqIntValue1();
    	String objType = "S";

		// 동기화 화면 묶음 확인
		InvSyncPackItem syncPackItem = invService.getSyncPackItemByScreenId(objId);
		if (syncPackItem != null) {
			objType = "P";
			objId = syncPackItem.getSyncPack().getId();
		}
		
    	try {
    		DataSourceResult result = adcService.getChanSubList(request, objType, objId);

    		ArrayList<OrgChannel> chanList = new ArrayList<OrgChannel>();
    		for(Object obj : result.getData()) {
    			OrgChanSub chanSub = (OrgChanSub)obj;
    			chanList.add(chanSub.getChannel());
    		}
    		
    		Collections.sort(chanList, new Comparator<OrgChannel>() {
    	    	public int compare(OrgChannel item1, OrgChannel item2) {
    	    		if (item1.getPriority() == item2.getPriority()) {
    	    			return item1.getShortName().compareTo(item2.getShortName());
    	    		} else {
    	    			return Integer.compare(item1.getPriority(), item2.getPriority());
    	    		}
    	    	}
    	    });
    		
    		ArrayList<String> regChans = new ArrayList<String>();
    		ArrayList<Integer> highIds = new ArrayList<Integer>();
    		for(OrgChannel chan : chanList) {
    			if (!chan.isActiveStatus()) {
    				continue;
    			}
    			String key = chan.getViewTypeCode() + "R" + chan.getResolution();
    			if (!regChans.contains(key)) {
    				regChans.add(key);
    				highIds.add(chan.getId());
    			}
    		}
    		
			for(Object obj : result.getData()) {
    			OrgChanSub chanSub = (OrgChanSub)obj;
    			
    			chanSub.getChannel().setSubCount(
    					orgService.getChanSubCountByChannelId(chanSub.getChannel().getId()));
    			
    			OrgRTChannel rtChannel = orgService.getRTChannelByChannelId(chanSub.getChannel().getId());
    			if (rtChannel != null) {
    				chanSub.setLastAdAppDate(rtChannel.getLastAdAppDate());
    				chanSub.setLastAdReqDate(rtChannel.getLastAdReqDate());
    			}
    			
    			chanSub.getChannel().setPriorityHigh(highIds.contains(chanSub.getChannel().getId()));
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
}
