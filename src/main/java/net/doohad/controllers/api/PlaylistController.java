package net.doohad.controllers.api;

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

import net.doohad.info.GlobalInfo;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.service.InvService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.knl.KnlMediumCompactItem;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 재생목록 API 컨트롤러
 */
@Controller("api-playlist-controller")
@RequestMapping(value="")
public class PlaylistController {
	
	//private static final Logger logger = LoggerFactory.getLogger(AdController.class);


	//
	//   chan pl API 정상화 후 삭제 예정
	//
	//
	// 재생목록 API: 읽기만 가능(test 파라미터 처리 완료)
	//
	
    @Autowired 
    private InvService invService;
	
    
    /**
	 * 재생목록 API
	 */
    @RequestMapping(value = {"/v1/playlist/{displayID}", "/v1/playlist/{displayID}/{viewType}"}, method = RequestMethod.GET)
    public void processApiPlaylist(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    	//String viewType = Util.parseString(pathMap.get("viewType"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String test = Util.parseString(paramMap.get("test"));
    	String player = Util.parseString(paramMap.get("player"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	boolean playerMode = Util.isNotValid(player) || !player.toLowerCase().equals("n");
    	
    	
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

    	
    	Date now = new Date();
    	
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
    			
    			// 동기화 화면 묶음에서는 더이상 playlist API를 사용치 않음(대신 recplaylist)
    			
    			// 개체 이벤트 처리: 화면(S)의 현재광고 요청(ad, playlist)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 12));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
    			//
    			//  - 플레이어에서의 요청이 아닐 경우에는 상태 라인에 추가하지 않음
    			//
    			if (playerMode) {
            		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    			}
    			
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {
    		JSONArray playlists = new JSONArray();
    		
    		/*
    		// viewType이 전달되면 viewType 모드로,
			// 아니면 해상도 모드로 진행
    		List<AdcPlaylist> plList = null;
    		
    		if (Util.isValid(viewType)) {
    			
    			// 게시 유형이 전달된 경우
    			//   1) 해당 화면과 연결된 재생목록이 존재하는지?
    			plList = adcService.getPlaylistListByViewTypeCodeScreenIdActiveStatus(viewType, screen.getId(), true);
    			
    			//   2) 존재하지 않는다면, 게시유형과 연결된 모든 재생목록을 적용
    			if (plList.size() == 0) {
        			plList = adcService.getPlaylistListByViewTypeCodeActiveStatusMediumId(viewType, true, screen.getMedium().getId());
    			}
    		} else {
    			
    			// 게시 유형이 전달되지 않아 해상도 모드로 진행
    			//   1) 해당 화면과 연결된 재생목록이 존재하는지?
    			plList = adcService.getPlaylistListByResoScreenIdActiveStatus(screen.getResolution(), screen.getId(), true);
    			
    			//   2) 존재하지 않는다면, 게시유형과 연결된 모든 재생목록을 적용
    			if (plList.size() == 0) {
        			plList = adcService.getPlaylistListByResoActiveStatus(screen.getResolution(), true);
    			}
    		}
    		
    		if (plList != null) {
    			for(AdcPlaylist playlist : plList) {
    				playlists.add(getPlaylistObject(playlist));
    			}
    		}
    		*/
    	
    		obj.put("playlists", playlists);
    	}
		
		Util.toJson(response, obj);
    }

    /*
    private JSONObject getPlaylistObject(AdcPlaylist playlist) {
    	
    	try {
    		
        	JSONObject obj = new JSONObject();

        	obj.put("id", playlist.getId());
        	obj.put("name", playlist.getName());
        	obj.put("start", Util.toSimpleString(playlist.getStartDate(), "yyyyMMdd HHmm"));
        	
        	JSONArray list = new JSONArray();
        	
        	HashMap<String, Integer> map = new HashMap<String, Integer>();
        	HashMap<String, String> packIdsMap = new HashMap<String, String>();
        	List<Tuple> tupleList = adcService.getAdCreativeTupleListIn(getIds(playlist.getAdValue()));
        	for(Tuple tuple : tupleList) {
        		map.put("A" + String.valueOf((Integer)tuple.get(0)), (Integer)tuple.get(1));
        		packIdsMap.put("A" + String.valueOf((Integer)tuple.get(0)), (String)tuple.get(2));
        	}
        	
        	List<String> currValues = Util.tokenizeValidStr(playlist.getAdValue());
        	for(String s : currValues) {
        		
        		JSONObject acObj = new JSONObject();
        		
        		Integer creatId = map.get("A" + s);
        		String adPackIds = packIdsMap.get("A" + s);
        		if (creatId != null) {
            		acObj.put("ad_id", creatId);
            		acObj.put("ac_id", Util.parseInt(s));
            		acObj.put("ad_pack_ids", Util.isValid(adPackIds) ? adPackIds : "");
        		}
        		
        		list.add(acObj);
        	}
        	
        	obj.put("list", list);
        	
        	return obj;
    		
    	} catch (Exception e) {
    		logger.error("Playlist API - getPlaylistObject", e);
    	}
    	
    	return null;
    }
    
    
    private List<Integer> getIds(String ids) {
    	
    	List<String> idList = Util.tokenizeValidStr(ids);
    	ArrayList<Integer> retList = new ArrayList<Integer>();
    	
    	for(String s : idList) {
    		Integer i = Util.parseInt(s);
    		if (i > 0 && !retList.contains(i)) {
    			retList.add(i);
    		}
    	}
    	
    	return retList;
    }
    */
}
