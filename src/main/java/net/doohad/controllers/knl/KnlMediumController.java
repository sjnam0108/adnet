package net.doohad.controllers.knl;

import java.util.ArrayList;
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

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.StringInfo;
import net.doohad.models.AdnMessageManager;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.SysService;
import net.doohad.models.sys.SysOpt;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 매체 컨트롤러
 */
@Controller("knl-medium-controller")
@RequestMapping(value="/knl/medium")
public class KnlMediumController {

	private static final Logger logger = LoggerFactory.getLogger(KnlAccountController.class);


    @Autowired 
    private KnlService knlService;

    @Autowired 
    private SysService sysService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 매체 페이지
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
    	model.addAttribute("pageTitle", "매체");
    	
		model.addAttribute("CustomResos", getCustomResoDropDownList());
		
		
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	//Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "knl/medium";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		DataSourceResult result = knlService.getMediumList(request, request.getReqStrValue1());
    		
    		for(Object obj : result.getData()) {
    			KnlMedium medium = (KnlMedium)obj;
    			
    			medium.setBizHours(SolUtil.getHourCnt(medium.getBizHour()));
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
    	String memo = (String)model.get("memo");
    	String apiKey = (String)model.get("apiKey");
    	
    	boolean rangeDurAllowed = (Boolean)model.get("rangeDurAllowed");
    	
    	int defaultDurSecs = (Integer)model.get("defaultDurSecs");
    	int minDurSecs = (Integer)model.get("minDurSecs");
    	int maxDurSecs = (Integer)model.get("maxDurSecs");

    	int aGradeMillis = (Integer)model.get("aGradeMillis");
    	int bGradeMillis = (Integer)model.get("bGradeMillis");
    	int cGradeMillis = (Integer)model.get("cGradeMillis");

    	// 선택된 자료가 없을 경우 resolutions = null
    	@SuppressWarnings("unchecked")
		ArrayList<Object> resolutions = (ArrayList<Object>) model.get("resolutions");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(shortName) || Util.isNotValid(name) || effectiveStartDate == null || resolutions == null ||
    			defaultDurSecs < 5 || minDurSecs < 5 || maxDurSecs < 5 || aGradeMillis < 50 || bGradeMillis < 50 || cGradeMillis < 50) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	String resolution = "";
    	for (Object res : resolutions) {
    		resolution += (Util.isValid(resolution) ? "|" : "") + ((String)res);
    	}
    	
    	if (Util.isNotValid(apiKey)) {
    		apiKey =Util.getRandomSalt();
    	}
    	
    	
    	KnlMedium target = new KnlMedium(shortName, name, resolution, apiKey, defaultDurSecs, rangeDurAllowed, minDurSecs, maxDurSecs,
    			effectiveStartDate, effectiveEndDate, memo, session);
    	
    	target.setaGradeMillis(aGradeMillis);
    	target.setbGradeMillis(bGradeMillis);
    	target.setcGradeMillis(cGradeMillis);
    	
    	
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
    	String memo = (String)model.get("memo");
    	String apiKey = (String)model.get("apiKey");
    	
    	boolean rangeDurAllowed = (Boolean)model.get("rangeDurAllowed");
    	
    	int defaultDurSecs = (Integer)model.get("defaultDurSecs");
    	int minDurSecs = (Integer)model.get("minDurSecs");
    	int maxDurSecs = (Integer)model.get("maxDurSecs");

    	int aGradeMillis = (Integer)model.get("aGradeMillis");
    	int bGradeMillis = (Integer)model.get("bGradeMillis");
    	int cGradeMillis = (Integer)model.get("cGradeMillis");

    	// 선택된 자료가 없을 경우 resolutions = null
    	@SuppressWarnings("unchecked")
		ArrayList<Object> resolutions = (ArrayList<Object>) model.get("resolutions");
    	
