package kr.adnetwork.controllers.rev;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.info.GlobalInfo;
import kr.adnetwork.info.StringInfo;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.CustomComparator;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvRTScreenView;
import kr.adnetwork.models.inv.InvRTSyncPack;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.inv.InvSyncPackItem;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.rev.RevAdSelect;
import kr.adnetwork.models.rev.RevChanAdRpt;
import kr.adnetwork.models.rev.RevEventReport;
import kr.adnetwork.models.rev.RevImpWave;
import kr.adnetwork.models.rev.RevObjTouch;
import kr.adnetwork.models.rev.RevPlayHist;
import kr.adnetwork.models.rev.RevSyncPackImp;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.rev.RevApiLogItem;
import kr.adnetwork.viewmodels.rev.RevMonitCurrStatItem;
import kr.adnetwork.viewmodels.rev.RevSyncPackCurrStatItem;

/**
 * 모니터링 컨트롤러
 */
@Controller("rev-monitoring-controller")
@RequestMapping(value="/rev/monitoring")
public class RevMonitoringController {

	private static final Logger logger = LoggerFactory.getLogger(RevMonitoringController.class);


    @Autowired 
    private InvService invService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 모니터링 페이지
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
    	model.addAttribute("pageTitle", "모니터링");

    	
    	model.addAttribute("screenID", Util.parseString((String)request.getParameter("screen"), ""));
    	model.addAttribute("apiTestServer", GlobalInfo.ApiTestServer);
    	
    	model.addAttribute("stat", getMonitCurrStatItem(Util.getSessionMediumId(session)));
    	
    	model.addAttribute("Channels", getChannelDropDownListByMediumId(Util.getSessionMediumId(session)));
    	
    	model.addAttribute("SyncPacks", getSyncPackDropDownListByMediumId(Util.getSessionMediumId(session)));
    	
    	
    	// 현재 매체의 API Key 전달
    	String mediumApiKey = "";
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	if (medium != null) {
    		mediumApiKey = medium.getApiKey();
    	}
    	model.addAttribute("mediumApiKey", mediumApiKey);
    	
    	
    	// 활성 동기화 화면 묶음 모드
    	model.addAttribute("syncPackMode", 
    			invService.getActiveSyncPackCountByMediumId(Util.getSessionMediumId(session)) > 0);
    	// 활성 동기화 화면 묶음 통계
    	model.addAttribute("syncStat", getSyncPackCurrStatItem(Util.getSessionMediumId(session)));

