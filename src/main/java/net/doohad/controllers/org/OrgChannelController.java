package net.doohad.controllers.org;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.Tuple;
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
import net.doohad.models.adc.AdcPlaylist;
import net.doohad.models.fnd.FndViewType;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSyncPackItem;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgChannel;
import net.doohad.models.org.OrgRTChannel;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;

/**
 * 광고 채널 컨트롤러
 */
@Controller("org-channel-controller")
@RequestMapping(value="/org/channel")
public class OrgChannelController {

	private static final Logger logger = LoggerFactory.getLogger(OrgChannelController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 광고 채널 페이지
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
    	model.addAttribute("pageTitle", "광고 채널");


    	int mediumId = Util.getSessionMediumId(session);
    	KnlMedium medium = knlService.getMedium(mediumId);
    	
    	model.addAttribute("Resolutions", getResolutionList(medium));
		model.addAttribute("ViewTypes", getViewTypeDropDownList(mediumId));
    	
    	

    	
        return "org/channel";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = orgService.getChannelList(request);
    		
    		for(Object obj : result.getData()) {
    			OrgChannel channel = (OrgChannel)obj;
    			
    			channel.setSubCount(
    					orgService.getChanSubCountByChannelId(channel.getId()));
    			
    			OrgRTChannel rtChannel = orgService.getRTChannelByChannelId(channel.getId());
    			if (rtChannel != null) {
    				channel.setLastAdAppDate(rtChannel.getLastAdAppDate());
    				channel.setLastAdReqDate(rtChannel.getLastAdReqDate());
    			}
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
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String type = (String)model.get("type");
    	
    	String resolution = "";
    	String viewTypeCode = "";
    	
    	int priority = (int)model.get("priority");
    	
    	String appendMode = Util.parseString((String)model.get("appendMode"));

    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(type) || Util.isNotValid(name) || 
    			Util.isNotValid(appendMode)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	if (type.startsWith("R")) {
    		resolution = type.substring(1);
    	} else if (type.startsWith("V")) {
    		viewTypeCode = type.substring(1);
    		
    		List<FndViewType> viewTypeList = fndService.getViewTypeList();
    		for(FndViewType viewType : viewTypeList) {
    			if (viewType.getCode().equals(viewTypeCode)) {
    				resolution = viewType.getResolution();
    			}
    		}
    	}

    	// 파라미터 검증 2
    	if (Util.isNotValid(resolution)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	
    	OrgChannel target = new OrgChannel(medium, shortName, name, resolution, viewTypeCode, priority, appendMode, activeStatus, session);
    	target.setAdAppended(target.isActiveStatus());

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	
    	String shortName = (String)model.get("shortName");
    	String name = (String)model.get("name");
    	String type = (String)model.get("type");
    	
    	String resolution = "";
    	String viewTypeCode = "";
    	
    	int priority = (int)model.get("priority");
    	
    	String appendMode = Util.parseString((String)model.get("appendMode"));
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (medium == null || Util.isNotValid(shortName) || Util.isNotValid(type) || Util.isNotValid(name) || 
    			Util.isNotValid(appendMode)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	if (type.startsWith("R")) {
    		resolution = type.substring(1);
    	} else if (type.startsWith("V")) {
    		viewTypeCode = type.substring(1);
    		
    		List<FndViewType> viewTypeList = fndService.getViewTypeList();
    		for(FndViewType viewType : viewTypeList) {
    			if (viewType.getCode().equals(viewTypeCode)) {
    				resolution = viewType.getResolution();
    			}
    		}
    	}

    	// 파라미터 검증 2
    	if (Util.isNotValid(resolution)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	

    	OrgChannel target = orgService.getChannel((int)model.get("id"));
    	if (target != null) {
        	
    		target.setShortName(shortName);
            target.setName(name);
            target.setActiveStatus(activeStatus);
            target.setResolution(resolution);
            target.setViewTypeCode(viewTypeCode);
            target.setPriority(priority);
            target.setAppendMode(appendMode);
            target.setAdAppended(activeStatus);
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(OrgChannel target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            orgService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID_OR_NAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_UKID_OR_NAME);
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
    	
    	List<OrgChannel> channels = new ArrayList<OrgChannel>();

    	for (Object id : objs) {
    		OrgChannel channel = orgService.getChannel((int)id);
    		if (channel != null) {
    			List<AdcPlaylist> playlists = adcService.getPlaylistListByChannelId(channel.getId());
    			if (playlists.size() > 0) {
    				throw new ServerOperationForbiddenException(StringInfo.DEL_ERROR_CHILD_PLAYLIST);
    			} else {
        			channels.add(channel);
    			}
    		}
    	}
    	
    	try {
        	orgService.deleteChannels(channels);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 화면 해상도 목록 획득
	 */
    private List<DropDownListItem> getResolutionList(KnlMedium medium) {

		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
		
		
		if (medium != null) {
			List<String> resolutions = Util.tokenizeValidStr(medium.getResolutions());
			for(String resolution : resolutions) {
				retList.add(new DropDownListItem(resolution.replace("x", " x ") , resolution));
			}
		}

		//Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
		
		return retList;
    }
    
    
	/**
	 * 현재 매체에서 이용가능한 게시 유형 획득
	 */
    private List<DropDownListItem> getViewTypeDropDownList(int mediumId) {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	
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
	 * 읽기 액션 - 광고 추가 모드 정보
	 */
    @RequestMapping(value = "/readAppendModes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readAppendModes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-robot fa-fw", "자율선택", "A"));
		list.add(new DropDownListItem("fa-regular fa-list-ol fa-fw", "재생목록", "P"));
		
		return list;
    }

    
    /**
	 * 상태 활성화 액션
	 */
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    public @ResponseBody String activate(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		OrgChannel channel = orgService.getChannel((int)id);
    		if (channel != null && !channel.isActiveStatus()) {
    			channel.setActiveStatus(true);
    			channel.setAdAppended(true);
    			channel.touchWho(session);
				
            	orgService.saveOrUpdate(channel);
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 상태 비활성화 액션
	 */
    @RequestMapping(value = "/deactivate", method = RequestMethod.POST)
    public @ResponseBody String deactivate(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		OrgChannel channel = orgService.getChannel((int)id);
    		if (channel != null && channel.isActiveStatus()) {
    			channel.setActiveStatus(false);
    			channel.setAdAppended(false);
    			channel.touchWho(session);
				
            	orgService.saveOrUpdate(channel);
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 광고 편성 활성화 액션
	 */
    @RequestMapping(value = "/enableAppMode", method = RequestMethod.POST)
    public @ResponseBody String enableAppMode(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		OrgChannel channel = orgService.getChannel((int)id);
    		if (channel != null && !channel.isAdAppended()) {
    			channel.setAdAppended(true);
    			channel.touchWho(session);
				
            	orgService.saveOrUpdate(channel);
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 광고 편성 비활성화 액션
	 */
    @RequestMapping(value = "/disableAppMode", method = RequestMethod.POST)
    public @ResponseBody String disableAppMode(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		OrgChannel channel = orgService.getChannel((int)id);
    		if (channel != null && channel.isAdAppended()) {
    			channel.setAdAppended(false);
    			channel.touchWho(session);
				
            	orgService.saveOrUpdate(channel);
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 기준 화면 설정 액션
	 */
    @RequestMapping(value = "/setScreenAuto", method = RequestMethod.POST)
    public @ResponseBody String setScreenAuto(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("id"));
    	
    	// 파라미터 검증
    	if (channel == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	if (channel.getAppendMode().equals("A")) {
    		
        	Integer reqScreenId = null;
    		List<Tuple> scrList = orgService.getChanSubScrTupleListByChannelId(channel.getId());
    		if (scrList.size() > 0) {
    			for(Tuple tuple : scrList) {
    				// SELECT s.short_name, s.name, s.screen_id
    				reqScreenId = (Integer) tuple.get(2);
    				break;
    			}
    		}
        	if (reqScreenId == null) {
        		Integer tmpSyncPackId = null;
        		List<Tuple> syncPackList = orgService.getChanSubSyncPackTupleListByChannelId(channel.getId());
    			for(Tuple tuple : syncPackList) {
    				// SELECT sp.short_name, sp.name, sp.sync_pack_id
    				tmpSyncPackId = (Integer) tuple.get(2);
    				break;
    			}
    			
    			if (tmpSyncPackId != null) {
    				InvSyncPackItem spi = invService.getSyncPackItemFirstLaneBySyncPackId(tmpSyncPackId.intValue());
    				if (spi != null) {
    					reqScreenId = spi.getScreenId();
    				}
    			}
        	}
        	
        	if (reqScreenId != null) {
        		InvScreen tmpScreen = invService.getScreen(reqScreenId.intValue());
        		if (tmpScreen != null) {
        			channel.setReqScreenId(tmpScreen.getId());
        		} else {
        			channel.setReqScreen(null);
        		}
        	} else {
        		channel.setReqScreen(null);
        	}
    	} else {
    		channel.setReqScreen(null);
    	}
    	
		channel.touchWho(session);
		
    	orgService.saveOrUpdate(channel);

    	
        return "Ok";
    }

}
