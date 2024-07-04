package net.doohad.controllers.rev;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.service.AdcService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.rev.RevRptOvwAdItem;
import net.doohad.viewmodels.rev.RevRptOvwCampaignItem;

/**
 * 리포트 컨트롤러
 */
@Controller("rev-campaign-report-controller")
@RequestMapping(value="/rev/camprpt")
public class RevCampRptController {

	private static final Logger logger = LoggerFactory.getLogger(RevCampRptController.class);
	
	
    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 리포트 페이지
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
    	model.addAttribute("pageTitle", "리포트");
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "rev/camprpt";
    }
    
    
	/**
	 * 읽기 액션 - 진행 중
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<RevRptOvwCampaignItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		ArrayList<RevRptOvwCampaignItem> retList = new ArrayList<RevRptOvwCampaignItem>();
    		
    		DataSourceResult result = adcService.getCampaignList(request, Util.getSessionMediumId(session));
    		
			int sysValuePct = Util.parseInt(SolUtil.getOptValue(Util.getSessionMediumId(session), "sysValue.pct"));
    		
    		for(Object obj : result.getData()) {
    			AdcCampaign campaign = (AdcCampaign) obj;
    			
    			int adCount = adcService.getAdCountByCampaignId(campaign.getId());
    			if (adCount == 0 || !campaign.getStatus().equals("R")) {
    				continue;
    			}
    			
    			RevRptOvwCampaignItem item = new RevRptOvwCampaignItem(campaign, adCount);
    			
    			if (campaign.isSelfManaged() && campaign.getGoalType().equals("I") && campaign.getGoalValue() > 0 &&
    					campaign.getSysValue() == 0 && sysValuePct > 0) {
    				item.setProposedSysValue((int)Math.ceil((float)campaign.getGoalValue() * (float)sysValuePct / 100f));
    			}
    			
    			// 오늘/하루 목표
    			if (campaign.getTgtToday() > 0 && campaign.isSelfManaged() && (campaign.getStatus().equals("U") || campaign.getStatus().equals("R")) &&
    					(campaign.getGoalType().equals("A") || campaign.getGoalType().equals("I"))) {
    					
					String tgtTodayDisp = String.format("%s: ", campaign.getStatus().equals("A") ? "하루 목표" : "오늘 목표");
					tgtTodayDisp += String.format("%s %s %s",
							campaign.getGoalType().equals("A") ? "광고 예산" : "노출량",
							new DecimalFormat("###,###,##0").format(campaign.getTgtToday()),
							campaign.getGoalType().equals("A") ? "원" : "회");
					
					item.setTgtTodayDisp(tgtTodayDisp);
    			}
    			//-
    			
    			
    			retList.add(item);
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 캠페인
	 */
    @RequestMapping(value = "/readCamp", method = RequestMethod.POST)
    public @ResponseBody List<RevRptOvwCampaignItem> readCamp(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		ArrayList<RevRptOvwCampaignItem> retList = new ArrayList<RevRptOvwCampaignItem>();
    		
    		DataSourceResult result = adcService.getCampaignList(request, Util.getSessionMediumId(session));
    		
    		for(Object obj : result.getData()) {
    			AdcCampaign campaign = (AdcCampaign) obj;
    			
    			int adCount = adcService.getAdCountByCampaignId(campaign.getId());
    			if (adCount == 0) {
    				continue;
    			}
    			
    			retList.add(new RevRptOvwCampaignItem(campaign, adCount));
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 광고
	 */
    @RequestMapping(value = "/readAd", method = RequestMethod.POST)
    public @ResponseBody List<RevRptOvwAdItem> readAd(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		ArrayList<RevRptOvwAdItem> retList = new ArrayList<RevRptOvwAdItem>();

    		int mediumId = Util.getSessionMediumId(session);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getAdTargetCountGroupByMediumAdId(mediumId);
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
			
			int sysValuePct = Util.parseInt(SolUtil.getOptValue(mediumId, "sysValue.pct"));
			
    		List<AdcAd> adList = adcService.getAdListByMediumId(mediumId);
    		for(AdcAd ad : adList) {
    			RevRptOvwAdItem item = new RevRptOvwAdItem(ad);
    			
    			if (targetIds.contains(ad.getId())) {
    				item.setInvenTargeted(true);
    			}
    			
    			// 모바일 타겟팅 여부 설정
    			if (adcService.getMobTargetCountByAdId(ad.getId()) > 0) {
    				item.setMobTargeted(true);
    			}
    			
    			if (ad.getGoalType().equals("I") && ad.getGoalValue() > 0 &&
    					ad.getSysValue() == 0 && sysValuePct > 0) {
    				item.setProposedSysValue((int)Math.ceil((float)ad.getGoalValue() * (float)sysValuePct / 100f));
    			}
    			
    			retList.add(item);
    		}
    		
    		return retList;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 캠페인 상태 정보
	 */
    @RequestMapping(value = "/readCampStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readCampStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-alarm-clock fa-fw", "시작전", "U"));
		list.add(new DropDownListItem("fa-regular fa-bolt-lightning fa-fw text-orange", "진행", "R"));
		list.add(new DropDownListItem("fa-regular fa-flag-checkered fa-fw", "완료", "C"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }
    
    
	/**
	 * 읽기 액션 - 광고 상태 정보
	 */
    @RequestMapping(value = "/readAdStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAdStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-asterisk fa-fw", "준비", "D"));
		list.add(new DropDownListItem("fa-regular fa-square-question fa-fw", "승인대기", "P"));
		list.add(new DropDownListItem("fa-regular fa-do-not-enter fa-fw", "거절", "J"));
		list.add(new DropDownListItem("fa-regular fa-alarm-clock fa-fw", "예약", "A"));
		list.add(new DropDownListItem("fa-regular fa-bolt-lightning text-orange fa-fw", "진행", "R"));
		list.add(new DropDownListItem("fa-regular fa-flag-checkered fa-fw", "완료", "C"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 광고 구매 유형 정보
	 */
    @RequestMapping(value = "/readAdPurchTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAdPurchTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-hexagon-check text-blue fa-fw", "목표 보장", "G"));
		list.add(new DropDownListItem("fa-regular fa-hexagon-exclamation fa-fw", "목표 비보장", "N"));
		list.add(new DropDownListItem("fa-regular fa-house fa-fw", "하우스 광고", "H"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 광고 집행 방법 정보
	 */
    @RequestMapping(value = "/readAdGoalTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAdGoalTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-sack-dollar fa-fw", "광고 예산", "A"));
		list.add(new DropDownListItem("fa-regular fa-eye fa-fw", "노출량", "I"));
		list.add(new DropDownListItem("fa-regular fa-infinity fa-fw", "무제한 노출", "U"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 일별 광고 분산 정책 정보
	 */
    @RequestMapping(value = "/readImpDailyTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readImpDailyTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-equals fa-fw", "모든 날짜 균등", "E"));
		list.add(new DropDownListItem("fa-regular fa-bars-staggered fa-fw", "통계 기반 요일별 차등", "W"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 하루 광고 분산 정책 정보
	 */
    @RequestMapping(value = "/readImpHourlyTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readImpHourlyTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-equals fa-fw", "모든 시간 균등", "E"));
		list.add(new DropDownListItem("fa-regular fa-sun fa-fw", "일과 시간 집중", "D"));
		
		return list;
    }
}
