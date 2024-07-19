package kr.adnetwork.controllers.inv;

import java.io.File;
import java.io.FilenameFilter;
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
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.rev.RevUploadFileItem;

/**
 * 화면 컨트롤러(로그)
 */
@Controller("inv-screen-log-controller")
@RequestMapping(value="/inv/screen/logs")
public class InvScreenLogController {

	private static final Logger logger = LoggerFactory.getLogger(InvScreenLogController.class);

	
    @Autowired 
    private InvService invService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 화면 컨트롤러(로그)
	 */
    @RequestMapping(value = {"/{screenId}", "/{screenId}/"}, method = RequestMethod.GET)
    public String index1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
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

    	
        return "inv/screen/screen-log";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<RevUploadFileItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	ArrayList<RevUploadFileItem> list = new ArrayList<RevUploadFileItem>();
    	InvScreen screen = invService.getScreen((int)request.getReqIntValue1());
    	
    	if (screen == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	try {
    		String typeRootDir = SolUtil.getPhysicalRoot("Log");
    		
    		File dir = new File(typeRootDir);
    		if (dir.exists() && dir.isDirectory()) {
				File[] files = dir.listFiles(new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.toLowerCase().endsWith(".zip");
					}
				});
				
				String prefix = screen.getMedium().getId() + "_" + screen.getId() + "_log_";
				
				//
				// 업로드 파일 형식: {mediumId}_{screenId}_log_{대상일:yyyy.MM.dd}.zip
				// 형식의 예: 17_17980_log_2024.07.24.zip
				//
				int idx = 0;
				for(File file : files) {
					if (file.getName().startsWith(prefix)) {
						String remain = file.getName().substring(prefix.length()).toLowerCase().replace(".zip", "");
						list.add(new RevUploadFileItem(file.getName(), file.length(), file.lastModified(), 
								Util.delimitDateStr(remain, "."), ++idx));
					}
				}
    		}
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    	
    	return list;
    }
}
