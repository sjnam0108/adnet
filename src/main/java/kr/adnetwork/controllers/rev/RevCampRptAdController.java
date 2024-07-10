package kr.adnetwork.controllers.rev;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import kr.adnetwork.models.CustomComparator;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.rev.RevDailyAchv;
import kr.adnetwork.models.rev.RevScrHourlyPlay;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.rev.RevRptAdItem;
import kr.adnetwork.viewmodels.rev.RevRptChartItem;
import kr.adnetwork.viewmodels.rev.RevRptDailyItem;
import kr.adnetwork.viewmodels.rev.RevRptWeekDailyItem;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 리포트 컨트롤러(광고)
 */
@Controller("rev-campaign-report-ad-controller")
@RequestMapping(value="")
public class RevCampRptAdController {

	private static final Logger logger = LoggerFactory.getLogger(RevCampRptAdController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private RevService revService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 리포트(광고) 페이지
	 */
    @RequestMapping(value = {"/rev/camprpt/{campId}", "/rev/camprpt/{campId}/", 
    		"/rev/camprpt/ad/{campId}", "/rev/camprpt/ad/{campId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	AdcCampaign campaign = adcService.getCampaign(Util.parseInt(pathMap.get("campId")));
    	if (campaign == null || campaign.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/rev/camprpt";
    	}

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "RevCampRpt");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});
    	
    	// 오늘/하루 목표
		if (campaign.getTgtToday() > 0 && campaign.isSelfManaged() && (campaign.getStatus().equals("U") || campaign.getStatus().equals("R")) &&
				(campaign.getGoalType().equals("A") || campaign.getGoalType().equals("I"))) {
				
			String tgtTodayDisp = String.format("<small>%s<span class='text-muted'>는</span></small>",
					campaign.getStatus().equals("A") ? "하루 목표" : "오늘 목표");
			tgtTodayDisp += String.format("<small class='pl-2'>%s</small><span class='px-2'>%s</span>",
					campaign.getGoalType().equals("A") ? "광고 예산" : "노출량",
					new DecimalFormat("###,###,##0").format(campaign.getTgtToday()));
			tgtTodayDisp += String.format("<small>%s<span class='text-muted'>입니다</span></small>", 
					campaign.getGoalType().equals("A") ? "원" : "회");
			
			campaign.setTgtTodayDisp(tgtTodayDisp);
		}
		//-

		
    	// 페이지 제목
    	model.addAttribute("pageTitle", "리포트");

    	model.addAttribute("Campaign", campaign);

    	
    	ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		List<AdcAd> adList = adcService.getAdListByCampaignId(campaign.getId());
		for (AdcAd ad : adList) {
			list.add(new DropDownListItem("selected", ad.getName(), String.valueOf(ad.getId())));
		}
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		
		
