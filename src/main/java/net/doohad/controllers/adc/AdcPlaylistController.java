package net.doohad.controllers.adc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import net.doohad.models.CustomComparator;
import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.Message;
import net.doohad.models.MessageManager;
import net.doohad.models.ModelManager;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcPlaylist;
import net.doohad.models.org.OrgChannel;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.OrgService;
import net.doohad.utils.Util;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.adc.AdcAdCreatDragItem;

/**
 * 재생목록 컨트롤러
 */
@Controller("adc-playlist-controller")
@RequestMapping(value="/adc/playlist")
public class AdcPlaylistController {

	private static final Logger logger = LoggerFactory.getLogger(AdcPlaylistController.class);


    @Autowired 
    private OrgService orgService;

    @Autowired 
    private AdcService adcService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 재생목록 페이지
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
    	model.addAttribute("pageTitle", "재생목록");

    	model.addAttribute("Channels", getChannelDropDownList(Util.getSessionMediumId(session)));

    	

    	
        return "adc/playlist";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	
    	try {
    		DataSourceResult result = adcService.getPlaylistList(request, request.getReqStrValue1());
    		for(Object obj : result.getData()) {
    			AdcPlaylist playlist = (AdcPlaylist)obj;
    			
    			if (playlist != null) {
    				OrgChannel channel = orgService.getChannel(playlist.getChannelId());
    				if (channel != null) {
    					playlist.setChannel(String.format("%s - %s", channel.getShortName(), channel.getName()));
    					playlist.setViewTypeCode(channel.getViewTypeCode());
    					playlist.setResolution(channel.getResolution().replace("x", " x "));
    				}
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
    	
    	String name = (String)model.get("name");
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("channel"));
    	
    	Date startDate = Util.parseZuluTime((String)model.get("startDate"));
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (channel == null || Util.isNotValid(name) || startDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	AdcPlaylist target = new AdcPlaylist(channel.getMedium(), name, channel.getId(), startDate, activeStatus, session);

        saveOrUpdate(target, locale, session);

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("channel"));
    	
    	Date startDate = Util.parseZuluTime((String)model.get("startDate"));
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (channel == null || Util.isNotValid(name) || startDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	

    	AdcPlaylist target = adcService.getPlaylist((int)model.get("id"));
    	if (target != null) {
        	
            target.setName(name);
            target.setActiveStatus(activeStatus);
            target.setChannelId(channel.getId());
            target.setStartDate(startDate);
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcPlaylist target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException("동일한 광고 채널 및 시작일시의 자료가 이미 등록되어 있습니다.");
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException("동일한 광고 채널 및 시작일시의 자료가 이미 등록되어 있습니다.");
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
    	
    	List<AdcPlaylist> playlists = new ArrayList<AdcPlaylist>();

    	for (Object id : objs) {
    		AdcPlaylist playlist = new AdcPlaylist();
    		
    		playlist.setId((int)id);
    		
    		playlists.add(playlist);
    	}
    	
    	try {
        	adcService.deletePlaylists(playlists);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 활성화된 광고 채널 획득
	 */
    private List<DropDownListItem> getChannelDropDownList(int mediumId) {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	
    	List<OrgChannel> channelList = orgService.getChannelListByMediumId(mediumId);
    	
    	for(OrgChannel channel : channelList) {
    		if (channel.getAppendMode().equals("P")) {
        		retList.add(new DropDownListItem(String.format("%s - %s", channel.getShortName(), channel.getName()), String.valueOf(channel.getId())));
    		}
    	}
    	
    	if (retList.size() == 0) {
        	// 빈 행 추가
        	retList.add(new DropDownListItem("", ""));
    	}

		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
		
    	
    	return retList;
    }
    
    
	/**
	 * 읽기 액션 - 가능한 광고 전체 
	 */
    @RequestMapping(value = "/readAds", method = RequestMethod.POST)
    public @ResponseBody List<AdcAdCreatDragItem> readAdCreats(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	try {
    		ArrayList<AdcAdCreatDragItem> dragItems = new ArrayList<AdcAdCreatDragItem>();
    		
    		AdcPlaylist playlist = adcService.getPlaylist((int)model.get("id"));
    		
        	// 파라미터 검증
        	if (playlist == null) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
            }
    		
    		OrgChannel channel = orgService.getChannel(playlist.getChannelId());
    		
        	// 파라미터 검증
        	if (channel == null) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
            }
        	
        	
        	ArrayList<Integer> currIds = new ArrayList<Integer>();
        	List<String> currValues = Util.tokenizeValidStr(playlist.getAdValue());
        	for(String s : currValues) {
        		Integer i = Util.parseInt(s);
        		if (!currIds.contains(i)) {
        			currIds.add(i);
        		}
        	}
        	ArrayList<Integer> dbIds = new ArrayList<Integer>();
        	
        	
        	String viewTypeCode = channel.getViewTypeCode();
        	//
        	// 1. 현재 유효한 광고/광고 소재를 먼저 확인
        	// 2. 현재 유효하지 않지만, 이전에 유효하여 저장되었던 항목 채우기
        	//
        	List<AdcAdCreative> candiList = adcService.getPlCandiAdCreativeListByMediumIdDate(
        			playlist.getMedium().getId(), Util.removeTimeOfDate(playlist.getStartDate()));
        	for(AdcAdCreative ac : candiList) {
        		if (!dbIds.contains(ac.getId())) {
        			dbIds.add(ac.getId());
        		}
        		
        		AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(
        				ac.getCreative().getId(), channel.getResolution());
        		if (creatFile == null) {
        			continue;
        		}
        		
        		if (Util.isValid(viewTypeCode)) {
        			if (!viewTypeCode.equals(ac.getAd().getViewTypeCode()) || 
        					!viewTypeCode.equals(ac.getCreative().getViewTypeCode())) {
        				continue;
        			}
        		} else {
        			if (Util.isValid(ac.getAd().getViewTypeCode()) || Util.isValid(ac.getCreative().getViewTypeCode())) {
        				continue;
        			}
        		}
        		dragItems.add(new AdcAdCreatDragItem(ac, creatFile, true));
        	}
    		
    		currIds.removeAll(dbIds);
    		for(Integer i : currIds) {
    			AdcAdCreative ac = adcService.getAdCreative(i);
    			if (ac != null) {
    				dragItems.add(new AdcAdCreatDragItem(ac, false));
    			}
    		}

        	
    		Collections.sort(dragItems, new Comparator<AdcAdCreatDragItem>() {
    	    	public int compare(AdcAdCreatDragItem item1, AdcAdCreatDragItem item2) {
    	    		return item1.getOrderCode().compareTo(item2.getOrderCode());
    	    	}
    	    });

    		return dragItems;
    	} catch (Exception e) {
    		logger.error("readAdCreats", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 변경 액션 - 재생목록 광고
	 */
	@RequestMapping(value = "/editAdValue", method = RequestMethod.POST)
    public @ResponseBody String editAdValue(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
		AdcPlaylist playlist = adcService.getPlaylist((int)model.get("id"));
    	
    	String ids = (String)model.get("ids");
    	int cnt = (Integer)model.get("cnt");
    	int duration = (Integer)model.get("duration");
    	
    	// 파라미터 검증
    	if (playlist == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	playlist.setAdValue(ids);
    	playlist.setAdCount(cnt);
    	playlist.setTotDurSecs(duration);
    	
    	playlist.touchWho(session);
    	
    	adcService.saveOrUpdate(playlist);
    	
        return "Ok";
    }
    
    
	/**
	 * 새 이름으로 저장 액션
	 */
	@RequestMapping(value = "/copyAs", method = RequestMethod.POST)
    public @ResponseBody String copyAs(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	
    	OrgChannel channel = orgService.getChannel((int)model.get("channel"));
    	
    	Date startDate = Util.parseZuluTime((String)model.get("startDate"));
    	
		AdcPlaylist playlist = adcService.getPlaylist((int)model.get("id"));
		
		OrgChannel prevChannel = null;
		if (playlist != null) {
			prevChannel = orgService.getChannel(playlist.getChannelId());
		}
    	
    	// 파라미터 검증
    	if (channel == null || prevChannel == null || playlist == null || Util.isNotValid(name) || startDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	// 재생목록의 광고 채널과 새로 선택된 광고 채널이 서로 동일한 형태인지를 확인
    	//   1) 게시 유형이 있다면 동일한 게시 유형
    	//   2) 게시 유형이 없으면 동일한 해상도
    	boolean goAhead = false;
    	if (Util.isValid(prevChannel.getViewTypeCode())) {
    		if (Util.isValid(channel.getViewTypeCode()) && prevChannel.getViewTypeCode().equals(channel.getViewTypeCode())) {
    			goAhead = true;
    		}
    	} else {
    		if (prevChannel.getResolution().equals(channel.getResolution())) {
    			goAhead = true;
    		}
    	}
    	
    	if (!goAhead) {
    		throw new ServerOperationForbiddenException("게시 유형 혹은 해상도의 정보가 달라서 복사 저장할 수 없습니다.");
    	}
    	

    	AdcPlaylist target = new AdcPlaylist(playlist.getMedium(), name, channel.getId(), startDate, false, session);
    	target.setAdCount(playlist.getAdCount());
    	target.setTotDurSecs(playlist.getTotDurSecs());
    	target.setAdValue(playlist.getAdValue());

        saveOrUpdate(target, locale, session);

        return "Ok";
    }

    
	/**
	 * 읽기 액션 - 광고 채널
	 */
    @RequestMapping(value = "/readChannels", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readChannels(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
    	
		List<OrgChannel> chanList = orgService.getChannelListByMediumId(Util.getSessionMediumId(session));
		for(OrgChannel channel : chanList) {
			if (channel.getAppendMode().equals("P")) {
				list.add(new DropDownListItem(channel.getName(), String.valueOf(channel.getId())));
			}
		}
		
		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);
		
		
		return list;
    }

}
