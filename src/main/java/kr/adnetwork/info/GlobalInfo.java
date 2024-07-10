package kr.adnetwork.info;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.viewmodels.fnd.FndViewTypeItem;
import kr.adnetwork.viewmodels.inv.InvSyncPackCompactItem;
import kr.adnetwork.viewmodels.knl.KnlMediumCompactItem;
import kr.adnetwork.viewmodels.rev.RevObjEventTimeItem;
import kr.adnetwork.viewmodels.rev.RevScrWorkTimeItem;
import kr.adnetwork.viewmodels.rev.RevSyncPackMinMaxItem;
import kr.adnetwork.viewmodels.sys.SysObjEventTimeCalcItem;

public class GlobalInfo {
	
	public static String AppId = "adnetwork";
	public static KeyPair RSAKeyPair = null;
	public static String RSAKeyMod = "";
	
	public static Date ServerStartDt = new Date();
	
	
	// 파일 API의 정상 동작 상태 플래그
	public static boolean FileApiReady = false;
	
	
	// API 테스트 서버 URL
	//
	//   대표 서비스 URL이 됨
	//
	public static String ApiTestServer = "https://ad.doohad.net";
	
	// 재생 결과 보고 서버 URL
	//
	//   집계 전용의 서버로 분리될 수도 있음
	//
	public static String ReportServer = "https://ad.doohad.net";
	
	
	// 유효 시간 있는 변수 맵
	public static HashMap<String, String> AutoExpVarMap = new HashMap<String, String>();
	
	// 유효 시간 있는 변수의 유효 시간 맵
	public static HashMap<String, Date> AutoExpVarTimeMap = new HashMap<String, Date>();
	
	
	// 백그라운드 시퀀스 제어
	public static HashMap<String, Integer> BgMaxValueMap = new HashMap<String, Integer>();
	public static HashMap<String, Integer> BgCurrValueMap = new HashMap<String, Integer>();
	
	
	
	// 현재 광고 API에서의 기본 광고/광고 소재 리스트 백그라운드 생성
	public static HashMap<String, String> AdCandiAdCreatVerKey = new HashMap<String, String>();

	// 현재 광고 API에서의 광고/광고 소재 순서 리스트 문자열 백그라운드 생성
	public static HashMap<String, String> AdRealAdCreatIdsMap = new HashMap<String, String>();
	public static HashMap<String, HashMap<String, AdcAdCreative>> AdRealAdCreatMap = new HashMap<String, HashMap<String, AdcAdCreative>>();
	
	
	// 파일 목록 API에서의 기본 광고/광고 소재 파일 리스트 백그라운드 생성
	public static HashMap<String, String> FileCandiCreatFileVerKey = new HashMap<String, String>();
	public static HashMap<String, ArrayList<AdcCreatFile>> FileCandiCreatFileMap = new HashMap<String, ArrayList<AdcCreatFile>>();
	
	
	// 광고 소재 인벤 타겟팅 대상의 화면 Id를 미리 백그라운드 생성
	public static HashMap<String, String> TgtScreenIdVerKey = new HashMap<String, String>();
	public static HashMap<String, List<Integer>> TgtScreenIdMap = new HashMap<String, List<Integer>>();
	
	
	// 광고 모바일 타겟팅 목록을 미리 백그라운드 생성
	public static HashMap<String, String> MobTgtItemVerKey = new HashMap<String, String>();
	public static HashMap<String, String> MobTgtItemMap = new HashMap<String, String>();
	
	
	// 매체 화면의 일한 시간 메모리 보관용
	public static ArrayList<RevScrWorkTimeItem> ScrWorkTimeItemList = new ArrayList<RevScrWorkTimeItem>();
	
	// 개체의 이벤트 작업 메모리 보관용(일단 단순화된 보관용으로 옮기고, 비동기로 집계로 넘김)
	public static ArrayList<RevObjEventTimeItem> ObjEventTimeItemList = new ArrayList<RevObjEventTimeItem>();
	
	// 개체의 이벤트 작업 메모리 집계용
	public static HashMap<String, SysObjEventTimeCalcItem> ObjTouchMap = new HashMap<String, SysObjEventTimeCalcItem>();
	
	
	//
	// Global Map
	//
	// Global Map - 간략화된 매체 정보(key는 shortName)
	public static HashMap<String, KnlMediumCompactItem> MediaMap = new HashMap<String, KnlMediumCompactItem>();
	
	// Global Map - 간략화된 매체 정보(key는 apiKey)
	public static HashMap<String, KnlMediumCompactItem> ApiKeyMediaMap = new HashMap<String, KnlMediumCompactItem>();
	
	// Global Map - 간략화된 동기화 묶음 정보(key는 spShortName)
	public static HashMap<String, InvSyncPackCompactItem> SyncPackMap = new HashMap<String, InvSyncPackCompactItem>();

	// Global Map - 광고 노출을 기록하지 말아야 하는 화면 id(동기화 묶음 화면에서 1번 기기 제외 나머지)
	public static List<Integer> LogProhibitedScreenIds = new ArrayList<Integer>();
	
	// 자율 광고선택 시의 최근 광고 소재 정보
	// - 광고 채널의 key = 광고 채널ID
	// - 동기화 묶음의 key = 동기화 묶음ID
	// - 화면의 key = I + 화면id
	public static HashMap<String, Integer> LastSelCreatMap = new HashMap<String, Integer>();
	
	
	// Global Map - 화면 및 사이트의 상태
	public static HashMap<String, String> InvenLastStatusMap = new HashMap<String, String>();
	
	// Global Map - 게시 유형 지정에 따른 레인별 소재 번호
	public static HashMap<String, String> LaneCreatMap = new HashMap<String, String>();
	
	
	// Global Map - 동기화 화면 시작 시간 보고
	public static HashMap<String, RevSyncPackMinMaxItem> SyncPackReportGlobalMap = new HashMap<String, RevSyncPackMinMaxItem>();
	
	// Global Map - 동기화 화면 노출 등급
	public static HashMap<String, String> SyncPackImpGradeMap = new HashMap<String, String>();
	
	// Global Map - 동기화 화면 노출 기기수
	public static HashMap<String, String> SyncPackImpCntMap = new HashMap<String, String>();

	
	// Global Map - 게시 유형 속성
	public static HashMap<String, FndViewTypeItem> ViewTypeGlobalMap = new HashMap<String, FndViewTypeItem>();
	
	
	// 동기화 화면에서 가장 최근 CMPL 이후, EXP 카운팅(key = 동기화 묶음 ID)
	public static HashMap<String, Integer> SyncPackExpCountMap = new HashMap<String, Integer>();
	
	// 동기화 화면에서 가장 마지막 보고 시간(key = 동기화 묶음 ID)
	public static HashMap<String, Date> SyncPackReportTimeMap = new HashMap<String, Date>();
	
	// 동기화 화면에서 자동으로 리셋이 걸리는 숫자 패턴
	public static List<String> SyncPackProhibitedPatterns = new ArrayList<String>();
	
	//-
	
}
