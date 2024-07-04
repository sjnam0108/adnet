package net.doohad.controllers.org;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgMediumOpt;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.inv.InvSiteMapLocItem;
import net.doohad.viewmodels.org.OrgOptionItem;

/**
 * 현재 매체 컨트롤러
 */
@Controller("org-curr-medium-controller")
@RequestMapping(value="/org/currmedium")
public class OrgCurrMediumController {

	private static final Logger logger = LoggerFactory.getLogger(OrgCurrMediumController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private InvService invService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 현재 매체 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});
    	
    	int mediumId = Util.getSessionMediumId(session);
    	
    	String title = "현재 매체";
    	String subtitle = "";
    	String name = "";
    	String shortName = "";
    	String apiKey = "";
    	String bizHour = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    	String bizHours = "0";
    	
    	KnlMedium medium = knlService.getMedium(mediumId);
    	if (medium != null) {
    		title = medium.getName();
    		subtitle = medium.getShortName();
    		name = medium.getName();
    		shortName = medium.getShortName();
    		apiKey = medium.getApiKey();
    		bizHour = medium.getBizHour();
    		bizHours = String.valueOf(SolUtil.getHourCnt(bizHour));
    	}

    	// 페이지 제목
    	model.addAttribute("pageTitle", title);
    	model.addAttribute("subtitle", subtitle);
    	
    	model.addAttribute("name", name);
    	model.addAttribute("shortName", shortName);
    	model.addAttribute("apiKey", apiKey);
    	
    	model.addAttribute("bizHour", bizHour);
    	model.addAttribute("bizHours", bizHours);
    	
    	
    	DecimalFormat dcFormat = new DecimalFormat("#,###,##0");
    	
    	model.addAttribute("availSiteCnt", 
    			dcFormat.format(invService.getAvailSiteCountByMediumId(mediumId)));
    	model.addAttribute("activeSiteCnt", 
    			dcFormat.format(invService.getActiveSiteCountByMediumId(mediumId)));
    	model.addAttribute("availScrCnt", 
    			dcFormat.format(invService.getAvailScreenCountByMediumId(mediumId)));
    	model.addAttribute("activeScrCnt", 
    			dcFormat.format(invService.getActiveScreenCountByMediumId(mediumId)));

    	
        return "org/currmedium";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<OrgOptionItem> read(Locale locale, HttpSession session) {
    	try {
    		List<OrgMediumOpt> list = orgService.getMediumOptListByMediumId(Util.getSessionMediumId(session));
    		
    		List<OrgOptionItem> baseItems = getBaseOptionItems();
    		
    		for(OrgMediumOpt medOpt : list) {
    			for(OrgOptionItem item : baseItems) {
    				if (item.getCode().equals(medOpt.getCode())) {
    					item.setValue(medOpt.getValue());
    					break;
    				}
    			}
    		}
    		
    		return baseItems;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
    private List<OrgOptionItem> getBaseOptionItems() {
    	
    	ArrayList<OrgOptionItem> list = new ArrayList<OrgOptionItem>();
    	
    	
    	// 매체 옵션 항목 기본값 설정
    	//
    	//   - activeCount.screen	activeScrCount	매체의 화면 수													[blank]
    	//   - freqCap.ad			adFreqCap		동일 광고 송출 금지 시간										[blank]
    	//   - freqCap.advertiser	advFreqCap		동일 광고주 광고 송출 금지 시간									[blank]
    	//   - freqCap.category		catFreqCap		동일 범주 광고 송출 금지 시간									[blank]
    	//   - freqCap.daily.screen	adDailyScrCap	화면당 하루 노출한도											[blank]
    	//   - impress.per.hour		impPlanPerHour	시간당 광고 노출 계획: 1/2/3/4/5/6/7/8/9/10/12/15/20/30/60		"6"
    	//   - inven.default		invenValues		인벤 자료 등록 요청 시 매체의 기본값: 							"{ }"
    	//   - sysValue.pct			sysValuePct		보장 노출량 대비 목표 노출량 기본 비율:							[blank]
    	//   - opt.list				opts			매체 옵션 목록:													tester,테스터
    	//
    	// 항목의 시스템 내정값이 [blank]가 아닐 경우 SolUtil.getOptValue 메소드 변경 필요함
    	//
    	list.add(new OrgOptionItem("activeCount.screen", ""));
    	list.add(new OrgOptionItem("freqCap.ad", ""));
    	list.add(new OrgOptionItem("freqCap.advertiser", ""));
    	list.add(new OrgOptionItem("freqCap.category", ""));
    	list.add(new OrgOptionItem("freqCap.daily.screen", ""));
    	list.add(new OrgOptionItem("impress.per.hour", "6"));
    	list.add(new OrgOptionItem("inven.default", "{ }"));
    	list.add(new OrgOptionItem("sysValue.pct", ""));
    	list.add(new OrgOptionItem("opt.list", "tester,테스터"));
    	
    	
    	return list;
    }
	
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		
    	// invenValues는 빈값이 전달될 수 있기 때문에 param validation에서 제외
		String impPlanPerHour = (String)model.get("impPlanPerHour");

		// 아래는 모두 빈값 전달이 가능
		String invenValues = (String)model.get("invenValues");
		String sysValuePct = Util.parseString((String)model.get("sysValuePct"));
		String adFreqCap = Util.parseString((String)model.get("adFreqCap"));
		String adDailyScrCap = Util.parseString((String)model.get("adDailyScrCap"));
		String advFreqCap = Util.parseString((String)model.get("advFreqCap"));
		String activeScrCount = Util.parseString((String)model.get("activeScrCount"));
		String catFreqCap = Util.parseString((String)model.get("catFreqCap"));
		String opts = (String)model.get("opts");
		
		if (Util.isNotValid(impPlanPerHour)) {
			throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
		}
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
		if (medium == null) {
			throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
		}
		
		
		List<OrgOptionItem> baseItems = getBaseOptionItems();
		for(OrgOptionItem item : baseItems) {
			if (item.getCode().equals("inven.default")) {
				if (Util.isValid(invenValues)) {
					item.setValue(invenValues);
				}
			} else if (item.getCode().equals("opt.list")) {
				if (Util.isValid(opts)) {
					item.setValue(opts);
				}
			} else if (item.getCode().equals("sysValue.pct")) {
				item.setValue(sysValuePct);
			} else if (item.getCode().equals("freqCap.ad")) {
				item.setValue(adFreqCap);
			} else if (item.getCode().equals("freqCap.daily.screen")) {
				item.setValue(adDailyScrCap);
			} else if (item.getCode().equals("activeCount.screen")) {
				item.setValue(activeScrCount);
			} else if (item.getCode().equals("freqCap.advertiser")) {
				item.setValue(advFreqCap);
			} else if (item.getCode().equals("impress.per.hour")) {
				item.setValue(impPlanPerHour);
			} else if (item.getCode().equals("freqCap.category")) {
				item.setValue(catFreqCap);
			}
		}
		
		ArrayList<OrgMediumOpt> items = new ArrayList<OrgMediumOpt>();
		for(OrgOptionItem item : baseItems) {
			OrgMediumOpt mediumOpt = orgService.getMediumOpt(medium, item.getCode());
			
			if (mediumOpt == null) {
				mediumOpt = new OrgMediumOpt(medium, item.getCode(), item.getValue(), session);
			} else {
				mediumOpt.setValue(item.getValue());
				mediumOpt.touchWho(session);
			}
			
			items.add(mediumOpt);
		}

		// 저장된 옵션 검토(삭제 대상 파악)
		List<OrgMediumOpt> dbOptions = orgService.getMediumOptListByMediumId(medium.getId());
		ArrayList<OrgMediumOpt> delOptions = new ArrayList<OrgMediumOpt>();
		
		for(OrgMediumOpt dbOpt : dbOptions) {
			boolean exists = false;
			for(OrgOptionItem item : baseItems) {
				if (item.getCode().equals(dbOpt.getCode())) {
					exists = true;
					break;
				}
			}
			
			if (!exists) {
				delOptions.add(dbOpt);
			}
		}
		
		try {
			// 삭제 대상 자료 삭제
			orgService.deleteMediumOpts(delOptions);
			
			// 추가, 변경 대상 자료 저장
			for (OrgMediumOpt mediumOpt : items) {
				orgService.saveOrUpdate(mediumOpt);
			}
		} catch (Exception e) {
    		logger.error("update", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}

        return "OK";
    }
    
    
	/**
	 * 변경 액션 - 운영 시간
	 */
    @RequestMapping(value = "/updateTime", method = RequestMethod.POST)
    public @ResponseBody String updateBizTime(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium target = knlService.getMedium(Util.getSessionMediumId(session));

    	String bizHour = (String)model.get("bizHour");
    	
    	// 파라미터 검증
    	if (target == null || Util.isNotValid(bizHour)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (target != null) {

    		target.setBizHour(bizHour);
            
            target.touchWho(session);

            
            // DB 작업 수행 결과 검증
            try {
                knlService.saveOrUpdate(target);
            } catch (Exception e) {
        		logger.error("updateBizTime", e);
            	throw new ServerOperationForbiddenException("SaveError");
            }
    	}
    	
        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 사이트의 활성화된 위치 정보 
	 */
    @RequestMapping(value = "/readSiteLocs", method = RequestMethod.POST)
    public @ResponseBody List<InvSiteMapLocItem> readSiteLocs(@RequestBody Map<String, Object> model, 
    		Locale locale, HttpSession session) {
    	
    	try {
    		ArrayList<InvSiteMapLocItem> items = new ArrayList<InvSiteMapLocItem>();
    		
    		List<Tuple> locList = invService.getActiveSiteLocListByMediumId(
    				Util.getSessionMediumId(session));
    		
    		for(Tuple tuple : locList) {
    			double lat = (double) Util.parseFloat((String) tuple.get(2));
    			double lng = (double) Util.parseFloat((String) tuple.get(3));

    			if (lng >= 124d && lng <= 131d && lat >= 33d && lat <= 43d) {
        			items.add(new InvSiteMapLocItem((String) tuple.get(0), (String) tuple.get(1),
        					lat, lng));
    			}
    		}
    		
    		return items;
    	} catch (Exception e) {
    		logger.error("readSiteLocs", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
