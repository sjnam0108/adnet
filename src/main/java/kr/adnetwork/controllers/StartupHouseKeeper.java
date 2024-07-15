package kr.adnetwork.controllers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import kr.adnetwork.info.GlobalInfo;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.adc.AdcPlaylist;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.inv.InvRTSyncPack;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.inv.InvSyncPackItem;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgAlimTalk;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.org.OrgRTChannel;
import kr.adnetwork.models.rev.RevAdSelect;
import kr.adnetwork.models.rev.RevChanAd;
import kr.adnetwork.models.rev.RevChanAdRpt;
import kr.adnetwork.models.rev.RevImpWave;
import kr.adnetwork.models.rev.RevObjTouch;
import kr.adnetwork.models.rev.RevPlayHist;
import kr.adnetwork.models.rev.RevScrHourlyPlay;
import kr.adnetwork.models.rev.RevScrHrlyFailTot;
import kr.adnetwork.models.rev.RevScrHrlyFbTot;
import kr.adnetwork.models.rev.RevScrHrlyNoAdTot;
import kr.adnetwork.models.rev.RevSyncPackImp;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.models.service.SysService;
import kr.adnetwork.models.sys.SysOpt;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.adc.AdcAdCreatFileObject;
import kr.adnetwork.viewmodels.fnd.FndViewTypeItem;
import kr.adnetwork.viewmodels.inv.InvSyncPackCompactItem;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import kr.adnetwork.viewmodels.rev.RevAdOrderItem;
import kr.adnetwork.viewmodels.rev.RevObjEventTimeItem;
import kr.adnetwork.viewmodels.rev.RevChanAdPlayItem;
import kr.adnetwork.viewmodels.rev.RevScrWorkTimeItem;
import kr.adnetwork.viewmodels.rev.RevChanAdItem;
import kr.adnetwork.viewmodels.rev.RevSyncPackMinMaxItem;
import kr.adnetwork.viewmodels.rev.RevChannelPlItem;
import kr.adnetwork.viewmodels.sys.SysObjEventTimeCalcItem;
import kr.adnetwork.viewmodels.sys.SysScrEventTimeCalcItem;
import kr.adnetwork.viewmodels.sys.SysScrWorkTimeCalcItem;

@SuppressWarnings("unused")
@Component
public class StartupHouseKeeper implements ApplicationListener<ContextRefreshedEvent> {
	private static final Logger logger = LoggerFactory.getLogger(StartupHouseKeeper.class);

	private static int adCalcCount = 0;
	

	@Autowired
	private InvService invService;

	@Autowired
	private RevService revService;

	@Autowired
	private AdcService adcService;

	@Autowired
	private KnlService knlService;

	@Autowired
	private OrgService orgService;

	@Autowired
	private SysService sysService;

	@Autowired
	private FndService fndService;
	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		String appId = event.getApplicationContext().getId();
		logger.info("Enter onApplicationEvent() - id=" + appId);
		
		if (!appId.equals("org.springframework.web.context.WebApplicationContext:/" + GlobalInfo.AppId)) {
			return;
		}
		
		SolUtil.setBgMaxSeq("PM", 100);		// 임의의 큰 값으로 설정 후 나중에 조정
		SolUtil.setBgMaxSeq("CA", 100);		// 임의의 큰 값으로 설정 후 나중에 조정
		SolUtil.setBgMaxSeq("MT", 100);		// 임의의 큰 값으로 설정 후 나중에 조정
		
		String repServer = Util.getFileProperty("url.report.server");
		if (Util.isValid(repServer) && repServer.startsWith("http")) {
			// http, https 로 한정 목적
			GlobalInfo.ReportServer = repServer;
		}
		
		String apiTestServer = Util.getFileProperty("url.apitest.server");
		if (Util.isValid(apiTestServer) && apiTestServer.startsWith("http")) {
			// http, https 로 한정 목적
			GlobalInfo.ApiTestServer = apiTestServer;
		}
		
		// 동기화 화면에서 리셋 기기수 패턴
		String[] patterns = {
				"111112","111141","111211","111411", "111414", "111551", "112111", "114111", "114114", "114141",
				"114411","115151","115155","115515", "116117", "121111", "121213", "121231", "121321", "121323",
				"123121", "123123","123231","131212","132131", "132132", "132312", "132323", "141111", "141114", 
				"141141", "141144","141411","144114","151115", "151151", "151155", "151511", "151515", "151551", 
				"155115", "155151","155155","161116","171717", "171771", "177117", "177171", "211111", "212132", 
				"212312", "213121","213213","213231","213232", "231213", "231231", "231232", "231312", "231321", 
				"232313", "242442","244242","312121","312123", "312132", "312312", "312323", "313121", "313213", 
				"321312", "321323","323123","323131","323132", "323231", "411114", "411141", "411411", "411414", 
				"414114", "424244","424424","441141","511515", "511551", "515111", "515115", "515151", "515155", 
				"515511", "515515","515551","551151","551511", "551551", "711611", "711717", "711771", "717117"
		};

		GlobalInfo.SyncPackProhibitedPatterns = new ArrayList<String>(Arrays.asList(patterns));
		
		
		// 서버 구조 위치에 대한 역할 구분
		//
		// - 모든 서버
		//   - 모든 서버에서 동작하는 것이니, 별도 역할 지정을 위한 file property가 불필요
		// - bg.for.app: 운영자용 서버 서비스용
		// - bg.for.was: API 서버 서비스용
		// - bg.for.login: 사용자 로그인용
		//
		
		
		// 모든 서버
		//
		
		// [동기화 그룹 재생목록 큐 유지]
		//   - 비록 모든 서버에서 동작한다고 하더라도, 로직에서 lock을 통해 한시점 한서버만 동작되도록 함
		//
		// 구동 직후, 1분 단위
		new Timer().schedule(new TimerTask() {
			public void run() {
	    		   
				logger.info("     -> [광고 채널 광고 큐 유지] - 시작");
				logger.info("     ->           [광고 채널 광고 큐 유지] - {}", queueChannelAds());
			}
		}, 0, 1 * 60 * 1000);
		
		
		
