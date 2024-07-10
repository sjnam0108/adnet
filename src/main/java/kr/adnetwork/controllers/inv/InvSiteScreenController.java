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
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;

/**
 * 사이트 컨트롤러(화면)
 */
@Controller("inv-site-screen-controller")
@RequestMapping(value="")
public class InvSiteScreenController {

	private static final Logger logger = LoggerFactory.getLogger(InvSiteScreenController.class);

	
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
	 * 사이트(화면) 페이지
	 */
    @RequestMapping(value = {"/inv/site/{siteId}", "/inv/site/{siteId}/", 
    		"/inv/site/screen/{siteId}", "/inv/site/screen/{siteId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	InvSite site = invService.getSite(Util.parseInt(pathMap.get("siteId")));
    	if (site == null || site.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/inv/site";
    	}

    	
    	// 사이트 상태 갱신
    	SolUtil.setSiteReqStatus(site);
    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "InvSite");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "사이트");

    	model.addAttribute("Site", site);

    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	model.addAttribute("Resolutions", getResolutionList(medium));
    	
    	// 매체의 재생 시간 정보 설정
    	int medium_defaultDurSecs = 15;
    	int medium_minDurSecs = 10;
    	int medium_maxDurSecs = 20;
    	boolean medium_rangeDurAllowed = false;
    	if (medium != null) {
    		medium_defaultDurSecs = medium.getDefaultDurSecs();
    		medium_rangeDurAllowed = medium.isRangeDurAllowed();
    		medium_minDurSecs = medium.getMinDurSecs();
    		medium_maxDurSecs = medium.getMaxDurSecs();
    	}
    	
    	model.addAttribute("medium_defaultDurSecs", medium_defaultDurSecs);
    	model.addAttribute("medium_rangeDurAllowed", medium_rangeDurAllowed);
    	model.addAttribute("medium_minDurSecs", medium_minDurSecs);
    	model.addAttribute("medium_maxDurSecs", medium_maxDurSecs);
    	
    	model.addAttribute("bizHour", medium.getBizHour());
    	
    	
        return "inv/site/site-screen";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/inv/site/screen/read", method = RequestMethod.POST)
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
    
    
	/**
	 * 화면 해상도 목록 획득
	 */
    private List<DropDownListItem> getResolutionList(KnlMedium medium) {

		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
		
		
		if (medium != null) {
			List<String> resolutions = Util.tokenizeValidStr(medium.getResolutions());
			for(String resolution : resolutions) {
				retList.add(new DropDownListItem(resolution.replace("x", " x ") , resolution));
			}
		}

		//Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
		
		return retList;
    }
}
