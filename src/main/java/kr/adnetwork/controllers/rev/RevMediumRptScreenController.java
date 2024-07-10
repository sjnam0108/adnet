package kr.adnetwork.controllers.rev;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.rev.RevScrHrlyFailTot;
import kr.adnetwork.models.rev.RevScrHrlyFbTot;
import kr.adnetwork.models.rev.RevScrHrlyNoAdTot;
import kr.adnetwork.models.rev.RevScrHrlyPlyTot;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.rev.RevDailyApiStatItem;
import kr.adnetwork.viewmodels.rev.RevScrHrlyPlyOvwItem;

/**
 * 매체 리포트 컨트롤러(화면)
 */
@Controller("rev-medium-report-screen-controller")
@RequestMapping(value="/rev/mediumrpt/screen")
public class RevMediumRptScreenController {

	private static final Logger logger = LoggerFactory.getLogger(RevMediumRptScreenController.class);

	
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
	 * 매체 리포트(화면) 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	modelMgr.addMainMenuModel(model, locale, session, request, "RevMediumRpt");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

		
    	// 페이지 제목
    	model.addAttribute("pageTitle", "매체 리포트");
    	
    	
    	int mediumId = Util.getSessionMediumId(session);
    	Date date = Util.removeTimeOfDate(Util.parseDate(request.getParameter("date")));
    	if (date == null) {
    		date = Util.removeTimeOfDate(new Date());
    	} else if (date.compareTo(Util.removeTimeOfDate(new Date())) > 0) {
    		date = Util.removeTimeOfDate(new Date());
    	}
    	
    	RevDailyApiStatItem item = new RevDailyApiStatItem();
    	DecimalFormat dcFormat1 = new DecimalFormat("###,###,##0");
    	DecimalFormat dcFormat2 = new DecimalFormat("###,###,##0.0");
    	Tuple scrStat = revService.getScrHrlyPlyTotStatByMediumIdPlayDate(mediumId, date);
    	Tuple sitStat = revService.getSitHrlyPlyTotStatByMediumIdPlayDate(mediumId, date);
    	Double scrStd = revService.getStdScrHrlyPlyTotByMediumIdPlayDate(mediumId, date);
    	Tuple scrStat2 = revService.getAvgScrHrlyPlyTotByMediumIdBetweenPlayDates(
    			mediumId, Util.addDays(date, -14), Util.addDays(date, 14));
    	Tuple scrNoAdStat = revService.getScrHrlyNoAdTotStatByMediumIdPlayDate(mediumId, date);
    	Tuple scrFbStat = revService.getScrHrlyFbTotStatByMediumIdPlayDate(mediumId, date);
    	
    	Long sumRequest = (Long) scrStat.get(0);
    	Long sumSucc = (Long) scrStat.get(1);
    	Long sumFail = (Long) scrStat.get(2);
    	Long sumNoAd = (Long) scrNoAdStat.get(0);
    	Long sumFb = (Long) scrFbStat.get(0);
    	Long cntScr = (Long) scrStat.get(3);
    	Long cntSit = (Long) sitStat.get(0);
    	Double avgRequest = (Double) scrStat.get(4);
    	Double avgSucc = (Double) scrStat.get(5);
    	Double avgFail = (Double) scrStat.get(6);
    	
    	if (sumRequest != null) {
    		if (sumSucc == null) { sumSucc = 0l; }
    		if (sumFail == null) { sumFail = 0l; }
    		if (sumNoAd == null) { sumNoAd = 0l; }
    		if (sumFb == null) { sumFb = 0l; }
    		
    		sumRequest = sumSucc + sumFail + sumNoAd + sumFb;
    	}
    	
    	BigDecimal avgTotRequest = (BigDecimal) scrStat2.get(0);
    	BigDecimal avgTotFail = (BigDecimal) scrStat2.get(1);
    	
    	item.setSumRequest(sumRequest == null ? "" : dcFormat1.format(sumRequest));
    	item.setSumSucc(sumSucc == null ? "" : dcFormat1.format(sumSucc));
    	item.setSumFail(sumFail == null ? "" : dcFormat1.format(sumFail));
    	item.setSumNoAd(sumNoAd == null ? "" : dcFormat1.format(sumNoAd));
    	item.setSumFb(sumFb == null ? "" : dcFormat1.format(sumFb));
    	item.setPctSucc(sumRequest == null || sumSucc == null ? "" :
    		String.valueOf(Math.round((double) (sumSucc * 10000l / sumRequest)) / 100d));
    	item.setPctFail(sumRequest == null || sumFail == null ? "" :
    		String.valueOf(Math.round((double) (sumFail * 10000l / sumRequest)) / 100d));
    	item.setPctFb(sumRequest == null || sumFb == null ? "" :
    		String.valueOf(Math.round((double) (sumFb * 10000l / sumRequest)) / 100d));
    	item.setPctNoAd(sumRequest == null || sumNoAd == null ? "" :
    		String.valueOf(Math.round((double) (sumNoAd * 10000l / sumRequest)) / 100d));
    	item.setCntScr(cntScr == null || cntScr == 0 ? "" : dcFormat1.format(cntScr));
    	item.setCntSit(cntSit == null || cntSit == 0 ? "" : dcFormat1.format(cntSit));
    	item.setStdRequest(scrStd == null ? "" : dcFormat2.format(scrStd));
    	