		// bg.for.login: 사용자 로그인용
		//
		if (isFilePropertyYes("bg.for.login")) {
			
			// [RSA 키 변경]
			//
			// 구동 직후, 30분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
					
					GlobalInfo.RSAKeyPair = Util.getKeyPairRSA();
					GlobalInfo.RSAKeyMod = "";

					logger.info("     -> [RSA 키 변경] - KeyPair: " + (GlobalInfo.RSAKeyPair == null ? "null" : "new"));

				}
			}, 0, 30 * 60 * 1000);
			
		}
		
		
		
		// bg.for.app: 운영자용 서버 서비스용
		//
		if (isFilePropertyYes("bg.for.app")) {
			
			// [광고 오늘 목표치 계산], [오늘 기반 캠페인/광고 상태 변경], [광고/화면별 시간 목표치 계산]
			// - 정각 구동(매분 정각)
			//
			// 매분 정각, 1분 단위, FixedRate
			
			Date now = new Date();
			Date nextDt = DateUtils.ceiling(now, Calendar.MINUTE);
			
			// 분 정각까지의 지연 시간
			// 의도적인 0.5s 지연(현재 시간 기반 분기가 진행 예정)
			long delay = nextDt.getTime() - now.getTime() + 500;
			
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		   
					GregorianCalendar calendar = new GregorianCalendar();
					calendar.setTime(new Date());

					// 매일 정각 즈음
					if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) == 1) {
						logger.info("     -> [광고 오늘 목표치 계산] - 시작");
						logger.info("     ->           [광고 오늘 목표치 계산] - {}", calcAdTodayTargetValues());
					}
		    		   
					// 매시간 정각: 오늘 기반 캠페인/광고 상태 변경
					if (calendar.get(Calendar.MINUTE) == 0 || calendar.get(Calendar.MINUTE) == 1) {
						logger.info("     -> [오늘 기반 캠페인/광고 상태 변경] - 시작");
						logger.info("     ->           [오늘 기반 캠페인/광고 상태 변경] - refreshed: {}", adcService.refreshCampaignAdStatusBasedToday());
					}
					if (calendar.get(Calendar.MINUTE) == 0) {
						logger.info("     -> [광고/화면별 시간 목표치 계산] - 시작");
						logger.info("     ->           [광고/화면별 시간 목표치 계산] - {}", calcAdHourlyGoalValue());
					}
		    		   
				}
			}, delay, 1 * 60 * 1000);
			
			// [광고 오늘 목표치 계산]
			//
			// 구동 30초 후, 1회성
			new Timer().schedule(new TimerTask() {
				public void run() {
		    		   
					logger.info("     -> [광고 오늘 목표치 계산] - 시작");
					logger.info("     ->           [광고 오늘 목표치 계산] - {}", calcAdTodayTargetValues());
				}
			}, 30 * 1000);

			
			// [광고 선택/보고 정리]
			//
			// 구동 20초 후, 30분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		   
					logger.info("     -> [광고 선택/보고 정리] - 시작");
					logger.info("     ->           [광고 선택/보고 정리] - {} rows organized.", organizePlayHists());
				}
			}, 20 * 1000, 30 * 60 * 1000);
			
			// [동기화 묶음 기준 재생목록 및 보고 에이징 자료 정리]
			//
			// 구동 20초 후, 30분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		
					logger.info("     -> [동기화 묶음 광고 보고 및 채널 광고 자료 정리] - 시작");
					logger.info("     ->           [동기화 묶음 광고 보고 및 채널 광고 자료 정리] - {}", organizeSyncPackChanAds());
				}
			}, 20 * 1000, 30 * 60 * 1000);
			
			// [광고 선택/보고 로그]
			// - 광고 선택/보고 자료를 텍스트 파일 로그로 만들고, DB 자료 삭제
			//
			// 구동 1분 후, 1분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		
					logger.info("     -> [광고 선택/보고 로그] - 시작");
					logger.info("     ->           [광고 선택/보고 로그] - {} rows logged.", logPlayHists());
				}
			}, 1 * 60 * 1000, 1 * 60 * 1000);
			
			// [인벤 상태 최신화]
			// - 매체 화면과 사이트의 현재 상태 자동 계산
			// - 시간 경과에 따라 상태값을 다르게 설정하기 위해 주기적으로 계산/설정
			//
			// 구동 10초 후, 1분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		
					logger.info("     -> [인벤 상태 최신화] - 시작");
					logger.info("     ->           [인벤 상태 최신화] - {} rows recalced.", doRecalcLastStatus());
				}
			}, 10 * 1000, 1 * 60 * 1000);
			
			// [통계 자료 계산]
			// - 시간당 화면 및 사이트 재생 합계를 계산
			//
			// 구동 1분 후, 30분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		
					logger.info("     -> [통계 자료 계산] - 시작");
					logger.info("     ->           [통계 자료 계산] - {}", calcStatsHourly());
				}
			}, 1 * 60 * 1000, 30 * 60 * 1000);
			
			// [광고/화면별 시간 목표치 계산]
			//
			// 구동 직후, 30분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		
					logger.info("     -> [광고/화면별 시간 목표치 계산] - 시작");
					logger.info("     ->           [광고/화면별 시간 목표치 계산] - {}", calcAdHourlyGoalValue());
				}
			}, 0, 30 * 60 * 1000);
			
			// [광고별 하루 송출량 계산]
			//
			// 구동 1분 후, 5분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		
					logger.info("     -> [광고별 하루 송출량 계산] - 시작");
					logger.info("     ->           [광고별 하루 송출량 계산] - {}", calcDailyAdImpressionStat());
				}
			}, 1 * 60 * 1000, 5 * 60 * 1000);
			
			// [동기화 화면 묶음 정보 새로고침]
			// - 동기화 화면의 시작 시간 계산에 사용되는 동기화 화면 묶음 정보 주기적 갱신
			//
			// 구동 30초 후, 1초 단위
			new Timer().schedule(new TimerTask() {
				public void run() {
		    		   
					removeExpiredSyncPackReportMap();
				}
			}, 30 * 1000, 1 * 1000);
			
			// [화면 분단위 상태행 최종 기록]
			// - 임시로 저장된 화면 분단위 상태행을 집계해서 기록
			//
			// 구동 30초 후, 30초 단위
			new Timer().schedule(new TimerTask() {
				public void run() {
		    		   
					logger.info("     -> [화면 분단위 상태행 최종 기록] - 시작");
					logger.info("     ->           [화면 분단위 상태행 최종 기록] - {}", saveScreenStatusLines());
				}
			}, 30 * 1000, 30 * 1000);
			
			// [화면 시간당 이벤트 합계 기록]
			// - 임시로 저장된 화면 시간당 이벤트를 집계해서 기록
			//
			// 구동 30초 후, 1분 단위
			new Timer().schedule(new TimerTask() {
				public void run() {
		    		   
					logger.info("     -> [화면 시간당 이벤트 합계 기록] - 시작");
					logger.info("     ->           [화면 시간당 이벤트 합계 기록] - {}", saveHourlyEvents());
				}
			}, 30 * 1000, 1 * 60 * 1000);
			
			// [알림톡 이벤트 확인]
			// - 알림톡 설정(이벤트 및 수신 정보)
			//
			// 구동 1분 후, 1분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
		    		
					logger.info("     -> [알림톡 이벤트 확인] - 시작");
					logger.info("     ->           [알림톡 이벤트 확인] - {}", checkAlimTalkEvents());
				}
			}, 1 * 60 * 1000, 1 * 60 * 1000);
		}
		
		
		
		// bg.for.was: API 서버 서비스용
		//
		if (isFilePropertyYes("bg.for.was")) {

			// [묶음 광고 레인별 목록], [광고/광고 소재 목록], [광고/광고 소재 타겟팅 대상]
			// [광고 모바일 타겟팅 대상], [광고 노출 웨이브], [게시 유형 목록]
			//
			// 구동 5초 후, 1분 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
					
					logger.info("     -> [묶음 광고 레인별 목록] - 시작");
					logger.info("     ->           [묶음 광고 레인별 목록] - {}", refreshViewTypeAdLaneList());
					logger.info("     -> [광고/광고 소재 목록] - 시작");
					logger.info("     ->           [광고/광고 소재 목록] - {}", createCandiAdCreatList());
					logger.info("     -> [광고/광고 소재 타겟팅 대상] - 시작");
					logger.info("     ->           [광고/광고 소재 타겟팅 대상] - {}", refreshInvenTargetScreenList());
					logger.info("     -> [광고 모바일 타겟팅 대상] - 시작");
					logger.info("     ->           [광고 모바일 타겟팅 대상] - {}", refreshMobileAdList());
					logger.info("     -> [광고 노출 웨이브] - 시작");
					logger.info("     ->           [광고 노출 웨이브] - {}", checkImpressionWaves());
					logger.info("     -> [게시 유형 목록] - 시작");
					logger.info("     ->           [게시 유형 목록] - {}", refreshViewTypeList());
				}
			}, 5 * 1000, 1 * 60 * 1000);
			
			// [간략화된 매체 목록], [간략화된 동기화 묶음 목록]
			//
			// 구동 직후, 30초 단위, FixedRate
			new Timer().scheduleAtFixedRate(new TimerTask() {
				public void run() {
					
					logger.info("     -> [간략화된 매체 목록] - 시작");
					logger.info("     ->           [간략화된 매체 목록] - {}", refreshMedia());
					logger.info("     -> [간략화된 동기화 묶음 목록] - 시작");
					logger.info("     ->           [간략화된 동기화 묶음 목록] - {}", refreshSyncPacks());
				}
			}, 0, 30 * 1000);
			
			// [화면 분단위 상태행 임시 기록]
			//
			// 구동 30초 후, 30초 단위
			new Timer().schedule(new TimerTask() {
				public void run() {
					
					logger.info("     -> [화면 분단위 상태행 임시 기록] - 시작");
					logger.info("     ->           [화면 분단위 상태행 임시 기록] - {}", saveTmpScreenStatusLines());
				}
			}, 30 * 1000, 30 * 1000);
			
			// [개체 최근 변경일시 임시 집계]
			// - 개체의 최근 변경일시를 WAS에서 받아 모아서 백그라운드로 임시 생성
			//
			// 구동 30초 후, 30초 단위
			new Timer().schedule(new TimerTask() {
				public void run() {
					
					logger.info("     -> [개체 최근 변경일시 임시 집계] - 시작");
					logger.info("     ->           [개체 최근 변경일시 임시 집계] - {}", calcTmpObjEvents());
				}
			}, 30 * 1000, 30 * 1000);
			
			// [개체 최근 변경일시 집계 기록]
			// - 개체의 최근 변경일시를 WAS에서 받아 모아서 백그라운드로 임시 생성
			//
			// 구동 1분 후, 1분 단위
			new Timer().schedule(new TimerTask() {
				public void run() {
					
					logger.info("     -> [개체 최근 변경일시 집계 기록] - 시작");
					logger.info("     ->           [개체 최근 변경일시 집계 기록] - {}", saveObjEvents());
				}
			}, 1 * 60 * 1000, 1 * 60 * 1000);
		}
		
	}
	
	
	// 화면과 사이트에 대한 현재 상태 재계산
	private int doRecalcLastStatus() {

		String status = "0";
		Date now = new Date();
		int cnt = 0;

		try {
			// 화면 현재 상태 새로고침
			
			List<RevObjTouch> objList = revService.getObjTouchList();
			for(RevObjTouch objTouch : objList) {
				if (objTouch.getType().equals("S")) {
					// 존재하지 않는 화면 / 사이트에 대한 상태 값: "0"
					GlobalInfo.InvenLastStatusMap.put("SC" + objTouch.getObjId(), objTouch.getScreenStatus());
					cnt ++;
				}
			}

			// 모든 화면/사이트의 관계를 미리 map으로 준비하고, monitSite에 대해서만 상태 관리
			HashMap<String, String> siteScrMap = new HashMap<String, String>();
			List<InvScreen> scrList = invService.getScreenList();
			for(InvScreen screen : scrList) {
				String key = "ST" + screen.getSite().getId();
				String prev = siteScrMap.get(key);
				
				siteScrMap.put(key, (Util.isValid(prev) ? prev + "|" : "") + String.valueOf(screen.getId()));
			}
			List<InvSite> monSiteList = invService.getMonitSiteList();
			for(InvSite site : monSiteList) {
				status = "0";
				String scr = siteScrMap.get("ST" + site.getId());
				if (Util.isValid(scr)) {
					List<String> scrs = Util.tokenizeValidStr(scr);
					for(String s : scrs) {
						String scrStatus = GlobalInfo.InvenLastStatusMap.get("SC" + s);
						if (Util.isValid(scrStatus) && status.compareTo(scrStatus) < 0) {
							status = scrStatus;
						}
					}
				}
				
				GlobalInfo.InvenLastStatusMap.put("ST" + site.getId(), status);
				cnt ++;
			}
		} catch (Exception e) {
    		logger.error("doRecalcLastStatus", e);
		}
		
		return cnt;
	}
	
	// 정상 처리되지 않은 자료 정리
	private int organizePlayHists() {
		
		GregorianCalendar calendar = new GregorianCalendar();
		int ret = 0;
		
		try {
			
			// Step 1. 예기치 않은 잔여 정상 자료(예를 들어 보고받은 기록을 한 직후, 서버 다운 등) 정리
			List<RevAdSelect> list = revService.getReportedAdSelectListOrderBySelDateBeforeReportDate(Util.addMinutes(new Date(), -30));
			for(RevAdSelect adSelect : list) {
				if (adSelect.getResult() != null && adSelect.getResult().booleanValue()) {
					
					// 시간당 화면/광고 재생 계산
		    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(adSelect.getScreen(), 
		    				adSelect.getAdCreative(), Util.removeTimeOfDate(adSelect.getSelectDate()));
		    		if (hourlyPlay == null) {
		    			hourlyPlay = new RevScrHourlyPlay(adSelect.getScreen(), adSelect.getAdCreative(), 
		    					Util.removeTimeOfDate(adSelect.getSelectDate()));
		    		}
		    		
		    		calendar.setTime(adSelect.getSelectDate());
			        
			        switch (calendar.get(Calendar.HOUR_OF_DAY)) {
			        case 0: hourlyPlay.setCnt00(hourlyPlay.getCnt00() + 1); break;
			        case 1: hourlyPlay.setCnt01(hourlyPlay.getCnt01() + 1); break;
			        case 2: hourlyPlay.setCnt02(hourlyPlay.getCnt02() + 1); break;
			        case 3: hourlyPlay.setCnt03(hourlyPlay.getCnt03() + 1); break;
			        case 4: hourlyPlay.setCnt04(hourlyPlay.getCnt04() + 1); break;
			        case 5: hourlyPlay.setCnt05(hourlyPlay.getCnt05() + 1); break;
			        case 6: hourlyPlay.setCnt06(hourlyPlay.getCnt06() + 1); break;
			        case 7: hourlyPlay.setCnt07(hourlyPlay.getCnt07() + 1); break;
			        case 8: hourlyPlay.setCnt08(hourlyPlay.getCnt08() + 1); break;
			        case 9: hourlyPlay.setCnt09(hourlyPlay.getCnt09() + 1); break;
			        case 10: hourlyPlay.setCnt10(hourlyPlay.getCnt10() + 1); break;
			        case 11: hourlyPlay.setCnt11(hourlyPlay.getCnt11() + 1); break;
			        case 12: hourlyPlay.setCnt12(hourlyPlay.getCnt12() + 1); break;
			        case 13: hourlyPlay.setCnt13(hourlyPlay.getCnt13() + 1); break;
			        case 14: hourlyPlay.setCnt14(hourlyPlay.getCnt14() + 1); break;
			        case 15: hourlyPlay.setCnt15(hourlyPlay.getCnt15() + 1); break;
			        case 16: hourlyPlay.setCnt16(hourlyPlay.getCnt16() + 1); break;
			        case 17: hourlyPlay.setCnt17(hourlyPlay.getCnt17() + 1); break;
			        case 18: hourlyPlay.setCnt18(hourlyPlay.getCnt18() + 1); break;
			        case 19: hourlyPlay.setCnt19(hourlyPlay.getCnt19() + 1); break;
			        case 20: hourlyPlay.setCnt20(hourlyPlay.getCnt20() + 1); break;
			        case 21: hourlyPlay.setCnt21(hourlyPlay.getCnt21() + 1); break;
			        case 22: hourlyPlay.setCnt22(hourlyPlay.getCnt22() + 1); break;
			        case 23: hourlyPlay.setCnt23(hourlyPlay.getCnt23() + 1); break;
			        }
			        
			        hourlyPlay.calcTotal();
			        hourlyPlay.touchWho();
			        
			        revService.saveOrUpdate(hourlyPlay);
			        
			        // 재생 기록 생성
			        revService.saveOrUpdate(new RevPlayHist(adSelect));
			        
			        // 광고 선택 삭제
			        revService.deleteAdSelect(adSelect);
			        
			        ret ++;
				}
			}
			
		} catch (Exception e) {
    		logger.error("organizePlayHists - Step 1", e);
		}
		
		
		try {
			
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			// Step 2. 미보고 자료 정리
			//
			// 미보고 자료(AdSelect)의 수가 과도하게 많아지게 되면, 시스템에서 처리하는 방법을 변경하는 것도 고려해야 한다.
			//
			//  method 1. 개별 자료 건에 대해 
			//            시간당 화면/광고 재생 계산 에서 실패 + 1, 재생 기록 생성, 광고 선택 삭제
			//            초당 10건 처리된다면, 분당 600건, 시간당 36,000건이 한계
			//
			//  method 2. 시간당 화면/광고 재생 계산 별로 그룹핑
			//            시간당 화면/광고 재생 계산 별로 그룹핑을 진행하고, 이후 재생 기록 생성(옵션), 광고 선택 삭제
			//
			//  method 2에 대해서는 이후에 다시 개선 예정
			//
			
			Date limitDate = Util.addHours(new Date(), -24);
			//Date limitDate = Util.parseDate("20230418210000");
			
			// method 1 시작
			//
			List<RevAdSelect> list = revService.getAdSelectListBeforeSelectDateOrderBySelDate(limitDate);
			for(RevAdSelect adSelect : list) {
				if (adSelect.getResult() == null || (adSelect.getResult() != null && adSelect.getResult().booleanValue() == false)) {
					
					// 시간당 화면/광고 재생 계산
		    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(adSelect.getScreen(), 
		    				adSelect.getAdCreative(), Util.removeTimeOfDate(adSelect.getSelectDate()));
		    		if (hourlyPlay == null) {
		    			hourlyPlay = new RevScrHourlyPlay(adSelect.getScreen(), adSelect.getAdCreative(), 
		    					Util.removeTimeOfDate(adSelect.getSelectDate()));
		    		}
					
		    		hourlyPlay.setFailTotal(hourlyPlay.getFailTotal() + 1);
		    		hourlyPlay.setDateTotal(hourlyPlay.getSuccTotal() + hourlyPlay.getFailTotal());
			        hourlyPlay.touchWho();
			        
			        revService.saveOrUpdate(hourlyPlay);
			        
			        
			        // 시간당 화면 실패 합계 임시 테이블로 추가(method 2에는 아직 포함안됨)
			        sysService.insertTmpHrlyEvent(adSelect.getScreen().getId(), adSelect.getSelectDate(), 1);
			        
			        
			        // 재생 기록 생성
			        revService.saveOrUpdate(new RevPlayHist(adSelect));
			        
			        // 광고 선택 삭제
			        //revService.deleteAdSelect(adSelect);
			        ids.add(adSelect.getId());
			        
			        ret ++;
			        
			        if (ret >= 10000) {
			        	break;
			        }
				}
			}
			revService.deleteAdSelectBulkRowsInIds(ids);
			//
			
			
			// method 2 시작
			/*
			logger.info("start");
			List<RevAdSelect> list = revService.getAdSelectListBeforeSelectDateOrderBySelDate(limitDate);
			HashMap<String, RevScrHrlyPlyAdCntItem> map = new HashMap<String, RevScrHrlyPlyAdCntItem>();
			logger.info("list size = " + list.size());
			for(RevAdSelect adSelect : list) {
				if (adSelect.getResult() == null || (adSelect.getResult() != null && adSelect.getResult().booleanValue() == false)) {
					
					String key = "S" + adSelect.getScreen().getId() + "A" + adSelect.getAdCreative().getAd().getId() +
							"D" + Util.toSimpleString(Util.removeTimeOfDate(adSelect.getSelectDate()), "dd");
					if (map.containsKey(key)) {
						RevScrHrlyPlyAdCntItem item = map.get(key);
						item.addOneCount();
					} else {
						map.put(key, new RevScrHrlyPlyAdCntItem(adSelect.getScreen().getId(), adSelect.getAdCreative().getAd().getId(),
								adSelect.getAdCreative().getId(), Util.removeTimeOfDate(adSelect.getSelectDate())));
					}
					
			        // 재생 기록 생성
			        //revService.saveOrUpdate(new RevPlayHist(adSelect));
			        
			        // 광고 선택 삭제
			        //revService.deleteAdSelect(adSelect);
			        ids.add(adSelect.getId());
			        
			        ret ++;
			        if (ret % 100 == 0) {
				        logger.info("cnt = " + ret);
			        }
				}
				
				//if (ret >= 2000) {
				//	break;
				//}
			}
			logger.info("cnt = " + ret);
			
			revService.deleteAdSelectBulkRowsInIds(ids);
			logger.info("deleted ");
			
			int cnt2 = 0;
			logger.info("map size = " + map.values().size());
			for(RevScrHrlyPlyAdCntItem item : map.values()) {
				
				// 시간당 화면/광고 재생 계산
	    		RevScrHourlyPlay hourlyPlay = revService.getScrHourlyPlay(item.getScreenId(), 
	    				item.getAdId(), item.getSelectDate());
	    		if (hourlyPlay == null) {
	    			hourlyPlay = new RevScrHourlyPlay(invService.getScreen(item.getScreenId()), 
	    					adcService.getAdCreative(item.getAdCreativeId()), 
	    					item.getSelectDate());
	    		}
				
	    		hourlyPlay.setFailTotal(hourlyPlay.getFailTotal() + item.getCnt());
	    		hourlyPlay.setDateTotal(hourlyPlay.getSuccTotal() + hourlyPlay.getFailTotal());
		        hourlyPlay.touchWho();
		        
		        revService.saveOrUpdate(hourlyPlay);
		        
		        cnt2 ++;
		        if (cnt2 % 100 == 0) {
		        	logger.info("cnt2 = " + cnt2);
		        }
			}
			*/
			
		} catch (Exception e) {
    		logger.error("organizePlayHists - Step 2", e);
		}
		
		return ret;
	}
	
	// 동기화 묶음 광고 보고 및 채널 광고 자료 정리
	private String organizeSyncPackChanAds() {
		
		long startAt = new Date().getTime();
		int cntImp = 0, cntChanAd = 0, cntChanAdRpt = 0;
		
		try {
			
			cntImp = revService.deleteSyncPackImpsBefore(Util.addDays(new Date(), -3));
			cntChanAd = revService.deleteChanAdBefore(Util.addDays(new Date(), -3));
			cntChanAdRpt = revService.deleteChanAdRptBefore(Util.addDays(new Date(), -3));
			
		} catch (Exception e) {
    		logger.error("organizeSyncPackChanAds", e);
		}
		
		return "SyncPackImp " + cntImp + " rows, ChanAd " + cntChanAd + " rows, ChanAdRpt " + cntChanAdRpt + " rows, time: " + (new Date().getTime() - startAt);
	}
	
	private boolean isFilePropertyYes(String propertyName) {
		
		if (Util.isValid(propertyName)) {
			String prop = Util.getFileProperty(propertyName);
			return Util.isValid(prop) && prop.equalsIgnoreCase("Y");
		}
		
		return false;
	}
	
	private String createCandiAdCreatList() {
		
		List<KnlMedium> mediumList = knlService.getValidMediumList();
		Date today = Util.removeTimeOfDate(new Date());
		
		long startAt = new Date().getTime();

		ArrayList<Integer> taskList1 = new ArrayList<Integer>();
		ArrayList<Integer> taskList2 = new ArrayList<Integer>();
		
		
		List<Integer> currScrIds = null;
		List<Integer> resultScrIds = null;
		List<AdcAdCreative> candiList = null;
		HashMap<String, AdcAdCreative> adCreativeMap = new HashMap<String, AdcAdCreative>();
		HashMap<String, AdcCreatFile> adCreatFileMap = new HashMap<String, AdcCreatFile>();
		HashMap<String, Integer> adCreatFileDurMap = new HashMap<String, Integer>();

		HashMap<String, RevAdOrderItem> adCreatOrderMap = new HashMap<String, RevAdOrderItem>();
		ArrayList<RevAdOrderItem> adCreatOrderList = new ArrayList<RevAdOrderItem>();
		
		
		//
		// Ad API용 광고 / 광고 소재 리스트
		//
		for(KnlMedium medium : mediumList) {

			//
			// 매체에 따른 화면 그룹을 따로 구분하지 않고 매체별 하나의 목록으로 처리
			//
			// 광고 선택 우선 순위:
			//     1. 구매유형(G: 목표 보장, N: 목표 비보장, H: 하우스 광고)
			//     2. 우선 순위(1-10, 1이 최고, 10이 최저, 하우스 광고에서는 의미없음)
			//
			// finalOrdStr 예:
			//     22|25:3_26:2,21,11|12:1_24:2,23|20|14,15,13
			//
			//     - id의 대상은 adCreativeId
			//     - 그룹 구분자(1차 구분자): |
			//     - 동일 그룹 내 광고 구분자: ,
			//     - 여러 소재로 구성된 광고일 경우: {id1}:{weight1}_{id2}:{weight2}_...
			//
			candiList = adcService.getCandiAdCreativeListByMediumIdDate
					(medium.getId(), today, today);
			
			adCreatOrderMap.clear();
			adCreatOrderList.clear();
			
			for(AdcAdCreative adc : candiList) {
				
				// 묶음 광고일 경우, 1번이 아닌 다른 광고 소재가 추가될 경우 선택 확률이 높아지기 때문에
				// 공평하게 하기 위해 1번 광고만 선택되도록 함
				if (Util.isValid(adc.getAd().getViewTypeCode())) {
					List<String> adPackIds = Util.tokenizeValidStr(adc.getAd().getAdPackIds());
					if (adPackIds.size() > 0) {
						int candiId = Util.parseInt(adPackIds.get(0));
						
						if (candiId != adc.getCreative().getId()) {
							continue;
						}
					}
				}
				
				String key = "A" + adc.getAd().getId();
				if (adCreatOrderMap.containsKey(key)) {
					RevAdOrderItem item = adCreatOrderMap.get(key);
					item.add(String.valueOf(adc.getId()), String.valueOf(adc.getWeight()));
				} else {
					adCreatOrderMap.put(key, new RevAdOrderItem(adc));
				}
			}
			
			adCreatOrderList = new ArrayList<RevAdOrderItem>(adCreatOrderMap.values());
			Collections.sort(adCreatOrderList, new Comparator<RevAdOrderItem>() {
		    	public int compare(RevAdOrderItem item1, RevAdOrderItem item2) {
		    		return item1.getSortCode().compareTo(item2.getSortCode());
		    	}
		    });
			
			String prevId = "";
			String finalOrdStr = "";
			for(RevAdOrderItem ordItem : adCreatOrderList) {
				if (Util.isValid(prevId)) {
					if (prevId.equals(ordItem.getSortCode())) {
						finalOrdStr += ",";
					} else {
						finalOrdStr += "|";
					}
				}
				prevId = ordItem.getSortCode();
				
				finalOrdStr += ordItem.getItemStr();
			}
			
			if (Util.isValid(finalOrdStr)) {
				int seq = SolUtil.getBgNextSeq("PM_CandiAdCreative");
				taskList1.add(seq);
				
				HashMap<String, AdcAdCreative> map = new HashMap<String, AdcAdCreative>();
				for(AdcAdCreative adc : candiList) {
					map.put("AC" + adc.getId(), adc);
				}
				
				GlobalInfo.AdRealAdCreatMap.put("v" + seq, map);
				
				GlobalInfo.AdCandiAdCreatVerKey.put("M" + medium.getId(), "v" + seq);
				GlobalInfo.AdRealAdCreatIdsMap.put("M" + medium.getId(), finalOrdStr);
			}
		}

		//
		// File API용 광고 소재 파일 리스트
		//
		for(KnlMedium medium : mediumList) {
			currScrIds = new ArrayList<Integer>();
			resultScrIds = null;
			
			// 대체 광고를 미리 조회
			List<AdcCreative> fallbackList = adcService.getValidCreativeFallbackListByMediumId(medium.getId());
			
			resultScrIds = invService.getMonitScreenIdsByMediumId(medium.getId());
			if (resultScrIds.size() == 0) {
				continue;
			} else {
				
				// 게시 유형이 명시되지 않은 일반 형식 대상
				candiList = adcService.getCandiAdCreativeListByMediumIdDate
						(medium.getId(), today, Util.addDays(today, 1));
				HashMap<String, List<Integer>> map = invService.getResoScreenIdMapByScreenIdIn(resultScrIds);
				
				Set<String> keys = map.keySet();
				for(String reso : keys) {
					List<Integer> ids = map.get(reso);

					ArrayList<String> creatFileIds = new ArrayList<String>();
					ArrayList<AdcCreatFile> list = new ArrayList<AdcCreatFile>();
					
					for(AdcAdCreative ac : candiList) {
						if (Util.isValid(ac.getAd().getViewTypeCode())) {
							continue;
						}
						AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(ac.getCreative().getId(), reso);
						
						// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
						if (creatFile != null) {
							// 광고에 설정된 재생 시간 고정값 수용
							if (ac.getAd().getDuration() >= 5) {
								creatFile.setFormalDurSecs(ac.getAd().getDuration());
							}
							
							String idKey = "C" + String.valueOf(ac.getCreative().getId()) + "R" + reso + "D" + ac.getAd().getDuration();
							if (!creatFileIds.contains(idKey)) {
								creatFileIds.add(idKey);
								list.add(creatFile);
							}
						}
					}
					
					for(AdcCreative c : fallbackList) {
						if (Util.isValid(c.getViewTypeCode())) {
							continue;
						}
						AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(c.getId(), reso);
						
						// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
						if (creatFile != null) {
							// 광고에 설정될 수 없기 때문에 추가 설정이 필요없고,
							// 해상도 뒤 Duration의 0은 화면 설정 값 기반을 의미
							String idKey = "C" + String.valueOf(c.getId()) + "R" + reso + "D0";
							if (!creatFileIds.contains(idKey)) {
								creatFileIds.add(idKey);
								list.add(creatFile);
							}
						}
					}
					
					int seq = SolUtil.getBgNextSeq("PM_CandiCreatFile");
					GlobalInfo.FileCandiCreatFileMap.put("v" + seq, list);
					for(Integer id : ids) {
						GlobalInfo.FileCandiCreatFileVerKey.put("S" + id, "v" + seq);
					}
					taskList2.add(seq);
				}
				
				
				// 게시유형이 명시된 항목 대상
				List<FndViewType> viewTypeList = fndService.getViewTypeList();
				ArrayList<String> viewTypeResos = new ArrayList<String>();
				for(FndViewType viewType : viewTypeList) {
					List<String> media = Util.tokenizeValidStr(viewType.getDestMedia());
					String r = "";
					for(String m : media) {
						if (medium.getShortName().equals(m)) {
							r = viewType.getResolution();
							break;
						}
					}
					if (Util.isValid(r) && !viewTypeResos.contains(r)) {
						// 이 해상도는 이 매체에 사용이 허용됨
						viewTypeResos.add(r);
					}
				}
				
				for(String reso : viewTypeResos) {
					
					ArrayList<String> creatFileIds = new ArrayList<String>();
					ArrayList<AdcCreatFile> list = new ArrayList<AdcCreatFile>();
					
					for(AdcAdCreative ac : candiList) {
						if (Util.isNotValid(ac.getAd().getViewTypeCode())) {
							continue;
						}
						AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(ac.getCreative().getId(), reso);
						
						// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
						if (creatFile != null) {
							// 광고에 설정된 재생 시간 고정값 수용
							if (ac.getAd().getDuration() >= 5) {
								creatFile.setFormalDurSecs(ac.getAd().getDuration());
							}
							
							String idKey = "C" + String.valueOf(ac.getCreative().getId()) + "R" + reso + "D" + ac.getAd().getDuration();
							if (!creatFileIds.contains(idKey)) {
								creatFileIds.add(idKey);
								list.add(creatFile);
							}
						}
					}
					
					for(AdcCreative c : fallbackList) {
						if (Util.isNotValid(c.getViewTypeCode())) {
							continue;
						}
						AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(c.getId(), reso);
						
						// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
						if (creatFile != null) {
							// 광고에 설정될 수 없기 때문에 추가 설정이 필요없고,
							// 해상도 뒤 Duration의 0은 화면 설정 값 기반을 의미
							String idKey = "C" + String.valueOf(c.getId()) + "R" + reso + "D0";
							if (!creatFileIds.contains(idKey)) {
								creatFileIds.add(idKey);
								list.add(creatFile);
							}
						}
					}
					
					int seq = SolUtil.getBgNextSeq("PM_CandiCreatFile");
					GlobalInfo.FileCandiCreatFileMap.put("v" + seq, list);
					GlobalInfo.FileCandiCreatFileVerKey.put("VTS" + medium.getId() + "R" + reso, "v" + seq);
					taskList2.add(seq);
				}

			}
		}
		
		// 파일 API가 서비스 가능하도록 활성화
		GlobalInfo.FileApiReady = true;
		
		
		String ret = "AdCreative ";
		if (taskList1.size() == 0) {
			ret += "NO";
		} else {
			ret += "ver " + taskList1.toString();
		}
		ret += ", CreatFile ";
		if (taskList2.size() == 0) {
			ret += "NO";
		} else {
			ret += "ver " + taskList2.toString();
		}
		ret += ", time: " + (new Date().getTime() - startAt);
		
		// 예: AdCreative ver[1, 2], CreatFile ver[1, 2, 3], time: 1355
		return ret;
	}
	
	/*
	// 임시
	private String getIdList1(List<AdcAdCreative> list) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (AdcAdCreative ac : list) {
			ids.add(ac.getId());
		}
		return ids.toString();
	}
	
	private String getIdList2(List<AdcCreatFile> list) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for (AdcCreatFile cf : list) {
			ids.add(cf.getId());
		}
		return ids.toString();
	}
	*/
	
	private String refreshInvenTargetScreenList() {
		
		// 
		// 광고와 광고 소재의 타겟팅을 확인하고, 인벤 타겟팅이 있으면, 최종 화면 목록을 만들어, 메모리에 올린다.
		//
		long startAt = new Date().getTime();

		ArrayList<Integer> taskList = new ArrayList<Integer>();

		List<AdcCreative> creativeList = adcService.getValidCreativeList();
		List<AdcAd> adList = adcService.getValidAdList();

		
		// 하나라도 타겟팅이 존재하는 광고 소재만 미리 확인
		ArrayList<Integer> targetIds = new ArrayList<Integer>();
		List<Tuple> countList = adcService.getCreatTargetCountGroupByCreativeId();
		for(Tuple tuple : countList) {
			targetIds.add((Integer) tuple.get(0));
		}
		
		for(AdcCreative creative : creativeList) {
			if (targetIds.contains(creative.getId())) {
				List<Integer> idList = invService.getTargetScreenIdsByCreativeId(creative.getId());
				
				int seq = SolUtil.getBgNextSeq("CA_ScreenId");
				GlobalInfo.TgtScreenIdMap.put("v" + seq, idList);
				GlobalInfo.TgtScreenIdVerKey.put("C" + creative.getId(), "v" + seq);
				
				taskList.add(seq);
			}
		}
		
		// 하나라도 타겟팅이 존재하는 광고만 미리 확인
		targetIds.clear();
		countList = adcService.getAdTargetCountGroupByAdId();
		for(Tuple tuple : countList) {
			targetIds.add((Integer) tuple.get(0));
		}
		
		for(AdcAd ad : adList) {
			if (targetIds.contains(ad.getId())) {
				List<Integer> idList = invService.getTargetScreenIdsByAdId(ad.getId());
				
				int seq = SolUtil.getBgNextSeq("CA_ScreenId");
				GlobalInfo.TgtScreenIdMap.put("v" + seq, idList);
				GlobalInfo.TgtScreenIdVerKey.put("A" + ad.getId(), "v" + seq);
				
				taskList.add(seq);
			}
		}
		
		
		String ret = "ScreenIds ";
		if (taskList.size() == 0) {
			ret += "NO";
		} else {
			ret += "ver " + taskList.toString();
		}
		ret += ", time: " + (new Date().getTime() - startAt);
		
		// 예: ScreenIds ver[1, 2], time: 1355
		return ret;
	}
	
	
	private String refreshMobileAdList() {
		
		// 
		// 광고의 모바일 타겟팅을 확인하고, 있으면 목록을 만들어 메모리에 올린다.
		//
		long startAt = new Date().getTime();

		ArrayList<Integer> taskList = new ArrayList<Integer>();
		
		
		HashMap<String, String> map = new HashMap<String, String>();
		
		List<Tuple> itemList = adcService.getMobTargetViewItemList();
		for(Tuple tuple : itemList) {
			int adId = (Integer) tuple.get(0);
			String filterType = (String) tuple.get(1);
			String mobType = (String) tuple.get(2);
			Integer activeStatus = (Integer) tuple.get(3);
			String gcName = (String) tuple.get(4);
			Double lat = (Double) tuple.get(5);
			Double lng = (Double) tuple.get(6);
			BigInteger radius = (BigInteger) tuple.get(7);
			
			if (activeStatus != null && activeStatus.intValue() == 1) {
				String item = "";
				if (mobType.equals("CR")) {
					item = filterType + ",CR," + String.valueOf(lat.doubleValue()) + "," + 
							String.valueOf(lng.doubleValue()) + "," + String.valueOf(radius.intValue());
				} else if (mobType.equals("RG")) {
					item = filterType + ",RG," + gcName;
				}
				
				if (Util.isValid(item)) {
					String items = map.get("A" + adId);
					if (Util.isValid(items)) {
						items += "|" + item;
					} else {
						items = item;
					}
					map.put("A" + adId, items);
				}
			}
		}
		
		Set<String> keys = map.keySet();
		for(String key : keys) {
			String items = map.get(key);
			if (Util.isValid(items)) {
				int seq = SolUtil.getBgNextSeq("MT_AdId");
				GlobalInfo.MobTgtItemMap.put("v" + seq, items);
				GlobalInfo.MobTgtItemVerKey.put(key, "v" + seq);
				
				taskList.add(seq);
			}
		}
		
		
		String ret = "Ads ";
		if (taskList.size() == 0) {
			ret += "NO";
		} else {
			ret += "ver " + taskList.toString();
		}
		ret += ", time: " + (new Date().getTime() - startAt);
		
		// 예: Ads ver[1, 2], time: 1355
		return ret;
	}
	
	
	// 광고 선택/보고 자료 로그
	private int logPlayHists() {
		
		int ret = 0;
		
		try {
			
			String path = Util.getFileProperty("log.play.hist.path");
			String prefix = Util.getFileProperty("log.play.hist.prefix");
			
			int cpm = Util.parseInt(Util.getFileProperty("log.play.hist.count.per.min"));
			
			if (Util.checkDirectory(path) && Util.isValid(path) && Util.isValid(prefix) && cpm > 0) {
				
				Util.checkDirectory(path);
				
				List<RevPlayHist> list = revService.getFirstPlayHistList(cpm);
				String prevFilename = "";
				String filename = "";
				StringBuilder sb = new StringBuilder();
				
				ArrayList<Integer> ids = new ArrayList<Integer>();
				
				boolean error = false;
				
				for(RevPlayHist hist : list) {
					filename = path + "/" + prefix + Util.toSimpleString(hist.getSelectDate(), "yyyy.MM.dd_HH") + ".txt";
					if (Util.isNotValid(prevFilename)) {
						prevFilename = filename;
					}
					
					if (!filename.equals(prevFilename)) {
						if (error = !Util.appendStrToFile(sb.toString(), prevFilename)) {
							break;
						}

						sb = new StringBuilder();
						prevFilename = filename;
					}
					
					sb.append(hist.toLogString());
					sb.append(System.lineSeparator());
					ids.add(hist.getId());
				}
				
				if (!error) {
					Util.appendStrToFile(sb.toString(), filename);
					ret = list.size();
				}
				
				revService.deleteBulkPlayHistRowsInIds(ids);
			}
			
		} catch (Exception e) {
    		logger.error("logPlayHists", e);
		}
		
		return ret;
	}
	
	private String calcStatsHourly() {
		
		// 
		// 시간당 화면 및 사이트 재생 합계를 계산한다.
		//
		long startAt = new Date().getTime();
		
		
		ArrayList<Date> dateList = new ArrayList<Date>();
		
		List<Tuple> dateTupleList = revService.getScrHourlyPlayPlayDateListByLastUpdateDate(
				Util.addDays(Util.removeTimeOfDate(new Date()), -1));
		for(Tuple tuple : dateTupleList) {
			dateList.add((Date)tuple.get(0));
		}
		
		String dateStr = "";
		int cnt = 0;
		for(Date date : dateList) {
			if (Util.isValid(dateStr)) {
				dateStr += ", ";
			}
			dateStr += Util.toSimpleString(date, "M/d");
			int upsertCnt = revService.calcDailyInvenConnectCountByPlayDate(date);
			cnt += upsertCnt;
		}
		
		String ret = "HrlyPlyTots " + cnt + " rows, ";
		ret += " date: [" + dateStr + "], ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		// 예: HrlyPlyTots 0 rows,  date: [2/22, 2/23],  time: 7084
		return ret;
	}

	private String calcAdHourlyGoalValue() {
		
		List<KnlMedium> mediumList = knlService.getValidMediumList();
		Date today = Util.removeTimeOfDate(new Date());
		
		long startAt = new Date().getTime();
		int cnt = 0;

		ArrayList<String> adIds = new ArrayList<String>();
		
		for(KnlMedium medium : mediumList) {
			List<AdcAdCreative> candiList = adcService.getCandiAdCreativeListByMediumIdDate
					(medium.getId(), today, today);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getAdTargetCountGroupByMediumAdId(medium.getId());
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
    		
    		// 모든 화면
    		List<Integer> allScrIds = invService.getMonitScreenIdsByMediumId(medium.getId());
    		
			
			// 광고의 하루 노출한도
			int dailyScrCapMedium = Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.daily.screen"));
			
			// 시간당 노출계획
			int impPlanPerHour = Util.parseInt(SolUtil.getOptValue(medium.getId(), "impress.per.hour"), 6);

			// 매체의 화면수
			int adScrCntMedium = Util.parseInt(SolUtil.getOptValue(medium.getId(), "activeCount.screen"), -1);

			
			for(AdcAdCreative adc : candiList) {
				String key = "A" + adc.getAd().getId();
				if (!adIds.contains(key)) {
					adIds.add(key);
					
					List<Integer> scrIds = new ArrayList<Integer>();
					if (targetIds.contains(adc.getAd().getId())) {
						// 타겟팅이 있다면
						scrIds = invService.getTargetScreenIdsByAdId(adc.getAd().getId());
					} else {
						scrIds = new ArrayList<Integer>(allScrIds);
					}
					
					int adScrCnt = adScrCntMedium;
					if (scrIds.size() < adScrCnt) {
						adScrCnt = scrIds.size();
					}
					if (adScrCnt < 1) {
						for (Integer scrId : scrIds) {
							String scrStatus = GlobalInfo.InvenLastStatusMap.get("SC" + scrId);
							if (Util.isValid(scrStatus) && 
									(scrStatus.equals("6") || scrStatus.equals("5") || scrStatus.equals("4"))) {
								// 10분, 1시간, 6시간내 요청 기기에 대해서만
								adScrCnt++;
							}
						}
					}

					
					// 광고/광고 소재의 비중 획득
					float weight = SolUtil.getCreatWeight(adc.getAd().getId(), adc.getId(), today);
					if (SolUtil.isViewTypeAdPackUsed(adc.getCreative().getViewTypeCode())) {
						weight = 1.0f;
					}

					List<RevScrHourlyPlay> list = revService.getScrHourlyPlayListByAdIdPlayDate(adc.getAd().getId(), today);
					
					// 각 광고에서의 성공 합계 미리 계산(한 광고가 여러 광고 소재일 경우 대비)
					HashMap<String, Integer> succTotMap = new HashMap<String, Integer>();
					for(RevScrHourlyPlay hourlyPlay : list) {
						Integer sum = succTotMap.get("A" + hourlyPlay.getAd().getId());
						if (sum == null || sum.intValue() < 1) {
							succTotMap.put("A" + hourlyPlay.getAd().getId(), hourlyPlay.getSuccTotal());
						} else {
							succTotMap.put("A" + hourlyPlay.getAd().getId(), sum.intValue() + hourlyPlay.getSuccTotal());
						}
					}
					for(RevScrHourlyPlay hourlyPlay : list) {
						
						Integer sum = succTotMap.get("A" + hourlyPlay.getAd().getId());
						Integer goalValue = SolUtil.getScrAdHourlyGoalValue(
								hourlyPlay, dailyScrCapMedium, adScrCnt, impPlanPerHour, weight, ((sum == null || sum.intValue() < 1) ? -1 : sum));
						
						// 갑자기 중단되는 광고의 시간 목표치 설정
						if (!Util.isBetween(today, hourlyPlay.getAdCreative().getStartDate(), hourlyPlay.getAdCreative().getEndDate())) {
							goalValue = null;
						}

						if (goalValue == null) {
							if (hourlyPlay.getCurrHourGoal() != null) {
								hourlyPlay.setCurrHourGoal(null);
								
								// who 컬럼 변경하지 않음
								revService.saveOrUpdate(hourlyPlay);
							}
							
							continue;
						}
						
						if (hourlyPlay.getCurrHourGoal() == null || hourlyPlay.getCurrHourGoal().intValue() != goalValue.intValue()) {
							
							hourlyPlay.setCurrHourGoal(goalValue.intValue());
							
							// who 컬럼 변경하지 않음
							revService.saveOrUpdate(hourlyPlay);
							cnt++;
						}
					}
				}
			}
		}
		
		return cnt + " rows, time: " + (new Date().getTime() - startAt);
	}
	
	private String saveTmpScreenStatusLines() {
		
		// 
		// 화면 분단위 상태행을 임시로 집계 후 임시 테이블에 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		ArrayList<RevScrWorkTimeItem> itemList = GlobalInfo.ScrWorkTimeItemList;
		int cnt = 0;

		try {
			GlobalInfo.ScrWorkTimeItemList = new ArrayList<RevScrWorkTimeItem>();
			
			ArrayList<Integer> screenIds = new ArrayList<Integer>();

			for(RevScrWorkTimeItem item : itemList) {
				if (item != null) {
					if (!screenIds.contains(item.getScreenId())) {
						screenIds.add(item.getScreenId());
					}
				}
			}

			for(Integer screenId : screenIds) {
				List<RevScrWorkTimeItem> screenItems = itemList.stream()
					    .filter(t -> t.getScreenId() == screenId)
					    .collect(Collectors.toList());
				
				ArrayList<String> dates = new ArrayList<String>();
				for(RevScrWorkTimeItem scrItem : screenItems) {
					String key = Util.toSimpleString(scrItem.getDate(), "yyyyMMdd");
					if (!dates.contains(key)) {
						dates.add(key);
					}
				}
				
				for(String d : dates) {
					Date currDate = Util.parseDate(d);
					String statusLine = SolUtil.getScrStatusLine("", currDate, "2");
		    		
					for (RevScrWorkTimeItem scrItem : screenItems) {
						if (!Util.toSimpleString(scrItem.getDate(), "yyyyMMdd").equals(d)) {
							continue;
						}
						
						statusLine = SolUtil.getScrStatusLine(statusLine, scrItem.getDate(), "6", true);
					}
					
			        cnt ++;
			        sysService.insertTmpStatusLine(screenId, currDate, statusLine);
				}
			}
			
		} catch (Exception e) {
    		logger.error("saveTmpScreenStatusLines", e);
    		cnt = -1;
		}
		
		
		String ret = "ScrWorkTimeItem " + itemList.size() + " rows, ";
		ret += " statusLine " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String saveScreenStatusLines() {
		
		// 
		// 화면 분단위 상태행을 집계 후 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		int cnt = 0;
		List<Tuple> itemList = new ArrayList<Tuple>();
		
		try {
			HashMap<String, SysScrWorkTimeCalcItem> map = new HashMap<String, SysScrWorkTimeCalcItem>();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			//
			// SELECT SCREEN_ID, PLAY_DATE, STATUS_LINE, STATUS_LINE_ID
			//
			itemList = sysService.getTmpStatusLineTupleList();
			for(Tuple tuple : itemList) {
				int screenId = (int) tuple.get(0);
				Date playDate = (Date) tuple.get(1);
				String statusLine = (String) tuple.get(2);
				int id = (int) tuple.get(3);
				
				String key = Util.toSimpleString(playDate, "yyyyMMdd") + "S" + screenId;
				if (map.containsKey(key)) {
					SysScrWorkTimeCalcItem item = map.get(key);
					item.setStatusLine(SolUtil.mergeScrStatusLines(item.getStatusLine(), statusLine));
				} else {
					map.put(key, new SysScrWorkTimeCalcItem(screenId, playDate, statusLine));
				}
				
				ids.add(id);
			}

			ArrayList<SysScrWorkTimeCalcItem> list = new ArrayList<SysScrWorkTimeCalcItem>(map.values());
			for(SysScrWorkTimeCalcItem item : list) {
				
				Tuple currStatusLine = revService.getScrStatusLineTuple(item.getScreenId(), item.getDate());
	    		String statusLine = "";
	    		int statusLineId = -1;
	    		if (currStatusLine == null) {
	    			statusLine = item.getStatusLine();
	    		} else {
	    			statusLine = SolUtil.mergeScrStatusLines((String)currStatusLine.get(0), item.getStatusLine());
	    			statusLineId = (int)currStatusLine.get(1);
	    		}
				
		        cnt ++;
        		if (statusLineId == -1) {
        			revService.insertScrStatusLine(item.getScreenId(), item.getDate(), statusLine);
        		} else {
        			revService.updateScrStatusLine(statusLineId, statusLine);
        		}
			}
			
			sysService.deleteTmpStatusLineBulkRowsInIds(ids);
			
		} catch (Exception e) {
    		logger.error("saveScreenStatusLines", e);
    		cnt = -1;
		}
		
		
		String ret = "SysScrWorkTimeCalcItem " + itemList.size() + " rows, ";
		ret += " statusLine " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String saveHourlyEvents() {
		
		// 
		// 화면의 시간단위 이벤트를 집계 후 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		GregorianCalendar calendar = new GregorianCalendar();
		int cnt1 = 0;
		int cnt2 = 0;
		int cnt3 = 0;
		List<Tuple> itemList = new ArrayList<Tuple>();
		
		
		try {
			ArrayList<Integer> ids = new ArrayList<Integer>();
			
			HashMap<String, SysScrEventTimeCalcItem> map = new HashMap<String, SysScrEventTimeCalcItem>();
			
			//
			// SELECT SCREEN_ID, EVENT_DATE, TYPE, HRLY_EVENT_ID
			//
			itemList = sysService.getTmpHrlyEventTupleList();
			for(Tuple tuple : itemList) {
				
				int screenId = (int) tuple.get(0);
				Date playDate = (Date) tuple.get(1);
				int type = (int) tuple.get(2);
				int id = (int) tuple.get(3);
				
				if (type == 1) {
					cnt1 ++;
				} else if (type == 2) {
					cnt2 ++;
				} else if (type == 3) {
					cnt3 ++;
				}
				
				String key = "S" + screenId + "T" + type + "D" + Util.toSimpleString(Util.removeTimeOfDate(playDate), "dd");
				if (!map.containsKey(key)) {
					map.put(key, new SysScrEventTimeCalcItem(tuple));
				}
				SysScrEventTimeCalcItem item = map.get(key);

				calendar.setTime(playDate);
				item.addCount(calendar.get(Calendar.HOUR_OF_DAY));
				
				ids.add(id);
			}
			
			
			List<SysScrEventTimeCalcItem> sumItems = new ArrayList<SysScrEventTimeCalcItem>(map.values());
			
			for(SysScrEventTimeCalcItem item : sumItems) {
				if (item.getType() == 1) {
					// 시간당 화면 실패 합계 계산
					RevScrHrlyFailTot failTot = revService.getScrHrlyFailTot(item.getScreenId(), Util.removeTimeOfDate(item.getDate()));
					if (failTot == null) {
						InvScreen screen = invService.getScreen(item.getScreenId());
						if (screen != null) {
							failTot = new RevScrHrlyFailTot(screen, Util.removeTimeOfDate(item.getDate()));
						}
					}
					
					if (failTot != null) {
						failTot.setCnt00(failTot.getCnt00() + item.getCnt00());
						failTot.setCnt01(failTot.getCnt01() + item.getCnt01());
						failTot.setCnt02(failTot.getCnt02() + item.getCnt02());
						failTot.setCnt03(failTot.getCnt03() + item.getCnt03());
						failTot.setCnt04(failTot.getCnt04() + item.getCnt04());
						failTot.setCnt05(failTot.getCnt05() + item.getCnt05());
						failTot.setCnt06(failTot.getCnt06() + item.getCnt06());
						failTot.setCnt07(failTot.getCnt07() + item.getCnt07());
						failTot.setCnt08(failTot.getCnt08() + item.getCnt08());
						failTot.setCnt09(failTot.getCnt09() + item.getCnt09());
						failTot.setCnt10(failTot.getCnt10() + item.getCnt10());
						failTot.setCnt11(failTot.getCnt11() + item.getCnt11());
						failTot.setCnt12(failTot.getCnt12() + item.getCnt12());
						failTot.setCnt13(failTot.getCnt13() + item.getCnt13());
						failTot.setCnt14(failTot.getCnt14() + item.getCnt14());
						failTot.setCnt15(failTot.getCnt15() + item.getCnt15());
						failTot.setCnt16(failTot.getCnt16() + item.getCnt16());
						failTot.setCnt17(failTot.getCnt17() + item.getCnt17());
						failTot.setCnt18(failTot.getCnt18() + item.getCnt18());
						failTot.setCnt19(failTot.getCnt19() + item.getCnt19());
						failTot.setCnt20(failTot.getCnt20() + item.getCnt20());
						failTot.setCnt21(failTot.getCnt21() + item.getCnt21());
						failTot.setCnt22(failTot.getCnt22() + item.getCnt22());
						failTot.setCnt23(failTot.getCnt23() + item.getCnt23());

						
				        failTot.calcTotal();
				        failTot.touchWho();
				        
				        revService.saveOrUpdate(failTot);
					}
				} else if (item.getType() == 2) {
					// 시간당 화면 광고없음 합계 계산
					RevScrHrlyNoAdTot noAdTot = revService.getScrHrlyNoAdTot(item.getScreenId(), Util.removeTimeOfDate(item.getDate()));
					if (noAdTot == null) {
						InvScreen screen = invService.getScreen(item.getScreenId());
						if (screen != null) {
							noAdTot = new RevScrHrlyNoAdTot(screen, Util.removeTimeOfDate(item.getDate()));
						}
					}
					
					if (noAdTot != null) {
						noAdTot.setCnt00(noAdTot.getCnt00() + item.getCnt00());
						noAdTot.setCnt01(noAdTot.getCnt01() + item.getCnt01());
						noAdTot.setCnt02(noAdTot.getCnt02() + item.getCnt02());
						noAdTot.setCnt03(noAdTot.getCnt03() + item.getCnt03());
						noAdTot.setCnt04(noAdTot.getCnt04() + item.getCnt04());
						noAdTot.setCnt05(noAdTot.getCnt05() + item.getCnt05());
						noAdTot.setCnt06(noAdTot.getCnt06() + item.getCnt06());
						noAdTot.setCnt07(noAdTot.getCnt07() + item.getCnt07());
						noAdTot.setCnt08(noAdTot.getCnt08() + item.getCnt08());
						noAdTot.setCnt09(noAdTot.getCnt09() + item.getCnt09());
						noAdTot.setCnt10(noAdTot.getCnt10() + item.getCnt10());
						noAdTot.setCnt11(noAdTot.getCnt11() + item.getCnt11());
						noAdTot.setCnt12(noAdTot.getCnt12() + item.getCnt12());
						noAdTot.setCnt13(noAdTot.getCnt13() + item.getCnt13());
						noAdTot.setCnt14(noAdTot.getCnt14() + item.getCnt14());
						noAdTot.setCnt15(noAdTot.getCnt15() + item.getCnt15());
						noAdTot.setCnt16(noAdTot.getCnt16() + item.getCnt16());
						noAdTot.setCnt17(noAdTot.getCnt17() + item.getCnt17());
						noAdTot.setCnt18(noAdTot.getCnt18() + item.getCnt18());
						noAdTot.setCnt19(noAdTot.getCnt19() + item.getCnt19());
						noAdTot.setCnt20(noAdTot.getCnt20() + item.getCnt20());
						noAdTot.setCnt21(noAdTot.getCnt21() + item.getCnt21());
						noAdTot.setCnt22(noAdTot.getCnt22() + item.getCnt22());
						noAdTot.setCnt23(noAdTot.getCnt23() + item.getCnt23());

						
				        noAdTot.calcTotal();
				        noAdTot.touchWho();
				        
				        revService.saveOrUpdate(noAdTot);
					}
				} else if (item.getType() == 3) {
					// 시간당 화면 대체광고 합계 계산
					RevScrHrlyFbTot fbTot = revService.getScrHrlyFbTot(item.getScreenId(), Util.removeTimeOfDate(item.getDate()));
					if (fbTot == null) {
						InvScreen screen = invService.getScreen(item.getScreenId());
						if (screen != null) {
							fbTot = new RevScrHrlyFbTot(screen, Util.removeTimeOfDate(item.getDate()));
						}
					}
					
					if (fbTot != null) {
						fbTot.setCnt00(fbTot.getCnt00() + item.getCnt00());
						fbTot.setCnt01(fbTot.getCnt01() + item.getCnt01());
						fbTot.setCnt02(fbTot.getCnt02() + item.getCnt02());
						fbTot.setCnt03(fbTot.getCnt03() + item.getCnt03());
						fbTot.setCnt04(fbTot.getCnt04() + item.getCnt04());
						fbTot.setCnt05(fbTot.getCnt05() + item.getCnt05());
						fbTot.setCnt06(fbTot.getCnt06() + item.getCnt06());
						fbTot.setCnt07(fbTot.getCnt07() + item.getCnt07());
						fbTot.setCnt08(fbTot.getCnt08() + item.getCnt08());
						fbTot.setCnt09(fbTot.getCnt09() + item.getCnt09());
						fbTot.setCnt10(fbTot.getCnt10() + item.getCnt10());
						fbTot.setCnt11(fbTot.getCnt11() + item.getCnt11());
						fbTot.setCnt12(fbTot.getCnt12() + item.getCnt12());
						fbTot.setCnt13(fbTot.getCnt13() + item.getCnt13());
						fbTot.setCnt14(fbTot.getCnt14() + item.getCnt14());
						fbTot.setCnt15(fbTot.getCnt15() + item.getCnt15());
						fbTot.setCnt16(fbTot.getCnt16() + item.getCnt16());
						fbTot.setCnt17(fbTot.getCnt17() + item.getCnt17());
						fbTot.setCnt18(fbTot.getCnt18() + item.getCnt18());
						fbTot.setCnt19(fbTot.getCnt19() + item.getCnt19());
						fbTot.setCnt20(fbTot.getCnt20() + item.getCnt20());
						fbTot.setCnt21(fbTot.getCnt21() + item.getCnt21());
						fbTot.setCnt22(fbTot.getCnt22() + item.getCnt22());
						fbTot.setCnt23(fbTot.getCnt23() + item.getCnt23());

						
				        fbTot.calcTotal();
				        fbTot.touchWho();
				        
				        revService.saveOrUpdate(fbTot);
					}
				}
			}
			
			sysService.deleteTmpHrlyEventBulkRowsInIds(ids);
			
		} catch (Exception e) {
    		logger.error("saveHourlyEvents", e);
    		cnt1 = -1;
    		cnt2 = -1;
    		cnt3 = -1;
		}
		
		
		String ret = "SysTmpHrlyEvent " + itemList.size() + " rows, ";
		ret += " FailTot " + cnt1 + " rows, ";
		ret += " NoAdTot " + cnt2 + " rows, ";
		ret += " FbTot " + cnt3 + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String calcTmpObjEvents() {
		
		// 
		// 개체 최근 변경일시를 임시 테이블에 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		ArrayList<RevObjEventTimeItem> itemList = GlobalInfo.ObjEventTimeItemList;
		int cnt = 0;

		try {
			GlobalInfo.ObjEventTimeItemList = new ArrayList<RevObjEventTimeItem>();
			
			ArrayList<Integer> screenIds = new ArrayList<Integer>();
			
			for(RevObjEventTimeItem item : itemList) {
		        cnt ++;
		        
		        // DB 테이블로 저장하지 않고, 메모리 상에 집계 작업을 진행
		        //sysService.insertTmpObjEvent(item.getObjId(), item.getDate(), item.getType());
		        
		        // type 31, 32를 RTSyncPack으로 대체
		        
		        String typeS = "";
		        if (item.getType() >= 10 && item.getType() < 20) {
		        	typeS = "S";
		        } else if (item.getType() >= 20 && item.getType() < 30) {
		        	typeS = "C";
		        } else if (item.getType() >= 30) {
		        	typeS = "P";
		        }
		        
		        if (Util.isValid(typeS)) {
			        String key = typeS + item.getObjId();
					
					if (Util.isValid(typeS) && !GlobalInfo.ObjTouchMap.containsKey(key)) {
						GlobalInfo.ObjTouchMap.put(key, new SysObjEventTimeCalcItem(typeS, item.getObjId(), item.getDate(), item.getType()));
					}
					SysObjEventTimeCalcItem cItem = GlobalInfo.ObjTouchMap.get(key);

					if (cItem != null) {
						if (item.getType() == 11 || item.getType() == 21 || item.getType() == 31) {
							if (cItem.getDate1() == null || cItem.getDate1().before(item.getDate())) {
								cItem.setDate1(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 12 || item.getType() == 32) {
							if (cItem.getDate2() == null || cItem.getDate2().before(item.getDate())) {
								cItem.setDate2(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 13) {
							if (cItem.getDate3() == null || cItem.getDate3().before(item.getDate())) {
								cItem.setDate3(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 14) {
							if (cItem.getDate4() == null || cItem.getDate4().before(item.getDate())) {
								cItem.setDate4(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 15) {
							if (cItem.getDate5() == null || cItem.getDate5().before(item.getDate())) {
								cItem.setDate5(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 16) {
							if (cItem.getDate6() == null || cItem.getDate6().before(item.getDate())) {
								cItem.setDate6(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						} else if (item.getType() == 17) {
							if (cItem.getDate7() == null || cItem.getDate7().before(item.getDate())) {
								cItem.setDate7(item.getDate());
								cItem.setLastUpdateDate(new Date());
							}
						}
					}
		        }
			}
			
		} catch (Exception e) {
    		logger.error("calcTmpObjEvents", e);
    		cnt = -1;
		}
		
		
		String ret = "ObjEventTimeItem " + itemList.size() + " rows, ";
		ret += " objEvent " + cnt + " rows, ";
		ret += " calcMap "  + GlobalInfo.ObjTouchMap.size() + " items, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String saveObjEvents() {
		
		// 
		// 개체 최근 변경일시를 집계 후 기록한다.
		//
		long startAt = new Date().getTime();
		
		
		GregorianCalendar calendar = new GregorianCalendar();
		int cnt11 = 0;
		int cnt12 = 0;
		int cnt13 = 0;
		int cnt14 = 0;
		int cnt15 = 0;
		int cnt16 = 0;
		int cnt17 = 0;
		int cnt18 = 0;
		int cnt21 = 0;
		int cnt31 = 0;
		int cnt32 = 0;
		List<SysObjEventTimeCalcItem> itemList = new ArrayList<SysObjEventTimeCalcItem>();
		
		try {
			HashMap<String, SysObjEventTimeCalcItem> currMap = GlobalInfo.ObjTouchMap;
			GlobalInfo.ObjTouchMap = new HashMap<String, SysObjEventTimeCalcItem>();
			
			itemList = new ArrayList<SysObjEventTimeCalcItem>(currMap.values());
			
			for(SysObjEventTimeCalcItem item : itemList) {
				if (item.getType().equals("S")) {
					RevObjTouch objTouch = revService.getObjTouch("S", item.getObjId());
					if (objTouch == null) {
						objTouch = new RevObjTouch("S", item.getObjId());
					}
					
					if (objTouch.getDate1() == null) {
						objTouch.setDate1(item.getDate1());
					} else if (item.getDate1() != null && objTouch.getDate1().before(item.getDate1())) {
						objTouch.setDate1(item.getDate1());
						cnt11++;
					}
					
					if (objTouch.getDate2() == null) {
						objTouch.setDate2(item.getDate2());
					} else if (item.getDate2() != null && objTouch.getDate2().before(item.getDate2())) {
						objTouch.setDate2(item.getDate2());
						cnt12++;
					}
					
					if (objTouch.getDate3() == null) {
						objTouch.setDate3(item.getDate3());
					} else if (item.getDate3() != null && objTouch.getDate3().before(item.getDate3())) {
						objTouch.setDate3(item.getDate3());
						cnt13++;
					}
					
					if (objTouch.getDate4() == null) {
						objTouch.setDate4(item.getDate4());
					} else if (item.getDate4() != null && objTouch.getDate4().before(item.getDate4())) {
						objTouch.setDate4(item.getDate4());
						cnt14++;
					}
					
					if (objTouch.getDate5() == null) {
						objTouch.setDate5(item.getDate5());
					} else if (item.getDate5() != null && objTouch.getDate5().before(item.getDate5())) {
						objTouch.setDate5(item.getDate5());
						cnt15++;
					}
					
					if (objTouch.getDate6() == null) {
						objTouch.setDate6(item.getDate6());
					} else if (item.getDate6() != null && objTouch.getDate6().before(item.getDate6())) {
						objTouch.setDate6(item.getDate6());
						cnt16++;
					}
					
					if (objTouch.getDate7() == null) {
						objTouch.setDate7(item.getDate7());
					} else if (item.getDate7() != null && objTouch.getDate7().before(item.getDate7())) {
						objTouch.setDate7(item.getDate7());
						cnt17++;
					}
					
					if (objTouch.getDate8() == null) {
						objTouch.setDate8(item.getDate8());
					} else if (item.getDate8() != null && objTouch.getDate8().before(item.getDate8())) {
						objTouch.setDate8(item.getDate8());
						cnt18++;
					}
					
					objTouch.touchWho();
					revService.saveOrUpdate(objTouch);

				} else if (item.getType().equals("C")) {
					
					RevObjTouch objTouch = revService.getObjTouch("C", item.getObjId());
					if (objTouch == null) {
						objTouch = new RevObjTouch("C", item.getObjId());
					}
					
					if (objTouch.getDate1() == null) {
						objTouch.setDate1(item.getDate1());
					} else if (item.getDate1() != null && objTouch.getDate1().before(item.getDate1())) {
						objTouch.setDate1(item.getDate1());
						cnt21++;
					}
					
					objTouch.touchWho();
					revService.saveOrUpdate(objTouch);
					
				} else if (item.getType().equals("P")) {
					
					RevObjTouch objTouch = revService.getObjTouch("P", item.getObjId());
					if (objTouch == null) {
						objTouch = new RevObjTouch("P", item.getObjId());
					}
					
					if (objTouch.getDate1() == null) {
						objTouch.setDate1(item.getDate1());
					} else if (item.getDate1() != null && objTouch.getDate1().before(item.getDate1())) {
						objTouch.setDate1(item.getDate1());
						cnt31++;
					}
					
					if (objTouch.getDate2() == null) {
						objTouch.setDate2(item.getDate2());
					} else if (item.getDate2() != null && objTouch.getDate2().before(item.getDate2())) {
						objTouch.setDate2(item.getDate2());
						cnt32++;
					}
					
					objTouch.touchWho();
					revService.saveOrUpdate(objTouch);

				}
			}
			
		} catch (Exception e) {
    		logger.error("saveObjEvents", e);
    		cnt11 = -1;
    		cnt12 = -1;
    		cnt13 = -1;
    		cnt14 = -1;
    		cnt15 = -1;
    		cnt16 = -1;
    		cnt17 = -1;
    		cnt18 = -1;
    		cnt21 = -1;
    		cnt31 = -1;
    		cnt32 = -1;
		}
		
		
		String ret = "Touch Objs " + itemList.size() + " rows, ";
		ret += " file API " + cnt11 + " items, ";
		ret += " ad/playlist API " + cnt12 + " items, ";
		ret += " report API " + cnt13 + " items, ";
		ret += " info API " + cnt14 + " items, ";
		ret += " command API " + cnt15 + " items, ";
		ret += " commandReport API " + cnt16 + " items, ";
		ret += " event API " + cnt17 + " items, ";
		ret += " playlist report API " + cnt18 + " items, ";
		ret += " creative " + cnt21 + " items, ";
		ret += " sync pack file API " + cnt31 + " items, ";
		ret += " sync pack ad API " + cnt32 + " items, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String calcDailyAdImpressionStat() {
		
		// 
		// 1. 광고별, 광고 소재별 하루 송출량을 계산한다.
		//
		//   - 오늘 자료(D-0)는 5분 단위(5분 단위 매번)
		//   - 어제 자료(D-1)는 1시간 단위(5분 단위 12번째)
		//   - 2일전 자료(D-2)는 6시간 단위(5분 단위 72번째)
		//
		// 2. 모든 광고의 집행관련 항목을 재계산한다.
		//
		long startAt = new Date().getTime();
		
		int cntUpd = 0;
		int cntIns = 0;
		
		Date today = Util.removeTimeOfDate(new Date());
		
		
		Date day0 = Util.removeTimeOfDate(new Date());
		Date day1 = Util.addDays(day0, -1);
		Date day2 = Util.addDays(day0, -2);
		
		boolean day1Included = false;
		boolean day2Included = false;

		List<Integer> retList = new ArrayList<Integer>();
		
		try {
			adCalcCount++;
			
			if (adCalcCount % 12 == 0) {
				day1Included = true;
				
				retList = SolUtil.calcOneDayAdImpression(day1,  false);
				if (retList != null && retList.size() == 2) {
					cntIns += retList.get(0);
					cntUpd += retList.get(1);
				}
			}
			if (adCalcCount % 72 == 0) {
				day2Included = true;
				
				retList = SolUtil.calcOneDayAdImpression(day2,  false);
				if (retList != null && retList.size() == 2) {
					cntIns += retList.get(0);
					cntUpd += retList.get(1);
				}
			}
			
			
			retList = SolUtil.calcOneDayAdImpression(day0,  false);
			if (retList != null && retList.size() == 2) {
				cntIns += retList.get(0);
				cntUpd += retList.get(1);
			}
			
			
			//
			//		SELECT SELECT ad_id, SUM(succ_tot), SUM(actual_amount)
			//
			List<Tuple> list = revService.getHourlyPlayActualStatGroupByAdId();
			for(Tuple tuple : list) {
				int adId = (Integer) tuple.get(0);
				int actualValue = ((BigDecimal) tuple.get(1)).intValue();
				int actualAmount = ((BigDecimal) tuple.get(2)).intValue();
				
				AdcAd ad = adcService.getAd(adId);
				if (ad != null) {
					if (ad.getActualValue() != actualValue || ad.getActualAmount() != actualAmount) {
						ad.setActualValue(actualValue);
						ad.setActualAmount(actualAmount);
						
						if (actualValue == 0) {
							ad.setActualCpm(0);
						} else {
							ad.setActualCpm((int)((long)actualAmount * 1000l / (long)actualValue));
						}
						
						double achvRatio = 0;
						// 달성률
						//
						//   %값으로, 보통은 100에 수렴함. 집행정책에 따라 그 대상의 항목이 다름.
						//
						//   if 집행정책 == 노출량, 
						//      if 보장 노출량 == 0, 달성률 == 0
						//      else 집행 노출량 / 보장 노출량
						//   else if 집행정책 == 광고예산, 집행 금액 / 예산
						//   else 달성률 == 0
						if (ad.getGoalType().equals("I")) {
							if (ad.getGoalValue() == 0) {
								achvRatio = 0d;
							} else {
								achvRatio = Math.round((double)actualValue * 10000d / (double)ad.getGoalValue()) / 100d;
							}
						} else if (ad.getGoalType().equals("A")) {
							achvRatio = Math.round((double)ad.getActualAmount() * 10000d / (double)ad.getBudget()) / 100d;
						}
						ad.setAchvRatio(achvRatio);
						
						// 의도적으로 who 터치 안함
						adcService.saveOrUpdate(ad);
					}
				}
			}
			
			
			// 캠페인의 목표량이 2차 설정(매체)에 의한 값을 먼저 계산함
			HashMap<String, Integer> sysValueMap = new HashMap<String, Integer>();
			List<AdcAd> adList = adcService.getAdList();
			for(AdcAd ad : adList) {
				if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
						(ad.getStatus().equals("A") || ad.getStatus().equals("R")) &&
						(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
						ad.getGoalType().equals("I") && ad.getGoalValue() > 0 && ad.getSysValue() == 0) {

					int sysValuePct = Util.parseInt(SolUtil.getOptValue(ad.getMedium().getId(), "sysValue.pct"));
					if (sysValuePct > 0) {
						String key = "C" + ad.getCampaign().getId();
						int sysValue = (int)Math.ceil((float)ad.getGoalValue() * (float)sysValuePct / 100f);
						Integer sum = sysValueMap.get(key);
						if (sum == null) {
							sysValueMap.put(key, sysValue);
						} else {
							sysValueMap.put(key, sum + sysValue);
						}
					}
				}
			}
			
			
			list = adcService.getCampaignBudgetStatGroupByCampaignId();
			for(Tuple tuple : list) {
				int campaignId = (Integer) tuple.get(0);
				int budget = ((BigDecimal) tuple.get(1)).intValue();
				int goalValue = ((BigDecimal) tuple.get(2)).intValue();
				int sysValue = ((BigDecimal) tuple.get(3)).intValue();
				
				AdcCampaign campaign = adcService.getCampaign(campaignId);
				if (campaign != null && !campaign.isSelfManaged()) {
					campaign.setBudget(budget);
					campaign.setGoalValue(goalValue);
					campaign.setSysValue(sysValue);
					
					// 매체의 기본 목표량의 계산에 의해 나온 목표량을 추가
					Integer sysValue1 = sysValueMap.get("C" + campaignId);
					if (sysValue1 != null) {
						campaign.setSysValue(campaign.getSysValue() + sysValue1);
					}
					//-

					List<Tuple> typeCntList = adcService.getAdGoalTypeCountByCampaignId(campaignId);
					
					if (typeCntList.size() == 0) {
						campaign.setGoalType("U");
					} else if (typeCntList.size() > 1) {
						campaign.setGoalType("M");
					} else {
						Tuple typeTuple = typeCntList.get(0);
						campaign.setGoalType((String) typeTuple.get(0));
					}
					
					// 의도적으로 who 터치 안함
					adcService.saveOrUpdate(campaign);
				}
			}
			
			list = revService.getHourlyPlayActualStatGroupByCampaignId();
			for(Tuple tuple : list) {
				int campaignId = (Integer) tuple.get(0);
				int actualValue = ((BigDecimal) tuple.get(1)).intValue();
				int actualAmount = ((BigDecimal) tuple.get(2)).intValue();
				
				AdcCampaign campaign = adcService.getCampaign(campaignId);
				if (campaign != null) {
					if (campaign.getActualValue() != actualValue || campaign.getActualAmount() != actualAmount) {
						
						campaign.setActualValue(actualValue);
						campaign.setActualAmount(actualAmount);
						
						if (actualValue == 0) {
							campaign.setActualCpm(0);
						} else {
							campaign.setActualCpm((int)((long)actualAmount * 1000l / (long)actualValue));
						}
						
						double achvRatio = 0;
						// 달성률
						//
						//   %값으로, 보통은 100에 수렴함. 집행정책에 따라 그 대상의 항목이 다름.
						//
						//   if 집행정책 == 노출량, 
						//      if 보장 노출량 == 0, 달성률 == 0
						//      else 집행 노출량 / 보장 노출량
						//   else if 집행정책 == 광고예산, 집행 금액 / 예산
						//   else 달성률 == 0
						if (campaign.getGoalType().equals("I")) {
							if (campaign.getGoalValue() == 0) {
								achvRatio = 0d;
							} else {
								achvRatio = Math.round((double)actualValue * 10000d / (double)campaign.getGoalValue()) / 100d;
							}
						} else if (campaign.getGoalType().equals("A")) {
							achvRatio = Math.round((double)campaign.getActualAmount() * 10000d / (double)campaign.getBudget()) / 100d;
						}
						campaign.setAchvRatio(achvRatio);
						
						// 의도적으로 who 터치 안함
						adcService.saveOrUpdate(campaign);
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("calcDailyAdImpressionStat", e);
			cntUpd = -1;
			cntIns = -1;
		}
		
		String ret = "Daily Stat, ins " + cntIns + " rows,";
		ret += " upd " + cntUpd + " rows, ";
		
		if (day1Included) {
			ret += " day-1, ";
		}
		if (day2Included) {
			ret += " day-2, ";
		}
		
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}

	private String calcAdTodayTargetValues() {
		
		long startAt = new Date().getTime();
		int cnt = 0;

		List<AdcAd> adList = adcService.getAdList();
		for(AdcAd ad : adList) {

			int tgtToday = 0;
			
			if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
					(ad.getStatus().equals("A") || ad.getStatus().equals("R")) &&
					(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
					(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
				
				//
				//   SELECT SUM(succ_tot), SUM(actual_amount)
				//
				Tuple tuple = adcService.getAdAccStatBeforePlayDate(ad.getId(), Util.removeTimeOfDate(new Date()));
				if (tuple != null) {
					BigDecimal sumView = (BigDecimal) tuple.get(0);
					BigDecimal sumAmount = (BigDecimal) tuple.get(1);
					
					int numerator = 0;
					if (ad.getGoalType().equals("I")) {
						// 노출량 based
						numerator = ad.getGoalValue();
						
						// 보장량이 설정되어 있을 경우, 매체 설정값 기반으로 목표량 설정
						int sysValue = ad.getSysValue();
						if (numerator > 0 && sysValue == 0) {
							// 보장량 설정되어 있고, 목표량이 미설정
							int sysValuePct = Util.parseInt(SolUtil.getOptValue(ad.getMedium().getId(), "sysValue.pct"));
							if (sysValuePct > 0) {
								sysValue = (int)Math.ceil((float)numerator * (float)sysValuePct / 100f);
							}
						}
						if (sysValue > numerator) {
							numerator = sysValue;
						}
						
						if (sumView != null) {
							numerator -= sumView.intValue();
						}
					} else if (ad.getGoalType().equals("A")) {
						// 광고예산 based
						numerator = ad.getBudget();
						
						if (sumAmount != null) {
							numerator -= sumAmount.intValue();
						}
					}
					
					if (numerator > 0) {
						tgtToday = SolUtil.getDayTargetOfProgressingAd(ad, numerator, 
								Util.removeTimeOfDate(new Date()));
					}
					
					try {
						ad.setTgtToday(tgtToday);
						adcService.saveOrUpdate(ad);
						
						// 일별 달성 기록 생성 확인
						SolUtil.checkDailyArch(ad, Util.removeTimeOfDate(new Date()));
						
						cnt++;
					} catch (Exception e) {
						logger.error("calcAdTodayTargetValues", e);
					}
				}
			}
		}
		
		List<AdcCampaign> campList = adcService.getCampaignList();
		for(AdcCampaign campaign : campList) {

			int tgtToday = 0;
			
			if (campaign.isSelfManaged() && (campaign.getStatus().equals("U") || campaign.getStatus().equals("R")) &&
					(campaign.getGoalType().equals("A") || campaign.getGoalType().equals("I"))) {
				
				List<AdcAd> list = adcService.getAdListByCampaignId(campaign.getId());
				ArrayList<Integer> ids = new ArrayList<Integer>();
				for(AdcAd ad : list) {
					ids.add(ad.getId());
				}
				
				//
				//   SELECT SUM(succ_tot), SUM(actual_amount)
				//
				Tuple tuple = adcService.getAdAccStatBeforePlayDate(ids, Util.removeTimeOfDate(new Date()));
				if (tuple != null) {
					BigDecimal sumView = (BigDecimal) tuple.get(0);
					BigDecimal sumAmount = (BigDecimal) tuple.get(1);
					
					int numerator = 0;
					if (campaign.getGoalType().equals("I")) {
						// 노출량 based
						numerator = campaign.getGoalValue();
						
						// 보장량이 설정되어 있을 경우, 매체 설정값 기반으로 목표량 설정
						int sysValue = campaign.getSysValue();
						if (numerator > 0 && sysValue == 0) {
							// 보장량 설정되어 있고, 목표량이 미설정
							int sysValuePct = Util.parseInt(SolUtil.getOptValue(campaign.getMedium().getId(), "sysValue.pct"));
							if (sysValuePct > 0) {
								sysValue = (int)Math.ceil((float)numerator * (float)sysValuePct / 100f);
							}
						}
						if (sysValue > numerator) {
							numerator = sysValue;
						}
						
						if (sumView != null) {
							numerator -= sumView.intValue();
						}
					} else if (campaign.getGoalType().equals("A")) {
						// 광고예산 based
						numerator = campaign.getBudget();
						
						if (sumAmount != null) {
							numerator -= sumAmount.intValue();
						}
					}
					
					if (numerator > 0) {
						tgtToday = SolUtil.getDayTargetOfProgressingCampaign(campaign, numerator, 
								Util.removeTimeOfDate(new Date()));
					}
					
					try {
						campaign.setTgtToday(tgtToday);
						adcService.saveOrUpdate(campaign);
						
						// 일별 달성 기록 생성 확인
						SolUtil.checkDailyArch(campaign, Util.removeTimeOfDate(new Date()));
						
						cnt++;
					} catch (Exception e) {
						logger.error("calcAdTodayTargetValues", e);
					}
				}
			}
		}
		
		return cnt + " rows, time: " + (new Date().getTime() - startAt);
	}
	
	private String refreshViewTypeAdLaneList() {
		
		long startAt = new Date().getTime();
		int cnt = 0;
		
		
		ArrayList<String> adPackTypes = new ArrayList<String>();
		ArrayList<String> allowedMedia = new ArrayList<String>();
		HashMap<String, Integer> maxLaneMap = new HashMap<String, Integer>();
		
		List<Tuple> maxLaneList = fndService.getViewTypeMaxLaneGroupByMediumId();
		for(Tuple tuple : maxLaneList) {
			maxLaneMap.put("M" + (int) tuple.get(0), (int) tuple.get(1));
		}
		
		List<FndViewType> viewTypeList = fndService.getViewTypeList();
		for(FndViewType viewType : viewTypeList) {
			GlobalInfo.LaneCreatMap.put(viewType.getCode(), viewType.isAdPackUsed() ? "Y" : "N");
			
			if (viewType.isAdPackUsed()) {
				List<String> media = Util.tokenizeValidStr(viewType.getDestMedia());
				for(String m : media) {
					adPackTypes.add(m + ":" + viewType.getCode());
					if (!allowedMedia.contains(m)) {
						allowedMedia.add(m);
					}
				}
			}
		}
		

		List<KnlMedium> mediumList = knlService.getValidMediumList();
		for(KnlMedium medium : mediumList) {
			if (!allowedMedia.contains(medium.getShortName())) {
				continue;
			}
			Integer maxLane = maxLaneMap.get("M" + medium.getId());
			if (maxLane == null || maxLane.intValue() < 1) {
				continue;
			}
			
			List<AdcAd> adList = adcService.getAdListByMediumId(medium.getId());
			for(AdcAd ad : adList) {
				if (Util.isNotValid(ad.getViewTypeCode())) {
					continue;
				} else if (!adPackTypes.contains(medium.getShortName() + ":" + ad.getViewTypeCode())) {
					continue;
				} else if (!(ad.getStatus().equals("A") || ad.getStatus().equals("R"))) {
					continue;
				}
				
	        	List<String> idList = Util.tokenizeValidStr(ad.getAdPackIds());
	        	if (idList.size() == 0) {
	        		continue;
	        	}
	        	
				cnt++;
				
	        	for(int i = 1; i <= maxLane.intValue(); i ++) {
	        		int idx = i % idList.size();
	        		if (idx == 0) {
	        			idx = idList.size();
	        		}
	        		
	        		String key = "M" + medium.getId() + "L" + i;
	        		String val = GlobalInfo.LaneCreatMap.get(key);
	        		if (Util.isNotValid(val)) {
	        			val = "|";
	        		}
	        		val += idList.get(idx - 1) + "|";
	        		
	        		GlobalInfo.LaneCreatMap.put(key, val);
	        	}
			}
		}
		
		return cnt + " ads, time: " + (new Date().getTime() - startAt);
	}
	
	private String checkImpressionWaves() {
		
		long startAt = new Date().getTime();
		

		int cnt = 0;
		
		
		try {
			
			List<RevImpWave> impWaveList = revService.getEffImpWaveList();
			for(RevImpWave impWave : impWaveList) {

				String key = "Imp" + impWave.getScreenId();
				int adCreatId = 0;
				
				if (impWave.getAdCreativeId() == null) {
					AdcAd ad = adcService.getAd(impWave.getAdId());
					if (ad != null) {
						Date today = Util.removeTimeOfDate(new Date());
						List<AdcAdCreative> adCreatList = adcService.getCandiAdCreativeListByMediumIdDate
								(ad.getMedium().getId(), today, today);
						for(AdcAdCreative adCreat : adCreatList) {
							if (adCreat.getAd().getId() == impWave.getAdId() && adCreat.getCreative().getId() == impWave.getCreativeId()) {
								adCreatId = adCreat.getId();
								break;
							}
						}
					}
				} else {
					adCreatId = impWave.getAdCreativeId();
				}

				if (adCreatId > 0) {
					GlobalInfo.AutoExpVarMap.put(key, String.valueOf(adCreatId));
			    	GlobalInfo.AutoExpVarTimeMap.put(key, Util.addMinutes(new Date(), 5));
				}

		    	cnt++;
			}
		} catch (Exception e) {
    		logger.error("checkImpressionWaves", e);
		}
		
		String ret = "ImpWaves " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String refreshViewTypeList() {
		
		long startAt = new Date().getTime();
		
		int cnt = 0;

		try {
			
			List<FndViewType> viewTypeList = fndService.getViewTypeList();
			for(FndViewType viewType : viewTypeList) {
				FndViewTypeItem item = GlobalInfo.ViewTypeGlobalMap.get(viewType.getCode());
				if (item == null) {
					GlobalInfo.ViewTypeGlobalMap.put(viewType.getCode(), new FndViewTypeItem(viewType));
				} else {
					// hashmap에 별도의 put을 하지 않더라도 적용됨
					item.setAdPackUsed(viewType.isAdPackUsed());
				}
				
		    	cnt++;
			}
		} catch (Exception e) {
    		logger.error("refreshViewTypeList", e);
		}
		
		String ret = "ViewTypeList " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}

	private void removeExpiredSyncPackReportMap() {
		
		// NegativeArraySizeException 넘기기
		if (GlobalInfo.SyncPackReportGlobalMap.size() > 0) {
			
			try {
				
				Date now = new Date();

				int oldCnt = GlobalInfo.SyncPackReportGlobalMap.size();
				
				List<String> keyList = new ArrayList<>(GlobalInfo.SyncPackReportGlobalMap.keySet());
				for(String key : keyList) {
					RevSyncPackMinMaxItem item = GlobalInfo.SyncPackReportGlobalMap.get(key);
					if (item != null) {
						boolean expiredItem = Util.addSeconds(item.getDate(), 5).before(now);
						if (expiredItem) {
							
	    					// 모든 묶음 구성원이 보고하기 전 시간 경과 상태
	    					
	    					String grade = SolUtil.getSyncPackGrade(item.getMediumID(), item.getGroupID(), item.getDiff(), item.getCnt());
	    					String gradeQ = SolUtil.getSyncPackGradeQueue(item.getGroupID());
	    					String countQ = SolUtil.getSyncPackCntQueue(item.getGroupID());
	    					
							revService.saveOrUpdate(new RevSyncPackImp(new Date(), item.getGroupID(), "EXP", grade, item.getDiff(), item.getCnt(), item.getMaxCnt(), 
									item.getAdID(), SolUtil.getSyncPackGradeQueue(item.getGroupID()), SolUtil.getSyncPackCntQueue(item.getGroupID())));
							logger.info("** syncAD:[" + item.getGroupID() + " - " + grade + " - EXP]  " + item.getDiff() + " - size=" + item.getCnt() + 
									", ad=" + item.getAdID() + ", gradeQ=" + gradeQ + ", cntQ=" + countQ);
							
							GlobalInfo.SyncPackReportGlobalMap.remove(key);
							
							
							SolUtil.proceedSyncPackControlRules(item, grade, false, now);
							InvSyncPack sp = invService.getSyncPackByShortName(item.getGroupID());
							if (sp != null && Util.parseInt(item.getAdID()) > 0) {
								
								String adName = "";
    				    		String channelID = "";
    				    		String playlist = "";
    				    		Integer seq = null;
    				    		Integer seqDiff = null;
    				    		
    				    		
    				    		// 기존 RevRecPlaylist에서 RevChanAd로 변경됨
    				    		//
								OrgChannel channel = orgService.getChannel(SolUtil.getFirstPriorityChannelByTypeObjId("P", sp.getId()));
								if (channel != null) {
        							RevChanAd thisAd = revService.getLastChanAdByChannelIdSeq(channel.getId(), Util.parseInt(item.getAdID()));
        							if (thisAd != null) {
        								adName = thisAd.getAdName();
        								channelID = channel.getShortName();
        								seq = thisAd.getSeq();

        								if (channel.getAppendMode().equals("P")) {
        									List<String> hints = Util.tokenizeValidStr(thisAd.getHint(), "_");
        									if (hints.size() == 2) {
        										AdcPlaylist pl = adcService.getPlaylist(Util.parseInt(hints.get(0)));
        										if (pl != null) {
        											playlist = pl.getName();
        										}
        									}
        								} else if (channel.getAppendMode().equals("A")) {
        									playlist = "[자율 광고선택]";
        								}
        								
        								RevChanAdRpt chanAdRpt = new RevChanAdRpt(thisAd, "P", sp.getId(), new Date(item.getMin()), item.getCnt());
        								seqDiff = chanAdRpt.getDiff();
        								
        								revService.saveOrUpdate(chanAdRpt);
        							}
								}

								
    							// 광고 시작 시간 기록
    				    		InvRTSyncPack rtSyncPack = invService.getRTSyncPackBySyncPackId(sp.getId());
    				    		if (rtSyncPack == null) {
    				    			invService.saveOrUpdate(new InvRTSyncPack(sp.getId()));
    				    			
    				    			rtSyncPack = invService.getRTSyncPackBySyncPackId(sp.getId());
    				    		}
    				    		if (rtSyncPack != null) {
    				    			
    				    			revService.getLastChanAdByChannelIdSeq(oldCnt, oldCnt);
    				    			
    				    			rtSyncPack.setLastAd(adName);
    				    			rtSyncPack.setLastAdBeginDate(new Date(item.getMin()));
    				    			rtSyncPack.setDiff(item.getDiff());
    				    			rtSyncPack.setGradeQueue(gradeQ);
    				    			rtSyncPack.setCountQueue(countQ);
    				    			rtSyncPack.setChannel(channelID);
    				    			rtSyncPack.setPlaylist(playlist);
    				    			rtSyncPack.setSeq(seq);
    				    			rtSyncPack.setSeqDiff(seqDiff);
    				    			
    				    			rtSyncPack.setWhoLastUpdateDate(now);
    				    			
    				    			invService.saveOrUpdate(rtSyncPack);
    				    		}
								
							}
						}
					}
				}
				
			} catch (Exception e) {
				logger.error("removeExpiredSyncPackReportMap", e);
			}
			
		}
	}
	
	private String refreshMedia() {
		
		long startAt = new Date().getTime();
		
		int cnt = 0;

		try {
			
			List<KnlMedium> mediaList = knlService.getValidMediumList();
			for(KnlMedium medium : mediaList) {
				KnlMediumCompactItem item = GlobalInfo.MediaMap.get(medium.getShortName());
				if (item == null) {
					GlobalInfo.MediaMap.put(medium.getShortName(), new KnlMediumCompactItem(medium));
					
					// ApiKeyMediaMap 은 API Key로 mediumId 획득만을 목적으로 하기 때문에
					// 변경값에 대한 갱신은 진행하지 않고, 초기 등록(mediumId 포함)만 진행
					GlobalInfo.ApiKeyMediaMap.put(medium.getApiKey(), new KnlMediumCompactItem(medium));
				} else {
					item.setAdFreqCap(Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.ad")));
					item.setAdvFreqCap(Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.advertiser")));
					item.setCatFreqCap(Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.category")));
					item.setDailyScrCap(Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.daily.screen")));
					
					item.setaGradeMillis(medium.getaGradeMillis());
					item.setbGradeMillis(medium.getbGradeMillis());
					item.setcGradeMillis(medium.getcGradeMillis());
					
					// 어떤 이유로 인해 API 키가 변경되었을 경우의 처리
					if (GlobalInfo.ApiKeyMediaMap.get(medium.getApiKey()) == null) {
						GlobalInfo.ApiKeyMediaMap.put(medium.getApiKey(), new KnlMediumCompactItem(medium));
					}
				}
				
		    	cnt++;
			}
		} catch (Exception e) {
    		logger.error("refreshMedia", e);
		}
		
		String ret = "Media " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String refreshSyncPacks() {
		
		long startAt = new Date().getTime();
		
		int cnt = 0;

		try {
			
			List<InvSyncPackItem> list = invService.getSyncPackItemList();
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for(InvSyncPackItem item : list) {
				// 일단 레인번호가 1번이 아닌 자료를 로깅에서 제외(ids에는 제외되는 자료 추가)
				if (item.getLaneId() == 1) {
					continue;
				}
				
				if (!ids.contains(item.getScreenId())) {
					ids.add(item.getScreenId());
				}
			}
			
			GlobalInfo.LogProhibitedScreenIds = ids;

			
			List<Tuple> tupleList = invService.getSyncPackTupleListGroupByShortName();
			for(Tuple tuple : tupleList) {
				
				int mediumId = (Integer) tuple.get(0);
				String spShortName = (String) tuple.get(1);
				int scrCnt = ((BigInteger) tuple.get(2)).intValue();
				int id = (Integer) tuple.get(3);
				int minLaneId = (Integer) tuple.get(4);
				String scrShortName = (String) tuple.get(5);
				int scrId = (Integer) tuple.get(6);
				
				GlobalInfo.SyncPackMap.put(spShortName, new InvSyncPackCompactItem(mediumId, spShortName, scrCnt, id, minLaneId, scrShortName, scrId));
				
		    	cnt++;
			}
		} catch (Exception e) {
    		logger.error("refreshSyncPacks", e);
		}
		
		String ret = "SyncPack " + cnt + " rows, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private String queueChannelAds() {
		
		//
		// ret 값
		// 		- Nothing: 초기값. server.ID 구성 항목이 존재하지 않을 경우
		//		- NEW/LOCK: 처음으로 serverID가 DB에 등록되는 경우
		//		- LOCK: 다른 serverID로부터 현재 serverID로 락을 거는 경우
		//		- PASS: 다른 serverID로부터 현재 serverID로 락을 걸기 전 충분한 시간(90초)이 경과하지 않은 경우
		//		- GO internal: 코드 실행
		//
		// 초기 시작 단계: NEW/LOCK - GO internal - GO internal - ...
		// 다른 서버로 이동: GO internal - GO internal - PASS - PASS - ...
		// 현재 서버로 교체: PASS - PASS - LOCK - GO internal - GO internal - ...
		//
		
		long startAt = new Date().getTime();
		
		String ret = "Nothing";
		
		try {
			
			//
			// server.ID가 구성 항목으로 포함되었는 지를 확인
			//   항목이 없거나, 혹은 빈 값이면 그 값도 동일하게 항목명이 되기 때문에 체크
			//
			String serverID = Util.getFileProperty("server.ID");
			if (Util.isValid(serverID) && !serverID.equals("server.ID")) {
				SysOpt sysOpt = sysService.getOpt("server.ID");
				if (sysOpt == null) {
					// 신규로 등록하면서, lock
					sysService.saveOrUpdate(new SysOpt("server.ID", serverID));
					
					ret = "NEW/LOCK";
				} else {
					if (sysOpt.getValue().equals(serverID)) {
						// 이미 lock 걸려있으니 현재 시간 설정 후, 본 작업 시작
						sysOpt.setDate(new Date());
						sysService.saveOrUpdate(sysOpt);
						
						ret = "GO internal - " + queueChannelAdsInternal() + "rows";
					} else {
						
						Date now = new Date();
						if (now.after(Util.addSeconds(sysOpt.getDate(), 90))) {
							
							// 다른 서버 장비로부터 lock
							sysOpt.setValue(serverID);
							sysOpt.setDate(now);
							sysService.saveOrUpdate(sysOpt);
							
							ret = "LOCK";
							
						} else {
							
							ret = "PASS";
							
						}
					}
				}
			}

		} catch (Exception e) {
    		logger.error("queueChannelAds", e);
		}

		
		ret += ", time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	private int queueChannelAdsInternal() {
		
		//
		// 목록의 기본 조건:
		//   - 활성화 상태 = true
		//
		
		//
		// 큐 규칙:
		//   - 현 시간 기준 최소 5분은 반드시 준비되어야 한다
		//   - 시작 시 필요한 항목:
		//     - 시작 시간
		//     - 시작 일련 번호
		//     - 루프 종료 시간
		//   - 시작 시간: 가장 최근 자료의 종료 시간 확인
		//     - 최근 10분 이전 자료 미존재: 현재 시간 0초 부터 채우기
		//     - 최근 10분 이전 자료 존재: 시작 시간 = 최근 종료 시간
		//   - 시작 일련 번호
		//     - 마지막 항목 확인:
		//       - 존재
		//         - 최근 30분 내 광고 요청 없음 && 마지막 초기화 후 24시간 경과한 상태: (초기화 0) + 1
		//         - 위 조건 아니면: (마지막 항목 일련 번호) + 1
		//       - 미존재: (초기화 0) + 1
		//     - 초기화 시간(현재 시간)을 sys option에 저장
		//   - 루프 종료 시간
		//     - (현재 시간) + 5분
		//
		// 채널 광고: RevChanAd
		//   - 항목
		//     - 광고 채널 번호
		//     - 일련번호
		//     - acId(adCreativeId), 광고
		//     - adId(creativeId), 광고 소재
		//     - 시작일시, 종료일시, 재생시간
		//     - 생성일시
		//     - 도움정보
		//       - 재생목록 유형: 해당 항목의 재생목록 번호 및 인덱스 번호 등(예: P13I9)
		//       - 자율 광고선택: 미정
		//
		HashMap<String, RevChanAdPlayItem> lastMap = new HashMap<String, RevChanAdPlayItem>();
		
		List<Tuple> lastTupleList = revService.getLastChanAdListGroupByChannelId();
		for(Tuple tuple : lastTupleList) {
			lastMap.put("CH" + String.valueOf((Integer) tuple.get(0)),
					new RevChanAdPlayItem(((Integer) tuple.get(1)).intValue(), (Date) tuple.get(2), (Date) tuple.get(3), 
							(String) tuple.get(4)));
		}
		
		HashMap<String, RevChanAdItem> adMap = new HashMap<String, RevChanAdItem>();
		List<Tuple> adTupleList = adcService.getChannelAdViewTypeTupleList();
		for(Tuple tuple : adTupleList) {
			// SELECT ac.ad_creative_id, c.creative_id, c.name as creat_name, a.ad_pack_ids, a.ad_id, a.name as ad_name,    0 ... 5
	        //        a.medium_id, a.view_type_code, vt.resolution, cf.media_type, cf.src_duration, m.default_dur_secs,		6 ... 11
			//        (ac.end_date IS NULL OR ac.end_date >= CURRENT_DATE()) as effective                                   12
			
			String mediaType = (String) tuple.get(9);
			int defaultDurSecs = ((Integer) tuple.get(11)).intValue();
			double srcDurSecs = ((Double) tuple.get(10)).doubleValue();
			int duration = mediaType.equals("I") ? defaultDurSecs * 1000 : (int)Math.round(srcDurSecs * 1000d);
			int effective = ((BigInteger) tuple.get(12)).intValue();
			
			String key = "AC" + String.valueOf(((Integer) tuple.get(0)).intValue());
			adMap.put(key, 
					new RevChanAdItem(((Integer) tuple.get(6)).intValue(), ((Integer) tuple.get(0)).intValue(), ((Integer) tuple.get(1)).intValue(),
							(String) tuple.get(2), ((Integer) tuple.get(4)).intValue(), (String) tuple.get(5), (String) tuple.get(3),
							(String) tuple.get(8), duration, effective == 1));
		}
		
		HashMap<String, RevChanAdItem> adMap2 = new HashMap<String, RevChanAdItem>();
		adTupleList = adcService.getChannelAdNoViewTypeTupleList();
		for(Tuple tuple : adTupleList) {
			
			String reso = (String) tuple.get(8);
			String mediaType = (String) tuple.get(9);
			int defaultDurSecs = ((Integer) tuple.get(11)).intValue();
			double srcDurSecs = ((Double) tuple.get(10)).doubleValue();
			int duration = mediaType.equals("I") ? defaultDurSecs * 1000 : (int)Math.round(srcDurSecs * 1000d);
			int effective = ((BigInteger) tuple.get(12)).intValue();
			
			String key = "AC" + String.valueOf(((Integer) tuple.get(0)).intValue()) + "R" + reso;
			adMap2.put(key, 
					new RevChanAdItem(((Integer) tuple.get(6)).intValue(), ((Integer) tuple.get(0)).intValue(), ((Integer) tuple.get(1)).intValue(),
							(String) tuple.get(2), ((Integer) tuple.get(4)).intValue(), (String) tuple.get(5), (String) tuple.get(3),
							(String) tuple.get(8), duration, effective == 1));
		}
		
		
		Date now = new Date();
		int cnt = 0;
		List<OrgChannel> list = orgService.getAdAppendableChannelList();
		for(OrgChannel channel : list) {

			// 현재 채널에서 추가 여부 확인
			int currCnt = cnt;
			
			RevChanAdPlayItem lastItem = lastMap.get("CH" + channel.getId());
			Date startDate = null, endDate = null;
			Date loopEndDate = Util.addMinutes(now, 5);
			int seq = 0;
			if (lastItem == null || lastItem.getPlayEndDate().before(Util.addMinutes(now, -10))) {
				startDate = Util.removeSecTimeOfDate(now);
			} else {
				startDate = lastItem.getPlayEndDate();
			}
			
			if (lastItem == null) {
				// sysopt에 초기화한 시간(현재 시간) 설정
				SysOpt sysOpt = sysService.getOpt("initTime.CH" + String.valueOf(channel.getId()));
				if (sysOpt == null) {
					sysService.saveOrUpdate(new SysOpt("initTime.CH" + String.valueOf(channel.getId()), "Y"));
				} else {
					sysOpt.setDate(startDate);
					sysService.saveOrUpdate(sysOpt);
				}
				// seq는 위에서 0으로 설정했기 때문에 그대로 이용
			} else {
				seq = lastItem.getSeq();
				// 이후 구현
				// 최근 30분 내 광고 요청 없음 && 마지막 초기화 후 24시간 경과한 상태 -> sysopt에 초기화한 시간(현재 시간) 설정
				// 아니면 seq = lastItem.getSeq()
			}
			
			// 광고 추가 모드 확인: 재생목록(P) / 자율 광고선택(A)
			if (channel.getAppendMode().equals("P")) {
				
				// 광고 채널에서의 재생목록은 고정/확정 상태
				List<AdcPlaylist> plList = adcService.getActivePlaylistListByChannelId(channel.getId());
				if (plList.size() == 0) {
					continue;
				}
				
				//
				// 두 유형으로 분기
				//   : plList는 시작 시간의 오름차순으로 되어 있기 때문에 루핑을 통해 조건 비교 진행
				//
				// - 1 재생목록: 단 하나의 재생목록만 존재. plA-1만 존재
				// - 2 재생목록: 2nd 재생목록 시작 시간이 현재 시간과 루프 종료 시간 사이인 경우. plB-1, plB-2 존재
				//
				// - plA-1: 종료시간 = 루프 종료 시간. 시작 인덱스 = 가능한 마지막 인덱스 + 1
				// - plB-1: 종료 시간 = 2nd 재생목록 시작 시간. 시작 인덱스 = 가능한 마지막 인덱스 + 1, nullable
				// - plB-2: 종료 시간 = 루프 종료 시간, 시작 인덱스 = 0
				//
				AdcPlaylist pl1st = null, pl2nd = null, plPrev = null;
				for(AdcPlaylist playlist : plList) {
					if (playlist.getStartDate().before(startDate) && 
							(playlist.getEndDate() == null || playlist.getEndDate().after(startDate))) {		// pl.startDate < Ts
						pl1st = playlist;
						pl2nd = null;
					} else if(playlist.getStartDate().before(loopEndDate) && 
							(playlist.getEndDate() == null || playlist.getEndDate().after(loopEndDate))) {		// Ts <= pl.startDate < Te
						pl1st = plPrev;
						pl2nd = playlist;
					}
					plPrev = playlist;
				}
				if (pl1st == null && pl2nd == null) {
					continue;
				}
				
				
				List<RevChannelPlItem> itemList1st = new ArrayList<RevChannelPlItem>();
				List<RevChannelPlItem> itemList2nd = new ArrayList<RevChannelPlItem>();

				List<RevChannelPlItem> itemListLoop = new ArrayList<RevChannelPlItem>();
				Date currEndDate = loopEndDate;

				if (pl1st != null) {
	        		List<String> currValues = Util.tokenizeValidStr(pl1st.getAdValue());
	        		if (currValues.size() > 0) {
						int prevIdx = -1;
			        	if (lastItem != null) {
			        		List<String> hints = Util.tokenizeValidStr(lastItem.getHint(), "_");
			        		if (hints.size() == 2 && Util.parseInt(hints.get(0)) == pl1st.getId()) {
			        			int prev = Util.parseInt(hints.get(1));
			        			if (prev > -1 && prev < currValues.size() - 1) {
			        				prevIdx = prev;
			        			}
			        		}
			        	}
			        	
			        	itemList1st = getPlItemList(currValues, channel.getResolution(), prevIdx, pl1st.getMedium().getId(),
			        			pl1st.getMedium().getDefaultDurSecs() * 1000, channel.getViewTypeCode(), adMap, adMap2);
	        		}
	        		
	        		// pl2nd 재생목록 존재에 따라 plA-1, plB-1 여부 판단
	        		if (pl2nd != null) {
	        			currEndDate = pl2nd.getStartDate();
	        		} else {
	        			// 루핑 목록 준비
	        			itemListLoop = getPlItemList(currValues, channel.getResolution(), -1, pl1st.getMedium().getId(),
			        			pl1st.getMedium().getDefaultDurSecs() * 1000, channel.getViewTypeCode(), adMap, adMap2);
	        		}
				}
				if (pl2nd != null) {
	        		List<String> currValues = Util.tokenizeValidStr(pl2nd.getAdValue());
	        		if (currValues.size() > 0) {
			        	itemList2nd = getPlItemList(currValues, channel.getResolution(), -1, pl2nd.getMedium().getId(), 
			        			pl2nd.getMedium().getDefaultDurSecs() * 1000, channel.getViewTypeCode(), adMap, adMap2);
			        	
			        	if (pl1st == null) {
		        			// 루핑 목록 준비
		        			itemListLoop = getPlItemList(currValues, channel.getResolution(), -1, pl2nd.getMedium().getId(), 
				        			pl2nd.getMedium().getDefaultDurSecs() * 1000, channel.getViewTypeCode(), adMap, adMap2);
			        	}
	        		}
				}
        		
				
        		for(RevChannelPlItem plItem : itemList1st) {

        			if (startDate.after(currEndDate)) {
        				break;
        			}
        			
        			RevChanAd chanAd = new RevChanAd(channel.getId(), ++seq, plItem.getAdItem(), startDate, 
        					plItem.getAdItem().getDuration(), String.valueOf(pl1st.getId() + "_" + String.valueOf(plItem.getIdx())));
        			revService.saveOrUpdate(chanAd);
        			
        			startDate = Util.addMilliseconds(startDate, plItem.getAdItem().getDuration());
        			cnt++;
        		}
				
        		for(RevChannelPlItem plItem : itemList2nd) {

        			if (startDate.after(loopEndDate)) {
        				break;
        			}
        			
        			RevChanAd chanAd = new RevChanAd(channel.getId(), ++seq, plItem.getAdItem(), startDate, 
        					plItem.getAdItem().getDuration(), String.valueOf(pl2nd.getId() + "_" + String.valueOf(plItem.getIdx())));
        			revService.saveOrUpdate(chanAd);
        			
        			startDate = Util.addMilliseconds(startDate, plItem.getAdItem().getDuration());
        			cnt++;
        		}
        		
        		if (itemListLoop.size() > 0) {
            		do {
            			
                		for(RevChannelPlItem plItem : itemListLoop) {

                			if (startDate.after(currEndDate)) {
                				break;
                			}
                			
                			RevChanAd chanAd = new RevChanAd(channel.getId(), ++seq, plItem.getAdItem(), startDate, 
                					plItem.getAdItem().getDuration(), String.valueOf(pl1st.getId() + "_" + String.valueOf(plItem.getIdx())));
                			revService.saveOrUpdate(chanAd);
                			
                			startDate = Util.addMilliseconds(startDate, plItem.getAdItem().getDuration());
                			cnt++;
                		}

            		} while (startDate.before(loopEndDate));
        		}
        		
			} else {
				// 자율 광고선택 모드
				
				InvScreen reqScr = null;
				if (channel.getReqScreenId() != null) {
					reqScr = invService.getScreen(channel.getReqScreenId().intValue());
				}
				
				if (reqScr != null) {
					do {
						
						AdcAdCreatFileObject adCreatFileObject = getNextAdFromCandiList(reqScr, channel.getShortName(), channel.getViewTypeCode(), startDate);
						if (adCreatFileObject != null) {
							
	            			RevChanAd chanAd = new RevChanAd(channel.getId(), ++seq, adCreatFileObject.getAdCreat(), startDate, 
	            					adCreatFileObject.getJsonFileObject().getDurMillis(), "");
	            			revService.saveOrUpdate(chanAd);
	            			
	            			startDate = Util.addMilliseconds(startDate, adCreatFileObject.getJsonFileObject().getDurMillis());
	            			cnt++;
						}
            			
					} while (startDate.before(loopEndDate));
				}
				
			}
			
			
			// 현재 채널에서 추가 여부 확인
			if (currCnt != cnt) {
				OrgRTChannel rtChannel = orgService.getRTChannelByChannelId(channel.getId());
				if (rtChannel == null) {
					orgService.saveOrUpdate(new OrgRTChannel(channel.getId()));
					
					rtChannel = orgService.getRTChannelByChannelId(channel.getId());
				}
				if (rtChannel != null) {
					rtChannel.setLastAdAppDate(now);
					rtChannel.setWhoLastUpdateDate(now);
					
					orgService.saveOrUpdate(rtChannel);
				}
			}
		}

		
		return cnt;
	}
	
	private AdcAdCreatFileObject getNextAdFromCandiList(InvScreen screen, String syncPackID, String viewType, Date date) {

		AdcAdCreatFileObject adCreatFileObject = null;

		for (int i = 0; i < 5; i ++) {
			adCreatFileObject = SolUtil.selectAdFromCandiList(screen, viewType, 0d, 0d, date);
			if (adCreatFileObject != null) {
				
				Integer lastId = GlobalInfo.LastSelCreatMap.get(syncPackID);
				if (lastId == null || lastId.intValue() != adCreatFileObject.getAdCreat().getCreative().getId()) {
					GlobalInfo.LastSelCreatMap.put(syncPackID, adCreatFileObject.getAdCreat().getCreative().getId());
					break;
				}
			}
		}
		
		if (adCreatFileObject != null) {

			String mapKey = "AdSel_A" + adCreatFileObject.getAdCreat().getAd().getId() + "S" + screen.getId();
			// 다음의 시간까지는 선택이 불가능하도록 함
			int impPlanPerHour = Util.parseInt(SolUtil.getOptValue(screen.getMedium().getId(), "impress.per.hour"), 6);
			if (impPlanPerHour < 1) {
				impPlanPerHour = 6;
			}

			// 1hr = 60 * 60 = 3600 sec
			// 매체에 정해진 시간당 송출 횟수 * 2.5배가 가능한 수치가 되도록
			// 의도적인 floor 처리
			int expireSecs = (int)(60f * 60f / (float)impPlanPerHour / 2.5f);
			SolUtil.putAutoExpVarValue(mapKey, "Y", Util.addSeconds(new Date(), expireSecs));
		}

		return adCreatFileObject;
	}
	
	private List<RevChannelPlItem> getPlItemList(List<String> adValues, String reso, int prevIdx, 
			int mediumId, int mediumDefDuration, String viewTypeCode, HashMap<String, RevChanAdItem> adMap, 
			HashMap<String, RevChanAdItem> adMap2) {
		
		int idx = 0;
		ArrayList<RevChannelPlItem> retList = new ArrayList<RevChannelPlItem>();
		
		for(String s : adValues) {
			if (Util.isValid(viewTypeCode)) {
	    		RevChanAdItem adItem = adMap.get("AC" + s);
	    		if (adItem != null && adItem.isEffective()) {
	    			RevChannelPlItem plItem = new RevChannelPlItem(idx, adItem);
	    			if (plItem.getAdItem().getMediumId() == mediumId && prevIdx < idx) {
	    				retList.add(plItem);
	    			}
	    		}
	    		idx++;
			} else {
				RevChanAdItem adItem = adMap2.get("AC" + s + "R" + reso);
	    		if (adItem != null && adItem.isEffective()) {
	    			RevChannelPlItem plItem = new RevChannelPlItem(idx, adItem);
	    			if (plItem.getAdItem().getMediumId() == mediumId && prevIdx < idx) {
	    				retList.add(plItem);
	    			}
	    		}
	    		idx++;
			}
		}
		
		return retList;
	}
	
	private String getAlimTalkForActScr(int mediumId, ArrayList<Integer> noAlimIds) {
    	
    	ArrayList<Integer> ids = new ArrayList<Integer>();
    	
    	// 메모리에 있는 현재 상태의 통계치 전달
    	List<Integer> monitList = invService.getMonitScreenIdsByMediumId(mediumId);
    	for(Integer i : monitList) {
    		if (noAlimIds.contains(i)) {
    			continue;
    		}
    		String status = GlobalInfo.InvenLastStatusMap.get("SC" + i);
    		if (!(Util.isValid(status) && status.equals("6"))) {
    			ids.add(i);
    		}
    	}

    	if (ids.size() > 0) {
    		List<Tuple> tupleList = revService.getLastObjTouchListIn(ids, 10);
    		
    		int failCnt = ids.size();
    		String desc = "";
    		for(Tuple tuple : tupleList) {
    			if (Util.isValid(desc)) {
    				desc += ", [" + (String) tuple.get(0) + "]";
    			} else {
    				desc = "[" + (String) tuple.get(0) + "]";
    			}
    		}
    		
    		return String.valueOf(ids.size()) + "_" + desc;
    	}
    	
    	return "0";
	}
	
	private String checkAlimTalkEvents() {
		
		// 
		// 알림톡 이벤트를 확인하고, 알림톡 발송 대상일 경우 알림톡을 발송한다.
		//
		long startAt = new Date().getTime();
		
		
		int cnt = 0, sendCnt = 0;
		
		try {
			List<OrgAlimTalk> list = orgService.getActiveAlimTalkList();
			for(OrgAlimTalk alimTalk : list) {
				List<String> subscribers = Util.tokenizeValidStr(alimTalk.getSubscribers());
				if (subscribers.size() > 0) {
					Date now = new Date();
		    		boolean isCurrentOpHours = SolUtil.isCurrentOpHours(alimTalk.getBizHour());
					if (isCurrentOpHours)  {
						
						// 이벤트: 활성 화면수
						if (alimTalk.getEventType().equals("ActScr")) {
							//
							// 현재 활성화된 기기수
							//
					    	
							// 이벤트 운영/점검 시간이 처음 시작되었을 경우, 기기가 준비되지 않았을 수도 있기 때문에
							// 운영으로 전환되는 시점인 점검 지연 시간을 적용한다.
							if (!SolUtil.isCurrentOpHours(alimTalk.getBizHour(), Util.addMinutes(now, -1 * alimTalk.getDelayChkMins()))) {
								continue;
							}
							
							
							// 알림톡 방지가 적용된 기기의 id 획득
							ArrayList<Integer> noAlimIds = new ArrayList<Integer>();
							SysOpt sysOpt = sysService.getOpt("opt.noalim." + alimTalk.getMedium().getShortName());
							if (sysOpt != null) {
	    						String value = sysOpt.getValue();
	    						if (Util.isValid(value)) {
	    							List<String> ids = Util.tokenizeValidStr(value);
	    							for(String id : ids) {
	    								Integer i = Util.parseInt(id);
	    								if (i != null && i.intValue() > 0 && !noAlimIds.contains(i)) {
	    									noAlimIds.add(i);
	    								}
	    							}
	    							cnt = ids.size();
	    						}
							}

							// 현재 상태
							String currStatus = "S";
							
					    	// 메모리에 있는 현재 상태의 통계치 전달
					    	int status6 = 0;
					    	List<Integer> monitList = invService.getMonitScreenIdsByMediumId(alimTalk.getMedium().getId());
					    	for(Integer i : monitList) {
					    		if (noAlimIds.contains(i)) {
					    			continue;
					    		}
					    		String status = GlobalInfo.InvenLastStatusMap.get("SC" + i);
					    		if (Util.isValid(status) && status.equals("6")) {
				    				status6 ++;
					    		}
					    	}
							
					    	// 활성화 기기수가 0인 경우는 서버의 셧다운 등 서버 기동 관련 문제 가능성이 있기 때문에 제외
					    	if (status6 > 0) {
					    		int setValue = Util.parseInt(alimTalk.getCfStr1());
					    		if (setValue > 0 && setValue > status6) {
					    			
					    			currStatus = "F";
					    		}
					    	}
					    	// 여기까지 이번 체크 타임의 최종 상태 확인 - currStatus
					    	
					    	
					    	// 장애 기기 수 및 장애 기기
					    	int failCnt = 0;
					    	String failList = "";
					    	if (currStatus.equals("F")) {
					    		String talkStr = getAlimTalkForActScr(alimTalk.getMedium().getId(), noAlimIds);
					    		String opStr1 = "";
					    		String opLgStr1 = "";
					    		if (!talkStr.equals("0") && talkStr.indexOf("_") > 0) {
					    			failCnt = Util.parseInt(talkStr.substring(0, talkStr.indexOf("_")));
					    			failList = talkStr.substring(talkStr.indexOf("_") + 1);
					    			
					    			if (failCnt < 1 || Util.isNotValid(failList)) {
					    				currStatus = "S";
					    			}
					    			
					    			// currStatus == "F"이면, 반드시 유효한 failCnt, failStr 값을 가짐
					    		}
					    	}
					    	
					    	// 실패(장애)에 대한 카운팅을 계속 진행하나,
					    	// 실패 카운팅은 S->F 전환 시에만 이용됨
					    	if (currStatus.equals("F")) {
					    		if (alimTalk.getOpInt1() == null) {
					    			alimTalk.setOpInt1(1);
					    		} else {
					    			alimTalk.setOpInt1(alimTalk.getOpInt1().intValue() + 1);
					    		}
					    	}
					    	
					    	// 발송 필요 처리
					    	boolean sendingRequired = false;
					    	
					    	if (alimTalk.getStatus().equals("S") && currStatus.equals("F")) {
					    		
					    		int failCounting = Util.parseInt(alimTalk.getCfStr2());
					    		if (failCounting > 0 && failCounting <= alimTalk.getOpInt1().intValue()) {
					    			sendingRequired = true;
					    		}
					    		
					    	} else if (alimTalk.getStatus().equals("S") && currStatus.equals("S")) {
					    		
					    		alimTalk.setStatus("S");
					    		alimTalk.setOpStr1(null);
					    		alimTalk.setOpLgStr1(null);
					    		alimTalk.setOpInt1(null);
					    		
					    	} else if (alimTalk.getStatus().equals("F") && currStatus.equals("S")) {
					    		
					    		alimTalk.setWaitDate(null);
					    		alimTalk.setOpStr1(null);
					    		alimTalk.setOpLgStr1(null);
					    		alimTalk.setOpInt1(null);
					    		alimTalk.setStatus("S");
					    		
					    		// 장애 종료 알림톡 발송
				    			SolUtil.sendAlimTalkForActScrEnd(alimTalk.getMedium().getShortName(), alimTalk.getShortName(), 
				    					alimTalk.getSubscribers());
				    			
					    	} else if (alimTalk.getStatus().equals("F") && currStatus.equals("F")) {
					    		
					    		String failCntStr = String.valueOf(failCnt);
					    		
				    			if (Util.isValid(alimTalk.getOpStr1()) && failCntStr.equals(alimTalk.getOpStr1()) && 
				    					Util.isValid(alimTalk.getOpLgStr1()) && failList.equals(alimTalk.getOpLgStr1())) {
				    				
				    				if (now.after(alimTalk.getWaitDate())) {
				    					sendingRequired = true;
				    				}
				    			} else {
			    					sendingRequired = true;
				    			}
					    		
					    	}
					    	
					    	if (sendingRequired) {
					    		
					    		cnt++;
					    		List<String> tmpList = Util.tokenizeValidStr(alimTalk.getSubscribers());
					    		if (tmpList.size() > 0) {
					    			sendCnt += tmpList.size();
					    		}
					    		
				    			// 알림톡 발송(장애)
				    			SolUtil.sendAlimTalkForActScr(alimTalk.getMedium().getShortName(), alimTalk.getShortName(), 
				    					alimTalk.getSubscribers(), alimTalk.getOpInt1().intValue(), failCnt, failList);
					    		
					    		alimTalk.setWaitDate(Util.addMinutes(now, alimTalk.getWaitMins()));
					    		alimTalk.setOpStr1(String.valueOf(failCnt));
					    		alimTalk.setOpLgStr1(failList);
					    		alimTalk.setStatus("F");				    		
					    	}
					    	
					    	alimTalk.setCheckDate(now);
					    	orgService.saveOrUpdate(alimTalk);
						}
						
					} else {
						
						// 운영 시간이 아닌 시간으로 넘어왔을 때, 장애 상태라면 초기 상태(비장애)로 되돌림
						if (alimTalk.getStatus().equals("F")) {
							
				    		alimTalk.setStatus("S");
				    		alimTalk.setOpStr1(null);
				    		alimTalk.setOpLgStr1(null);
				    		alimTalk.setOpInt1(null);
				    		alimTalk.setWaitDate(null);
					    	
					    	alimTalk.setCheckDate(now);
					    	orgService.saveOrUpdate(alimTalk);
						}
					}
					
				}
			}

		} catch (Exception e) {
    		logger.error("checkAlimTalkEvents", e);
    		cnt = -1;
    		sendCnt = -1;
		}
		
		
		String ret = "AlimTalk " + cnt + " cases, ";
		ret += " AlimTalkSending " + sendCnt + " cases, ";
		ret += " time: " + (new Date().getTime() - startAt);
		
		return ret;
	}
	
	public void executeThis() {

		/*
		Date day = Util.addDays(Util.removeTimeOfDate(new Date()), -2);
		
		System.out.println("compl...start");
		SolUtil.calcOneDayAdImpression(day,  false);
		System.out.println("compl...");
		*/
		/*
		try {
    		JSONObject infoObj = JSONObject.fromObject(JSONSerializer.toJSON("{ a: 'abc', b: 15 }"));
    		if (infoObj != null) {
    			String a = infoObj.getString("a");
    			int b = infoObj.getInt("b");
    			String c = "";
    			try {
    				c = infoObj.getString("c");
    			} catch (JSONException je) { }
    			
    			System.out.println("a=" + a + ", b=" + b + ", c=" + c);
    		}
    		
		} catch (Exception e) {
			logger.error("rcv - json parsing ------- ", e);
		}
		*/
		
		/*
		long startAt = new Date().getTime();

		
		for(int i = 500; i < 1700; i ++) {
			AdcCreatFile creatFile = adcService.getCreatFile(i);
			if (creatFile == null || Util.isValid(creatFile.getHash())) {
				logger.info("skipped");
				continue;
			}
			
			String pathFile = creatFile.getCtntFolder().getLocalPath() + "/" + creatFile.getCtntFolder().getName() + "/" +
						creatFile.getUuid().toString() + "/" + creatFile.getFilename();
			
			String hash = Util.getFileHashSha256(pathFile);
			if (Util.isValid(hash) && hash.length() == 64) {
				creatFile.setHash(hash);
				adcService.saveOrUpdate(creatFile);
				logger.info("saved");
			}
		}
		
		
		logger.info("time: " + (new Date().getTime() - startAt));
		*/
		
	}
}
