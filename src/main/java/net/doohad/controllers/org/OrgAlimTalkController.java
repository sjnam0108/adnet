package net.doohad.controllers.org;

import java.util.ArrayList;
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
import net.doohad.models.org.OrgAlimTalk;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.sf.json.JSONObject;

/**
 * 알림톡 컨트롤러
 */
@Controller("org-alim-talk-controller")
@RequestMapping(value="/org/alimtalk")
public class OrgAlimTalkController {

	private static final Logger logger = LoggerFactory.getLogger(OrgAlimTalkController.class);


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
	 * 알림톡 페이지
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
    	model.addAttribute("pageTitle", "알림톡");

    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	String bizHours = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
    	if (medium != null && Util.isValid(medium.getBizHour()) && medium.getBizHour().length() == 168) {
    		bizHours = medium.getBizHour();
    	}
    	
    	model.addAttribute("mediumBizHours", bizHours);

    	
		// 활성 화면 수
		int activeScrCnt = Util.parseInt(SolUtil.getOptValue(Util.getSessionMediumId(session), "activeCount.screen"));
		if (activeScrCnt < 1 && medium != null) {
			activeScrCnt = invService.getActiveScreenCountByMediumId(medium.getId());
		}
    	
    	model.addAttribute("activeScrCnt", activeScrCnt);
    	
    	
    	
    	// Device가 PC일 경우에만, 다중 행 선택 설정
    	Util.setMultiSelectableIfFromComputer(model, request);
    	
        return "org/alimtalk";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = orgService.getAlimTalkList(request);
    		
    		for(Object obj : result.getData()) {
    			OrgAlimTalk alimTalk = (OrgAlimTalk)obj;

    			List<String> subscribers = Util.tokenizeValidStr(alimTalk.getSubscribers());
    			alimTalk.setSubCount(subscribers.size());
    			
    			if (alimTalk.getBizHour().length() == 168) {
    				alimTalk.setBizHours(alimTalk.getBizHour().replaceAll("0", "").length());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션 - ActScr
	 */
    @RequestMapping(value = "/createActScr", method = RequestMethod.POST)
    public @ResponseBody String createActScr(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String shortName = (String)model.get("shortName");
    	String subscribers = Util.parseString((String)model.get("subscribers"));
    	String bizHours = Util.parseString((String)model.get("bizHours"));
    	
    	int scrCount = (int)model.get("scrCount");
    	int failCount = (int)model.get("failCount");
    	int coolMins = (int)model.get("coolMins");
    	int delayMins = (int)model.get("delayMins");

    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || bizHours.length() != 168) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (Util.isValid(subscribers)) {
    		subscribers = subscribers.replaceAll("-", "").replaceAll(",", "|");
    	}
    	
    	OrgAlimTalk target = new OrgAlimTalk(medium, shortName, bizHours, "ActScr", activeStatus, session);
    	
    	target.setSubscribers(subscribers);
    	target.setCfStr1(String.valueOf(scrCount));
    	target.setCfStr2(String.valueOf(failCount));
    	target.setWaitMins(coolMins);
    	target.setDelayChkMins(delayMins);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/updateActScr", method = RequestMethod.POST)
    public @ResponseBody String updateActScr(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String shortName = (String)model.get("shortName");
    	String subscribers = Util.parseString((String)model.get("subscribers"));
    	String bizHours = Util.parseString((String)model.get("bizHours"));
    	
    	int scrCount = (int)model.get("scrCount");
    	int failCount = (int)model.get("failCount");
    	int coolMins = (int)model.get("coolMins");
    	int delayMins = (int)model.get("delayMins");

    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || bizHours.length() != 168) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	if (Util.isValid(subscribers)) {
    		subscribers = subscribers.replaceAll("-", "").replaceAll(",", "|");
    	}
    	
    	OrgAlimTalk target = orgService.getAlimTalk((int)model.get("id"));
    	if (target != null) {
        	
    		target.setShortName(shortName);
    		target.setSubscribers(subscribers);
    		target.setBizHour(bizHours);
            target.setActiveStatus(activeStatus);
    		
        	target.setCfStr1(String.valueOf(scrCount));
        	target.setCfStr2(String.valueOf(failCount));
        	target.setWaitMins(coolMins);
        	target.setDelayChkMins(delayMins);

            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(OrgAlimTalk target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_ID);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_ID);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }
    
    
	/**
	 * 추가 액션 - Test
	 */
    @RequestMapping(value = "/createTest", method = RequestMethod.POST)
    public @ResponseBody String createTest(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String subscribers = Util.parseString((String)model.get("subscribers"));
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(subscribers)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	subscribers = subscribers.replaceAll("-", "").replaceAll(",", "|");
    	
    	if (!sendAlimTalkTest("adnet-test", medium.getShortName(), subscribers)) {
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "Ok";
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<OrgAlimTalk> alimTalks = new ArrayList<OrgAlimTalk>();

    	for (Object id : objs) {
    		OrgAlimTalk alimTalk = new OrgAlimTalk();
    		
    		alimTalk.setId((int)id);
    		
    		alimTalks.add(alimTalk);
    	}
    	
    	try {
        	orgService.deleteAlimTalks(alimTalks);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
    private boolean sendAlimTalkTest(String templCode, String mediumShortName, String subscribers) {
    	
    	String server = Util.getFileProperty("alim.baseUrl");
    	
    	if (!server.toLowerCase().startsWith("http")) {
    		return false;
    	}
    	
    	/*
    	// 활성화면 모니터링 알림톡 테스트
		JSONObject reqParams = new JSONObject();
		reqParams.put("mediumID", mediumShortName);
		reqParams.put("Phone", subscribers);

		reqParams.put("alimID", "firstTalk");
		reqParams.put("failCnt", "5");
		reqParams.put("failList", "[건대_LCD_1], [홍대상행_LED_1], [홍대상행_LCD_1], [홍대상행_LCD_2], [홍대상행_LCD_3]");

		return sendAlimTalk(server + "/alimtalk/send/adnet-active-screen-img", reqParams.toString());
		*/
    	
		JSONObject reqParams = new JSONObject();
		reqParams.put("mediumID", mediumShortName);
		reqParams.put("Phone", subscribers);
    	
    	return sendAlimTalk(server + "/alimtalk/send/" + templCode, reqParams.toString());
    	
    }
    
    private boolean sendAlimTalk(String serverUrl, String jsonStr) {
    	
    	if (Util.isNotValid(jsonStr) || Util.isNotValid(serverUrl)) {
    		return false;
    	}
    	
    	int retCode = Util.sendStreamToServer(serverUrl, jsonStr);
    	
    	return retCode == 200;
    }
    
}