    	Double avgNoAd = null;
    	Double avgFb = null;
    	if (cntScr != null) {
        	if (sumNoAd != null) {
        		avgNoAd = (double)sumNoAd / (double)cntScr;
        	}
    		if (sumFb != null) {
    			avgFb = (double)sumFb / (double)cntScr;
    		}
    		if (sumRequest != null) {
    			avgRequest = (double)sumRequest / (double)cntScr;
    		}
    	}

    	if (sumRequest == null) {
        	item.setAvgRequest(avgRequest == null || avgRequest == 0 ? "" : dcFormat2.format(avgRequest));
        	item.setAvgSucc(avgSucc == null || avgSucc == 0 ? "" : dcFormat2.format(avgSucc));
        	item.setAvgFail(avgFail == null || avgFail == 0 ? "" : dcFormat2.format(avgFail));
        	item.setAvgNoAd(avgNoAd == null || avgNoAd == 0 ? "" : dcFormat2.format(avgNoAd));
        	item.setAvgFb(avgFb == null || avgFb == 0 ? "" : dcFormat2.format(avgFb));
    	} else {
        	item.setAvgRequest(avgRequest == null || avgRequest == 0 ? "0.0" : dcFormat2.format(avgRequest));
        	item.setAvgSucc(avgSucc == null || avgSucc == 0 ? "0.0" : dcFormat2.format(avgSucc));
        	item.setAvgFail(avgFail == null || avgFail == 0 ? "0.0" : dcFormat2.format(avgFail));
        	item.setAvgNoAd(avgNoAd == null || avgNoAd == 0 ? "0.0" : dcFormat2.format(avgNoAd));
        	item.setAvgFb(avgFb == null || avgFb == 0 ? "0.0" : dcFormat2.format(avgFb));
    	}
    	
    	item.setAvgTotRequest(avgTotRequest == null || avgTotRequest.doubleValue() == 0d ? 
    			"" : dcFormat2.format(avgTotRequest.doubleValue()));
    	if (avgTotRequest != null && avgTotFail != null) {
    		item.setAvgTotFail(String.valueOf(
    				Math.round(avgTotFail.doubleValue() * 10000l / avgTotRequest.doubleValue()) / 100d));
    	}


    	model.addAttribute("stat", item);
    	model.addAttribute("currDateTitle", getDateLongFormat(date));
    	model.addAttribute("currDate", Util.toSimpleString(date, "yyyy-MM-dd"));
    	
