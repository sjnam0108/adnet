package net.doohad.controllers.rev;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.rev.RevScrHrlyPlyTot;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.rev.RevDailyApiStatItem;

/**
 * 송출일지 컨트롤러
 */
@Controller("rev-daily-api-controller")
@RequestMapping(value="/rev/dailyapi")
public class RevDailyApiController {

	private static final Logger logger = LoggerFactory.getLogger(RevMonitoringController.class);


    @Autowired 
    private RevService revService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 송출일지 페이지
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
    	model.addAttribute("pageTitle", "송출일지");
    	
    	
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


    	KnlUser user = knlService.getUser(Util.loginUserId(session));
    	boolean hasKernelPriv = user != null && user.getAccount().isScopeKernel();
    	
    	model.addAttribute("stat", item);
    	model.addAttribute("currDateTitle", getDateLongFormat(date));
    	model.addAttribute("currDate", Util.toSimpleString(date, "yyyy-MM-dd"));
    	
    	model.addAttribute("prevDate", Util.toSimpleString(Util.addDays(date, -1), "yyyy-MM-dd"));
    	model.addAttribute("nextDate", Util.toSimpleString(Util.addDays(date, 1), "yyyy-MM-dd"));
    	model.addAttribute("today", Util.toSimpleString(new Date(), "yyyy-MM-dd"));
    	
    	model.addAttribute("hasKernelPriv", hasKernelPriv);
    	
    	
        return "rev/dailyapi";
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
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	Date playDate = null;
    	ArrayList<Integer> screenIds = new ArrayList<Integer>();
    	ArrayList<Integer> siteIds = new ArrayList<Integer>();
    	
    	for (Object id : objs) {
    		RevScrHrlyPlyTot playTot = revService.getScrHrlyPlyTot((int)id);
    		if (playTot != null) {
    			playDate = playTot.getPlayDate();
    			screenIds.add(playTot.getScreen().getId());
    			
    			int siteId = playTot.getScreen().getSite().getId();
    			if (!siteIds.contains(siteId)) {
    				siteIds.add(siteId);
    			}
    		}
    	}

    	
    	try {
        	if (playDate == null || screenIds.size() == 0 || siteIds.size() == 0) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        	}
        	
        	// 기존 화면 관련 자료의 삭제
        	for(Integer id : screenIds) {
        		revService.deleteScreenHrlyPlayData(playDate, id);
        	}
        	
        	// 기존 사이트 관련 자료의 삭제
        	for(Integer id : siteIds) {
        		revService.deleteSiteHrlyPlayData(playDate, id);
        	}
    		
    		// 시간당 화면 및 사이트 재생 합계를 계산(사이트 합계 재계산이 목적)
    		revService.calcDailyInvenConnectCountByPlayDate(playDate);
    		
    		// 하루 동안의 광고 노출량을 계산
    		SolUtil.calcOneDayAdImpression(playDate, true);
    		
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
}
