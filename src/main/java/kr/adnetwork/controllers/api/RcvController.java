package kr.adnetwork.controllers.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import kr.adnetwork.info.GlobalInfo;
import kr.adnetwork.models.adc.AdcPlaylist;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvRTSyncPack;
import kr.adnetwork.models.inv.InvScrLoc;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.knl.KnlUser;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.rev.RevChanAd;
import kr.adnetwork.models.rev.RevChanAdRpt;
import kr.adnetwork.models.rev.RevEventReport;
import kr.adnetwork.models.rev.RevObjTouch;
import kr.adnetwork.models.rev.RevSyncPackImp;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.inv.InvSyncPackCompactItem;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import kr.adnetwork.viewmodels.rev.RevObjEventTimeItem;
import kr.adnetwork.viewmodels.rev.RevScrWorkTimeItem;
import kr.adnetwork.viewmodels.rev.RevSyncPackMinMaxItem;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 보고 수령 API 컨트롤러
 */
@Controller("api-rcv-controller")
@RequestMapping(value="")
public class RcvController {
	
	private static final Logger logger = LoggerFactory.getLogger(RcvController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;
	
    
    /**
	 * 보고 수령 API
	 */
    @RequestMapping(value = {"/v1/rcv"}, method = RequestMethod.POST)
    public void rcv(HttpServletRequest request, HttpServletResponse response) {
		
    	BufferedReader reader = null;
		String line = null;
		StringBuilder builder = new StringBuilder();
		
    	try {
			reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				builder.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			logger.error("rcv", e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException ex) {
	    			logger.error("rcv", ex);
				}
			}
		}
    	
    	String info = Util.removeTrailingChar(builder.toString(), System.getProperty("line.separator"));
		if (Util.isValid(info)) {
			boolean isValid = false;
			
			try {
	    		JSONObject infoObj = JSONObject.fromObject(JSONSerializer.toJSON(info));
	    		if (infoObj != null) {
	    			String displayID = infoObj.getString("displayID");
	    			String apiKey = infoObj.getString("apikey");
	    			int reportType = infoObj.getInt("reporttype");
	    			
	    			InvScreen screen = null;
	    			
	    			
	    			if (Util.isValid(displayID) && Util.isValid(apiKey) && reportType > 0) {
	    	    		
	    	        	KnlMediumCompactItem mediumItem = GlobalInfo.ApiKeyMediaMap.get(apiKey);
	    	        	if (mediumItem != null) {
	    	        		screen = invService.getScreenByMediumIdShortName(mediumItem.getId(), displayID);

	    	    			if (screen != null && SolUtil.isEffectiveDate(screen.getEffectiveStartDate(), screen.getEffectiveEndDate())
	    	    					&& screen.isActiveStatus() == true && screen.isAdServerAvailable() == true) {

	    	    				isValid = process(screen, reportType, infoObj);
	    	    			}
	    	        	}
	    			}
	    		}
	    		
			} catch (Exception e) {
				logger.error("rcv - json parsing", e);
			}
			
			
			if (!isValid) {
	        	logger.info("----------------- rcv[S]");
			}

			logger.info("[API] rcv: " + info);
			
			if (!isValid) {
	        	logger.info("----------------- rcv[E]");
			}
		}
    }
    