    	model.addAttribute("prevDate", Util.toSimpleString(Util.addDays(date, -1), "yyyy-MM-dd"));
    	model.addAttribute("nextDate", Util.toSimpleString(Util.addDays(date, 1), "yyyy-MM-dd"));
    	model.addAttribute("today", Util.toSimpleString(new Date(), "yyyy-MM-dd"));
    	
    	
    	KnlMedium medium = knlService.getMedium(mediumId);
    	model.addAttribute("mediumName", medium != null ? medium.getName() : "?");
    	model.addAttribute("mediumShortName", medium != null ? medium.getShortName() : "?");

		
        return "rev/mediumrpt/mediumrpt-screen";
    }
    
    private String getDateLongFormat(Date d) {
    	
    	if (d == null) {
    		d = new Date();
    	}
    	
    	return Util.toSimpleString(d, "M") + "<small>월</small> " + Util.toSimpleString(d, "d") + "<small>일</small> <small>" +
    			new SimpleDateFormat("EEE", Locale.KOREAN).format(d) + "요일</small><span class='mr-3'></span><small class='text-muted'>" +
    			Util.toSimpleString(d, "yyyy") + "년</small>";
    }
    
    
	/**
	 * 읽기 액션 - 화면 요약
	 */
    @RequestMapping(value = "/readScrTotOvw", method = RequestMethod.POST)
    public @ResponseBody List<RevScrHrlyPlyOvwItem> readScrTotOvw(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		Date date = Util.parseDate(request.getReqStrValue1());
    		if (date == null) {
    			date = Util.removeTimeOfDate(new Date());
    		}
    		
    		int mediumId = Util.getSessionMediumId(session);
    		ArrayList<RevScrHrlyPlyOvwItem> retList = new ArrayList<RevScrHrlyPlyOvwItem>();
    		
    		List<RevScrHrlyPlyTot> totList = revService.getScrHrlyPlyTotListByMediumIdPlayDate(mediumId, date);
    		List<RevScrHrlyFbTot> fbList = revService.getScrHrlyFbTotListByMediumIdPlayDate(mediumId, date);
    		List<RevScrHrlyNoAdTot> noAdList = revService.getScrHrlyNoAdTotListByMediumIdPlayDate(mediumId, date);
    		
    		HashMap<String, InvScreen> screenMap = new HashMap<String, InvScreen>();
    		HashMap<String, Integer> valueMap = new HashMap<String, Integer>();
    		
    		for(RevScrHrlyPlyTot plyTot : totList) {
    			int id = plyTot.getScreen().getId();
    			if (!screenMap.containsKey("S" + id)) {
    				SolUtil.setScreenReqStatus(plyTot.getScreen());
    				screenMap.put("S" + id, plyTot.getScreen());
    			}
    			valueMap.put("Succ" + id, plyTot.getSuccTotal());
    			valueMap.put("Fail" + id, plyTot.getFailTotal());
    		}
    		
    		for(RevScrHrlyFbTot plyTot : fbList) {
    			int id = plyTot.getScreen().getId();
    			if (!screenMap.containsKey("S" + id)) {
    				SolUtil.setScreenReqStatus(plyTot.getScreen());
    				screenMap.put("S" + id, plyTot.getScreen());
    			}
    			valueMap.put("Fb" + id, plyTot.getDateTotal());
    		}
    		
    		for(RevScrHrlyNoAdTot plyTot : noAdList) {
    			int id = plyTot.getScreen().getId();
    			if (!screenMap.containsKey("S" + id)) {
    				SolUtil.setScreenReqStatus(plyTot.getScreen());
    				screenMap.put("S" + id, plyTot.getScreen());
    			}
    			valueMap.put("NoAd" + id, plyTot.getDateTotal());
    		}
    		
    		Collection<InvScreen> screens = screenMap.values();
    		for(InvScreen screen : screens) {
    			SolUtil.setScreenReqStatus(screen);
    			RevScrHrlyPlyOvwItem item = new RevScrHrlyPlyOvwItem(screen);
    			
    			Integer succ = valueMap.get("Succ" + screen.getId());
    			Integer fail = valueMap.get("Fail" + screen.getId());
    			Integer fb = valueMap.get("Fb" + screen.getId());
    			Integer noAd = valueMap.get("NoAd" + screen.getId());
    			
    			if (succ != null) { item.setSuccTotal(succ.intValue()); }
    			if (fail != null) { item.setFailTotal(fail.intValue()); }
    			if (fb != null) { item.setFbTotal(fb.intValue()); }
    			if (noAd != null) { item.setNoAdTotal(noAd.intValue()); }
    			
    			retList.add(item);
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readScrTotOvw", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 화면(성공)
	 */
    @RequestMapping(value = "/readScrTot", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScrTot(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		Date date = Util.parseDate(request.getReqStrValue1());
    		if (date == null) {
    			date = Util.removeTimeOfDate(new Date());
    		}
    		
    		DataSourceResult result = revService.getScrHrlyPlyTotList(request, date);
    		for(Object obj : result.getData()) {
    			RevScrHrlyPlyTot playTot = (RevScrHrlyPlyTot) obj;
    			SolUtil.setScreenReqStatus(playTot.getScreen());
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readScrTot", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 화면(실패)
	 */
    @RequestMapping(value = "/readScrFailTot", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScrFailTot(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		Date date = Util.parseDate(request.getReqStrValue1());
    		if (date == null) {
    			date = Util.removeTimeOfDate(new Date());
    		}
    		
    		DataSourceResult result = revService.getScrHrlyFailTotList(request, date);
    		for(Object obj : result.getData()) {
    			RevScrHrlyFailTot playTot = (RevScrHrlyFailTot) obj;
    			SolUtil.setScreenReqStatus(playTot.getScreen());
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readScrFailTot", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 화면(광고없음)
	 */
    @RequestMapping(value = "/readScrNoAdTot", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScrNoAdTot(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		Date date = Util.parseDate(request.getReqStrValue1());
    		if (date == null) {
    			date = Util.removeTimeOfDate(new Date());
    		}
    		
    		DataSourceResult result = revService.getScrHrlyNoAdTotList(request, date);
    		for(Object obj : result.getData()) {
    			RevScrHrlyNoAdTot playTot = (RevScrHrlyNoAdTot) obj;
    			SolUtil.setScreenReqStatus(playTot.getScreen());
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readScrNoAdTot", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 화면(대체광고)
	 */
    @RequestMapping(value = "/readScrFbTot", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScrFbTot(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		Date date = Util.parseDate(request.getReqStrValue1());
    		if (date == null) {
    			date = Util.removeTimeOfDate(new Date());
    		}
    		
    		DataSourceResult result = revService.getScrHrlyFbTotList(request, date);
    		for(Object obj : result.getData()) {
    			RevScrHrlyFbTot playTot = (RevScrHrlyFbTot) obj;
    			SolUtil.setScreenReqStatus(playTot.getScreen());
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readScrFbTot", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

}
