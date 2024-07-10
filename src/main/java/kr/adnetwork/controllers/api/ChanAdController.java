package kr.adnetwork.controllers.api;

import java.util.Date;
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
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.org.OrgRTChannel;
import kr.adnetwork.models.rev.RevChanAd;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import kr.adnetwork.viewmodels.rev.RevObjEventTimeItem;
import kr.adnetwork.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 채널 광고 API 컨트롤러
 */
@Controller("api-chan-ad-controller")
@RequestMapping(value="")
public class ChanAdController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChanAdController.class);


	//
	// 채널 광고 API: 읽기만 가능.
	//
	
    @Autowired 
    private InvService invService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private OrgService orgService;
	
    
    /**
	 * 채널 광고 API
	 */
    @RequestMapping(value = {"/v1/chan/{displayID}", "/v1/chan/{displayID}/{viewType}"}, method = RequestMethod.GET)
    public void processApiChanAd(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    	String viewType = Util.parseString(pathMap.get("viewType"));
    
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

    		// 화면의 광고 요청일시 기록
    		if (!testMode) {
    			
    			// 개체 이벤트 처리: 화면(S)의 현재광고 요청(chan)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 12));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
    			//
    			//  - 플레이어에서의 요청이 아닐 경우에는 상태 라인에 추가하지 않음
    			//
    			GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {

    		String chan = "";
    		JSONArray playlist = new JSONArray();
    		
    		// viewType의 전달 여부에 따라 동일한 조건(게시유형 존재 여부 및 값 동일)의 채널 확인
    		
    		OrgChannel channel = orgService.getChannel(SolUtil.getFirstPriorityChannelByTypeObjId("S", screen.getId(), viewType));
    		if (channel != null) {
    			
    			// 채널의 광고 요청 기록
				OrgRTChannel rtChannel = orgService.getRTChannelByChannelId(channel.getId());
				if (rtChannel == null) {
					orgService.saveOrUpdate(new OrgRTChannel(channel.getId()));
					
					rtChannel = orgService.getRTChannelByChannelId(channel.getId());
				}
				if (rtChannel != null) {
					rtChannel.setLastAdReqDate(now);
					rtChannel.setWhoLastUpdateDate(now);
					
					orgService.saveOrUpdate(rtChannel);
				}
    			
    			
    			chan = channel.getShortName();
    			
    			List<RevChanAd> adList = revService.getCurrChanAdListByChannelId(channel.getId());
        		for(RevChanAd chanAd : adList) {
        			
        			JSONObject plObj = new JSONObject();
        			
        			plObj.put("seq", chanAd.getSeq());
        			plObj.put("ad_id", chanAd.getCreatId());
        			plObj.put("ac_id", chanAd.getAdCreatId());
        			plObj.put("ad_pack_ids", chanAd.getAdPackIds());
        			
        			plObj.put("begin", Util.toSimpleString(chanAd.getPlayBeginDate(), "yyyyMMdd HHmmss.SSS"));
        			plObj.put("end", Util.toSimpleString(chanAd.getPlayEndDate(), "yyyyMMdd HHmmss.SSS"));
        			
        			playlist.add(plObj);
        		}
    		}
    		
    		obj.put("channel", chan);
    		obj.put("playlist", playlist);
			
        	logger.info("[API] chan: " + screen.getName() + (testMode ? ", test mode": ""));
    	}
		
		Util.toJson(response, obj);
    }

}