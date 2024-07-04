package net.doohad.controllers.rev;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevDailyAchv;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.RevService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.rev.RevDailyApiStatItem;
import net.doohad.viewmodels.rev.RevMedRptCampItem;
import net.doohad.viewmodels.rev.RevMedRptDataItem;

/**
 * 매체 리포트 컨트롤러(캠페인)
 */
@Controller("rev-medium-report-campaign-controller")
@RequestMapping(value="/rev/mediumrpt/camp")
public class RevMediumRptCampaignController {

	private static final Logger logger = LoggerFactory.getLogger(RevMediumRptCampaignController.class);

	
    @Autowired 
    private AdcService adcService;

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
	 * 매체 리포트(캠페인) 페이지
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
    	
    	
		boolean hasDailyAchvData = false;

		// 필터 대상 캠페인 목록
    	HashMap<String, AdcCampaign> allCampMap = new HashMap<String, AdcCampaign>();
    	HashMap<String, AdcCampaign> campMap = new HashMap<String, AdcCampaign>();
		List<AdcAd> mediumAdList = adcService.getAdListByMediumId(medium.getId());
		for(AdcAd ad : mediumAdList) {
			allCampMap.put("C" + ad.getCampaign().getId(), ad.getCampaign());
		}
		
		List<Tuple> hpCampIdList = revService.getHourlyPlayCampIdListByMediumPlayDate(medium.getId(), date);
		for(Tuple tuple : hpCampIdList) {
			AdcCampaign camp = allCampMap.get("C" + (Integer) tuple.get(0));
			if (camp != null) {
				campMap.put("C" + camp.getId(), camp);

    			if (!hasDailyAchvData && revService.getDailyAchvByTypeIdPlayDate(
    					"C", camp.getId(), date) != null) {
    				hasDailyAchvData = true;
    			}
			}
		}
		
		
		Date today = Util.removeTimeOfDate(new Date());
		boolean isToday = Util.toSimpleString(today).equals(Util.toSimpleString(date));
		if (isToday) {
			// 오늘 날짜인 경우, 아직 노출 통계가 없는 자료가 존재할 수 있다.
    		List<AdcAdCreative> candiList = adcService.getCandiAdCreativeListByMediumIdDate
					(medium.getId(), today, today);
			
    		for(AdcAdCreative adCreat : candiList) {
    			campMap.put("C" + adCreat.getAd().getCampaign().getId(), adCreat.getAd().getCampaign());
    		}
		}
		
    	ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		ArrayList<AdcCampaign> campList = new ArrayList<AdcCampaign>(campMap.values());
		for(AdcCampaign camp : campList) {
			list.add(new DropDownListItem("selected", camp.getName(), String.valueOf(camp.getId())));
		}
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

