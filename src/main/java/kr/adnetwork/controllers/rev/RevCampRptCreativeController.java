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
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.rev.RevRptChartItem;
import kr.adnetwork.viewmodels.rev.RevRptCreatItem;
import kr.adnetwork.viewmodels.rev.RevRptDailyItem;
import kr.adnetwork.viewmodels.rev.RevRptWeekDailyItem;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * 리포트 컨트롤러(광고 소재)
 */
@Controller("rev-campaign-report-creative-controller")
@RequestMapping(value="/rev/camprpt/creat")
public class RevCampRptCreativeController {

	private static final Logger logger = LoggerFactory.getLogger(RevCampRptCreativeController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private FndService fndService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 리포트(광고 소재) 페이지
	 */
    @RequestMapping(value = {"/{campId}", "/{campId}/"}, method = RequestMethod.GET)
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

    	
    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
    	List<Tuple> tupleList = adcService.getCreativeIdListByCampaignId(campaign.getId());
    	for (Tuple tuple : tupleList) {
    		if (ids.contains((Integer)tuple.get(0))) {
    			continue;
    		}
    		list.add(new DropDownListItem("selected", (String)tuple.get(1), String.valueOf((Integer)tuple.get(0))));
    		ids.add((Integer)tuple.get(0));
    	}
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		
		
		model.addAttribute("Creatives", list);
		model.addAttribute("CreativesSize", list.size());
		model.addAttribute("filterMinDate", Util.toSimpleString(campaign.getStartDate(), "yyyy-MM-dd"));
		model.addAttribute("filterMaxDate", Util.toSimpleString(campaign.getEndDate(), "yyyy-MM-dd"));
		
		
		// 필터 광고 확인(다이렉트 광고 소재 필터링)
		AdcCreative creat = adcService.getCreative(Util.parseInt(request.getParameter("filter")));
		String filterId = "", filterSDate = "", filterEDate = "";
		if (creat != null) {
			filterId = String.valueOf(creat.getId());
			filterSDate = Util.toSimpleString(campaign.getStartDate(), "yyyy-MM-dd");
			filterEDate = Util.toSimpleString(campaign.getEndDate(), "yyyy-MM-dd");
		}
		model.addAttribute("filterId", filterId);
		model.addAttribute("filterSDate", filterSDate);
		model.addAttribute("filterEDate", filterEDate);
    	
    	
        return "rev/camprpt/camprpt-creative";
    }
    
    
	/**
	 * 읽기 액션 - 광고 소재별
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<RevRptCreatItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		Date sDate = null;
    		Date eDate = null;

    		HashMap<String, RevRptCreatItem> map = new HashMap<String, RevRptCreatItem>();
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
    		List<Tuple> countList = adcService.getCreatTargetCountGroupByMediumCreativeId(Util.getSessionMediumId(session));
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
    			
    			boolean idsReady = ids.size() > 0;
    	    	List<Tuple> tupleList = adcService.getCreativeIdListByCampaignId(campaign.getId());
    	    	for (Tuple tuple : tupleList) {
    	    		AdcCreative creative = adcService.getCreative((Integer)tuple.get(0));
    	    		if (creative != null) {
        				if (!idsReady) {
            				ids.add(creative.getId());
        				}
        				
        				if (ids.contains(creative.getId())) {
            				RevRptCreatItem item = new RevRptCreatItem(creative);
            				item.setInvenTargeted(targetIds.contains(creative.getId()));
                			
                			String resolutions = "";
                			
                			// 이 값이 유효하다는 것: 게시 유형이 지정되어 있고, 유효한 게시 크기(해상도) 존재
                			String fixedReso = "";
                			if (Util.isValid(creative.getViewTypeCode())) {
                				fixedReso = fndService.getViewTypeResoByCode(creative.getViewTypeCode());
                			}

                			List<AdcCreatFile> fileList = adcService.getCreatFileListByCreativeId(creative.getId());
                			for(AdcCreatFile creatFile : fileList) {
                    			
                    			// 20% 범위로 적합도 판정
                				int fitness = Util.isValid(fixedReso) ?
                						SolUtil.measureResolutionWith(creatFile.getResolution(), fixedReso, 20) :
                						adcService.measureResolutionWithMedium(
                	            				creatFile.getResolution(), creatFile.getMedium().getId(), 20);
                				
                				if (Util.isValid(resolutions)) {
                					resolutions += "|" + String.valueOf(fitness) + ":" + creatFile.getResolution();
                				} else {
                					resolutions = String.valueOf(fitness) + ":" + creatFile.getResolution();
                				}
                			}
                			
                			item.setFileResolutions(resolutions);
            				
            				
            				map.put("C" + creative.getId(), item);
        				}
    	    		}
    			}
    		}
    		
    		List<Tuple> list = revService.getHourlyPlayStatGroupByCreatIdInBetween(ids, sDate, eDate);
    		for (Tuple tuple : list) {
    			String key = "C" + String.valueOf((Integer)tuple.get(0));
    			if (map.containsKey(key)) {
    				RevRptCreatItem item = map.get(key);
    				item.setTupleData(tuple);
    				
    				map.put(key, item);
    			}
    		}
    		
    		List<Tuple> cntList = revService.getScrHourlyPlayScrCntGroupByCreatIdInBetween(ids, sDate, eDate);
    		for (Tuple tuple : cntList) {
    			String key = "C" + String.valueOf((Integer)tuple.get(0));
    			if (map.containsKey(key)) {
    				RevRptCreatItem item = map.get(key);
    				item.setCntScreen(((BigInteger)tuple.get(1)).intValue());
    				map.put(key, item);
    			}
    		}

    		ArrayList<RevRptCreatItem> retList = new ArrayList<RevRptCreatItem>(map.values());
    		
    		Collections.sort(retList, new Comparator<RevRptCreatItem>() {
    	    	public int compare(RevRptCreatItem item1, RevRptCreatItem item2) {
    	    		return item1.getName().compareTo(item2.getName());
    	    	}
    	    });

    		
    		// 합계 행
    		RevRptCreatItem totalItem = new RevRptCreatItem();
    		totalItem.setName("캠페인 합계: " + retList.size() + " 건");
    		totalItem.setTotalRow(true);
    		totalItem.setCntScreen(revService.getScrHourlyPlayScrCntCreatIdInBetween(ids, sDate, eDate));
    		
    		for(RevRptCreatItem item : retList) {
    			if (retList.size() > 1) {
    				item.setSingleCreatFiltered(true);
    			}
    			
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
    		}

			
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
    @RequestMapping(value = "/readDaily", method = RequestMethod.POST)
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
    		
    		AdcCampaign campaign = adcService.getCampaign((int)request.getReqIntValue1());
    		if (campaign != null) {
    			if (sDate == null) {
        			sDate = campaign.getStartDate();
    			}
    			if (eDate == null) {
        			eDate = campaign.getEndDate();
    			}
    			
    			boolean idsReady = ids.size() > 0;
    			List<Tuple> tupleList = adcService.getCreativeIdListByCampaignId(campaign.getId());
    			for (Tuple tuple : tupleList) {
    				if (!idsReady) {
        				ids.add((Integer)tuple.get(0));
    				}
    			}
    		}

    		HashMap<String, RevRptDailyItem> map = new HashMap<String, RevRptDailyItem>();
    		if (!campaign.getStatus().equals("U") && !campaign.getStatus().equals("T")) {
        		Date today = Util.removeTimeOfDate(new Date());
        		if (sDate != null && eDate != null) {
        			Date date = new Date(sDate.getTime());
        			do {
        				map.put(Util.toSimpleString(date, "yyyyMMdd"), new RevRptDailyItem(date));
        				date = Util.addDays(date, 1);
        			} while (!date.after(eDate) && !date.after(today));
        		}
        		
        		List<Tuple> list = revService.getHourlyPlayStatGroupByPlayDateCreatIdInBetween(ids, sDate, eDate);
        		for (Tuple tuple : list) {
        			String key = Util.toSimpleString((Date)tuple.get(0), "yyyyMMdd");
        			if (map.containsKey(key)) {
        				RevRptDailyItem item = map.get(key);
        				item.setTupleData(tuple);
        				map.put(key, item);
        			}
        		}
        		
        		List<Tuple> cntList = revService.getScrHourlyPlayScrCntGroupByPlayDateCreatIdInBetween(ids, sDate, eDate);
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
    @RequestMapping(value = "/readWeekDaily", method = RequestMethod.POST)
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
    			List<Tuple> tupleList = adcService.getCreativeIdListByCampaignId(campaign.getId());
    			for (Tuple tuple : tupleList) {
    				if (!idsReady) {
        				ids.add((Integer)tuple.get(0));
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
        		
        		List<Tuple> list = revService.getHourlyPlayStatGroupByWeekDayCreatIdInBetween(ids, sDate, eDate);
        		for (Tuple tuple : list) {
        			String key = (String)tuple.get(0);
        			key = key.equals("0") ? "7" : key;
        			if (map.containsKey(key)) {
        				RevRptWeekDailyItem item = map.get(key);
        				item.setTupleData(tuple);
        				map.put(key, item);
        			}
        		}
        		
        		List<Tuple> cntList = revService.getScrHourlyPlayScrCntGroupByWeekDayCreatIdInBetween(ids, sDate, eDate);
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
    @RequestMapping(value = "/readChart/{interval}", method = RequestMethod.POST)
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
    			List<Tuple> tupleList = adcService.getCreativeIdListByCampaignId(campaign.getId());
    			for (Tuple tuple : tupleList) {
    				if (!idsReady) {
        				ids.add((Integer)tuple.get(0));
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
        		
        		List<Tuple> list = revService.getHourlyPlayStatGroupByPlayDateCreatIdInBetween(ids, sDate, eDate);
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

}
