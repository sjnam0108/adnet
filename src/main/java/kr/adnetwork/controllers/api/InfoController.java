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
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.SysService;
import kr.adnetwork.models.sys.SysOpt;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import net.sf.json.JSONObject;

/**
 * 정보 API 컨트롤러
 */
@Controller("api-info-controller")
@RequestMapping(value="")
public class InfoController {
	
	private static final Logger logger = LoggerFactory.getLogger(InfoController.class);


	//
	// 정보 획득 API: 읽기만 가능. 이후에 test 모드 필요 시 추가 예정
	//
	
    @Autowired 
    private InvService invService;

	@Autowired
	private SysService sysService;
	
    
    /**
	 * 정보 API
	 */
    @RequestMapping(value = {"/v1/info/{displayID}"}, method = RequestMethod.GET)
    public void processApiPlaylist(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	
    	
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

    		// rcvController에서 버전 전송 시 자동으로 처리되기 때문에 여기서는 사용 안함
    		/*
    		// 화면만 확정되면 바로 요청일시 저장
    		if (!testMode) {

    	    	Date now = new Date();

    			// 개체 이벤트 처리: 화면(S)의 플레이어 시작(info)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 14));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    		}
    		*/
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {
    		
    		//
    		//  display_id :
    		//       기기 화면에서 요청할 때 전달된 화면ID
    		//
    		//  lane_id: (숫자)
    		//       동기화 화면 묶음에서의 레인번호. 1-based 일련번호.
    		//       0 이면 동기화 묶음 멤버가 아니거나, 지정되지 않은 상태.
    		//       1 이상이면 특정 동기화 화면 묶음에서의 레인번호.
    		//
    		//  각 매체별 화면 옵션
    		//    - 등록된 화면 옵션(일반 설정 - 화면 옵션)을 항목으로 해당 화면 포함을 bool 값으로 처리
    		//    - 화면 옵션이 등록되지 않은 매체는 기본적으로 'tester' 항목을 추가(값은 false)
    		//    - * 항목은 커스텀 옵션
    		//
    		//  tester: (boolean)
    		//       true면, 테스트 기기로 등록되어 있는 것으로, API 이용 시 모두 test=y로 요청
    		//               (lite player에만 현재 해당됨. sync player에서는 의미 없음)
    		//       false면, 일반 기기
    		//  *paid: (boolean)
    		//       true면, 기기가 유료형 구매이기 때문에 병원 광고만 진행
    		//  *egs: (boolean)
    		//       true면, egs 앱을 lite player 구동시에 실행시킴
    		//
    		
    		int laneId = invService.getSyncPackItemLaneIdByScreenId(screen.getId());

    		
    		JSONObject scrObj = new JSONObject();
    		scrObj.put("display_id", screen.getShortName());
    		scrObj.put("lane_id", laneId);

        	logger.info("[API] info: " + screen.getName() + " / " + screen.getShortName() + " - " + laneId);
    		
    		
    		boolean hasTester = false;
    		
    		String opts = SolUtil.getOptValue(screen.getMedium().getId(), "opt.list");
    		if (Util.isValid(opts)) {
        		List<String> optList = Util.tokenizeValidStr(opts);
        		for(String opt : optList) {
        			List<String> optPair = Util.tokenizeValidStr(opt, ",");
        			if (optPair.size() == 2) {
        				String ID = optPair.get(0);
        				//String name = optPair.get(1);
        				
        				if (ID.equals("tester")) {
        					hasTester = true;
        				}
        				
        				
        		    	SysOpt sysOpt = sysService.getOpt("opt." + ID + "." + screen.getMedium().getShortName());
        		    	boolean objValue = false;
    					if (sysOpt != null) {
    						String value = sysOpt.getValue();
    						if (Util.isValid(value)) {
    							List<String> ids = Util.tokenizeValidStr(value);
    							for(String id : ids) {
    								if (Util.parseInt(id) == screen.getId()) {
    									objValue = true;
    									break;
    								}
    							}
    						}
    					}
    					
    					scrObj.put(ID, objValue);
        			}
        		}
    		}
    		
    		if (!hasTester) {
        		scrObj.put("tester", false);
    		}
    		
    		obj.put("screen", scrObj);
    	}
		
		Util.toJson(response, obj);
    }

}
