package net.doohad.controllers.api;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
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

import net.doohad.info.GlobalInfo;
import net.doohad.models.adc.AdcPlaylist;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.org.OrgChannel;
import net.doohad.models.org.OrgRTChannel;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.knl.KnlMediumCompactItem;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 채널 재생목록 API 컨트롤러
 */
@Controller("api-chan-pl-controller")
@RequestMapping(value="")
public class ChanPlController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChanPlController.class);


	//
	// 채널 재생목록 API: 읽기만 가능.
	//
	
    @Autowired 
    private InvService invService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private AdcService adcService;
	
    
    /**
	 * 채널 재생목록 API
	 */
    @RequestMapping(value = {"/v1/chan/pl/{displayID}", "/v1/chan/pl/{displayID}/{viewType}"}, method = RequestMethod.GET)
    public void processApiChanPl(HttpServletRequest request, HttpServletResponse response,
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
    		JSONObject plObj = new JSONObject();
    		
    		// viewType의 전달 여부에 따라 동일한 조건(게시유형 존재 여부 및 값 동일)의 채널 확인
    		
    		OrgChannel channel = orgService.getChannel(SolUtil.getFirstPriorityChannelByTypeObjId("S", screen.getId(), viewType));
    		if (channel != null && channel.getAppendMode().equals("P")) {
    			
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
    			
    			
	    		AdcPlaylist currPl = null;
	    		
	    		
	    		// 현재 시간 기준 적용 재생목록 후보 확인
				List<AdcPlaylist> plList = adcService.getActivePlaylistListByChannelId(channel == null ? -1 : channel.getId());
				for(AdcPlaylist pl : plList) {
					if (pl.getStartDate().before(now) && 
							(pl.getEndDate() == null || pl.getEndDate().after(now))) {
						currPl = pl;
					}
				}

				if (currPl != null) {
					plObj = getPlaylistObject(currPl, viewType, channel.getResolution(), screen.getMedium().getId());
				}
    		}
    		
    		obj.put("channel", chan);
    		obj.put("playlist", plObj);
			
        	logger.info("[API] chan pl: " + screen.getName() + (testMode ? ", test mode": ""));
    	}
		
		Util.toJson(response, obj);
    }

    
    private JSONObject getPlaylistObject(AdcPlaylist playlist, String viewType, String reso, int mediumId) {
    	
    	try {
    		
        	JSONObject obj = new JSONObject();

        	obj.put("id", playlist.getId());
        	obj.put("name", playlist.getName());
        	obj.put("start", Util.toSimpleString(playlist.getStartDate(), "yyyyMMdd HHmm"));
        	
        	JSONArray list = new JSONArray();
        	
        	HashMap<String, Integer> map = new HashMap<String, Integer>();
        	List<Tuple> tupleList = adcService.getAdCreativeTupleListIn(getIds(playlist.getAdValue()));
        	for(Tuple tuple : tupleList) {
        		map.put("A" + String.valueOf((Integer)tuple.get(0)), (Integer)tuple.get(1));
        	}
        	
        	// 게시유형 지정 여부에 따른 유효 컨텐츠 확인
        	ArrayList<String> validKeys = new ArrayList<String>();
        	if (Util.isValid(viewType)) {
        		
        		tupleList = adcService.getChannelAdViewTypeTupleListByMediumId(mediumId);
        		for(Tuple tuple : tupleList) {
        			int effective = ((BigInteger) tuple.get(12)).intValue();
        			if (effective == 1) {
        				validKeys.add("AC" + String.valueOf(((Integer) tuple.get(0)).intValue()));
        			}
        		}
        		
        	} else {
        		
        		tupleList = adcService.getChannelAdNoViewTypeTupleListByMediumId(mediumId);
        		for(Tuple tuple : tupleList) {
        			int effective = ((BigInteger) tuple.get(12)).intValue();
        			if (effective == 1) {
        				validKeys.add("AC" + String.valueOf(((Integer) tuple.get(0)).intValue()) + "R" + ((String) tuple.get(8)));
        			}
        		}
        		
        	}
        	
        	List<String> currValues = Util.tokenizeValidStr(playlist.getAdValue());
        	for(String s : currValues) {
        		
        		String key = "AC" + s;
        		if (Util.isNotValid(viewType)) {
        			key += "R" + reso;
        		}
        		
        		if (validKeys.contains(key)) {
        			
            		Integer creatId = map.get("A" + s);
            		if (creatId != null) {
            			
                		JSONObject acObj = new JSONObject();
                		
                		acObj.put("ad_id", creatId);
                		acObj.put("ac_id", Util.parseInt(s));
                		
                		list.add(acObj);
            		}
        		}
        	}
        	
        	obj.put("list", list);
        	
        	return obj;
    		
    	} catch (Exception e) {
    		logger.error("Chan pl API - getPlaylistObject", e);
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

}