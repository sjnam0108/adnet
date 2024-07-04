package net.doohad.controllers.sys;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceRequest.FilterDescriptor;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.RevService;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysOpt;
import net.doohad.models.sys.SysRtUnit;
import net.doohad.models.sys.SysSvcRespTime;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 데이터베이스 개체 컨트롤러
 */
@Controller("sys-db-obj-controller")
@RequestMapping(value="/sys/dbobj")
public class SysDbObjController {

	private static final Logger logger = LoggerFactory.getLogger(SysDbObjController.class);


    @Autowired 
    private SysService sysService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private RevService revService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 데이터베이스 개체 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "AD Network");
    	
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	Date sDate = Util.removeTimeOfDate(new Date());
    	Date eDate = Util.removeTimeOfDate(new Date());
    	if (medium != null) {
    		sDate = medium.getEffectiveStartDate();
    		if (medium.getEffectiveEndDate() != null) {
    			eDate = medium.getEffectiveEndDate();
    		}
    	}
    	
    	ArrayList<String> dates = new ArrayList<String>();
    	for(int i = 0; i < 4000; i++) {
    		Date d = Util.addDays(sDate, i);
    		if (d.after(eDate)) {
    			break;
    		}
    		dates.add(Util.toSimpleString(d, "yyyy M/d"));
    	}

    	model.addAttribute("dates", Util.getObjectToJson(dates, false));
    	
    	
    	// 솔루션 설정: 커스텀 화면 해상도
    	String resos = "";
    	SysOpt sysOpt = sysService.getOpt("sol.resos");
    	if (sysOpt != null) {
    		resos = Util.parseString(sysOpt.getValue());
    	}
    	
    	model.addAttribute("resos", resos); 
    	
    	
        return "sys/dbobj";
    }
	
    
	/**
	 * 읽기 액션 - Kendo MultiSelect 용 광고 정보
	 */
    @RequestMapping(value = "/readAds", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAds(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<AdcAd> adList = adcService.getAdListByMediumIdNameLike(Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(AdcAd ad : adList) {
			list.add(new DropDownListItem(ad.getName(), String.valueOf(ad.getId())));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }
	
    
	/**
	 * 읽기 액션 - Kendo MultiSelect 용 화면 정보
	 */
    @RequestMapping(value = "/readScreens", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readScreens(@RequestBody DataSourceRequest request, 
    		HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();

		FilterDescriptor filter = request.getFilter();
		List<FilterDescriptor> filters = filter.getFilters();
		String userInput = "";
		if (filters.size() > 0) {
			userInput = Util.parseString((String) filters.get(0).getValue());
		}

		List<InvScreen> scrList = invService.getScreenListByMediumIdNameLike(Util.getSessionMediumId(session), userInput);
		
		// 전부 읽어 오지 않을 경우, "수정" 모드에서의 값 설정이 정상적이지 않기에 갯수에 대한 제한을 해제
		for(InvScreen screen : scrList) {
			list.add(new DropDownListItem(screen.getName(), String.valueOf(screen.getId())));
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

    	return list;
    }

    
    /**
	 * 액션 - 광고 노출 자료 정리
	 */
    @RequestMapping(value = "/cleanUp", method = RequestMethod.POST)
    public @ResponseBody String cleanUp(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> ads = (ArrayList<Object>) model.get("ads");
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> screens = (ArrayList<Object>) model.get("screens");
    	
    	
    	ArrayList<Integer> adIds = new ArrayList<Integer>();
    	for (Object id : ads) {
    		adIds.add(Integer.parseInt((String) id));
    	}
    	
    	ArrayList<Integer> screenIds = new ArrayList<Integer>();
    	for (Object id : screens) {
    		screenIds.add(Integer.parseInt((String) id));
    	}
    	
    	Date sDate = null, eDate = null;
    	try {
    		SimpleDateFormat df = new SimpleDateFormat("yyyy M/d");
    		sDate = df.parse((String) model.get("start"));
    		eDate = df.parse((String) model.get("end"));
    	} catch (Exception e) { }
    	
    	// 파라미터 검증
    	if (sDate == null || eDate == null || (adIds.size() == 0 && screenIds.size() == 0)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	ArrayList<Date> dateList = new ArrayList<Date>();
    	List<Tuple> tupleList = revService.getScrHourlyPlayPlayDateListByAdIdScreenIdPlayDate(
    			sDate, eDate, adIds, screenIds);
    	for(Tuple tuple : tupleList) {
    		dateList.add((Date)tuple.get(0));
    	}
    	
    	try {
    		// 기간 중 해당 자료가 존재함
        	if (dateList.size() > 0) {
        		logger.info(" cleanUp - date size = " + dateList.size());
        		
        		//
        		// Step 1. 시간당 화면/광고 재생(RevScrHourlyPlay) 삭제
        		//
        		if (revService.deleteScrHourlyPlaysByAdIdScreenIdPlayDate(
        				sDate, eDate, adIds, screenIds)) {

        			//
        			// Step 2. 시간당 광고 재생(RevHourlyPlay) 삭제
        			//         - 매체의 특정일자의 모든 자료 삭제 후, Step 6에서 보존 자료를 새로 계산 및 생성
        			//
        			if (revService.deleteHourlyPlaysByMediumIdPlayDate(
        					Util.getSessionMediumId(session), dateList)) {
        				
            	    	//
            	    	// Step 3. 잔여 시간당 사이트 재생(성공/실패) 합계(RevSitHrlyPlyTot) 삭제
            	    	//         - 삭제 대상 자료만으로 합계된 자료는 삭제되어야 한다.
            	    	//         - 삭제 + 보존 대상 자료는 아래의 계산에 의해 통계가 재계산된다.
    					//           - revService.calcDailyInvenConnectCountByPlayDate
            	    	//
            	    	for(Date date : dateList) {
            	    		revService.deleteInactiveSitHrlyPlyTotsByPlaDate(date);
            	    	}
        				
            	    	//
            	    	// Step 4. 잔여 시간당 화면 재생(성공/실패) 합계(RevScrHrlyPlyTot) 삭제
            	    	//         - 삭제 대상 자료만으로 합계된 자료는 삭제되어야 한다.
            	    	//         - 삭제 + 보존 대상 자료는 아래의 계산에 의해 통계가 재계산된다.
    					//           - revService.calcDailyInvenConnectCountByPlayDate
            	    	//
            	    	revService.deleteInactiveScrHrlyPlyTots();

            	    	
            	    	//
            	    	// Step 5. 시간당 사이트 재생(성공/실패) 합계(RevSitHrlyPlyTot) 및 시간당 화면 재생(성공/실패) 합계(RevScrHrlyPlyTot) 재계산
            	    	//         - revService.calcDailyInvenConnectCountByPlayDate
            	    	//
            	    	
            	    	//
            	    	// Step 6. 시간당 광고 재생(RevHourlyPlay) 생성
            	    	//         - SolUtil.calcOneDayAdImpression
            	    	for(Date date : dateList) {
            	    		logger.info(" cleanUp - date = " + Util.toSimpleString(date, "yyyy-MM-dd"));
            	    		
        					revService.calcDailyInvenConnectCountByPlayDate(date);
        					SolUtil.calcOneDayAdImpression(date, false);
            	    	}
        			}
        		}
        		
        		logger.info(" cleanUp - completed");
        	}
    	} catch (Exception e) {
    		logger.error("cleanUp", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 추가 액션 - 응답 시간 유닛
	 */
    @RequestMapping(value = "/createRtUnit", method = RequestMethod.POST)
    public @ResponseBody String createRtUnit(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String ukid = (String)model.get("ukid");
    	
    	boolean active = (Boolean)model.get("active");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(ukid) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	SysRtUnit target = new SysRtUnit(ukid, name, active);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 응답 시간 유닛
	 */
    @RequestMapping(value = "/updateRtUnit", method = RequestMethod.POST)
    public @ResponseBody String updateRtUnit(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String ukid = (String)model.get("ukid");
    	
    	boolean active = (Boolean)model.get("active");
    	
    	// 파라미터 검증
    	if (Util.isNotValid(ukid) || Util.isNotValid(name)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	SysRtUnit target = sysService.getRtUnit((int)model.get("id"));
    	if (target != null) {
        	
    		target.setUkid(ukid);
            target.setName(name);
            target.setActive(active);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장 - 응답 시간 유닛
	 */
    private void saveOrUpdate(SysRtUnit target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            sysService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션 - 응답 시간 보고
	 */
    @RequestMapping(value = "/destroyRt", method = RequestMethod.POST)
    public @ResponseBody String destroyRt(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<SysSvcRespTime> restTimes = new ArrayList<SysSvcRespTime>();

    	for (Object id : objs) {
    		SysSvcRespTime restTime = new SysSvcRespTime();
    		
    		restTime.setId((int)id);
    		
    		restTimes.add(restTime);
    	}
    	
    	try {
        	sysService.deleteSvcRespTimes(restTimes);
    	} catch (Exception e) {
    		logger.error("destroyRt", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
    /**
	 * 삭제 액션 - 응답 시간 유닛
	 */
    @RequestMapping(value = "/destroyRtUnit", method = RequestMethod.POST)
    public @ResponseBody String destroyRtUnit(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<SysRtUnit> rtUnits = new ArrayList<SysRtUnit>();

    	for (Object id : objs) {
    		SysRtUnit rtUnit = new SysRtUnit();
    		
    		rtUnit.setId((int)id);
    		
    		rtUnits.add(rtUnit);
    	}
    	
    	try {
        	sysService.deleteRtUnits(rtUnits);
    	} catch (Exception e) {
    		logger.error("destroyRtUnit", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
	
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		
		// 아래는 모두 빈값 전달이 가능
		String resos = (String)model.get("resos");
		
		try {
			SysOpt sysOpt = sysService.getOpt("sol.resos");
			if (sysOpt == null) {
				sysOpt = new SysOpt("sol.resos", resos);
			} else {
				sysOpt.setValue(resos);
			}
			
			sysOpt.setDate(new Date());
			
			sysService.saveOrUpdate(sysOpt);
		} catch (Exception e) {
    		logger.error("update", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}

        return "OK";
    }

}
