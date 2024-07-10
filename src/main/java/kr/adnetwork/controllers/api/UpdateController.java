package kr.adnetwork.controllers.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import kr.adnetwork.info.GlobalInfo;
import kr.adnetwork.models.fnd.FndSetupFile;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import net.sf.json.JSONObject;

/**
 * 업데이트 API 컨트롤러
 */
@Controller("api-update-controller")
@RequestMapping(value="")
public class UpdateController {
	
	private static final Logger logger = LoggerFactory.getLogger(UpdateController.class);


	//
	// 업데이트 API: 읽기만 가능(test 파라미터 처리 완료)
	//
	
    @Autowired 
    private InvService invService;

    @Autowired 
    private FndService fndService;
    
    
    /**
	 * 업데이트 API
	 */
    @RequestMapping(value = {"/v1/update/{displayID}"}, method = RequestMethod.GET)
    public void processApiPlaylist(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String ver = Util.parseString(paramMap.get("ver"));
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID) || Util.isNotValid(ver)) {
    		
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

    		// 업데이트 API는 별도의 touch 컬럼을 가지지 않음
    		/*
	    	Date now = new Date();

			// 개체 이벤트 처리: 화면(S)의 플레이어 시작(info)
			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 14));
    		
    		// 상태 라인 처리 위해 공용리스트에 추가
    		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    		*/
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {
			
    		String url = "";
    		String prodKeyword = "";
    		String plat = "";
    		int verNumber = 0;
    		
	    	try {
	    		
	    		//
	    		// ver 예: lite_2.1.5 or sync_1.0.2_QB2 or lite.egs_2.1.67
	    		//
	    		List<String> tokens = Util.tokenizeValidStr(ver, "_");
	    		if (tokens.size() == 2 || tokens.size() == 3) {
	    			prodKeyword = tokens.get(0);
	    			if (tokens.size() == 3) {
	    				plat = tokens.get(2);
	    			}
	    			
	    			if (prodKeyword.equals("lite") || prodKeyword.equals("sync") || 
	    					prodKeyword.startsWith("lite.") || prodKeyword.startsWith("sync.")) {
	    				
	    				List<String> strs = Util.tokenizeValidStr(tokens.get(1), ".");
	    				if (strs.size() == 3) {
		    				int v1 = Util.parseInt(strs.get(0));
		    				int v2 = Util.parseInt(strs.get(1));
		    				int v3 = Util.parseInt(strs.get(2));
							
							if (v1 > -1 && v2 > -1 && v3 > -1 && v1 < 100 && v2 < 100 && v3 < 1000) {
								verNumber = v1 * 100000 + v2 * 1000 + v3;
							}
	    				}
	    			}
	    		}
	    		
	    		if (verNumber > 0 && Util.isValid(prodKeyword)) {
	    			FndSetupFile setupFile = fndService.getLastVerSetupFile(prodKeyword, verNumber, plat);
	    			if (setupFile != null) {
	    				url = setupFile.getHttpFilename();
	    			}
	    		}
			
	    	} catch (Exception e) {
	    		logger.error("Update API", e);
	    	}
    		
    		
    		obj.put("url", url);
    	}
		
		Util.toJson(response, obj);
    }

}
