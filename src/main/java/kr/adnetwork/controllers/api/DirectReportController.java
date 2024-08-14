package kr.adnetwork.controllers.api;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.rev.RevAdSelCache;
import kr.adnetwork.models.rev.RevAdSelect;
import kr.adnetwork.models.rev.RevPlayHist;
import kr.adnetwork.models.rev.RevScrHourlyPlay;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import kr.adnetwork.viewmodels.rev.RevObjEventTimeItem;
import kr.adnetwork.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONObject;

/**
 * 직접(uuid없이) 결과 보고 API 컨트롤러
 */
@Controller("api-direct-report-controller")
@RequestMapping(value="")
public class DirectReportController {
	
	private static final Logger logger = LoggerFactory.getLogger(DirectReportController.class);


	//
	// 직접 결과 보고 API: 읽기 / 쓰기 가능(test 파라미터 처리 완료)
	//
	//   - playerlist 방식의 결과 보고
	//   - ad 방식의 결과 보고 중 지연 결과 보고. 기타 보고(성공/실패)는 일반 보고로 진행
	//
	
    @Autowired 
    private RevService revService;

    @Autowired 
    private InvService invService;

    
    /**
	 * 직접 결과 보고 API
	 */
    @RequestMapping(value = {"/v1/report/direct/{displayID}/{acID}"}, method = RequestMethod.GET)
    public void processApiDirectReport(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    	
    	// acID - adCreative ID
    	int adCreatId = Util.parseInt(pathMap.get("acID"));

    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	long start = Util.parseLong(paramMap.get("start"), -1);
    	long end = Util.parseLong(paramMap.get("end"), -1);

    	// 여러 출력 영역에 대한 lane처리
    	String lane = Util.parseString(pathMap.get("lane"));
    	if (Util.isNotValid(lane) || lane.length() > 1) {
    		lane = "A";
    	} else {
    		lane = lane.toUpperCase();
    	}
    	
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	
    	AdcAdCreative adCreative = null;
    	
    	Date startDt = null;
    	Date endDt = null;
    	Integer dur = null;
    	
    	
    		
    	//
   		// adCreatId == -19이면, 광고 노출 통계는 포함시키지 않고
   		//   - 플레이어의 상태라인 처리
   		//   - 정상 응답을 통한 명령 접수
    	//
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID) || (adCreatId != -19 && adCreatId < 1) || start < 1 || end < 1) {
    		
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
    		} else if (adCreatId == -19) {
    			
    	    	if (start > 0) {
    	    		startDt = new Date(start);
    	    	}
    	    	if (end > 0) {
    	    		endDt = new Date(end);
    	    	}
    	    	dur = (int)(end - start);

    	    	// 시작일시가 현재 -7일 이내여야 함
    	    	if (dur < 1000 || dur > 3600000 || start > end || Util.addDays(new Date(), -7).after(startDt)) {
    	    		startDt = null;
    	    		endDt = null;
    	    		dur = null;
    	    		
        			statusCode = -14;
        			message = "InvalidTime";
        			localMessage = "시간 정보가 유효하지 않습니다.";
    	    	}
    			
    		} else {
    	    	if (start > 0) {
    	    		startDt = new Date(start);
    	    	}
    	    	if (end > 0) {
    	    		endDt = new Date(end);
    	    	}
    	    	dur = (int)(end - start);

    	    	// 시작일시가 현재 -7일 이내여야 함
    	    	if (dur < 1000 || dur > 3600000 || start > end || Util.addDays(new Date(), -7).after(startDt)) {
    	    		startDt = null;
    	    		endDt = null;
    	    		dur = null;
    	    		
        			statusCode = -14;
        			message = "InvalidTime";
        			localMessage = "시간 정보가 유효하지 않습니다.";
        			/*
    	    	} else if (revService.getPlayHistCountByScreenIdStartDate(screen.getId(), startDt) > 0) {
    	    		startDt = null;
    	    		endDt = null;
    	    		dur = null;
    	    		
        			statusCode = -13;
        			message = "TimeCollision";
        			localMessage = "등록 시도한 시간과 충돌하는 자료가 존재합니다.";
        			*/
    	    	} else {
        			
    	    		HashMap<String, AdcAdCreative> candiMap = null;
    	    		
    	    		String key = GlobalInfo.AdCandiAdCreatVerKey.get("M" + screen.getMedium().getId());
    	    		if (Util.isValid(key)) {
    	    			candiMap = GlobalInfo.AdRealAdCreatMap.get(key);
    	    		}

    	    		if (candiMap != null) {
    	    			// 직접 보고 시 이전의 광고 선택 문자열 대신 candiMap으로부터 바로 선택함
    	    			// 묶음 광고에서 대표 광고가 아닌 경우 선택되지 않는 문제 해결 목적
    	    			//
    	    			/*
    	    			// 해당 매체의 모든 광고(기본 + 게시유형 모두)를 포함하고 있고,
    	    			// 누락이 발생된 동일 광고 다른 광고 소재에도 부합되도록 변경됨
		    			String orderStr = SolUtil.getAllAdSeqList(GlobalInfo.AdRealAdCreatIdsMap.get("M" + screen.getMedium().getId()));
			    		List<String> seqList = Util.tokenizeValidStr(orderStr);
			    		for(String s : seqList) {
			    			AdcAdCreative adCreat = candiMap.get("AC" + s);
			    			if (adCreat.getId() == adCreatId) {
			    				adCreative = adCreat;
			    				break;
			    			}
			    		}
			    		*/
    	    			adCreative = candiMap.get("AC" + adCreatId);
    	    		}
    	    		
	        		if (adCreative == null) {
	        			statusCode = -15;
	        			message = "AdNotFound";
	        			localMessage = "광고 정보를 확인할 수 없습니다.";
	        		}
    	    	}
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);
    	

    	if (statusCode >= 0 && screen != null) {
        	
        	
        	boolean opResult = true;
        	
        	try {
        		
            	if (adCreatId == -19) {
            		
            		String checkKey = "CheckSHP" + screen.getId() + lane + Util.toSimpleString(startDt, "mmssSSS");
            		boolean alreadyReg = Util.isValid(SolUtil.getAutoExpVarValue(checkKey));
            		
                	logger.info("[API] direct report: " + screen.getName() + ", " + (!testMode && !alreadyReg) + " / code -19");
            		
            		if (!testMode && !alreadyReg) {
            			
                		Date now = new Date();
                		
            			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
            			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 13));
    	    			
    	        		
    	        		// 상태 라인 처리 위해 공용리스트에 추가
    		    		if (startDt != null && endDt != null) {
    		    			// 광고 선택일시(없음)
    		    			
    		    			// 광고 재생기간(분단위 시간)
    		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
    		    			for(Date d : playMins) {
    			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), d));
    		    			}
    		    		}
    			        
    			        // 동일 노출에 대한 중복 등록을 방지하기 위해 기존 등록여부 정보 등록
    					GlobalInfo.AutoExpVarMap.put(checkKey, "Y");
    					GlobalInfo.AutoExpVarTimeMap.put(checkKey,  Util.addMinutes(new Date(), 10));
            		}
            		
            	} else {
            		
                	RevAdSelect adSelect = new RevAdSelect(screen, adCreative);

                	// 동일 노출에 대한 중복 등록을 방지하기 위해 기존 등록여부 확인
                	//   - 특정 화면 + lane + 시작시간
                	//   - 유효 시간은 10분으로. DB 작업이 아닌 메모리 작업으로만 처리
                	//     (10분 이상의 간격으로의 등록은 기술적으로는 가능하나, 플레이어에서의 처리 방식으로는 불가능하게)
                	//   - 이러한 중복 등록이 동일한 WAS를 통해 전달되기 때문에 적용된 로직
                	String checkKey = "CheckSHP" + adSelect.getScreen().getId() + lane + Util.toSimpleString(startDt, "mmssSSS");
                	boolean alreadyReg = Util.isValid(SolUtil.getAutoExpVarValue(checkKey));
                	
                	// 동기화 그룹의 화면이 아니거나, 동기화 그룹 화면이면서 1번일 경우만
                	boolean isLoggableScreen = GlobalInfo.LogProhibitedScreenIds == null || 
                			!GlobalInfo.LogProhibitedScreenIds.contains(screen.getId());

                	logger.info("[API] direct report: " + screen.getName() + ", " + (!testMode && !alreadyReg) + " / " + isLoggableScreen);

                	
                	if (!testMode && !alreadyReg) {
            			
                		Date now = new Date();
                		
                		if (isLoggableScreen) {
                			
                    		// 광고 선택일자를 재생의 시작일시로 변경
                    		adSelect.setSelectDate(startDt);
            		    	adSelect.setReportDate(now);
            		    	
            		    	adSelect.setPlayBeginDate(startDt);
            		    	adSelect.setPlayEndDate(endDt);
            		    	adSelect.setDuration(dur);
            		    	
            		    	adSelect.setResult(true);
                    		
                    		revService.saveOrUpdate(adSelect);

            	    		
                        	RevAdSelCache adSelCache = revService.getLastAdSelCacheByScreenIdAdCreativeId(
                        			screen.getId(), adCreative.getId());
                        	if (adSelCache == null) {
                        		// 재생시간이 밀리초 단위이기 때문에, 초 단위로 변경
                        		revService.saveOrUpdate(new RevAdSelCache(adSelect, adSelect.getDuration() / 1000));
                        	} else if (adSelCache.getSelectDate().before(adSelect.getSelectDate())) {
                        		//adSelCache.setSelectDate(adSelect.getSelectDate());
                        		adSelCache.setSelectDate(Util.addSeconds(
                        				adSelect.getSelectDate(), adSelect.getDuration() / 1000));
                        		
                        		revService.saveOrUpdate(adSelCache);
                        	}
        		    		
        		    		
            	    		// 집계 테이블에 바로 등록
            	    		//   기준 시간은 처음 이 자료가 생성된 시간
            	    		//if (adCreative != null) {
            	    			
        	    			//
        	    			// 동일 ScrHourlyPlay 자료에 접근하는 경우(한 화면 다른 영역에서 동일 광고 동시 노출, 동시 보고)
        	    			//
        	    			String lockKey = "LockSHP" + adSelect.getScreen().getId() + "C" + adSelect.getAdCreative().getId() +
        	    					Util.toSimpleString(Util.removeTimeOfDate(adSelect.getSelectDate()), "MMdd");
        	    			
        	    			// lock 설정
        	    			SolUtil.lockProcess(lockKey);
        	    			
        		    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(adSelect.getScreen(), 
        		    				adSelect.getAdCreative(), Util.removeTimeOfDate(adSelect.getSelectDate()));
        		    		if (hourlyPlay == null) {
        		    			hourlyPlay = new RevScrHourlyPlay(adSelect.getScreen(), adSelect.getAdCreative(), 
        		    					Util.removeTimeOfDate(adSelect.getSelectDate()));
        		    			
        						// 광고/광고 소재의 비중은 내정값 1로 설정. 시간별로 계산 시 정확한 값 산출
        		    			
        		    			// 시간이 많이 소요될 수도 있음. 확인 필요
        		    			hourlyPlay.setCurrHourGoal(SolUtil.getScrAdHourlyGoalValue(hourlyPlay, 1f));
        		    		}

        		    		
        	    			// 시간당 화면/광고 재생 계산
        	    			GregorianCalendar calendar = new GregorianCalendar();
        	    			
        		    		calendar.setTime(adSelect.getSelectDate());
        			        
        			        switch (calendar.get(Calendar.HOUR_OF_DAY)) {
        			        case 0: hourlyPlay.setCnt00(hourlyPlay.getCnt00() + 1); break;
        			        case 1: hourlyPlay.setCnt01(hourlyPlay.getCnt01() + 1); break;
        			        case 2: hourlyPlay.setCnt02(hourlyPlay.getCnt02() + 1); break;
        			        case 3: hourlyPlay.setCnt03(hourlyPlay.getCnt03() + 1); break;
        			        case 4: hourlyPlay.setCnt04(hourlyPlay.getCnt04() + 1); break;
        			        case 5: hourlyPlay.setCnt05(hourlyPlay.getCnt05() + 1); break;
        			        case 6: hourlyPlay.setCnt06(hourlyPlay.getCnt06() + 1); break;
        			        case 7: hourlyPlay.setCnt07(hourlyPlay.getCnt07() + 1); break;
        			        case 8: hourlyPlay.setCnt08(hourlyPlay.getCnt08() + 1); break;
        			        case 9: hourlyPlay.setCnt09(hourlyPlay.getCnt09() + 1); break;
        			        case 10: hourlyPlay.setCnt10(hourlyPlay.getCnt10() + 1); break;
        			        case 11: hourlyPlay.setCnt11(hourlyPlay.getCnt11() + 1); break;
        			        case 12: hourlyPlay.setCnt12(hourlyPlay.getCnt12() + 1); break;
        			        case 13: hourlyPlay.setCnt13(hourlyPlay.getCnt13() + 1); break;
        			        case 14: hourlyPlay.setCnt14(hourlyPlay.getCnt14() + 1); break;
        			        case 15: hourlyPlay.setCnt15(hourlyPlay.getCnt15() + 1); break;
        			        case 16: hourlyPlay.setCnt16(hourlyPlay.getCnt16() + 1); break;
        			        case 17: hourlyPlay.setCnt17(hourlyPlay.getCnt17() + 1); break;
        			        case 18: hourlyPlay.setCnt18(hourlyPlay.getCnt18() + 1); break;
        			        case 19: hourlyPlay.setCnt19(hourlyPlay.getCnt19() + 1); break;
        			        case 20: hourlyPlay.setCnt20(hourlyPlay.getCnt20() + 1); break;
        			        case 21: hourlyPlay.setCnt21(hourlyPlay.getCnt21() + 1); break;
        			        case 22: hourlyPlay.setCnt22(hourlyPlay.getCnt22() + 1); break;
        			        case 23: hourlyPlay.setCnt23(hourlyPlay.getCnt23() + 1); break;
        			        }
        			        
        			        hourlyPlay.calcTotal();
        			        hourlyPlay.touchWho();
        			        
        			        revService.saveOrUpdate(hourlyPlay);

        			        // lock 해제
        	    			SolUtil.unlockProcess(lockKey);
        			        
        			        
        			        // 재생 기록 생성
        			        revService.saveOrUpdate(new RevPlayHist(adSelect));
        			        
        			        
        			        // 광고 선택 삭제
        			        revService.deleteAdSelect(adSelect);
        			        
                		}  // if (isLoggableScreen)

                		
            			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
            			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 13));

    	    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료
    	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));
    	    			
    	        		
    	        		// 상태 라인 처리 위해 공용리스트에 추가
    		    		if (startDt != null && endDt != null) {
    		    			// 광고 선택일시(없음)
    		    			
    		    			// 광고 재생기간(분단위 시간)
    		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
    		    			for(Date d : playMins) {
    			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), d));
    		    			}
    		    		}
    			        
    					
    			        // 동일 노출에 대한 중복 등록을 방지하기 위해 기존 등록여부 정보 등록
    					GlobalInfo.AutoExpVarMap.put(checkKey, "Y");
    					GlobalInfo.AutoExpVarTimeMap.put(checkKey,  Util.addMinutes(new Date(), 10));
                	}
                	
            	}
            	
	    	} catch (Exception e) {
	    		logger.error("Direct Report API - process", e);
	    		opResult = false;
        	}
	    	
			obj.put("success", opResult);
			
			
			// 명령 포함 여부
			boolean hasCommand = false;
			InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
			if (rtScreen != null) {
				hasCommand = Util.isValid(rtScreen.getNextCmd()) && !rtScreen.isCmdFailed();
			}
			obj.put("command", hasCommand);
			// / 명령 포함 여부
    	}

		Util.toJson(response, obj);
    }
}
