package net.doohad.controllers.api;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import net.doohad.models.inv.InvRTScreen;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.rev.RevAdSelCache;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.service.InvService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONObject;

/**
 * 결과 보고 API 컨트롤러
 */
@Controller("api-report-controller")
@RequestMapping(value="")
public class ReportController {
	
	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);


	//
	// 결과 보고 API: 쓰기만 가능(test 파라미터 처리 완료)
	//
	
    @Autowired 
    private RevService revService;

    @Autowired 
    private InvService invService;

    
    //
    // 상태 코드 정리:
    //
    //		-1		WrongApiKey					등록되지 않은 API key가 전달되었습니다.
    //		-2		WrongDisplayID				등록되지 않은 디스플레이 ID가 전달되었습니다.
    //		-3		WrongParams					필수 인자의 값이 전달되지 않았습니다.
    //		-4		EffectiveDateExpired		유효 기간의 범위에 포함되지 않습니다.
    //		-5		NotActive					정상 서비스 중이 아닙니다.
    //		-6		NotAdServerAvailable		광고 서비스로 이용할 수 없습니다.
    //
    //		-11		WrongAttemptID				등록되지 않은 재생 시도 ID가 전달되었습니다.
    //		-12		AlreadyReportedAttemptID	이미 완료 처리된 보고입니다.
    //
    
    /**
	 * 성공 결과 보고 API
	 */
    @RequestMapping(value = {"/v1/report/success/{attemptID}"}, method = RequestMethod.GET)
    public void processApiReportSuccess(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	process(Util.parseString(pathMap.get("attemptID")), paramMap.get("start"), paramMap.get("end"),
    			paramMap.get("duration"), true, testMode, response);
    }

    
    /**
	 * 오류 결과 보고 API
	 */
    @RequestMapping(value = {"/v1/report/error/{attemptID}"}, method = RequestMethod.GET)
    public void processApiReportError(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	process(Util.parseString(pathMap.get("attemptID")), paramMap.get("start"), paramMap.get("end"),
    			paramMap.get("duration"), false, testMode, response);
    }
    
    /**
	 * 성공 결과 보고 API - 게시 유형에 따른 커스텀 프로세스 진행
	 */
    @RequestMapping(value = {"/v1/report/{displayID}/success/{attemptID}"}, method = RequestMethod.GET)
    public void processCustomApiReportSuccess(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	processCustom1(Util.parseString(pathMap.get("attemptID")), Util.parseString(pathMap.get("displayID")), 
    			paramMap.get("start"), paramMap.get("end"),
    			paramMap.get("duration"), true, testMode, response);
    }

    
    /**
	 * 보고 처리
	 */
    private void process(String attemptID, String startS, String endS, String durS, boolean success, boolean testMode,
    		HttpServletResponse response) {
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	RevAdSelect adSelect = null;
    	
    	Date now = new Date();

    	
    	// 시작시간 및 종료시간 정보를 이미 완료된 보고(code -12)에서 사용하기 위해 앞으로 당김
    	
    	// 시작시간, 종료시간, 재생시간 정보는 모두 옵셔널 값
    	// 유효 조건 위반일 경우 모든 값을 null로 처리
    	//
    	// 유효 조건:
    	//   1) 재생시간: 1s <= dur <= 1hr
    	//   2) 종료시간이 시작시간 이후
    	//
    	long start = Util.parseLong(startS, -1l);
    	long end = Util.parseLong(endS, -1l);
    	
    	Date startDt = null;
    	Date endDt = null;
    	Integer dur = null;

    	if (start > 0) {
    		startDt = new Date(start);
    	}
    	if (end > 0) {
    		endDt = new Date(end);
    	}
    	dur = (int)(end - start);
    	if (dur < 1000 || dur > 3600000 || start > end) {
    		startDt = null;
    		endDt = null;
    		dur = null;
    	}
    	
    	// 시작시간, 종료시간에 대한 처리 완료
    	// 재생시간 값이 유효하면 패스, 아니면 재생시간 값만 처리
    	if (dur == null) {
    		int duration = Util.parseInt(durS, -1);
    		if (duration >= 1000 && duration <= 3600000) {
    			dur = duration;
    		}
    	}

    	
    	// 재생 시도 ID == adSelect UUID
    	if (Util.isNotValid(attemptID)) {
    		statusCode = -11;
    		message = "WrongAttemptID";
    		localMessage = "등록되지 않은 재생 시도 ID가 전달되었습니다.";
    	} else {
    		
    		adSelect = revService.getAdSelect(UUID.fromString(attemptID));
    		if (adSelect == null) {
        		statusCode = -11;
        		message = "WrongAttemptID";
        		localMessage = "등록되지 않은 재생 시도 ID가 전달되었습니다.";
    		} else if (adSelect.getReportDate() != null) {
        		statusCode = -12;
        		message = "AlreadyReportedAttemptID";
        		localMessage = "이미 완료 처리된 보고입니다.";


        		if (!testMode) {
	    			
	    			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getScreen().getId(), now, 13));

	    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료
	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));
	    			
	        		
	        		// 상태 라인 처리 위해 공용리스트에 추가
		    		if (startDt != null && endDt != null) {
		    			// 광고 선택일시(없음)
		    			
		    			// 광고 재생기간(분단위 시간)
		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
		    			for(Date d : playMins) {
			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(adSelect.getScreen().getId(), d));
		    			}
		    		}
        		}
    		}
    	}
    	
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		if (statusCode == 0 && adSelect != null) {

	    	boolean opResult = true;
	    	try {

	    		int screenId = adSelect.getScreen().getId();
            	
            	// 동기화 그룹의 화면이 아니거나, 동기화 그룹 화면이면서 1번일 경우만
            	boolean isLoggableScreen = GlobalInfo.LogProhibitedScreenIds == null || 
            			!GlobalInfo.LogProhibitedScreenIds.contains(screenId);
	    		
            	logger.info("[API] report(no viewtype): " + attemptID + ", " + (!testMode) + " / " + isLoggableScreen);
		    	
		    	
	    		if (!testMode) {
	    			
	    			if (isLoggableScreen) {
		    			
				    	adSelect.setReportDate(now);
				    	
				    	adSelect.setPlayBeginDate(startDt);
				    	adSelect.setPlayEndDate(endDt);
				    	adSelect.setDuration(dur);
				    	
				    	adSelect.setResult(success);
				    	
			    		revService.saveOrUpdate(adSelect);


		    			if (success) {
				    		
				    		// 왜 report에서 이 항목이 기존에 누락되었을까?
				    		// side effect 검증 필요
				    		if (adSelect.getAdCreative() != null) {
				    			
		                    	RevAdSelCache adSelCache = revService.getLastAdSelCacheByScreenIdAdCreativeId(
		                    			screenId, adSelect.getAdCreative().getId());
		                    	if (adSelCache == null) {
		                    		// 재생시간이 밀리초 단위이기 때문에, 초 단위로 변경
		                    		revService.saveOrUpdate(new RevAdSelCache(adSelect, adSelect.getDuration() / 1000));
		                    	} else if (adSelCache.getSelectDate().before(adSelect.getSelectDate())) {
		                    		//adSelCache.setSelectDate(adSelect.getSelectDate());
		                    		adSelCache.setSelectDate(Util.addSeconds(
		                    				adSelect.getSelectDate(), adSelect.getDuration() / 1000));
		                    		
		                    		revService.saveOrUpdate(adSelCache);
		                    	}
				    		}

				    		
				    		// 집계 테이블에 바로 등록
				    		//   기준 시간은 처음 이 자료가 생성된 시간
				    		if (adSelect.getAdCreative() != null) {
				    			
				    			// 시간당 화면/광고 재생 계산
				    			GregorianCalendar calendar = new GregorianCalendar();
				    			
					    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(adSelect.getScreen(), 
					    				adSelect.getAdCreative(), Util.removeTimeOfDate(adSelect.getSelectDate()));
					    		if (hourlyPlay == null) {
					    			hourlyPlay = new RevScrHourlyPlay(adSelect.getScreen(), adSelect.getAdCreative(), 
					    					Util.removeTimeOfDate(adSelect.getSelectDate()));
					    			
									// 광고/광고 소재의 비중은 내정값 1로 설정. 시간별로 계산 시 정확한 값 산출
									
					    			// 시간이 많이 소요될 수도 있음. 확인 필요
					    			hourlyPlay.setCurrHourGoal(SolUtil.getScrAdHourlyGoalValue(hourlyPlay, 1f));
					    		}
					    		
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
						        
						        
						        // 재생 기록 생성
						        revService.saveOrUpdate(new RevPlayHist(adSelect));
						        
						        // 광고 선택 삭제
						        revService.deleteAdSelect(adSelect);
				    		}
		    			}
	    			}
			    	
			    	if (success) {
		    			
		    			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
		    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screenId, now, 13));

		    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료
		    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));
		    			
		        		
		        		// 상태 라인 처리 위해 공용리스트에 추가
			    		if (startDt != null && endDt != null) {
			    			// 광고 선택일시(없음)
			    			
			    			// 광고 재생기간(분단위 시간)
			    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
			    			for(Date d : playMins) {
				        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screenId, d));
			    			}
			    		}
			    	}
	    		}
	    	} catch (Exception e) {
	    		logger.error("Report API - process", e);
	    		opResult = false;
	    	}
	    	
			obj.put("success", opResult);
			
			
			// 명령 포함 여부
			boolean hasCommand = false;
			InvRTScreen rtScreen = invService.getRTScreenByScreenId(adSelect.getScreen().getId());
			if (rtScreen != null) {
				hasCommand = Util.isValid(rtScreen.getNextCmd());
			}
			obj.put("command", hasCommand);
			// / 명령 포함 여부
    	}
		
		Util.toJson(response, obj);
    }

    
    /**
	 * 보고 처리 - 게시 유형에 따른 커스텀 프로세스 진행
	 */
    private void processCustom1(String attemptID, String displayID, String startS, String endS, String durS, 
    		boolean success, boolean testMode, HttpServletResponse response) {
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	RevAdSelect adSelect = null;
    	
    	InvScreen screen = null;
    	
    	
    	Date now = new Date();

    	
    	// 시작시간, 종료시간, 재생시간 정보는 모두 옵셔널 값
    	// 유효 조건 위반일 경우 모든 값을 null로 처리
    	//
    	// 유효 조건:
    	//   1) 재생시간: 1s <= dur <= 1hr
    	//   2) 종료시간이 시작시간 이후
    	//
    	long start = Util.parseLong(startS, -1l);
    	long end = Util.parseLong(endS, -1l);
    	
    	Date startDt = null;
    	Date endDt = null;
    	Integer dur = null;

    	if (start > 0) {
    		startDt = new Date(start);
    	}
    	if (end > 0) {
    		endDt = new Date(end);
    	}
    	dur = (int)(end - start);
    	if (dur < 1000 || dur > 3600000 || start > end) {
    		startDt = null;
    		endDt = null;
    		dur = null;
    	}
    	
    	// 시작시간, 종료시간에 대한 처리 완료
    	// 재생시간 값이 유효하면 패스, 아니면 재생시간 값만 처리
    	if (dur == null) {
    		int duration = Util.parseInt(durS, -1);
    		if (duration >= 1000 && duration <= 3600000) {
    			dur = duration;
    		}
    	}
    	

    	
    	// 재생 시도 ID == adSelect UUID
    	if (Util.isNotValid(attemptID) || Util.isNotValid(displayID)) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	} else {
    		
    		try {
    			adSelect = revService.getAdSelect(UUID.fromString(attemptID));
    		} catch (Exception e) {
	    		logger.error("Report API - processCustom1", e);
    		}
    		
    		if (adSelect == null) {
        		
        		RevPlayHist playHist = revService.getPlayHistByUuid(attemptID);
        		if (playHist != null) {
        			InvScreen reqScreen = invService.getScreenByMediumIdShortName(playHist.getMediumId(), displayID);
        			if (reqScreen != null && reqScreen.getMedium().getId() == playHist.getMediumId() && 
        					GlobalInfo.LogProhibitedScreenIds != null && GlobalInfo.LogProhibitedScreenIds.contains(reqScreen.getId())) {
    	    			
    	    			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
    	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(reqScreen.getId(), now, 13));

    	    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료 (1번에서 진행할 것이기 때문에 생략)
    	    			//GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));
    	        		
    	        		// 상태 라인 처리 위해 공용리스트에 추가
    		    		if (startDt != null && endDt != null) {
    		    			// 광고 선택일시(없음)
    		    			
    		    			// 광고 재생기간(분단위 시간)
    		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
    		    			for(Date d : playMins) {
    			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(reqScreen.getId(), d));
    		    			}
    		    		}
        			}
        		}
        		
        		statusCode = -11;
        		message = "WrongAttemptID";
        		localMessage = "등록되지 않은 재생 시도 ID가 전달되었습니다.";
        		
    		} else if (adSelect.getReportDate() != null) {
        		statusCode = -12;
        		message = "AlreadyReportedAttemptID";
        		localMessage = "이미 완료 처리된 보고입니다.";


        		if (!testMode) {
	    			
	    			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getScreen().getId(), now, 13));

	    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료
	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));
	    			
	        		
	        		// 상태 라인 처리 위해 공용리스트에 추가
		    		if (startDt != null && endDt != null) {
		    			// 광고 선택일시(없음)
		    			
		    			// 광고 재생기간(분단위 시간)
		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
		    			for(Date d : playMins) {
			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(adSelect.getScreen().getId(), d));
		    			}
		    		}
        		}
    		} else {
    			
        		screen = invService.getScreen(adSelect.getScreen().getId());
        		boolean goAhead = false;
        		
        		if (screen == null) {
        			
        		} else if (!screen.getShortName().equals(displayID)) {
        			
        			//
        			// 동기화 묶음 그룹에서 아직 1번이 보고하지 않은 상태
        			//
        			
        			// adSelect는 등록되어 있으나, 현재 화면 이름으로 등록된 것은 아니다.
        			// 요청된 화면ID가 동기화 묶음 그룹의 secondary라면 방송완료 및 상태 라인 처리 진행
        			InvScreen reqScreen = invService.getScreenByMediumIdShortName(screen.getMedium().getId(), displayID);
        			if (reqScreen != null && GlobalInfo.LogProhibitedScreenIds != null && GlobalInfo.LogProhibitedScreenIds.contains(reqScreen.getId())) {
                		
                		if (!testMode) {
        	    			
        	    			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
        	    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(reqScreen.getId(), now, 13));

        	    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료 (1번에서 진행할 것이기 때문에 생략)
        	    			//GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));
        	        		
        	        		// 상태 라인 처리 위해 공용리스트에 추가
        		    		if (startDt != null && endDt != null) {
        		    			// 광고 선택일시(없음)
        		    			
        		    			// 광고 재생기간(분단위 시간)
        		    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
        		    			for(Date d : playMins) {
        			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(reqScreen.getId(), d));
        		    			}
        		    		}
                		}
        			}
        		} else {
        			goAhead = true;
        		}
        		
        		if (!goAhead) {
        			
        			statusCode = -2;
            		message = "WrongDisplayID";
            		localMessage = "등록되지 않은 디스플레이 ID가 전달되었습니다.";
        		}
    		}
    	}
    	
    	if (statusCode == 0 && screen != null) {
    		
	    	if (startDt == null || endDt == null || dur == null) {

    			statusCode = -3;
        		message = "WrongParams";
        		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
	    	}
    	}
    	
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		if (statusCode == 0 && screen != null && adSelect != null) {

    		Date selDate = null;
    		
    		boolean opResult = true;
			try {
            	
            	// 동기화 그룹의 화면이 아니거나, 동기화 그룹 화면이면서 1번일 경우만
            	boolean isLoggableScreen = GlobalInfo.LogProhibitedScreenIds == null || 
            			!GlobalInfo.LogProhibitedScreenIds.contains(screen.getId());
	    		
            	logger.info("[API] report: " + attemptID + ", " + displayID + ", " + (!testMode) + " / " + isLoggableScreen);

            	
	    		if (!testMode) {
	    			
	    			if (isLoggableScreen) {
	    				
						if (adSelect != null) {
					        
					        // 상태 라인 처리를 위해 저장
					        selDate = adSelect.getSelectDate();

					    	adSelect.setPlayBeginDate(startDt);
					    	adSelect.setPlayEndDate(endDt);
					    	adSelect.setDuration(dur);

					    	adSelect.setReportDate(now);
					    	adSelect.setResult(success);
					    	
					    	revService.saveOrUpdate(adSelect);
						}
						
						if (success) {
				    		
				    		// 왜 report에서 이 항목이 기존에 누락되었을까?
				    		// side effect 검증 필요
				    		if (adSelect.getAdCreative() != null) {
				    			
		                    	RevAdSelCache adSelCache = revService.getLastAdSelCacheByScreenIdAdCreativeId(
		                    			screen.getId(), adSelect.getAdCreative().getId());
		                    	if (adSelCache == null) {
		                    		// 재생시간이 밀리초 단위이기 때문에, 초 단위로 변경
		                    		revService.saveOrUpdate(new RevAdSelCache(adSelect, adSelect.getDuration() / 1000));
		                    	} else if (adSelCache.getSelectDate().before(adSelect.getSelectDate())) {
		                    		//adSelCache.setSelectDate(adSelect.getSelectDate());
		                    		adSelCache.setSelectDate(Util.addSeconds(
		                    				adSelect.getSelectDate(), adSelect.getDuration() / 1000));
		                    		
		                    		revService.saveOrUpdate(adSelCache);
		                    	}
				    		}

				    		
				    		// 집계 테이블에 바로 등록
				    		//   기준 시간은 처음 이 자료가 생성된 시간
				    		if (adSelect.getAdCreative() != null) {
				    			
				    			// 시간당 화면/광고 재생 계산
				    			GregorianCalendar calendar = new GregorianCalendar();
				    			
					    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(adSelect.getScreen(), 
					    				adSelect.getAdCreative(), Util.removeTimeOfDate(adSelect.getSelectDate()));
					    		if (hourlyPlay == null) {
					    			hourlyPlay = new RevScrHourlyPlay(adSelect.getScreen(), adSelect.getAdCreative(), 
					    					Util.removeTimeOfDate(adSelect.getSelectDate()));
					    			
									// 광고/광고 소재의 비중은 내정값 1로 설정. 시간별로 계산 시 정확한 값 산출
									
					    			// 시간이 많이 소요될 수도 있음. 확인 필요
					    			hourlyPlay.setCurrHourGoal(SolUtil.getScrAdHourlyGoalValue(hourlyPlay, 1f));
					    		}
					    		
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
						        
						        
						        // 재생 기록 생성
						        revService.saveOrUpdate(new RevPlayHist(adSelect));
						        
						        // 광고 선택 삭제
						        revService.deleteAdSelect(adSelect);
				    		}
						}
	    			}

	    			if (success) {
						
		    			// 개체 이벤트 처리: 화면(S)의 방송완료 보고(report, directReport)
		    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 13));

		    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료
		    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(adSelect.getCreative().getId(), now, 21));

		        		// 상태 라인 처리 위해 공용리스트에 추가
			    		if (startDt != null && endDt != null && selDate != null) {
			    			// 광고 선택일시
			        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), selDate));
			    			
			    			// 광고 재생기간(분단위 시간)
			    			List<Date> playMins = SolUtil.getOnTimeMinuteDateListBetween(startDt, endDt);
			    			for(Date d : playMins) {
				        		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), d));
			    			}
			    		}
	    			}
	    		}
	    	} catch (Exception e) {
	    		logger.error("Report API - processCustom1", e);
	    		opResult = false;
	    	}
			
			obj.put("success", opResult);
			
			
			// 명령 포함 여부
			boolean hasCommand = false;
			InvRTScreen rtScreen = invService.getRTScreenByScreenId(screen.getId());
			if (rtScreen != null) {
				hasCommand = Util.isValid(rtScreen.getNextCmd());
			}
			obj.put("command", hasCommand);
			// / 명령 포함 여부
    	}
		
		Util.toJson(response, obj);
    }

}
