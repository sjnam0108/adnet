package net.doohad.controllers.adc;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.fnd.FndViewType;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 캠페인 컨트롤러(광고 목록)
 */
@Controller("adc-campaign-ad-controller")
@RequestMapping(value="")
public class AdcCampaignAdController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCampaignAdController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private FndService fndService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 캠페인 컨트롤러(광고 목록)
	 */
    @RequestMapping(value = {"/adc/campaign/{campId}", "/adc/campaign/{campId}/", 
    		"/adc/campaign/ads/{campId}", "/adc/campaign/ads/{campId}/"}, method = RequestMethod.GET)
    public String index1(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	AdcCampaign campaign = adcService.getCampaign(Util.parseInt(pathMap.get("campId")));
    	if (campaign == null || campaign.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/campaign";
    	}

    	
    	// 캠페인의 상태카드 설정
    	SolUtil.setCampaignStatusCard(campaign);
		
		// 쿠키에 있는 "현재" 광고 정보 등을 확인하고, 최종적으로 session에 currAdId, currAds 이름으로 정보를 설정한다.
		SolUtil.saveCurrAdsToSession(request, response, session, campaign.getId(), -1);

		
    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcAd");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	int mediumId = Util.getSessionMediumId(session);
		
		// 동일 광고 송출 금지 시간
		int freqCapAd = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.ad"));
		String mediumFreqCapAd = freqCapAd <= 0 ? "설정 안함" : freqCapAd + " 초";
		
		// 화면당 하루 노출한도
		int dailyScrCap = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.daily.screen"));
		String mediumDailyScrCap = dailyScrCap <= 0 ? "설정 안함" : dailyScrCap + " 회";
		
		// 매체의 화면 수
		int activeScrCnt = Util.parseInt(SolUtil.getOptValue(mediumId, "activeCount.screen"), 1);

		
    	// 페이지 제목
    	model.addAttribute("pageTitle", "캠페인");

    	model.addAttribute("Campaign", campaign);

    	model.addAttribute("mediumFreqCapAd", mediumFreqCapAd);
    	model.addAttribute("mediumDailyScrCap", mediumDailyScrCap);
    	
    	// 노출량 계산기 전달용
    	model.addAttribute("mediumActiveScrCnt", activeScrCnt);
    	
		model.addAttribute("ViewTypes", getViewTypeDropDownList(mediumId));
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "adc/campaign/camp-ad";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/adc/campaign/ads/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = adcService.getAdList(request, (int)request.getReqIntValue1());

    		int mediumId = Util.getSessionMediumId(session);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getAdTargetCountGroupByMediumAdId(mediumId);
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}

    		
			int sysValuePct = Util.parseInt(SolUtil.getOptValue(mediumId, "sysValue.pct"));
			int freqCapAd = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.ad"));
			int dailyScrCap = Util.parseInt(SolUtil.getOptValue(mediumId, "freqCap.daily.screen"));

			for(Object obj : result.getData()) {
    			AdcAd ad = (AdcAd) obj;
    			
    			ad.setCreativeCount(adcService.getAdCreativeCountByAdId(ad.getId()));
    			
    			if (targetIds.contains(ad.getId())) {
    				ad.setInvenTargeted(true);
    			}
    			
    			// 모바일 타겟팅 여부 설정
    			if (adcService.getMobTargetCountByAdId(ad.getId()) > 0) {
    				ad.setMobTargeted(true);
    			}
    			
    			if (ad.getGoalType().equals("I") && ad.getGoalValue() > 0 &&
    					ad.getSysValue() == 0 && sysValuePct > 0) {
    				ad.setProposedSysValue((int)Math.ceil((float)ad.getGoalValue() * (float)sysValuePct / 100f));
    			}
    			
    			if (freqCapAd > 0 && ad.getFreqCap() == 0) {
    				ad.setProposedFreqCap(freqCapAd);
    			}
    			if (dailyScrCap > 0 && ad.getDailyScrCap() == 0) {
    				ad.setProposedDailyScrCap(dailyScrCap);
    			}
    			
    			// 광고의 상태카드 설정
    			SolUtil.setAdStatusCard(ad);
    			
    			// 오늘/하루 목표
    			if (ad != null && ad.getTgtToday() > 0) {
    				if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
    						(ad.getStatus().equals("A") || ad.getStatus().equals("R")) &&
    						(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
    						(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
    					
    					String tgtTodayDisp = String.format("%s: ", ad.getStatus().equals("A") ? "하루 목표" : "오늘 목표");
    					tgtTodayDisp += String.format("%s %s %s",
    							ad.getGoalType().equals("A") ? "광고 예산" : "노출량",
    							new DecimalFormat("###,###,##0").format(ad.getTgtToday()),
    							ad.getGoalType().equals("A") ? "원" : "회");
    					
    					ad.setTgtTodayDisp(tgtTodayDisp);
    				}
    			}
    			//-
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 현재 매체에서 이용가능한 게시 유형 획득
	 */
    private List<DropDownListItem> getViewTypeDropDownList(int mediumId) {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	// 빈 행 추가
    	retList.add(new DropDownListItem("", ""));
    	
    	
    	List<FndViewType> viewTypeList = fndService.getViewTypeList();
    	
    	List<String> viewTypes = SolUtil.getViewTypeListByMediumId(mediumId);
    	for(String s : viewTypes) {
    		String text = s;
    		for(FndViewType viewType : viewTypeList) {
    			if (viewType.getCode().equals(s)) {
    				text = String.format("%s - %s", s, viewType.getName());
    			}
    		}
    		retList.add(new DropDownListItem(text, s));
    	}
    	
    	return retList;
    }
}