		model.addAttribute("Campaigns", list);
		model.addAttribute("CampaignsSize", list.size());
		model.addAttribute("hasDailyAchvData", hasDailyAchvData);

		
        return "rev/mediumrpt/mediumrpt-campaign";
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
	 * 읽기 액션 - 필터 개요
	 */
    @RequestMapping(value = "/readOverview", method = RequestMethod.POST)
    public @ResponseBody List<RevMedRptCampItem> readOverview(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		
    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    		
    		ArrayList<RevMedRptCampItem> retList = new ArrayList<RevMedRptCampItem>();

    		Date date = Util.parseDate(request.getReqStrValue1());
    		String srchIds = request.getReqStrValue2();
    		
    		// 검색 폼에서 검색 선택된 캠페인 id
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		
			if (Util.isValid(srchIds)) {
				List<String> tokens = Util.tokenizeValidStr(srchIds);
				for(String s : tokens) {
					ids.add(Util.parseInt(s));
				}
			}

			
    		if (medium != null && date != null) {
    			
    			HashMap<String, RevMedRptCampItem> map = new HashMap<String, RevMedRptCampItem>();
    			
    			HashMap<String, AdcCampaign> allCampMap = new HashMap<String, AdcCampaign>();
    			List<AdcCampaign> mediumCampList = adcService.getCampaignListByMediumId(medium.getId());
    			for(AdcCampaign camp : mediumCampList) {
    				allCampMap.put("C" + camp.getId(), camp);
    			}
    			
        		Date today = Util.removeTimeOfDate(new Date());
        		boolean isToday = Util.toSimpleString(today).equals(Util.toSimpleString(date));
        		if (isToday) {
        			// 오늘 날짜인 경우, 아직 노출 통계가 없는 자료가 존재할 수 있다.
            		List<AdcAdCreative> candiList = adcService.getCandiAdCreativeListByMediumIdDate
        					(medium.getId(), today, today);
        			
            		for(AdcAdCreative adCreat : candiList) {
            			map.put("C" + adCreat.getAd().getCampaign().getId(), new RevMedRptCampItem(adCreat.getAd().getCampaign()));
            		}
        		}
        		

        		boolean idsReady = ids.size() > 0;
        		List<Tuple> hpCampIdList = revService.getHourlyPlayCampIdListByMediumPlayDate(medium.getId(), date);
        		for(Tuple tuple : hpCampIdList) {
        			AdcCampaign camp = allCampMap.get("C" + (Integer) tuple.get(0));
        			if (camp != null) {
        				if (!idsReady) {
            				ids.add(camp.getId());
        				}
        				
        				if (ids.contains(camp.getId())) {
            				map.put("C" + camp.getId(), new RevMedRptCampItem(camp));
        				}
        			}
        		}

        		
        		int sysValuePct = Util.parseInt(SolUtil.getOptValue(medium.getId(), "sysValue.pct"));

        		retList = new ArrayList<RevMedRptCampItem>(map.values());
        		
        		for(RevMedRptCampItem item : retList) {
        			if (isToday) {
            			List<AdcAdCreative> adCreatList = adcService.getActiveAdCreativeListByCampaignId(item.getId());
            			if (adCreatList.size() == 0) {
            				item.setStatusCard("R");
            			} else {
                			
                			boolean hasEffActive = false;
                			for(AdcAdCreative adCreative : adCreatList) {
                				if (Util.isBetween(Util.removeTimeOfDate(new Date()), adCreative.getStartDate(), adCreative.getEndDate())) {
                					hasEffActive = true;
                					break;
                				}
                			}
                			if (!hasEffActive) {
                				item.setStatusCard("Y");
                			}
            			}
        			}
        			
        			if (item.getGoalType().equals("I") && item.getGoalValue() > 0 &&
        					item.getSysValue() == 0 && sysValuePct > 0) {
        				item.setSysValue((int)Math.ceil((float)item.getGoalValue() * (float)sysValuePct / 100f));
        				item.setSysValueProposed(true);
        			}
        		}
        		
        		Collections.sort(retList, new Comparator<RevMedRptCampItem>() {
        	    	public int compare(RevMedRptCampItem item1, RevMedRptCampItem item2) {
        	    		return item1.getName().compareTo(item2.getName());
        	    	}
        	    });
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("readOverview", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 통계
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<RevMedRptDataItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		
    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    		
    		ArrayList<RevMedRptDataItem> retList = new ArrayList<RevMedRptDataItem>();

    		Date date = Util.parseDate(request.getReqStrValue1());
    		String srchIds = request.getReqStrValue2();
    		
    		// 검색 폼에서 검색 선택된 캠페인 id
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		
			if (Util.isValid(srchIds)) {
				List<String> tokens = Util.tokenizeValidStr(srchIds);
				for(String s : tokens) {
					ids.add(Util.parseInt(s));
				}
			}
    		
			
    		if (medium != null && date != null) {
    			
    			HashMap<String, RevMedRptDataItem> map = new HashMap<String, RevMedRptDataItem>();
    			
    			HashMap<String, AdcCampaign> allCampMap = new HashMap<String, AdcCampaign>();
    			List<AdcCampaign> mediumCampList = adcService.getCampaignListByMediumId(medium.getId());
    			for(AdcCampaign camp : mediumCampList) {
    				allCampMap.put("C" + camp.getId(), camp);
    			}

    			
        		boolean idsReady = ids.size() > 0;
        		List<Tuple> hpCampIdList = revService.getHourlyPlayCampIdListByMediumPlayDate(medium.getId(), date);
        		for(Tuple tuple : hpCampIdList) {
        			AdcCampaign camp = allCampMap.get("C" + (Integer) tuple.get(0));
        			if (camp != null) {
        				if (!idsReady) {
            				ids.add(camp.getId());
        				}
        				
        				if (ids.contains(camp.getId())) {
        					
        					RevMedRptDataItem item = new RevMedRptDataItem(camp);
        					
            				// 대상 광고의 일별 달성 기록 설정
            				RevDailyAchv dailyAchv = revService.getDailyAchvByTypeIdPlayDate("C", camp.getId(), date);
            				if (dailyAchv != null) {
            					item.setGoalType(dailyAchv.getGoalType());
            					item.setTgtToday(dailyAchv.getTgtToday());
            					item.setAchvRatio(dailyAchv.getAchvRatio());
            				}
            				
            				map.put("C" + camp.getId(), item);
        				}
        			}
        		}
        		
        		List<Tuple> list = revService.getHourlyPlayStatGroupByCampIdInBetween(ids, date, date);
        		for (Tuple tuple : list) {
        			String key = "C" + String.valueOf((Integer)tuple.get(0));
        			if (map.containsKey(key)) {
        				RevMedRptDataItem item = map.get(key);
        				item.setTupleData(tuple);
            			
        				// actualCpm 설정
                		if (item.getTotal() == 0) {
                			item.setActualCpm(0);
                		} else {
                			item.setActualCpm((int)((long)item.getActualAmount() * 1000l / (long)item.getTotal()));
                		}
                		//-
        				
        				map.put(key, item);
        			}
        		}
        		
        		List<Tuple> cntList = revService.getScrHourlyPlayScrCntGroupByCampIdInBetween(ids, date, date);
        		for (Tuple tuple : cntList) {
        			String key = "C" + String.valueOf((Integer)tuple.get(0));
        			if (map.containsKey(key)) {
        				RevMedRptDataItem item = map.get(key);
        				item.setCntScreen(((BigInteger)tuple.get(1)).intValue());
        				map.put(key, item);
        			}
        		}

        		
        		retList = new ArrayList<RevMedRptDataItem>(map.values());
        		
        		Collections.sort(retList, new Comparator<RevMedRptDataItem>() {
        	    	public int compare(RevMedRptDataItem item1, RevMedRptDataItem item2) {
        	    		return item1.getName().compareTo(item2.getName());
        	    	}
        	    });
        		
        		
        		// 합계 행
        		RevMedRptDataItem totalItem = new RevMedRptDataItem();
        		totalItem.setName("캠페인: " + retList.size() + " 건");
        		totalItem.setTotalRow(true);
        		
        		for(RevMedRptDataItem item : retList) {
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
    			
        		if (totalItem.getTotal() == 0) {
        			totalItem.setActualCpm(0);
        		} else {
        			totalItem.setActualCpm((int)((long)totalItem.getActualAmount() * 1000l / (long)totalItem.getTotal()));
        		}
    			
        		retList.add(totalItem);
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

}