		model.addAttribute("Ads", list);
		model.addAttribute("AdsSize", list.size());
		model.addAttribute("filterMinDate", Util.toSimpleString(campaign.getStartDate(), "yyyy-MM-dd"));
		model.addAttribute("filterMaxDate", Util.toSimpleString(campaign.getEndDate(), "yyyy-MM-dd"));
		
		
		// 필터 광고 확인(다이렉트 광고 필터링)
		AdcAd ad = adcService.getAd(Util.parseInt(request.getParameter("filter")));
		String filterId = "", filterSDate = "", filterEDate = "";
		if (ad != null) {
			filterId = String.valueOf(ad.getId());
			filterSDate = Util.toSimpleString(ad.getStartDate(), "yyyy-MM-dd");
			filterEDate = Util.toSimpleString(ad.getEndDate(), "yyyy-MM-dd");
		}
		model.addAttribute("filterId", filterId);
		model.addAttribute("filterSDate", filterSDate);
		model.addAttribute("filterEDate", filterEDate);
		
		
        return "rev/camprpt/camprpt-ad";
    }
    
    
	/**
	 * 읽기 액션 - 광고별
	 */
    @RequestMapping(value = "/rev/camprpt/ad/read", method = RequestMethod.POST)
    public @ResponseBody List<RevRptAdItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		Date sDate = null;
    		Date eDate = null;

    		HashMap<String, RevRptAdItem> map = new HashMap<String, RevRptAdItem>();
    		String searchData = request.getReqStrValue1();
    		
    		JSONObject dataObj = JSONObject.fromObject(JSONSerializer.toJSON(searchData));
    		String srchIds = "", srchSDate = "", srchEDate = "";
    		
    		if (dataObj != null) {
    			try {
        			srchIds = dataObj.getString("ids");
        			srchSDate = dataObj.getString("startDate");
        			srchEDate = dataObj.getString("endDate");
    			} catch (Exception e) {}
    			
    			if (Util.isValid(srchSDate)) {
    				sDate = Util.parseZuluTime(srchSDate);
    			}
    			if (Util.isValid(srchEDate)) {
    				eDate = Util.parseZuluTime(srchEDate);
    			}
    			
    			if (Util.isValid(srchIds)) {
    				List<String> tokens = Util.tokenizeValidStr(srchIds);
    				for(String s : tokens) {
    					ids.add(Util.parseInt(s));
    				}
    			}
    		}
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getAdTargetCountGroupByMediumAdId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}


    		AdcCampaign campaign = adcService.getCampaign((int)request.getReqIntValue1());
    		if (campaign != null) {
    			if (sDate == null) {
        			sDate = campaign.getStartDate();
    			}
    			if (eDate == null) {
        			eDate = campaign.getEndDate();
    			}
    			
    			int sysValuePct = Util.parseInt(SolUtil.getOptValue(campaign.getMedium().getId(), "sysValue.pct"));
    			int dailyScrCap = Util.parseInt(SolUtil.getOptValue(campaign.getMedium().getId(), "freqCap.daily.screen"));
    			
    			boolean idsReady = ids.size() > 0;
    			List<AdcAd> adList = adcService.getAdListByCampaignId(campaign.getId());
    			for (AdcAd ad : adList) {
    				if (!idsReady) {
        				ids.add(ad.getId());
    				}
    				
    				if (ids.contains(ad.getId())) {
        				RevRptAdItem item = new RevRptAdItem(ad);
        				item.setInvenTargeted(targetIds.contains(ad.getId()));
            			if (item.getGoalType().equals("I") && item.getGoalValue() > 0 &&
            					item.getSysValue() == 0 && sysValuePct > 0) {
            				item.setSysValue((int)Math.ceil((float)item.getGoalValue() * (float)sysValuePct / 100f));
            				item.setSysValueProposed(true);
            			}
            			
            			if (dailyScrCap > 0 && item.getDailyScrCap() == 0) {
            				item.setProposedDailyScrCap(dailyScrCap);
            			}
            			
            			// 모바일 타겟팅 여부 설정
            			if (adcService.getMobTargetCountByAdId(ad.getId()) > 0) {
            				item.setMobTargeted(true);
            			}
        				
        				map.put("A" + ad.getId(), item);
    				}
    			}
    		}
    		
    		List<Tuple> list = revService.getHourlyPlayStatGroupByAdIdInBetween(ids, sDate, eDate);
    		for (Tuple tuple : list) {
    			String key = "A" + String.valueOf((Integer)tuple.get(0));
    			if (map.containsKey(key)) {
    				RevRptAdItem item = map.get(key);
    				item.setTupleData(tuple);
    				
    				map.put(key, item);
    			}
    		}
    		
    		List<Tuple> cntList = revService.getScrHourlyPlayScrCntGroupByAdIdInBetween(ids, sDate, eDate);
    		for (Tuple tuple : cntList) {
    			String key = "A" + String.valueOf((Integer)tuple.get(0));
    			if (map.containsKey(key)) {
    				RevRptAdItem item = map.get(key);
    				item.setCntScreen(((BigInteger)tuple.get(1)).intValue());
    				map.put(key, item);
    			}
    		}

    		ArrayList<RevRptAdItem> retList = new ArrayList<RevRptAdItem>(map.values());
    		
    		Collections.sort(retList, new Comparator<RevRptAdItem>() {
    	    	public int compare(RevRptAdItem item1, RevRptAdItem item2) {
    	    		return item1.getName().compareTo(item2.getName());
    	    	}
    	    });
    		
    		
    		// 합계 행
    		RevRptAdItem totalItem = new RevRptAdItem();
    		totalItem.setName("캠페인 합계: " + retList.size() + " 건");
    		totalItem.setTotalRow(true);
    		totalItem.setCntScreen(revService.getScrHourlyPlayScrCntAdIdInBetween(ids, sDate, eDate));
    		
    		String campGoalType = "";
    		for(RevRptAdItem item : retList) {
    			if (retList.size() > 1) {
    				item.setSingleAdFiltered(true);
    			}
    			
    			totalItem.setBudget(totalItem.getBudget() + item.getBudget());
    			totalItem.setGoalValue(totalItem.getGoalValue() + item.getGoalValue());
    			totalItem.setSysValue(totalItem.getSysValue() + item.getSysValue());
    			totalItem.setActualAmount(totalItem.getActualAmount() + item.getActualAmount());
    			totalItem.setTotal(totalItem.getTotal() + item.getTotal());
    			
    			totalItem.setCnt00(totalItem.getCnt00() + item.getCnt00());
    			totalItem.setCnt01(totalItem.getCnt01() + item.getCnt01());
    			totalItem.setCnt02(totalItem.getCnt02() + item.getCnt02());
    			totalItem.setCnt03(totalItem.getCnt03() + item.getCnt03());
    			totalItem.setCnt04(totalItem.getCnt04() + item.getCnt04());
    			totalItem.setCnt05(totalItem.getCnt05() + item.getCnt05());
    			totalItem.setCnt06(totalItem.getCnt06() + item.getCnt06());
    			totalItem.setCnt07(totalItem.getCnt07() + item.getCnt07());
    			totalItem.setCnt08(totalItem.getCnt08() + item.getCnt08());
    			totalItem.setCnt09(totalItem.getCnt09() + item.getCnt09());
    			totalItem.setCnt10(totalItem.getCnt10() + item.getCnt10());
    			totalItem.setCnt11(totalItem.getCnt11() + item.getCnt11());
    			totalItem.setCnt12(totalItem.getCnt12() + item.getCnt12());
    			totalItem.setCnt13(totalItem.getCnt13() + item.getCnt13());
    			totalItem.setCnt14(totalItem.getCnt14() + item.getCnt14());
    			totalItem.setCnt15(totalItem.getCnt15() + item.getCnt15());
    			totalItem.setCnt16(totalItem.getCnt16() + item.getCnt16());
    			totalItem.setCnt17(totalItem.getCnt17() + item.getCnt17());
    			totalItem.setCnt18(totalItem.getCnt18() + item.getCnt18());
    			totalItem.setCnt19(totalItem.getCnt19() + item.getCnt19());
    			totalItem.setCnt20(totalItem.getCnt20() + item.getCnt20());
    			totalItem.setCnt21(totalItem.getCnt21() + item.getCnt21());
    			totalItem.setCnt22(totalItem.getCnt22() + item.getCnt22());
    			totalItem.setCnt23(totalItem.getCnt23() + item.getCnt23());
    			
    			if (Util.isValid(campGoalType)) {
    				if (campGoalType.equals("?")) {
    				} else {
    					if (!campGoalType.equals(item.getGoalType())) {
    						campGoalType = "?";
    					}
    				}
    			} else {
    				campGoalType = item.getGoalType();
    			}
    		}
			
    		if (totalItem.getTotal() == 0) {
    			totalItem.setActualCpm(0);
    		} else {
    			totalItem.setActualCpm((int)((long)totalItem.getActualAmount() * 1000l / (long)totalItem.getTotal()));
    		}

			// 캠페인의 집행 방법(포함된 광고의 집행 방법에 대한 대표 유형)
			// 기본적인 값: A(광고예산) / I(노출량) / U(무제한 노출) / ?(포함된 광고가 모두 일치하지 않고 다른 값)
			double achvRatio = 0d;
			if (campGoalType.equals("A")) {
				achvRatio = Math.round((double)totalItem.getActualAmount() * 10000d / (double)totalItem.getBudget()) / 100d;
			} else if (campGoalType.equals("I")) {
				if (totalItem.getGoalValue() != 0) {
					achvRatio = Math.round((double)totalItem.getTotal() * 10000d / (double)totalItem.getGoalValue()) / 100d;
				}
			}
			totalItem.setAchvRatio(achvRatio);

			
    		retList.add(totalItem);
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 일자별
	 */
    @RequestMapping(value = "/rev/camprpt/ad/readDaily", method = RequestMethod.POST)
    public @ResponseBody List<RevRptDailyItem> readDaily(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		Date sDate = null;
    		Date eDate = null;
    		
    		String searchData = request.getReqStrValue1();
    		
    		JSONObject dataObj = JSONObject.fromObject(JSONSerializer.toJSON(searchData));
    		String srchIds = "", srchSDate = "", srchEDate = "";
    		
    		if (dataObj != null) {
    			try {
        			srchIds = dataObj.getString("ids");
        			srchSDate = dataObj.getString("startDate");
        			srchEDate = dataObj.getString("endDate");
    			} catch (Exception e) {}
    			
    			if (Util.isValid(srchSDate)) {
    				sDate = Util.parseZuluTime(srchSDate);
    			}
    			if (Util.isValid(srchEDate)) {
    				eDate = Util.parseZuluTime(srchEDate);
    			}
    			
    			if (Util.isValid(srchIds)) {
    				List<String> tokens = Util.tokenizeValidStr(srchIds);
    				for(String s : tokens) {
    					ids.add(Util.parseInt(s));
    				}
    			}
    		}
    		
    		boolean campDailyAchvMode = false;
    		AdcCampaign campaign = adcService.getCampaign((int)request.getReqIntValue1());
    		if (campaign != null) {
    			if (sDate == null) {
        			sDate = campaign.getStartDate();
    			}
    			if (eDate == null) {
        			eDate = campaign.getEndDate();
    			}
    			
    			boolean idsReady = ids.size() > 0;
    			List<AdcAd> adList = adcService.getAdListByCampaignId(campaign.getId());
    			for (AdcAd ad : adList) {
    				if (!idsReady) {
        				ids.add(ad.getId());
    				}
    			}
    			
    			if (ids.size() == adList.size() && ids.size() > 0 && campaign.isSelfManaged()) {
    				campDailyAchvMode = true;
    			}
    		}

    		
    		// 광고와 캠페인의 일별 달성 기록을 미리 준비
    		//
    		//  - 광고: 필터링 대상이 단 하나일 경우
    		//  - 캠페인: 필터링이 없는 경우
    		//
    		HashMap<String, RevDailyAchv> dailyAchvMap = new HashMap<String, RevDailyAchv>();
    		if (campDailyAchvMode) {
        		List<RevDailyAchv> dailyAchvList = revService.getDailyAchvListByTypeId("C", campaign.getId());
    			for(RevDailyAchv dailyAchv : dailyAchvList) {
    				dailyAchvMap.put(Util.toSimpleString(dailyAchv.getPlayDate(), "yyyyMMdd"), dailyAchv);
    			}
    		} else if (ids.size() == 1) {
        		List<RevDailyAchv> dailyAchvList = revService.getDailyAchvListByTypeId("A", ids.get(0));
    			for(RevDailyAchv dailyAchv : dailyAchvList) {
    				dailyAchvMap.put(Util.toSimpleString(dailyAchv.getPlayDate(), "yyyyMMdd"), dailyAchv);
    			}
    		}
    		
    		
    		HashMap<String, RevRptDailyItem> map = new HashMap<String, RevRptDailyItem>();
    		if (!campaign.getStatus().equals("U") && !campaign.getStatus().equals("T")) {
        		Date today = Util.removeTimeOfDate(new Date());
        		if (sDate != null && eDate != null) {
        			Date date = new Date(sDate.getTime());
        			int idx = 0;
        			do {
        				map.put(Util.toSimpleString(date, "yyyyMMdd"), new RevRptDailyItem(date, ++idx));
        				date = Util.addDays(date, 1);
        			} while (!date.after(eDate) && !date.after(today));
        		}
        		
        		List<Tuple> list = revService.getHourlyPlayStatGroupByPlayDateAdIdInBetween(ids, sDate, eDate);
        		for (Tuple tuple : list) {
        			String key = Util.toSimpleString((Date)tuple.get(0), "yyyyMMdd");
        			if (map.containsKey(key)) {
        				RevRptDailyItem item = map.get(key);
        				item.setTupleData(tuple);
        				
        				// 광고의 대상 일자의 달성 기록 설정
        				RevDailyAchv dailyAchv = dailyAchvMap.get(Util.toSimpleString(item.getPlayDate(), "yyyyMMdd"));
        				if (dailyAchv != null) {
        					item.setGoalType(dailyAchv.getGoalType());
        					item.setTgtToday(dailyAchv.getTgtToday());
        					item.setAchvRatio(dailyAchv.getAchvRatio());
        				}
        				
        				map.put(key, item);
        			}
        		}
        		
        		List<Tuple> cntList = revService.getScrHourlyPlayScrCntGroupByPlayDateAdIdInBetween(ids, sDate, eDate);
        		for (Tuple tuple : cntList) {
        			String key = Util.toSimpleString((Date)tuple.get(0), "yyyyMMdd");
        			if (map.containsKey(key)) {
        				RevRptDailyItem item = map.get(key);
        				item.setCntScreen(((BigInteger)tuple.get(1)).intValue());
        				map.put(key, item);
        			}
        		}
    		}

    		ArrayList<RevRptDailyItem> retList = new ArrayList<RevRptDailyItem>(map.values());
    		
    		Collections.sort(retList, new Comparator<RevRptDailyItem>() {
    	    	public int compare(RevRptDailyItem item1, RevRptDailyItem item2) {
    	    		return item1.getPlayDate().compareTo(item2.getPlayDate());
    	    	}
    	    });
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readDaily", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 요일별
	 */
    @RequestMapping(value = "/rev/camprpt/ad/readWeekDaily", method = RequestMethod.POST)
    public @ResponseBody List<RevRptWeekDailyItem> readWeekDaily(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		Date sDate = null;
    		Date eDate = null;
    		
    		String searchData = request.getReqStrValue1();
    		
    		JSONObject dataObj = JSONObject.fromObject(JSONSerializer.toJSON(searchData));
    		String srchIds = "", srchSDate = "", srchEDate = "";
    		
    		if (dataObj != null) {
    			try {
        			srchIds = dataObj.getString("ids");
        			srchSDate = dataObj.getString("startDate");
        			srchEDate = dataObj.getString("endDate");
    			} catch (Exception e) {}
    			
    			if (Util.isValid(srchSDate)) {
    				sDate = Util.parseZuluTime(srchSDate);
    			}
    			if (Util.isValid(srchEDate)) {
    				eDate = Util.parseZuluTime(srchEDate);
    			}
    			
    			if (Util.isValid(srchIds)) {
    				List<String> tokens = Util.tokenizeValidStr(srchIds);
    				for(String s : tokens) {
    					ids.add(Util.parseInt(s));
    				}
    			}
    		}
    		
    		AdcCampaign campaign = adcService.getCampaign((int)request.getReqIntValue1());
    		if (campaign != null) {
    			if (sDate == null) {
        			sDate = campaign.getStartDate();
    			}
    			if (eDate == null) {
        			eDate = campaign.getEndDate();
    			}
    			
    			boolean idsReady = ids.size() > 0;
    			List<AdcAd> adList = adcService.getAdListByCampaignId(campaign.getId());
    			for (AdcAd ad : adList) {
    				if (!idsReady) {
        				ids.add(ad.getId());
    				}
    			}
    		}

    		
    		HashMap<String, RevRptWeekDailyItem> map = new HashMap<String, RevRptWeekDailyItem>();
    		if (!campaign.getStatus().equals("U") && !campaign.getStatus().equals("T")) {
        		map.put("1", new RevRptWeekDailyItem("1", "월요일"));
        		map.put("2", new RevRptWeekDailyItem("2", "화요일"));
        		map.put("3", new RevRptWeekDailyItem("3", "수요일"));
        		map.put("4", new RevRptWeekDailyItem("4", "목요일"));
        		map.put("5", new RevRptWeekDailyItem("5", "금요일"));
        		map.put("6", new RevRptWeekDailyItem("6", "토요일"));
        		map.put("7", new RevRptWeekDailyItem("7", "일요일"));
        		
        		List<Tuple> list = revService.getHourlyPlayStatGroupByWeekDayAdIdInBetween(ids, sDate, eDate);
        		for (Tuple tuple : list) {
        			String key = (String)tuple.get(0);
        			key = key.equals("0") ? "7" : key;
        			if (map.containsKey(key)) {
        				RevRptWeekDailyItem item = map.get(key);
        				item.setTupleData(tuple);
        				map.put(key, item);
        			}
        		}
        		
        		List<Tuple> cntList = revService.getScrHourlyPlayScrCntGroupByWeekDayAdIdInBetween(ids, sDate, eDate);
        		for (Tuple tuple : cntList) {
        			String key = (String)tuple.get(0);
        			key = key.equals("0") ? "7" : key;
        			if (map.containsKey(key)) {
        				RevRptWeekDailyItem item = map.get(key);
        				item.setCntScreen(((BigInteger)tuple.get(1)).intValue());
        				map.put(key, item);
        			}
        		}
    		}
    		
    		return new ArrayList<RevRptWeekDailyItem>(map.values());
    	} catch (Exception e) {
    		logger.error("readWeekDaily", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 차트용
	 */
    @RequestMapping(value = "/rev/camprpt/ad/readChart/{interval}", method = RequestMethod.POST)
    public @ResponseBody List<RevRptChartItem> readChartData(@RequestBody DataSourceRequest request, HttpSession session,
    		@PathVariable Map<String, String> pathMap) {
    	
    	try {
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		Date sDate = null;
    		Date eDate = null;
    		
    		String searchData = request.getReqStrValue1();
    		
    		JSONObject dataObj = JSONObject.fromObject(JSONSerializer.toJSON(searchData));
    		String srchIds = "", srchSDate = "", srchEDate = "";
    		
    		// 자료의 간격: H(시간), D(날짜)
    		String interval = Util.parseString(pathMap.get("interval"), "D");
    		if (!interval.equals("H")) {
    			interval = "D";
    		}
    		
    		if (dataObj != null) {
    			try {
        			srchIds = dataObj.getString("ids");
        			srchSDate = dataObj.getString("startDate");
        			srchEDate = dataObj.getString("endDate");
    			} catch (Exception e) {}
    			
    			if (Util.isValid(srchSDate)) {
    				sDate = Util.parseZuluTime(srchSDate);
    			}
    			if (Util.isValid(srchEDate)) {
    				eDate = Util.parseZuluTime(srchEDate);
    			}
    			
    			if (Util.isValid(srchIds)) {
    				List<String> tokens = Util.tokenizeValidStr(srchIds);
    				for(String s : tokens) {
    					ids.add(Util.parseInt(s));
    				}
    			}
    		}
    		
    		AdcCampaign campaign = adcService.getCampaign((int)request.getReqIntValue1());
    		if (campaign != null) {
    			if (sDate == null) {
        			sDate = campaign.getStartDate();
    			}
    			if (eDate == null) {
        			eDate = campaign.getEndDate();
    			}
    			
    			boolean idsReady = ids.size() > 0;
    			List<AdcAd> adList = adcService.getAdListByCampaignId(campaign.getId());
    			for (AdcAd ad : adList) {
    				if (!idsReady) {
        				ids.add(ad.getId());
    				}
    			}
    		}

    		
    		HashMap<String, RevRptChartItem> map = new HashMap<String, RevRptChartItem>();
    		if (!campaign.getStatus().equals("U") && !campaign.getStatus().equals("T")) {
        		Date today = Util.removeTimeOfDate(new Date());
        		if (sDate != null && eDate != null) {
        			Date date = new Date(sDate.getTime());
        			do {
        				if (interval.equals("D")) {
            				map.put(Util.toSimpleString(date, "yyyyMMdd"), new RevRptChartItem(date));
        				} else {
        					for (int i = 0; i < 24; i ++) {
                				map.put(Util.toSimpleString(Util.addHours(date, i), "yyyyMMddHH"), 
                						new RevRptChartItem(Util.addHours(date, i), true));
        					}
        				}
        				date = Util.addDays(date, 1);
        			} while (!date.after(eDate) && !date.after(today));
        		}
        		
        		List<Tuple> list = revService.getHourlyPlayStatGroupByPlayDateAdIdInBetween(ids, sDate, eDate);
        		for (Tuple tuple : list) {
        			if (interval.equals("D")) {
            			String key = Util.toSimpleString((Date)tuple.get(0), "yyyyMMdd");
            			if (map.containsKey(key)) {
            				RevRptChartItem item = map.get(key);
            				item.setValue(((BigDecimal)tuple.get(1)).intValue());
            				
            				map.put(key, item);
            			}
        			} else {
    					for (int i = 0; i < 24; i ++) {
    						String key = Util.toSimpleString(Util.addHours((Date)tuple.get(0), i), "yyyyMMddHH");
                			if (map.containsKey(key)) {
                				RevRptChartItem item = map.get(key);
                				item.setValue(((BigDecimal)tuple.get(2 + i)).intValue());
                				
                				map.put(key, item);
                			}
    					}
        			}
        		}
    		}

    		ArrayList<RevRptChartItem> retList = new ArrayList<RevRptChartItem>(map.values());
    		
    		Collections.sort(retList, new Comparator<RevRptChartItem>() {
    	    	public int compare(RevRptChartItem item1, RevRptChartItem item2) {
    	    		return item1.getPlayTime().compareTo(item2.getPlayTime());
    	    	}
    	    });
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readChartData", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 개별 화면 / 일자별
	 */
    @RequestMapping(value = "/rev/camprpt/ad/readScreenDaily", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScreenDaily(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		Date sDate = null;
    		Date eDate = null;
    		
    		String searchData = request.getReqStrValue1();
    		
    		JSONObject dataObj = JSONObject.fromObject(JSONSerializer.toJSON(searchData));
    		String srchIds = "", srchSDate = "", srchEDate = "";
    		
    		if (dataObj != null) {
    			try {
        			srchIds = dataObj.getString("ids");
        			srchSDate = dataObj.getString("startDate");
        			srchEDate = dataObj.getString("endDate");
    			} catch (Exception e) {}
    			
    			if (Util.isValid(srchSDate)) {
    				sDate = Util.parseZuluTime(srchSDate);
    			}
    			if (Util.isValid(srchEDate)) {
    				eDate = Util.parseZuluTime(srchEDate);
    			}
    			
    			if (Util.isValid(srchIds)) {
    				List<String> tokens = Util.tokenizeValidStr(srchIds);
    				for(String s : tokens) {
    					ids.add(Util.parseInt(s));
    				}
    			}
    		}
    		
    		AdcCampaign campaign = adcService.getCampaign((int)request.getReqIntValue1());
    		if (campaign != null) {
    			if (sDate == null) {
        			sDate = campaign.getStartDate();
    			}
    			if (eDate == null) {
        			eDate = campaign.getEndDate();
    			}
    			
    			boolean idsReady = ids.size() > 0;
    			List<AdcAd> adList = adcService.getAdListByCampaignId(campaign.getId());
    			for (AdcAd ad : adList) {
    				if (!idsReady) {
        				ids.add(ad.getId());
    				}
    			}
    		}

    		
    		DataSourceResult result = revService.getScrHourlyPlayList(request, (int)request.getReqIntValue2(),
    				sDate, eDate);
    		
    		for(Object obj : result.getData()) {
    			RevScrHourlyPlay scrHrlyPlay = (RevScrHourlyPlay) obj;
    			
    			SolUtil.setScreenReqStatus(scrHrlyPlay.getScreen());
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readWeekDaily", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