    	Date effectiveStartDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("effectiveStartDate")));
    	Date effectiveEndDate = Util.setMaxTimeOfDate(Util.parseZuluTime((String)model.get("effectiveEndDate")));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(shortName) || Util.isNotValid(name) || effectiveStartDate == null || resolutions == null ||
    			defaultDurSecs < 5 || minDurSecs < 5 || maxDurSecs < 5 || aGradeMillis < 50 || bGradeMillis < 50 || cGradeMillis < 50) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	String resolution = "";
    	for (Object res : resolutions) {
    		resolution += (Util.isValid(resolution) ? "|" : "") + ((String)res);
    	}
    	
    	if (Util.isNotValid(apiKey)) {
    		apiKey =Util.getRandomSalt();
    	}

    	
    	KnlMedium target = knlService.getMedium((int)model.get("id"));
    	if (target != null) {

        	target.setShortName(shortName);
            target.setName(name);
            target.setMemo(memo);
            target.setEffectiveStartDate(effectiveStartDate);
            target.setEffectiveEndDate(effectiveEndDate);
            target.setApiKey(apiKey);
            target.setResolutions(resolution);
            target.setDefaultDurSecs(defaultDurSecs);
            target.setMinDurSecs(minDurSecs);
            target.setMaxDurSecs(maxDurSecs);
            target.setRangeDurAllowed(rangeDurAllowed);
            target.setaGradeMillis(aGradeMillis);
            target.setbGradeMillis(bGradeMillis);
            target.setcGradeMillis(cGradeMillis);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(KnlMedium target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        if (target.getEffectiveStartDate() != null && target.getEffectiveEndDate() != null
        		&& target.getEffectiveStartDate().after(target.getEffectiveEndDate())) {
        	throw new ServerOperationForbiddenException(StringInfo.CMN_NOT_BEFORE_EFF_END_DATE);
        } else if (target.getMaxDurSecs() < target.getMinDurSecs()) {
        	throw new ServerOperationForbiddenException(StringInfo.VAL_LESS_THAN_MIN_DURATION);
        }
        
        if (target.getMinDurSecs() > target.getDefaultDurSecs() || target.getMaxDurSecs() < target.getDefaultDurSecs()) {
        	throw new ServerOperationForbiddenException(StringInfo.VAL_NOT_BETWEEN_MIN_MAX_DUR);
        }
        
        if (target.getaGradeMillis() >= target.getbGradeMillis() || target.getbGradeMillis() >= target.getcGradeMillis()) {
        	throw new ServerOperationForbiddenException("동기화 등급 A, B, C는 각각 다음 항목보다 작은 값이어야 합니다.");
        }
        
        // DB 작업 수행 결과 검증
        try {
            knlService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_MEDIUM_ID_OR_API_KEY);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_MEDIUM_ID_OR_API_KEY);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<KnlMedium> media = new ArrayList<KnlMedium>();

    	for (Object id : objs) {
    		KnlMedium medium = new KnlMedium();
    		
    		medium.setId((int)id);
    		
    		media.add(medium);
    	}
    	
    	try {
        	knlService.deleteMedia(media);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 변경 액션 - 운영 시간
	 */
    @RequestMapping(value = "/updateTime", method = RequestMethod.POST)
    public @ResponseBody String updateBizTime(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium target = knlService.getMedium((int)model.get("id"));

    	String bizHour = (String)model.get("bizHour");
    	
    	// 파라미터 검증
    	if (target == null || Util.isNotValid(bizHour)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (target != null) {

    		target.setBizHour(bizHour);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }
    
    
	/**
	 * 커스텀 해상도 정보 획득
	 */
    private List<DropDownListItem> getCustomResoDropDownList() {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	SysOpt sysOpt = sysService.getOpt("sol.resos");
    	if (sysOpt != null) {
    		List<String> resos = Util.tokenizeValidStr(sysOpt.getValue());
    		for(String reso : resos) {
    			String text = reso.replace("x", " x ");
    			retList.add(new DropDownListItem(text, reso));
    		}
    	}
    	
    	return retList;
    }

}