    	return "rev/monitoring";
    }

    
    /**
	 * 읽기 액션 - 활성 화면
	 */
    @RequestMapping(value = "/readScr", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readScr(@RequestBody DataSourceRequest request, HttpSession session) {
    	
		String status = request.getReqStrValue1();
		String cmd = request.getReqStrValue2();
		
    	try {
    		
    		List<Integer> monitList = invService.getMonitScreenIdsByMediumId(Util.getSessionMediumId(session));
    		
    		DataSourceResult result = null;
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		boolean inOperated = false;

    		if (Util.isValid(status) && (status.equals("6") || status.equals("5") || status.equals("4") ||
    				status.equals("3") || status.equals("1") || status.equals("0"))) {
    			
    	    	for(Integer i : monitList) {
    	    		String currStatus = GlobalInfo.InvenLastStatusMap.get("SC" + i);
    	    		if (Util.isValid(currStatus)) {
    	    			if (currStatus.equals(status)) {
    	    				ids.add(i);
    	    			}
    	    		} else {
    	    			if (status.equals("0")) {
    	    				ids.add(i);
    	    			}
    	    		}
    	    	}
    			
    			inOperated = true;
    		}
    		
    		if (Util.isValid(cmd) && (cmd.equals("C") || cmd.equals("F"))) {
    			
    			ArrayList<Integer> cmdIds = new ArrayList<Integer>();
    			
    	    	List<Tuple> tupleList = invService.getRTScreenCmdTupleListByMediumId(Util.getSessionMediumId(session));
    	    	for(Tuple tuple : tupleList) {
    	    		// SELECT s.screen_id, rt.next_cmd, rt.cmd_failed
    	    		int screenId = (Integer) tuple.get(0);
    	    		boolean failed = (Boolean) tuple.get(2);
    	    		
    	    		if (cmd.equals("F") && failed || cmd.equals("C")) {
    	    			if (!cmdIds.contains(screenId)) {
    	    				cmdIds.add(screenId);
    	    			}
    	    		}
    	    	}
    	    	
    	    	if (inOperated) {
        	    	List<Integer> tmpIds = Util.intersection(ids, cmdIds);
        			ids.clear();
        			ids.addAll(tmpIds);
    	    	} else {
    	    		ids.addAll(cmdIds);
    	    		
    	    		inOperated = true;
    	    	}
    		}
    		
    		if (inOperated) {
    			result = invService.getRTScreenViewListByScreenIdIn(request, ids);
    		} else {
    			result = invService.getRTScreenViewList(request);
    		}

    		
    		for(Object obj : result.getData()) {
    			InvRTScreenView rtScreen = (InvRTScreenView) obj;
    			
    			if (rtScreen != null) {
        			
        			RevObjTouch objTouch = revService.getObjTouch("S", rtScreen.getId());
        			if (objTouch != null) {
        				
        				// SolUtil.setScreenReqStatus 코드이며, 나중에 또 필요하게 되면 SolUtil에 생성하여 처리
    					String tmpStatus = GlobalInfo.InvenLastStatusMap.get("SC" + String.valueOf(rtScreen.getId()));
    					if (Util.isNotValid(tmpStatus)) {
    						tmpStatus = "0";
    					}
    					
    					rtScreen.setReqStatus(tmpStatus);
        			}
    				
        			// 화면 묶음명 설정
        			rtScreen.setScrPackName(SolUtil.getScreenPackNamesByScreenId(rtScreen.getId()).replace("|", ", "));
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readScr", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
    /**
	 * 읽기 액션 - 활성 동기화 묶음
	 */
    @RequestMapping(value = "/readSyncPack", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readSyncPack(@RequestBody DataSourceRequest request, HttpSession session) {
    	
		String status = request.getReqStrValue1();
		
    	try {
    		
    		int mediumId = Util.getSessionMediumId(session);
    		
    		ArrayList<Integer> ids = new ArrayList<Integer>();
    		boolean inOperated = false;
    		DataSourceResult result = null;
    		
    		if (Util.isValid(status) && (status.equals("6") || status.equals("5") || status.equals("4") ||
    				status.equals("3") || status.equals("1") || status.equals("0"))) { 
    			
            	HashMap<String, String> packScrMap = new HashMap<String, String>();
            	List<InvSyncPackItem> packItemList = invService.getActiveParentSyncPackItemListByMediumId(mediumId);
        		
        		for(InvSyncPackItem item : packItemList) {
        			String key = "PK" + item.getSyncPack().getId();
        			String prev = packScrMap.get(key);
        			
        			packScrMap.put(key, (Util.isValid(prev) ? prev + "|" : "") + String.valueOf(item.getScreenId()));
        		}
        		
        		List<InvSyncPack> list = invService.getActiveSyncPackList();
        		for(InvSyncPack syncPack : list) {
        			if (syncPack.getMedium().getId() != mediumId) {
        				continue;
        			}
        			
        			String tmpStatus = "0";
        			String scr = packScrMap.get("PK" + syncPack.getId());
        			if (Util.isValid(scr)) {
        				List<String> scrs = Util.tokenizeValidStr(scr);
        				for(String s : scrs) {
        					String scrStatus = GlobalInfo.InvenLastStatusMap.get("SC" + s);
        					if (Util.isValid(scrStatus) && tmpStatus.compareTo(scrStatus) < 0) {
        						tmpStatus = scrStatus;
        					}
        				}
        			}
        			
        			if (status.equals(tmpStatus)) {
        				ids.add(syncPack.getId());
        			}
        		}
    			
    			inOperated = true;
    		}
    		
    		if (inOperated) {
    			result = invService.getSyncPackListBySyncPackIdIn(request, ids);
    		} else {
    			result = invService.getSyncPackList(request);
    		}

    		
			HashMap<String, String> packScrMap = new HashMap<String, String>();

			if (result.getData().size() > 0) {
    			
    			List<InvSyncPackItem> packItemList = invService.getActiveParentSyncPackItemListByMediumId(Util.getSessionMediumId(session));
    			
    			for(InvSyncPackItem item : packItemList) {
    				String key = "PK" + item.getSyncPack().getId();
    				String prev = packScrMap.get(key);
    				
    				packScrMap.put(key, (Util.isValid(prev) ? prev + "|" : "") + String.valueOf(item.getScreenId()));
    			}
    		}
			
			
			String tmpStatus = "0";
    		for(Object obj : result.getData()) {
    			InvSyncPack syncPack = (InvSyncPack)obj;
    			
    			syncPack.setScreenCount(invService.getSyncPackItemCountBySyncPackId(syncPack.getId()));

    			tmpStatus = "0";
    			int activeScrCnt = 0;
				String scr = packScrMap.get("PK" + syncPack.getId());
				if (Util.isValid(scr)) {
					List<String> scrs = Util.tokenizeValidStr(scr);
					for(String s : scrs) {
						String scrStatus = GlobalInfo.InvenLastStatusMap.get("SC" + s);
						if (Util.isValid(scrStatus) && tmpStatus.compareTo(scrStatus) < 0) {
							tmpStatus = scrStatus;
						}
						if (Util.isValid(scrStatus) && scrStatus.equals("6")) {
							activeScrCnt ++;
						}
					}
				}
				
				syncPack.setReqStatus(tmpStatus);
				syncPack.setActiveScreenCount(activeScrCnt);
				
				InvRTSyncPack rtSyncPack = invService.getRTSyncPackBySyncPackId(syncPack.getId());
				if (rtSyncPack != null) {
					syncPack.setLastAdReq(rtSyncPack.getLastAdReqDate());
					syncPack.setLastAdBegin(rtSyncPack.getLastAdBeginDate());
					syncPack.setLastAd(rtSyncPack.getLastAd());
					syncPack.setGradeQueue(rtSyncPack.getGradeQueue());
					syncPack.setCountQueue(rtSyncPack.getCountQueue());
					syncPack.setChannel(rtSyncPack.getChannel());
					syncPack.setPlaylist(rtSyncPack.getPlaylist());
					
					syncPack.setDiff(rtSyncPack.getDiff());
					syncPack.setSeq(rtSyncPack.getSeq());
					syncPack.setSeqDiff(rtSyncPack.getSeqDiff());
				}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readSyncPack", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    

    /**
	 * 읽기 액션 - 광고 선택 / 보고 로그
	 */
    @RequestMapping(value = "/readApiLog", method = RequestMethod.POST)
    public @ResponseBody List<RevApiLogItem> readApiLog(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		String screenID = request.getReqStrValue1();
    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));

    		
    		ArrayList<RevApiLogItem> retList = new ArrayList<RevApiLogItem>();
    		ArrayList<String> keys = new ArrayList<String>();
    		
    		if (medium != null) {
        		InvScreen screen = invService.getScreen(medium, screenID);
        		if (screen != null) {
        			
        			List<AdcAd> adList = adcService.getAdListByMediumId(medium.getId());
        			HashMap<String, String> adPurchTypeMap = new HashMap<String, String>();
        			for(AdcAd ad : adList) {
        				adPurchTypeMap.put("A" + ad.getId(), ad.getPurchType());
        			}
        			
        			List<RevPlayHist> playHistList = revService.getPlayHistListByScreenId(screen.getId());
        			for(RevPlayHist playHist : playHistList) {
        				retList.add(new RevApiLogItem(playHist, adPurchTypeMap.get("A" + playHist.getAdId())));
        				
        				if (playHist.getPlayBeginDate() != null && playHist.getPlayEndDate() != null &&
        						playHist.getAdId() != null && playHist.getDuration() != null) {
        					String key = Util.toSimpleString(playHist.getPlayBeginDate(), "HHmmss") +
        							Util.toSimpleString(playHist.getPlayEndDate(), "HHmmss") +
        							"D" + String.valueOf(playHist.getDuration()) +
        							"A" + String.valueOf(playHist.getAdId());
        					keys.add(key);
        				}
        			}

        			List<RevAdSelect> adSelectList = revService.getAdSelectListByScreenId(screen.getId());
        			for(RevAdSelect adSelect : adSelectList) {
        				if (adSelect.getPlayBeginDate() != null && adSelect.getPlayEndDate() != null &&
        						adSelect.getDuration() != null && adSelect.getAdCreative() != null) {
        					String key = Util.toSimpleString(adSelect.getPlayBeginDate(), "HHmmss") +
        							Util.toSimpleString(adSelect.getPlayEndDate(), "HHmmss") +
        							"D" + String.valueOf(adSelect.getDuration()) +
        							"A" + String.valueOf(adSelect.getAdCreative().getAd().getId());
        					if (keys.contains(key)) {
        						continue;
        					}
        				}
        					
        				retList.add(new RevApiLogItem(adSelect));
        			}
        		}
    		}

    		return retList;
    	} catch (Exception e) {
    		logger.error("readApiLog", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 채널 광고
	 */
    @RequestMapping(value = "/readChanAd", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readChanAd(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		return revService.getChanAdList(request, Util.parseInt(request.getReqStrValue1()));
    	} catch (Exception e) {
    		logger.error("readChanAd", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 묶음 광고 노출
	 */
    @RequestMapping(value = "/readChanAdRpt", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readChanAdRpt(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = revService.getChanAdRptList(request, "P", Util.parseInt(request.getReqStrValue1()));
    		
    		HashMap<String, String> chanMap = new HashMap<String, String>();
    		
    		for(Object obj : result.getData()) {
    			RevChanAdRpt chanAdRpt = (RevChanAdRpt)obj;
    			
    			if (chanAdRpt != null) {
    				
    				chanAdRpt.setAdPackType(SolUtil.getAdPackType(chanAdRpt.getAdPackIds(), true));
    				
    				String chanName = chanMap.get("I" + chanAdRpt.getChannelId());
    				if (Util.isNotValid(chanName)) {
    					OrgChannel chan = orgService.getChannel(chanAdRpt.getChannelId());
    					if (chan != null) {
    						chanName = chan.getName();
    						chanMap.put("I" + chanAdRpt.getChannelId(), chanName);
    					}
    				}
    				chanAdRpt.setChanName(chanName);
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readChanAd", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 읽기 액션 - 이벤트 보고
	 */
    @RequestMapping(value = "/readEvent", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readEventReports(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = revService.getEventReportList(request, Util.getSessionMediumId(session));
    			
    		return result;
    	} catch (Exception e) {
    		logger.error("readEventReports", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
    /**
	 * 삭제 액션 - 이벤트 보고
	 */
    @RequestMapping(value = "/destroyEvent", method = RequestMethod.POST)
    public @ResponseBody String destroyEvent(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<RevEventReport> eventReports = new ArrayList<RevEventReport>();

    	for (Object id : objs) {
    		RevEventReport eventReport = new RevEventReport();
    		
    		eventReport.setId((int)id);
    		
    		eventReports.add(eventReport);
    	}

    	try {
        	revService.deleteEventReports(eventReports);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }

    
	/**
	 * 액션 - 명령 등록 및 취소
	 */
	@RequestMapping(value = "/processCmd", method = RequestMethod.POST)
    public @ResponseBody String processCmd(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		
    	String command = (String)model.get("command");

    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("ids");
		
		ArrayList<Integer> screenIds = new ArrayList<Integer>();
    	for (Object scrIdObj : objs) {
    		screenIds.add((int)scrIdObj);
    	}

    	if (Util.isNotValid(command) || screenIds.size() == 0) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
		try {
			Date now = new Date();
	    	for (int id : screenIds) {
	    		
	    		InvRTScreen rtScreen = invService.getRTScreenByScreenId(id);
	    		if (rtScreen == null) {
	    			invService.saveOrUpdate(new InvRTScreen(id));
	    			
	    			rtScreen = invService.getRTScreenByScreenId(id);
	    		}
	    		
	    		if (rtScreen != null) {
	    			if (command.equals("Cancel")) {
	    				rtScreen.setNextCmd("");
	    			} else {
	    				rtScreen.setNextCmd(command);
	    			}
    				rtScreen.setCmdFailed(false);
    				rtScreen.setWhoLastUpdateDate(now);
    				rtScreen.setCmdBy(Util.loginUserId(session));
    				
    				invService.saveOrUpdate(rtScreen);
    				
    				
    				RevObjTouch objTouch = revService.getObjTouch("S", id);
    				if (objTouch != null) {
						objTouch.setDate6(null);
						objTouch.touchWho();
						
						revService.saveOrUpdate(objTouch);
    				}
	    		}
	    	}
		} catch (Exception e) {
    		logger.error("processCmd", e);
    		throw new ServerOperationForbiddenException("SaveError");
    	}
    	
    	return "OK";
    }

	
	/**
	 * 액션 - 동기화 명령 등록 및 취소
	 */
	@RequestMapping(value = "/processSyncCmd", method = RequestMethod.POST)
    public @ResponseBody String processSyncCmd(@RequestBody Map<String, Object> model, Locale locale, 
    		HttpSession session) {
		
    	String command = (String)model.get("command");

    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("ids");
		
		ArrayList<Integer> packIds = new ArrayList<Integer>();
    	for (Object scrIdObj : objs) {
    		packIds.add((int)scrIdObj);
    	}

    	if (Util.isNotValid(command) || packIds.size() == 0) {
    		throw new ServerOperationForbiddenException(
    				msgMgr.message("common.server.msg.wrongParamError", locale));
    	}
    	
		try {

			String uri = "";
			String lockKey = "";
			String logStr = "";
			
			int resInt = -1;
			
			for (int id : packIds) {
	    		
	    		InvSyncPack syncPack = invService.getSyncPack(id);
	    		if (syncPack != null) {
	    			resInt = -1;
	    			
	    			lockKey = syncPack.getShortName() + command;
	    			logStr = "****** syncAd:[" + syncPack.getShortName() + "] - " + command;
	    			
	    			if (command.equals("Refresh")) {
						
						uri = "refresh?groupId=" + syncPack.getShortName() + "&list=" + 
								SolUtil.get90SecChannelAds(syncPack.getShortName());
						
						logger.info(logStr + " : uri = " + uri);
	    				resInt = SolUtil.requestRunnableFirebaseFunc(syncPack.getShortName(), uri, lockKey);
	    				
	    				revService.saveOrUpdate(new RevSyncPackImp(new Date(), syncPack.getShortName(), "RST1"));
	    			}
	    			
	    			if (resInt < 1) {
						logger.info(logStr + " : result = " + resInt);
	    			}
	    		}
	    	}
		} catch (Exception e) {
    		logger.error("processSyncCmd", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}
    	
    	return "OK";
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
	 * 읽기 액션 - 화면 해상도 정보
	 */
    @RequestMapping(value = "/readResolutions", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readResolutions(HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	return getResolutionList(medium);
    }
    
    
	/**
	 * 노출 액션
	 */
    @RequestMapping(value = "/impress", method = RequestMethod.POST)
    public @ResponseBody String impress(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String key = "";
    	String value = "";
    	
    	String screenName = "";
    	String adName = "";
    	
    	
    	// id의 값이 (+): playHist의 id, (-): adSelect의 id
    	int id = (int)model.get("id");
    	if (id > 0) {
    		RevPlayHist playHist = revService.getPlayHist(id);
    		if (playHist != null) {
    			Integer adId = playHist.getAdId();
    			Integer creativeId = playHist.getCreativeId();
    			Integer screenId = playHist.getScreenId();

    			if (adId == null && creativeId != null && screenId != null) {
    				throw new ServerOperationForbiddenException("대체 광고 항목은 강제 노출을 등록할 수 없습니다.");
    			} else if (screenId != null && adId != null && creativeId != null) {
    				key = "ImpS" + playHist.getScreenId().intValue();
    				value = String.valueOf(adId.intValue()) + "|" + String.valueOf(creativeId.intValue());
    				
    				screenName = playHist.getScreenName();
    				adName = playHist.getAdName();
    				
    				revService.saveOrUpdate(new RevImpWave(playHist.getScreenId().intValue(), playHist.getScreenName(),
    						adId.intValue(), playHist.getAdName(), creativeId.intValue(), playHist.getCreativeName(),
    						null, session));
    			}
    		}
    	} else if (id < 0) {
    		RevAdSelect adSelect = revService.getAdSelect(id * -1);
    		if (adSelect != null) {
				key = "ImpS" + adSelect.getScreen().getId();
				value = String.valueOf(adSelect.getAdCreative().getId());
				
				screenName = adSelect.getScreen().getName();
				adName = adSelect.getAdCreative().getAd().getName();
				
				revService.saveOrUpdate(new RevImpWave(adSelect.getScreen().getId(), adSelect.getScreen().getName(),
						adSelect.getAdCreative().getAd().getId(), adSelect.getAdCreative().getAd().getName(), 
						adSelect.getAdCreative().getCreative().getId(), adSelect.getAdCreative().getCreative().getName(),
						adSelect.getAdCreative().getId(), session));
    		}
    	}

    	if (Util.isNotValid(key) || Util.isNotValid(value)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	

    	return String.format("[%s] 화면에 [%s] 광고 노출을 10분동안 진행 등록되었습니다.", screenName, adName);
    }

    
	/**
	 * 읽기 액션 - 이벤트 보고 범주 정보
	 */
    @RequestMapping(value = "/readEventCats", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readEventCats(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-solid fa-circle fa-fw text-red", "빨간색", "R"));
		list.add(new DropDownListItem("fa-solid fa-square fa-fw text-orange", "주황색", "O"));
		list.add(new DropDownListItem("fa-solid fa-star text-blue fa-fw text-yellow", "노란색", "Y"));
		list.add(new DropDownListItem("fa-solid fa-diamond fa-fw text-green", "초록색", "G"));
		list.add(new DropDownListItem("fa-solid fa-heart fa-fw text-blue", "파란색", "B"));
		list.add(new DropDownListItem("fa-solid fa-apple-whole fa-fw text-purple", "보라색", "P"));

		return list;
    }

    
	/**
	 * 읽기 액션 - 이벤트 보고 보고 유형 정보
	 */
    @RequestMapping(value = "/readReportTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readReportTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-circle-exclamation fa-fw text-info", "정보", "I"));
		list.add(new DropDownListItem("fa-regular fa-bell fa-fw text-orange", "경고", "W"));
		list.add(new DropDownListItem("fa-regular fa-light-emergency-on fa-fw text-red", "오류", "E"));

		return list;
    }

    
	/**
	 * 읽기 액션 - 이벤트 보고 기기 유형 정보
	 */
    @RequestMapping(value = "/readEquipTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readEquipTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-flag fa-fw", "STB(Player)", "P"));

		return list;
    }

    
	/**
	 * 읽기 액션 - 이벤트 보고 시작점 정보
	 */
    @RequestMapping(value = "/readTriggerTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readTriggerTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-gear fa-fw", "내부 프로세스", "P"));
		list.add(new DropDownListItem("fa-regular fa-command fa-fw", "명령", "C"));
		list.add(new DropDownListItem("fa-regular fa-rocket fa-fw", "외부 앱", "A"));
		list.add(new DropDownListItem("fa-regular fa-circle-dashed fa-fw", "기타", "E"));

		return list;
    }
    
    
	/**
	 * 모니터링 현재 통계 항목 획득
	 */
    private RevMonitCurrStatItem getMonitCurrStatItem(int mediumId) {
    	
    	RevMonitCurrStatItem ret = new RevMonitCurrStatItem();
    	
    	// 메모리에 있는 현재 상태의 통계치 전달
    	int status0 = 0, status1 = 0, status3 = 0, status4 = 0, status5 = 0, status6 = 0;
    	List<Integer> monitList = invService.getMonitScreenIdsByMediumId(mediumId);
    	for(Integer i : monitList) {
    		String status = GlobalInfo.InvenLastStatusMap.get("SC" + i);
    		if (Util.isValid(status)) {
    			if (status.equals("6")) {
    				status6 ++;
    			} else if (status.equals("5")) {
    				status5 ++;
    			} else if (status.equals("4")) {
    				status4 ++;
    			} else if (status.equals("3")) {
    				status3 ++;
    			} else if (status.equals("1")) {
    				status1 ++;
    			} else {
    				status0 ++;
    			}
    		} else {
    			status0 ++;
    		}
    	}
    	
    	ret.setStatusCnt6(String.format("%,d", status6));
    	ret.setStatusCnt5(String.format("%,d", status5));
    	ret.setStatusCnt4(String.format("%,d", status4));
    	ret.setStatusCnt3(String.format("%,d", status3));
    	ret.setStatusCnt1(String.format("%,d", status1));
    	ret.setStatusCnt0(String.format("%,d", status0));

    	
    	// 명령이 설정되어 있는, 명령 실행 오류가 있는 화면의 통계치 전달
    	int failed = 0;
    	List<Tuple> tupleList = invService.getRTScreenCmdTupleListByMediumId(mediumId);
    	for(Tuple tuple : tupleList) {
    		// SELECT s.screen_id, rt.next_cmd, rt.cmd_failed
    		if (((Boolean) tuple.get(2)).booleanValue()) {
    			failed++;
    		}
    	}
    	
    	ret.setCmdCnt(String.format("%,d", tupleList.size()));
    	ret.setCmdFailCnt(String.format("%,d", failed));
    	
    	
    	return ret;
    }
    
    
	/**
	 * 동기화 화면 묶음 현재 통계 항목 획득
	 */
    private RevSyncPackCurrStatItem getSyncPackCurrStatItem(int mediumId) {
    	
    	RevSyncPackCurrStatItem ret = new RevSyncPackCurrStatItem();
    	
    	int syncPackCount = invService.getActiveSyncPackCountByMediumId(mediumId);
    	if (syncPackCount > 0) {
    		
    		ret.setSyncPackMode(true);
    	}

    	// 메모리에 있는 현재 상태의 통계치 전달
    	int status0 = 0, status1 = 0, status3 = 0, status4 = 0, status5 = 0, status6 = 0;
    	HashMap<String, String> packScrMap = new HashMap<String, String>();
    	List<InvSyncPackItem> packItemList = invService.getActiveParentSyncPackItemListByMediumId(mediumId);
		
		for(InvSyncPackItem item : packItemList) {
			String key = "PK" + item.getSyncPack().getId();
			String prev = packScrMap.get(key);
			
			packScrMap.put(key, (Util.isValid(prev) ? prev + "|" : "") + String.valueOf(item.getScreenId()));
		}
		
		List<InvSyncPack> list = invService.getActiveSyncPackList();
		for(InvSyncPack syncPack : list) {
			if (syncPack.getMedium().getId() != mediumId) {
				continue;
			}
			
			String status = "0";
			String scr = packScrMap.get("PK" + syncPack.getId());
			if (Util.isValid(scr)) {
				List<String> scrs = Util.tokenizeValidStr(scr);
				for(String s : scrs) {
					String scrStatus = GlobalInfo.InvenLastStatusMap.get("SC" + s);
					if (Util.isValid(scrStatus) && status.compareTo(scrStatus) < 0) {
						status = scrStatus;
					}
				}
			}
			
			if (status.equals("6")) {
				status6 ++;
			} else if (status.equals("5")) {
				status5 ++;
			} else if (status.equals("4")) {
				status4 ++;
			} else if (status.equals("3")) {
				status3 ++;
			} else if (status.equals("1")) {
				status1 ++;
			} else {
				status0 ++;
			}
		}
    	
    	ret.setStatusCnt6(String.format("%,d", status6));
    	ret.setStatusCnt5(String.format("%,d", status5));
    	ret.setStatusCnt4(String.format("%,d", status4));
    	ret.setStatusCnt3(String.format("%,d", status3));
    	ret.setStatusCnt1(String.format("%,d", status1));
    	ret.setStatusCnt0(String.format("%,d", status0));
    	
    	
    	return ret;
    }
    
    
	/**
	 * 읽기 액션 - 모니터링 현재 통계 항목
	 */
    @RequestMapping(value = "/readMonitStat", method = RequestMethod.POST)
    public @ResponseBody RevMonitCurrStatItem readMonitStat(HttpSession session) {
    	
    	return getMonitCurrStatItem(Util.getSessionMediumId(session));
    }
    
    
	/**
	 * 읽기 액션 - 동기화 화면 묶음 현재 통계 항목
	 */
    @RequestMapping(value = "/readSyncPackStat", method = RequestMethod.POST)
    public @ResponseBody RevSyncPackCurrStatItem readSyncPackStat(HttpSession session) {
    	
    	return getSyncPackCurrStatItem(Util.getSessionMediumId(session));
    }

    
    /**
	 * 액션 - 재생목록 시간 삭제
	 */
    @RequestMapping(value = "/destroyPlTime", method = RequestMethod.POST)
    public @ResponseBody String destroyPlTime(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {

		InvRTScreen rtScreen = invService.getRTScreenByScreenId((int)model.get("id"));
		String lane = (String)model.get("lane");

		if (rtScreen == null || Util.isNotValid(lane) || ("ABCD").indexOf(lane) == -1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
		}

    	try {
    		
    		if (lane.equals("A")) {
    			rtScreen.setaPlaylistDate(null);
    			rtScreen.setaPlaylist("");
    		} else if (lane.equals("B")) {
    			rtScreen.setbPlaylistDate(null);
    			rtScreen.setbPlaylist("");
    		} else if (lane.equals("C")) {
    			rtScreen.setcPlaylistDate(null);
    			rtScreen.setcPlaylist("");
    		} else if (lane.equals("D")) {
    			rtScreen.setdPlaylistDate(null);
    			rtScreen.setdPlaylist("");
    		}
    		
    		rtScreen.setWhoLastUpdateDate(new Date());
    		
    		invService.saveOrUpdate(rtScreen);

    	} catch (Exception e) {
    		logger.error("destroyPlTime", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 채널 목록 획득
	 */
    public List<DropDownListItem> getChannelDropDownListByMediumId(int mediumId) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		List<OrgChannel> chanList = orgService.getChannelListByMediumId(mediumId);
		for (OrgChannel channel : chanList) {
			String icon = channel.isActiveStatus() ? "fa-tower-cell" : "fa-circle-dashed";
			String text = channel.getShortName();
			String value = String.valueOf(channel.getId());
			String subIcon = channel.getName();
			
			list.add(new DropDownListItem(icon, subIcon, text, value));
		}

		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

		list.add(0, new DropDownListItem("fa-tower-cell", "선택", "광고 채널", "-1"));
		
		return list;
    }
    
    
	/**
	 * 동기화 묶음 목록 획득
	 */
    public List<DropDownListItem> getSyncPackDropDownListByMediumId(int mediumId) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		List<InvSyncPack> syncPackList = invService.getSyncPackListByMediumId(mediumId);
		for (InvSyncPack syncPack : syncPackList) {
			String icon = syncPack.isActiveStatus() ? "fa-box-taped" : "fa-circle-dashed";
			String text = syncPack.getShortName();
			String value = String.valueOf(syncPack.getId());
			String subIcon = syncPack.getName();
			
			list.add(new DropDownListItem(icon, subIcon, text, value));
		}

		Collections.sort(list, CustomComparator.DropDownListItemTextComparator);

		list.add(0, new DropDownListItem("fa-box-taped", "선택", "동기화 화면 묶음", "-1"));

		return list;
    }

}
