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
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;

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
    private KnlService knlService;

    
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
    		"/inv/screen/chan/{screenId}", "/inv/screen/chan/{screenId}/"}, method = RequestMethod.GET)
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
    	
    	model.addAttribute("Screen", screen);

    	
        return "inv/screen/screen-chan";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/inv/screen/chan/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = invService.getScreenList(request, (int)request.getReqIntValue1());

			for(Object obj : result.getData()) {
    			InvScreen screen = (InvScreen) obj;
    			
    			SolUtil.setScreenReqStatus(screen);
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
}