    /**
	 * 주요 프로세스 코드
	 */
    private boolean process(InvScreen screen, int reportType, JSONObject infoObj) {
    	
		//
		// 화면의 상시 보고 목록
    	//
    	//  기본적으로 apikey, displayID, reporttype, 세 항목은 필수(대소문자 구분)이고, 유형별 추가 항목 필요
    	//    예: {"apikey":"ecOcUBed13W6WTiANkiDRT","displayID":"cube_03_A7","event":"Reboot","reporttype":1,"result":"Y"}
		//
    	//  1024) 테스트 모드 여부
    	//
    	//  1) 명령 수행 결과 보고:
    	//     결과(result) 및 이벤트(event) - 총 2 항목
    	//  2) 이벤트 보고:
    	//     이벤트(event), 트리거링 포인트(trigger, 시작점) 및 간략 정보(desc) - 총 3 항목
    	//  4) 버전 정보 보고:
    	//     버전(ver) - 총 1 항목
    	//  8) [삭제]재생목록 정보 보고:
    	//     재생목록 번호(playlist - int), 이름(name), 표시영역(lane) - 총 3 항목
    	//  16) 이동형 화면 GPS 위치 보고:
    	//      위도(lat), 경도(lng) - 총 2 항목
    	//	32) 동기화 묶음 기기의 광고 노출 시작 보고
    	//	    그룹(gid), 광고(ad) - 총 2 항목
		//

    	Date now = new Date();
    	boolean ret = false;
    	
    	boolean testMode = ((reportType >>> 10) & 1) != 0;
    	
    	
    	if (!testMode) {
    		
    		// 1: 명령 수행 결과 보고(aka commandReport API)
    		if (((reportType >>> 0) & 1) != 0) {

    			// 개체 이벤트 처리: 화면(S)의 명령결과 보고(commandReport)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 16));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    			
    			ret = handleCommandReport(screen, infoObj, now);
    		}

    		// 2: 이벤트 보고(aka event API)
    		if (((reportType >>> 1) & 1) != 0) {

    			// 개체 이벤트 처리: 화면(S)의 이벤트 보고(event)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 17));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    			
    			ret = handleEventReport(screen, infoObj);
    		}
    		
    		// 4: 버전 정보 보고(aka info API)
    		//    info API에서 서버에 전달하는 부분과 받아야 하는 부분을 분리하고, 전달하는 부분만 구현
    		if (((reportType >>> 2) & 1) != 0) {

    			// 개체 이벤트 처리: 화면(S)의 플레이어 시작(info)
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 14));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    			
        		ret = handleVerReport(screen, infoObj);
    		}

    		// 8: [삭제]재생목록 정보 보고
    		if (((reportType >>> 3) & 1) != 0) {

    			/*
    			// 개체 이벤트 처리: 화면(S)의 재생목록 정보 확인
    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 18));
        		
        		// 상태 라인 처리 위해 공용리스트에 추가
        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
    			
        		ret = handlePlaylistReport(screen, infoObj, now);
        		*/
    		}

    		// 16: 이동형 화면 GPS 위치 보고
    		if (((reportType >>> 4) & 1) != 0) {
    			
        		ret = handleScreenGpsReport(screen, infoObj, now);
    		}

