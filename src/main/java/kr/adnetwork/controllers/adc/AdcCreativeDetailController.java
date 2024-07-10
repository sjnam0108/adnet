package kr.adnetwork.controllers.adc;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.knl.KnlUser;
import kr.adnetwork.models.org.OrgAdvertiser;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.SysService;
import kr.adnetwork.models.sys.SysAuditTrail;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;

/**
 * 광고주 컨트롤러(소재 상세)
 */
@Controller("adc-creative-detail-controller")
@RequestMapping(value="/adc/creative/detail")
public class AdcCreativeDetailController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCreativeDetailController.class);

	
    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private SysService sysService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;

    
	/**
	 * 광고주 컨트롤러(소재 상세)
	 */
    @RequestMapping(value = {"/{advId}", "/{advId}/", "/{advId}/{creatId}", "/{advId}/{creatId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	OrgAdvertiser advertiser = orgService.getAdvertiser(Util.parseInt(pathMap.get("advId")));
    	if (advertiser == null || advertiser.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/adc/creative";
    	}

    	// "현재" 광고 소재 선택 변경의 경우
    	int creatId = Util.parseInt(pathMap.get("creatId"));
    	if (creatId > 0) {
    		AdcCreative creative = adcService.getCreative(creatId);
    		if (creative == null || creative.getAdvertiser().getId() != advertiser.getId()) {
    			creatId = -1;
    		}
    	}
    	
		
		List<AdcCampaign> campList = adcService.getCampaignLisyByAdvertiserId(advertiser.getId());
    	for(AdcCampaign campaign : campList) {
    		SolUtil.setCampaignStatusCard(campaign);
    	}
    	model.addAttribute("Camp01", (campList.size() > 0 ? Util.getObjectToJson(campList.get(0), false) : "null"));
    	model.addAttribute("Camp02", (campList.size() > 1 ? Util.getObjectToJson(campList.get(1), false) : "null"));
    	model.addAttribute("Camp03", (campList.size() > 2 ? Util.getObjectToJson(campList.get(2), false) : "null"));
    	
    	// 쿠키에 있는 "현재" 광고 소재 정보 등을 확인하고, 최종적으로 session에 currCreatId, currCreatives 이름으로 정보를 설정한다.
		int currCreatId = SolUtil.saveCurrCreativesToSession(request, response, session, advertiser.getId(), creatId);
		AdcCreative creative = adcService.getCreative(currCreatId);

		// 광고 소재의 인벤 타겟팅 여부 설정
    	SolUtil.setCreativeInvenTargeted(creative);
    	SolUtil.setCreativeResolutions(creative);
    	SolUtil.setCreativeFixedResolution(creative);
		

    	modelMgr.addMainMenuModel(model, locale, session, request, "AdcCreative");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고 소재");

    	model.addAttribute("Advertiser", advertiser);
    	model.addAttribute("Creative", creative);
    	model.addAttribute("CreatCount", adcService.getCreativeCountByAdvertiserId(advertiser.getId()));
    	model.addAttribute("CreatFileCount", adcService.getCreatFileCountByAdvertiserId(advertiser.getId()));
    	
		model.addAttribute("ViewTypes", getViewTypeDropDownList(Util.getSessionMediumId(session)));
    	
    	
        return "adc/creative/creat-detail";
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
    
    
	/**
	 * 읽기 액션 - 감사 추적
	 */
    @RequestMapping(value = "/readAuditTrail", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readAuditTrail(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = sysService.getAuditTrailList(request, "C", (int)request.getReqIntValue1());

			for(Object obj : result.getData()) {
				SysAuditTrail auditTrail = (SysAuditTrail) obj;
    			KnlUser actedByUser = knlService.getUser(auditTrail.getWhoCreatedBy());
    			
    			auditTrail.setActedByShortName(actedByUser == null ? "-" : actedByUser.getShortName());
    			
    			if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("P")) {
        			auditTrail.setTarget("광고 소재 속성");
    			} else if ((auditTrail.getActType().equals("S") || auditTrail.getActType().equals("U")) 
    					&& auditTrail.getTgtType().equals("File")) {
        			auditTrail.setTarget("{IconCF}" + auditTrail.getTgtName());
    			} else if (auditTrail.getActType().equals("S") && auditTrail.getTgtType().equals("Time")) {
        			auditTrail.setTarget("시간 타겟팅 - {TagSmallO}총 " + auditTrail.getTgtName() + " 시간{TagSmallC}");
    			} else if (auditTrail.getActType().equals("U") && auditTrail.getTgtType().equals("Time")) {
        			auditTrail.setTarget("시간 타겟팅");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Sts")) {
	        		auditTrail.setTarget("광고 소재 상태");
    			} else if ((auditTrail.getActType().equals("S") || auditTrail.getActType().equals("U")) 
    					&& auditTrail.getTgtType().equals("Inven")) {
    				auditTrail.setTarget("인벤토리 타겟팅 - {TagSmallO}{IconInv" + auditTrail.getTgtValue() + "}" + auditTrail.getTgtName() + "{TagSmallC}");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Inven") 
    					&& Util.isValid(auditTrail.getTgtValue())) {
    				auditTrail.setTarget("인벤토리 타겟팅 - {TagSmallO}{IconInv" + auditTrail.getTgtValue() + "}" + auditTrail.getTgtName() + "{TagSmallC}");
    			} else if (auditTrail.getActType().equals("E") && auditTrail.getTgtType().equals("Inven") 
    					&& Util.isNotValid(auditTrail.getTgtValue())) {
    				auditTrail.setTarget("인벤토리 타겟팅 - {TagSmallO}" + auditTrail.getTgtName() + "{TagSmallC}");
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readAuditTrail", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 감사 추적 항목 값
	 */
    @RequestMapping(value = "/readAuditTrailValue", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readAuditTrailValue(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		return sysService.getAuditTrailValueList(request, (int)request.getReqIntValue1());
    	} catch (Exception e) {
    		logger.error("readAuditTrailValue", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

}
