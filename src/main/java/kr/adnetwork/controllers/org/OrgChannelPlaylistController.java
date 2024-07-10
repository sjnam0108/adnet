package kr.adnetwork.controllers.org;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.info.StringInfo;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.CustomComparator;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.adc.AdcPlaylist;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.adc.AdcAdCreatDragItem;
import kr.adnetwork.viewmodels.adc.AdcPlaylistItem;

/**
 * 광고 채널 컨트롤러(재생목록)
 */
@Controller("org-channel-playlist-controller")
@RequestMapping(value="/org/channel/playlist")
public class OrgChannelPlaylistController {

	private static final Logger logger = LoggerFactory.getLogger(OrgChannelPlaylistController.class);

	
    @Autowired 
    private InvService invService;

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
	 * 광고 채널(재생목록) 페이지
	 */
    @RequestMapping(value = {"/{channelId}", "/{channelId}/"}, method = RequestMethod.GET)
    public String index(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap,
    		Model model, Locale locale) {

    	OrgChannel channel = orgService.getChannel(Util.parseInt(pathMap.get("channelId")));
    	if (channel == null || channel.getMedium().getId() != Util.getSessionMediumId(session)) {
    		return "forward:/org/channel";
    	}

    	
    	modelMgr.addMainMenuModel(model, locale, session, request, "OrgChannel");
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});
		
    	// 세션에 현재 채널 및 채널 목록 설정
    	setCurrChannelsToSession(request, response, session, channel.getId());
		

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고 채널");

		channel.setSubCount(
				orgService.getChanSubCountByChannelId(channel.getId()));
		
		if (channel.getAppendMode().equals("A")) {
			// A: 자율 광고선택
			if (channel.getReqScreenId() == null) {
				channel.setReqScreen("미지정");
			} else {
				InvScreen tmpScr = invService.getScreen(channel.getReqScreenId().intValue());
				if (tmpScr == null) {
					channel.setReqScreen("새로운 지정 필요");
				} else {
					channel.setReqScreen(tmpScr.getName() + "(" + tmpScr.getShortName() + ")");
				}
			}
		}
		
    	model.addAttribute("Channel", channel);

    	model.addAttribute("Channels", getSaveToChannelDropDownList(Util.getSessionMediumId(session), channel.getId()));

    	
        return "org/channel/channel-playlist";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<AdcPlaylistItem> read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		
    		ArrayList<AdcPlaylistItem> retList = new ArrayList<AdcPlaylistItem>();
    		
    		OrgChannel channel = orgService.getChannel((int)request.getReqIntValue1());
    		int channelId = channel == null ? -1 : channel.getId();
    		
    		Date now = new Date();
    		AdcPlaylist currPl = null;
    		
    		
    		// 현재 시간 기준 적용 재생목록 후보 확인
			List<AdcPlaylist> plList = adcService.getActivePlaylistListByChannelId(channelId);
			for(AdcPlaylist playlist : plList) {
				if (playlist.getStartDate().before(now) && 
						(playlist.getEndDate() == null || playlist.getEndDate().after(now))) {
					currPl = playlist;
				}
			}

			
    		List<AdcPlaylist> list = adcService.getPlaylistListByChannelId(channelId);
    		
    		for(AdcPlaylist pl : list) {
    			AdcPlaylistItem plItem = new AdcPlaylistItem(pl);
    			if (currPl != null && pl.getId() == currPl.getId()) {
    				
    				plItem.setCode(channel.isAdAppended() ? "OC" : "C");
    			}
    			
    			retList.add(plItem);
    		}
    		
    		return retList;
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
    	Date endDate = Util.parseZuluTime((String)model.get("endDate"));
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	// 파라미터 검증
    	if (channel == null || Util.isNotValid(name) || startDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }

    	
    	AdcPlaylist target = new AdcPlaylist(channel.getMedium(), name, channel.getId(), startDate, activeStatus, session);
    	target.setEndDate(endDate);

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
    	Date endDate = Util.parseZuluTime((String)model.get("endDate"));
    	
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
            target.setEndDate(endDate);
            
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
    	if (target.getStartDate() != null && target.getEndDate() != null) {
    		if (!target.getStartDate().before(target.getEndDate())) {
    			throw new ServerOperationForbiddenException("시작일시는 반드시 종료일시 이전이어야 합니다.");
    		}
    	}
        
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
    	Date endDate = Util.parseZuluTime((String)model.get("endDate"));
    	
		AdcPlaylist playlist = adcService.getPlaylist((int)model.get("id"));
		
		OrgChannel prevChannel = null;
		if (playlist != null) {
			prevChannel = orgService.getChannel(playlist.getChannelId());
		}
    	
    	// 파라미터 검증
    	if (channel == null || prevChannel == null || playlist == null || Util.isNotValid(name) || startDate == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	// 비즈니스 로직 검증
    	if (endDate != null && !startDate.before(endDate)) {
			throw new ServerOperationForbiddenException("시작일시는 반드시 종료일시 이전이어야 합니다.");
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
    	target.setEndDate(endDate);
    	target.setAdCount(playlist.getAdCount());
    	target.setTotDurSecs(playlist.getTotDurSecs());
    	target.setAdValue(playlist.getAdValue());

        saveOrUpdate(target, locale, session);

        return "Ok";
    }

    
    private void setCurrChannelsToSession(HttpServletRequest request, HttpServletResponse response, HttpSession session,
    		int chanId) {
    	
		int currChanId = chanId;
		if (chanId < 0) {
			currChanId = Util.parseInt(Util.cookieValue(request, "currChanId"));
		}
    	
    	boolean goAhead = false;
    	List<OrgChannel> chanList = orgService.getChannelListByMediumId(Util.getSessionMediumId(session));
    	ArrayList<DropDownListItem> currChans = new ArrayList<DropDownListItem>();
    	for(OrgChannel chan : chanList) {
			String icon = chan.isActiveStatus() ? "fa-tower-cell" : "fa-circle-dashed";
			String text = chan.getShortName();
			String value = String.valueOf(chan.getId());
			String subIcon = chan.getName();
			
			currChans.add(new DropDownListItem(icon, subIcon, text, value));
    		if (chan.getId() == currChanId) {
    			goAhead = true;
    		}
    	}
		Collections.sort(currChans, CustomComparator.DropDownListItemTextComparator);
    	
    	if (!goAhead) {
    		if (chanList.size() == 0) {
    			currChanId = -1;
    		} else {
    			currChanId = Util.parseInt(currChans.get(0).getValue());
    		}
    	}
    	if ((goAhead && chanId > 0) || !goAhead) {
    		response.addCookie(Util.cookie("currChanId", String.valueOf(currChanId)));
    	}
    	
		session.setAttribute("currChanId", String.valueOf(currChanId));
		session.setAttribute("currChannels", currChans);
    }
    
    private List<DropDownListItem> getSaveToChannelDropDownList(int mediumId, int channelId) {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	OrgChannel channel = orgService.getChannel(channelId);
    	if (channel == null) {
    		retList.add(new DropDownListItem("fa-blank", "", "", "-1"));
    	} else {
    		// 리스트 순서
    		// 1. 현재 채널
    		// 2. 현재 채널 아닌 채널을 shortName asc로
    		
    		List<OrgChannel> chanList = orgService.getChannelListByMediumId(mediumId);
    		for(OrgChannel chan : chanList) {
    			if (chan.getId() == channel.getId()) {
    				continue;
    			}
    			if (Util.isValid(channel.getViewTypeCode())) {
    				if (Util.isValid(chan.getViewTypeCode()) && 
    						channel.getViewTypeCode().equals(chan.getViewTypeCode())) {
    					// 자료 추가
        				String icon = chan.isActiveStatus() ? "fa-tower-cell" : "fa-circle-dashed";
        				String text = chan.getShortName();
        				String value = String.valueOf(chan.getId());
        				String subIcon = chan.getName();
        				
        				retList.add(new DropDownListItem(icon, subIcon, text, value));
    				}
    			} else if (channel.getResolution().equals(chan.getResolution())) {
    				// 자료 추가
    				String icon = chan.isActiveStatus() ? "fa-tower-cell" : "fa-circle-dashed";
    				String text = chan.getShortName();
    				String value = String.valueOf(chan.getId());
    				String subIcon = chan.getName();
    				
    				retList.add(new DropDownListItem(icon, subIcon, text, value));
    			}
    		}

    		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
    		
    		retList.add(0, new DropDownListItem(
    				channel.isActiveStatus() ? "fa-tower-cell" : "fa-circle-dashed", 
    				"", "현재 채널", String.valueOf(channel.getId())));
    	}
    	
    	return retList;
    }
}
