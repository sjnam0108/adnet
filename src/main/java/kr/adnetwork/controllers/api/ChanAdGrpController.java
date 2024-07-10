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
import kr.adnetwork.models.inv.InvRTSyncPack;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.org.OrgRTChannel;
import kr.adnetwork.models.rev.RevChanAd;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 그룹 채널 광고 API 컨트롤러
 */
@Controller("api-grp-chan-ad-controller")
@RequestMapping(value="")
public class ChanAdGrpController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChanAdGrpController.class);


	//
	// 그룹 채널 광고 API: 읽기만 가능.
	//
	
    @Autowired 
    private InvService invService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private OrgService orgService;
	
    
    /**
	 * 그룹 채널 광고 API
	 */
    @RequestMapping(value = {"/v1/chan/g/{groupID}"}, method = RequestMethod.GET)
    public void processApiChanAdGrp(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String groupID = Util.parseString(pathMap.get("groupID"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
		Date now = new Date();
		
    	JSONObject obj = new JSONObject();
    	InvSyncPack syncPack = null;
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(groupID)) {
    		
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
        		syncPack = invService.getActiveSyncPackByShortName(groupID);
        		if (syncPack == null) {
            		statusCode = -8;
            		message = "WrongGroupID";
            		localMessage = "등록되지 않은 동기화 묶음 ID가 전달되었습니다.";
        		}
        	}
    	}

    	
    	if (statusCode >= 0 && syncPack != null) {
    		// 아직까지는 큰 문제 없음
    		
    		if (syncPack.isActiveStatus() != true) {
    			statusCode = -5;
    			message = "NotActive";
    			localMessage = "정상 서비스 중이 아닙니다.";
    		}

    		
    		// 동기화 화면 묶음의 광고 요청 기록
    		if (!testMode) {
    			
        		InvRTSyncPack rtSyncPack = invService.getRTSyncPackBySyncPackId(syncPack.getId());
        		if (rtSyncPack == null) {
        			invService.saveOrUpdate(new InvRTSyncPack(syncPack.getId()));
        			
        			rtSyncPack = invService.getRTSyncPackBySyncPackId(syncPack.getId());
        		}
        		if (rtSyncPack != null) {
        			rtSyncPack.setLastAdReqDate(now);
        			rtSyncPack.setWhoLastUpdateDate(now);
        			
        			invService.saveOrUpdate(rtSyncPack);
        		}
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && syncPack != null) {

    		String chan = "";
    		JSONArray playlist = new JSONArray();
    		
    		OrgChannel channel = orgService.getChannel(SolUtil.getFirstPriorityChannelByTypeObjId("P", syncPack.getId()));
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
			
        	logger.info("[API] chan g: " + syncPack.getShortName() + "/" + syncPack.getName() + (testMode ? ", test mode": ""));
    	}
		
		Util.toJson(response, obj);
    }

}