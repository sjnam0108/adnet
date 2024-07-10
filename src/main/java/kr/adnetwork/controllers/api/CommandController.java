package kr.adnetwork.controllers.api;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kr.adnetwork.info.GlobalInfo;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.rev.RevObjTouch;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import kr.adnetwork.viewmodels.rev.RevObjEventTimeItem;
import kr.adnetwork.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONObject;

/**
 * 명령 API 컨트롤러
 */
@Controller("api-command-controller")
@RequestMapping(value="")
public class CommandController {
	
	//private static final Logger logger = LoggerFactory.getLogger(CommandController.class);


	//
	// 명령 획득 API: 읽기만 가능(test 파라미터 처리 완료)
	//
	
    @Autowired 
    private InvService invService;

    @Autowired 
    private RevService revService;
	
    
    /**
	 * 명령 API
	 */
    @RequestMapping(value = {"/v1/cmd/{displayID}"}, method = RequestMethod.GET)
    public void processApiPlaylist(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID)) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	} else {
    		
        	KnlMediumCompactItem mediumItem = GlobalInfo.ApiKeyMediaMap.get(apiKey);
        	if (mediumItem == null) {
        		statusCode = -1;
        		message = "WrongApiKey";
        		localMessage = "등록되지 않은 API key가 전달되었습니다.";
        	} else {
        		screen = invService.getScreenByMediumIdShortName(mediumItem.getId(), displayID);
        		if (screen == null) {
            		statusCode = -2;
            		message = "WrongDisplayID";
            		localMessage = "등록되지 않은 디스플레이 ID가 전달되었습니다.";
        		}
        	}
    	}

    	
    	if (statusCode >= 0 && screen != null) {
    		// 아직까지는 큰 문제 없음
    		
    		if (!SolUtil.isEffectiveDate(screen.getEffectiveStartDate(), screen.getEffectiveEndDate())) {
    			statusCode = -4;
    			message = "EffectiveDateExpired";
    			localMessage = "유효 기간의 범위에 포함되지 않습니다.";
    		} else if (screen.isActiveStatus() != true) {
    			statusCode = -5;
    			message = "NotActive";
    			localMessage = "정상 서비스 중이 아닙니다.";
    		} else if (screen.isAdServerAvailable() != true) {
    			statusCode = -6;
    			message = "NotAdServerAvailable";
    			localMessage = "광고 서비스로 이용할 수 없습니다.";
    		}

    		// 화면만 확정되면 바로 요청일시 저장
    		if (!testMode) {

    	    	Date now = new Date();

    	    	// 개체 이벤트 처리: 화면(S)의 명령 확인(command)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 15));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
        		
        		// 빠른 명령 확인 시간 등록을 위해
        		RevObjTouch objTouch = revService.getObjTouch("S", screen.getId());
        		if (objTouch != null) {
        			objTouch.setDate5(now);
    				
    				objTouch.touchWho();
    				revService.saveOrUpdate(objTouch);
        		}
        		//-
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {
    		
    		JSONObject scrObj = new JSONObject();
    		
    		String nextCmd = "";
    		String event = "";
    		
    		InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
    		if (rtScreen != null && !rtScreen.isCmdFailed()) {
    			nextCmd = rtScreen.getNextCmd();
        		if (Util.isValid(nextCmd)) {
        			if (nextCmd.equals("Reboot")) {
        				event = "Reboot";
        			}
        		}
    		}
    		
    		scrObj.put("command", nextCmd);
    		scrObj.put("event", event);
    		
    		obj.put("screen", scrObj);
    	}
		
		Util.toJson(response, obj);
    }

}
