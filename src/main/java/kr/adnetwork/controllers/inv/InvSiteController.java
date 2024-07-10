package kr.adnetwork.controllers.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.info.StringInfo;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.CustomComparator;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.fnd.FndRegion;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgSiteCond;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;

/**
 * 사이트 컨트롤러
 */
@Controller("inv-site-controller")
@RequestMapping(value="/inv/site")
public class InvSiteController {

	private static final Logger logger = LoggerFactory.getLogger(InvSiteController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 사이트 페이지
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
    	model.addAttribute("pageTitle", "사이트");

    	model.addAttribute("Regions", getRegionList());
    	model.addAttribute("SiteConds", getSiteCondList(Util.getSessionMediumId(session)));
    	
    	

    	
        return "inv/site";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		DataSourceResult result = invService.getSiteList(request);
    		
    		for(Object obj : result.getData()) {
    			InvSite site = (InvSite) obj;
    			
    			SolUtil.setSiteReqStatus(site);
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String latitude = (String)model.get("latitude");
    	String longitude = (String)model.get("longitude");
    	String regionCode = (String)model.get("regionCode");
    	String address = (String)model.get("address");
    	String memo = (String)model.get("memo");
    	String siteCondType = (String)model.get("siteCondType");
    	String venueType = (String)model.get("venueType");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(name) || effectiveStartDate == null ||
    			Util.isNotValid(regionCode) || Util.isNotValid(siteCondType) || Util.isNotValid(venueType) ||
    			Util.isNotValid(latitude) || Util.isNotValid(longitude)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	OrgSiteCond siteCond = orgService.getSiteCond(medium, siteCondType);
    	if (siteCond == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	InvSite target = new InvSite(medium, shortName, name, latitude, longitude, regionCode, address,
    			effectiveStartDate, effectiveEndDate, memo, session);
    	
    	target.setRegionName(getRegionName(regionCode));
    	target.setSiteCond(siteCond);
    	target.setVenueType(venueType);
    	
        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String latitude = (String)model.get("latitude");
    	String longitude = (String)model.get("longitude");
    	String regionCode = (String)model.get("regionCode");
    	String address = (String)model.get("address");
    	String memo = (String)model.get("memo");
    	String siteCondType = (String)model.get("siteCondType");
    	String venueType = (String)model.get("venueType");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(name) || effectiveStartDate == null ||
    			Util.isNotValid(regionCode) || Util.isNotValid(siteCondType) || Util.isNotValid(venueType) ||
    			Util.isNotValid(latitude) || Util.isNotValid(longitude)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	OrgSiteCond siteCond = orgService.getSiteCond(medium, siteCondType);
    	if (siteCond == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	InvSite target = invService.getSite((int)model.get("id"));
    	if (target != null) {
    		
    		target.setShortName(shortName);
            target.setName(name);
            target.setMemo(memo);
            target.setEffectiveStartDate(effectiveStartDate);
            target.setEffectiveEndDate(effectiveEndDate);
            target.setLatitude(latitude);
            target.setLongitude(longitude);
            target.setAddress(address);
            target.setRegionCode(regionCode);
            target.setRegionName(getRegionName(regionCode));
            target.setSiteCond(siteCond);
            target.setVenueType(venueType);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
            
            
            // 화면 상태 및 수량 기준으로 사이트 정보 변경
            invService.updateSiteActiveStatusCountBasedScreens(target.getId());
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(InvSite target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        if (target.getEffectiveStartDate() != null && target.getEffectiveEndDate() != null
        		&& target.getEffectiveStartDate().after(target.getEffectiveEndDate())) {
        	throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_EFF_END_DATE);
        }
        
        // DB 작업 수행 결과 검증
        try {
            invService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_SITE_ID_OR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_SITE_ID_OR_NAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<InvSite> sites = new ArrayList<InvSite>();

    	// 실제 사이트 정보가 필요하기 때문에 대상 사이트를 직접 가져옴
    	for (Object id : objs) {
    		InvSite site = invService.getSite((int)id);
    		if (site != null) {
    			if (site.isServed()) {
    				// 소프트 삭제 진행
    				invService.deleteSoftSite(site, session);
    			} else {
    	    		sites.add(site);
    			}
    		}
    	}
    	
    	try {
        	invService.deleteSites(sites);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 지역 목록 획득
	 */
    private List<DropDownListItem> getRegionList() {

		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
		
		List<FndRegion> regions = fndService.getRegionListByListIncluded(true);
		for(FndRegion region : regions) {
			retList.add(new DropDownListItem(region.getName(), region.getCode()));
		}

		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
		
		return retList;
    }
    
    
	/**
	 * 입지 유형 목록 획득
	 */
    private List<DropDownListItem> getSiteCondList(int mediumId) {

		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
		
		List<OrgSiteCond> siteConds = orgService.getSiteCondListByMediumIdActiveStatus(mediumId, true);
		for(OrgSiteCond siteCond : siteConds) {
			retList.add(new DropDownListItem(siteCond.getName(), siteCond.getCode()));
		}

		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
		
		return retList;
    }

    
	/**
	 * 지역 코드를 통해 지역명 획득
	 */
    private String getRegionName(String code) {
    	
    	FndRegion region = fndService.getRegion(code);
    	if (region == null) {
    		return "";
    	} else {
    		return region.getName();
    	}
    }
    
    
	/**
	 * 읽기 액션 - 장소 유형 정보
	 */
    @RequestMapping(value = "/readVenueTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readVenueTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-mug-hot fa-fw ", "고속도로 휴게소", "HISTP"));
		list.add(new DropDownListItem("fa-regular fa-building-columns fa-fw", "대학교", "UNIV"));
		list.add(new DropDownListItem("fa-regular fa-bus-simple fa-fw", "버스", "BUS"));
		list.add(new DropDownListItem("fa-regular fa-cube fa-fw", "버스/택시 쉘터", "BUSSH"));
		list.add(new DropDownListItem("fa-regular fa-syringe fa-fw", "병/의원", "HOSP"));
		list.add(new DropDownListItem("fa-regular fa-billboard fa-fw", "빌딩 전광판", "BLDG"));
		list.add(new DropDownListItem("fa-regular fa-star fa-fw", "일반", "GEN"));
		list.add(new DropDownListItem("fa-regular fa-gas-pump fa-fw", "주유소", "FUEL"));
		list.add(new DropDownListItem("fa-regular fa-store fa-fw", "편의점", "CVS"));
		
		return list;
    }
}