    		// 32: 동기화 묶음 기기의 광고 노출 시작 보고
    		if (((reportType >>> 5) & 1) != 0) {
    			
        		ret = handleSyncScreenAdStartTime(screen, infoObj, now);
    		}
    		
    	}
		

		return ret;
    }
    
    
    /**
	 * 부분 처리: 1: 명령 수행 결과 보고(aka commandReport API)
	 */
    private boolean handleCommandReport(InvScreen screen, JSONObject infoObj, Date date) {
    	
    	try {
    		
    		String result = Util.parseString(infoObj.getString("result"));
    		String event = Util.parseString(infoObj.getString("event"));
    		
    		
    		// 빠른 실행 결과 시간 등록을 위해
    		RevObjTouch objTouch = revService.getObjTouch("S", screen.getId());
    		if (objTouch != null) {
    			objTouch.setDate6(date);
				
				objTouch.touchWho();
				revService.saveOrUpdate(objTouch);
    		}
    		//-
    		
    		
    		int cmdBy = -1;
    		
    		// 명령 실행 결과 기록
    		boolean failed = Util.isValid(result) && result.toLowerCase().equals("n");
			
    		InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
    		if (rtScreen == null) {
    			invService.saveOrUpdate(new InvRTScreen(screen.getId()));
    			
    			rtScreen = invService.getRTScreenByScreenId(screen.getId());
    		}
    		
    		if (rtScreen != null && Util.isValid(rtScreen.getNextCmd())) {
    			
    			if (failed) {
	    			rtScreen.setCmdFailed(failed);
    			} else {
    				// 성공했을 경우 명령 등록 초기화
    				rtScreen.setNextCmd("");
	    			rtScreen.setCmdFailed(false);
	    			cmdBy = rtScreen.getCmdBy();
    			}
    			
    			rtScreen.setWhoLastUpdateDate(new Date());
    			
    			invService.saveOrUpdate(rtScreen);
    		}
		
    		
    		// 결과 기록 후 이벤트 등록 필요한 경우
    		if (!failed && Util.isValid(event)) {
        		
        		String reportType = "";
        		String category = "";
        		String details = "";
        		
        		// 트리거 유형은 "명령"
        		String triggerType = "C";
        		
        		boolean isRightEvent = false;

        		// 명령에 의해 트리거된 이벤트는 현재 Reboot 하나
        		if (event.equals("Reboot") || event.equals("Restart")) {
        			reportType = "I";
        			category = "O";
        			isRightEvent = true;
        			
        			if (cmdBy > -1) {
        				KnlUser user = knlService.getUser(cmdBy);
        				if (user != null) {
        					details = "by " + user.getShortName();
        				}
        			}
        		}
        		
        		if (isRightEvent) {
            		revService.saveOrUpdate(new RevEventReport(screen, reportType, category, event, triggerType, details));
        		}
    		}
    		
    		return true;
    	} catch (Exception e) {
			logger.error("handleCommandReport", e);
		}
    	
    	return false;
    }
    
    
    /**
	 * 부분 처리: 2: 이벤트 보고(aka event API)
	 */
    private boolean handleEventReport(InvScreen screen, JSONObject infoObj) {
    	
    	try {
    		
    		String event = Util.parseString(infoObj.getString("event"));
    		String trigger = Util.parseString(infoObj.getString("trigger"));
    		String desc = "";
    		
    		// desc 항목은 이후에 추가되어, 선택적으로 포함되거나 명시되지 않을 예정
    		// 따라서, 값 체크 시 오류 방지를 위해 별도 try catch 적용
    		try {
    			desc = Util.parseString(infoObj.getString("desc"));
    		} catch (Exception e1) {}
    		
    		
    		String reportType = "";
    		String category = "";
    		String details = "";
    		String triggerType = "P";
    		
    		boolean isRightEvent = false;
    		
    		if (event.equals("Reboot")) {
    			reportType = "I";
    			category = "O";
    			isRightEvent = true;
    		} else if (event.equals("Restart")) {
    			reportType = "I";
    			category = "O";
    			isRightEvent = true;

    			if (Util.isValid(trigger) && trigger.equals("C")) {
            		int cmdBy = -1;
            		InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
            		if (rtScreen != null) {
            			cmdBy = rtScreen.getCmdBy();
            		}
        			if (cmdBy > -1) {
        				KnlUser user = knlService.getUser(cmdBy);
        				if (user != null) {
        					details = "by " + user.getShortName();
        				}
        			}
    			}
    		} else if (event.equals("UpdateError")) {
    			reportType = "E";
    			category = "R";
    			isRightEvent = true;
    			
    			if (Util.isValid(desc)) {
    				details = desc;
    			}
    		// 재생목록 오류는 이제 사용하지 않음. 여기서의 재생목록은 채널의 재생목록과는 다름
    		/*
    		} else if (event.equals("PlaylistError")) {
    			reportType = "W";
    			category = "G";
    			isRightEvent = true;

    			if (Util.isValid(desc)) {
    				details = desc;
    			}
    		*/
    		}
    		
    		if (isRightEvent) {
				if (Util.isValid(trigger)) {
					triggerType = trigger;
				}
        		revService.saveOrUpdate(new RevEventReport(screen, reportType, category, event, triggerType, details));
    		}
    		
    		return true;
    	} catch (Exception e) {
			logger.error("handleEventReport", e);
		}
    	
    	return false;
    }
    
    
    /**
	 * 부분 처리: 4: 버전 정보 보고(aka info API)
	 */
    private boolean handleVerReport(InvScreen screen, JSONObject infoObj) {
    	
    	try {
    		
    		String ver = Util.parseString(infoObj.getString("ver"));
    		
    		ver = ver.replace("_", " ");
    		
    		InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
    		if (rtScreen == null) {
    			invService.saveOrUpdate(new InvRTScreen(screen.getId()));
    			
    			rtScreen = invService.getRTScreenByScreenId(screen.getId());
    		}
    		
    		if (rtScreen != null && Util.isValid(ver)) {
    			if (ver.startsWith("keep.")) {
        			if (Util.isNotValid(rtScreen.getKeeperVer()) ||
            				(Util.isValid(rtScreen.getKeeperVer()) && !rtScreen.getKeeperVer().equals(ver))) {

        				rtScreen.setKeeperVer(ver);
        				invService.saveOrUpdate(rtScreen);
        			}
    			} else {
        			if (Util.isNotValid(rtScreen.getPlayerVer()) ||
            				(Util.isValid(rtScreen.getPlayerVer()) && !rtScreen.getPlayerVer().equals(ver))) {

        				rtScreen.setPlayerVer(ver);
        				invService.saveOrUpdate(rtScreen);
        			}
    			}
    		}
    		
    		return true;
    	} catch (Exception e) {
			logger.error("handleVerReport", e);
		}
    	
    	return false;
    }
    
    
    //
	// 부분 처리: 8: 재생목록 정보 보고
	//
    /*
    private boolean handlePlaylistReport(InvScreen screen, JSONObject infoObj, Date date) {
    	
    	try {
    		
    		int id = infoObj.getInt("playlist");
    		String name = Util.parseString(infoObj.getString("name"));
    		String lane = Util.parseString(infoObj.getString("lane"));
    		
    		boolean success = false;
    		
			if (Util.isNotValid(lane) || lane.length() > 1) {
				lane = "A";
			} else {
				lane = lane.toUpperCase();
			}
			if (("ABCD").indexOf(lane) == -1) {
				lane = "A";
			}
    		
    		
    		// 빠른 재생목록 보고 등록을 위해
    		RevObjTouch objTouch = revService.getObjTouch("S", screen.getId());
    		if (objTouch != null) {
    			objTouch.setDate8(date);
				
				objTouch.touchWho();
				revService.saveOrUpdate(objTouch);
    		}
    		//-
			
			
			//
			// 동일 RTScreen 자료에 접근하는 경우(한 기기에서 둘 이상의 영역에서 동시 보고)
			//
			String lockKey = "LockRTS" + screen.getId();
			
			// lock 설정
			SolUtil.lockProcess(lockKey);
			
    		
    		InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
    		if (rtScreen == null) {
    			invService.saveOrUpdate(new InvRTScreen(screen.getId()));
    			
    			rtScreen = invService.getRTScreenByScreenId(screen.getId());
    		}
    		
    		if (rtScreen != null) {

    			// 정상적인 경우: id > 0, 해당 재생목록 이름 == name
    			// id == 0, name == "": 재생목록이 없거나 재생 실패
        		if (id == 0) {
        			
        			Date now = new Date();
        			if (lane.equals("A")) {
        				rtScreen.setaPlaylist("");
        				rtScreen.setaPlaylistDate(now);
        			} else if (lane.equals("B")) {
        				rtScreen.setbPlaylist("");
        				rtScreen.setbPlaylistDate(now);
        			} else if (lane.equals("C")) {
        				rtScreen.setcPlaylist("");
        				rtScreen.setcPlaylistDate(now);
        			} else if (lane.equals("D")) {
        				rtScreen.setdPlaylist("");
        				rtScreen.setdPlaylistDate(now);
        			}
        			rtScreen.setWhoLastUpdateDate(now);
        			
        			invService.saveOrUpdate(rtScreen);
        			
        			success = true;
        		} else {
            		AdcPlaylist playlist = adcService.getPlaylist(id);
            		if (playlist != null && playlist.getName().equals(name)) {
            			
            			// 보고된 재생목록 번호와 그 이름이 일치하는 경우에만 변경
            			Date now = new Date();
            			if (lane.equals("A")) {
            				rtScreen.setaPlaylist(name);
            				rtScreen.setaPlaylistDate(now);
            			} else if (lane.equals("B")) {
            				rtScreen.setbPlaylist(name);
            				rtScreen.setbPlaylistDate(now);
            			} else if (lane.equals("C")) {
            				rtScreen.setcPlaylist(name);
            				rtScreen.setcPlaylistDate(now);
            			} else if (lane.equals("D")) {
            				rtScreen.setdPlaylist(name);
            				rtScreen.setdPlaylistDate(now);
            			}
            			rtScreen.setWhoLastUpdateDate(now);
            			
            			invService.saveOrUpdate(rtScreen);
            			
            			success = true;
            		}
        		}
    		}

	        // lock 해제
			SolUtil.unlockProcess(lockKey);
    		
    		return success;
    		

    	} catch (Exception e) {
			logger.error("handlePlaylistReport", e);
		}
    	
    	return false;
    }
    */
    
    
    /**
	 * 부분 처리: 16: 이동형 화면 GPS 위치 보고
	 */
    private boolean handleScreenGpsReport(InvScreen screen, JSONObject infoObj, Date date) {
    	
    	try {
    		
    		double lat = infoObj.getDouble("lat");
    		double lng = infoObj.getDouble("lng");
    		

    		// 국내 지역으로 한정
			if (lng < 124d || lng > 131d || lat < 33d || lat > 43d) {
				return false;
			}
    		
    		
			//
			// 등록 후, 3초 이내 접근 시에는 자료 등록 PASS
			//
			String lockKey = "LockGPS" + screen.getId();
			
			if (Util.isNotValid(SolUtil.getAutoExpVarValue(lockKey))) {
				
				// 3초 내 기록 사실 없는 경우
	    		InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
	    		if (rtScreen == null) {
	    			invService.saveOrUpdate(new InvRTScreen(screen.getId()));
	    			
	    			rtScreen = invService.getRTScreenByScreenId(screen.getId());
	    		}
	    		
	    		if (rtScreen != null) {

	    			Double scrLat = rtScreen.getGpsLat();
	    			Double scrLng = rtScreen.getGpsLng();
	    			if (scrLat == null || scrLng == null || 
	    					Util.distance(lat, lng, scrLat.doubleValue(), scrLng.doubleValue()) > 0.1d) {
	    				
	    				// 500m 범위 벗어남
	    				
	    				// 1) rtScreen 자료 update(time, lat, lng 변경)
	    			
	    				rtScreen.setGpsTime(date);
	    				rtScreen.setGpsLat(lat);
	    				rtScreen.setGpsLng(lng);
	    				
            			rtScreen.setWhoLastUpdateDate(date);
            			
            			invService.saveOrUpdate(rtScreen);
	    				

	    				// 2) invScrLoc 자료 insert
            			
            			invService.saveOrUpdate(new InvScrLoc(screen.getId(), date, lat, lng));
            			//logger.info("rcvCon 결과: insert");
	    				
	    			} else {
	    				// 500m 범위 내라면
	    				
	    				// 1) rtScreen 자료 update (time만 변경)
	    				
	    				rtScreen.setGpsTime(date);
	    				
            			rtScreen.setWhoLastUpdateDate(date);
            			
            			invService.saveOrUpdate(rtScreen);
	    				
            			
            			// 2) invScrLoc 자료 update (time2만 변경)
            			
            			InvScrLoc scrLoc = invService.getLastScrLocByScreenId(screen.getId());
            			if (scrLoc == null || !Util.isToday(scrLoc.getTime1()) || 
            					Util.distance(lat, lng, scrLoc.getLat(), scrLoc.getLng()) > 0.1d) {

            				invService.saveOrUpdate(new InvScrLoc(screen.getId(), date, lat, lng));
                			//logger.info("rcvCon 결과: update - insert");
            			} else {
            				
            				scrLoc.setTime2(date);
            				
            				invService.saveOrUpdate(scrLoc);
                			//logger.info("rcvCon 결과: update - update");
            			}
	    			}
	    		}
	    		
	    		GlobalInfo.AutoExpVarMap.put(lockKey, "Y");
	    		GlobalInfo.AutoExpVarTimeMap.put(lockKey,  Util.addSeconds(date, 3));
    		} else {
    			//logger.info("rcvCon 결과: pass");
			}

			
    		return true;

    	} catch (Exception e) {
			logger.error("handleScreenGpsReport", e);
		}
    	
    	return false;
    }
    
    
    /**
	 * 부분 처리: 32: 동기화 묶음 기기의 광고 노출 시작 보고
	 */
    private boolean handleSyncScreenAdStartTime(InvScreen screen, JSONObject infoObj, Date date) {
    	
    	try {
    		
    		String groupID = Util.parseString(infoObj.getString("gid"));
    		String ukAdID = Util.parseString(infoObj.getString("ad"));

			
    		if (Util.isValid(groupID) && Util.isValid(ukAdID)) {
    			String key = groupID + ukAdID;
    			
    			RevSyncPackMinMaxItem item = GlobalInfo.SyncPackReportGlobalMap.get(key);
    			if (item == null) {
    				InvSyncPackCompactItem spCompactItem = GlobalInfo.SyncPackMap.get(groupID);
    				if (spCompactItem != null) {
    					GlobalInfo.SyncPackReportGlobalMap.put(key, new RevSyncPackMinMaxItem(
    							screen.getMedium().getShortName(), groupID, ukAdID, spCompactItem.getCnt(), date.getTime(), false));
    				}
    			} else {
					
    				if (item.reportNext(date.getTime())) {
						
    					// 모든 묶음 구성원이 보고한 상태
    					
    					if (ukAdID.equals("0")) {
    						
    						GlobalInfo.SyncPackReportGlobalMap.remove(key);
    					} else {
        					
    						synchronized (this) {
    							
            					String grade = SolUtil.getSyncPackGrade(item.getMediumID(), item.getGroupID(), item.getDiff(), item.getCnt());
    	    					String gradeQ = SolUtil.getSyncPackGradeQueue(item.getGroupID());
    	    					String countQ = SolUtil.getSyncPackCntQueue(item.getGroupID());
            					
        						revService.saveOrUpdate(new RevSyncPackImp(new Date(), item.getGroupID(), "CMPL", grade, item.getDiff(), item.getCnt(), item.getMaxCnt(), 
        								item.getAdID(), gradeQ, countQ));
        						logger.info("** syncAD:[" + item.getGroupID() + " - " + grade + " - CMPL]  " + item.getDiff() + " - size=" + item.getCnt() +
        								", ad=" + item.getAdID() + ", gradeQ=" + gradeQ + ", cntQ=" + countQ);
        						
        						GlobalInfo.SyncPackReportGlobalMap.remove(key);
        						
        						
        						SolUtil.proceedSyncPackControlRules(item, grade, true, new Date());
        						InvSyncPack sp = invService.getSyncPackByShortName(item.getGroupID());
        						if (sp != null && Util.parseInt(item.getAdID()) > 0) {
        							
        				    		String adName = "";
        				    		String channelID = "";
        				    		String playlist = "";
        				    		Integer seq = null;
        				    		Integer seqDiff = null;
        				    		
        				    		
        				    		// 기존 RevRecPlaylist에서 RevChanAd로 변경됨
        				    		//
    								OrgChannel channel = orgService.getChannel(SolUtil.getFirstPriorityChannelByTypeObjId("P", sp.getId()));
    								if (channel != null) {
            							RevChanAd thisAd = revService.getLastChanAdByChannelIdSeq(channel.getId(), Util.parseInt(item.getAdID()));
            							if (thisAd != null) {
            								adName = thisAd.getAdName();
            								channelID = channel.getShortName();
            								seq = thisAd.getSeq();
            								
            								if (channel.getAppendMode().equals("P")) {
            									List<String> hints = Util.tokenizeValidStr(thisAd.getHint(), "_");
            									if (hints.size() == 2) {
            										AdcPlaylist pl = adcService.getPlaylist(Util.parseInt(hints.get(0)));
            										if (pl != null) {
            											playlist = pl.getName();
            										}
            									}
            								} else if (channel.getAppendMode().equals("A")) {
            									playlist = "[자율 광고선택]";
            								}
            								
            								RevChanAdRpt chanAdRpt = new RevChanAdRpt(thisAd, "P", sp.getId(), new Date(item.getMin()), item.getCnt());
            								seqDiff = chanAdRpt.getDiff();
            								
            								revService.saveOrUpdate(chanAdRpt);
            							}
    								}
    								
    								
        							// 광고 시작 시간 기록
        				    		InvRTSyncPack rtSyncPack = invService.getRTSyncPackBySyncPackId(sp.getId());
        				    		if (rtSyncPack == null) {
        				    			invService.saveOrUpdate(new InvRTSyncPack(sp.getId()));
        				    			
        				    			rtSyncPack = invService.getRTSyncPackBySyncPackId(sp.getId());
        				    		}
        				    		if (rtSyncPack != null) {
        				    			Date now = new Date();

        				    			rtSyncPack.setLastAd(adName);
        				    			rtSyncPack.setLastAdBeginDate(new Date(item.getMin()));
        				    			rtSyncPack.setDiff(item.getDiff());
        				    			rtSyncPack.setGradeQueue(gradeQ);
        				    			rtSyncPack.setCountQueue(countQ);
        				    			rtSyncPack.setChannel(channelID);
        				    			rtSyncPack.setPlaylist(playlist);
        				    			rtSyncPack.setSeq(seq);
        				    			rtSyncPack.setSeqDiff(seqDiff);
        				    			
        				    			rtSyncPack.setWhoLastUpdateDate(now);
        				    			
        				    			invService.saveOrUpdate(rtSyncPack);
        				    		}
        						}
        						
    						}
    					}
    				}
    			}
    			
    			return true;
    		}
    	} catch (Exception e) {
			logger.error("handleSyncScreenAdStartTime", e);
		}
    	
    	return false;
    }
}
