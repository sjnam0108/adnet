package kr.adnetwork.models.knl;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.adc.AdcAdTarget;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.adc.AdcCreatTarget;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.adc.AdcMobTarget;
import kr.adnetwork.models.adc.AdcPlaylist;
import kr.adnetwork.models.adn.AdnExcelRow;
import kr.adnetwork.models.inv.InvScrPack;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.org.OrgAdvertiser;
import kr.adnetwork.models.org.OrgAlimTalk;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.models.org.OrgMediumOpt;
import kr.adnetwork.models.org.OrgRadRegion;
import kr.adnetwork.models.rev.RevAdSelect;
import kr.adnetwork.models.rev.RevCreatDecn;
import kr.adnetwork.models.rev.RevDailyAchv;
import kr.adnetwork.models.rev.RevEventReport;
import kr.adnetwork.models.rev.RevHourlyPlay;
import kr.adnetwork.models.rev.RevInvenRequest;
import kr.adnetwork.models.rev.RevScrHourlyPlay;
import kr.adnetwork.models.rev.RevScrHrlyFailTot;
import kr.adnetwork.models.rev.RevScrHrlyFbTot;
import kr.adnetwork.models.rev.RevScrHrlyNoAdTot;
import kr.adnetwork.models.rev.RevScrHrlyPlyTot;
import kr.adnetwork.models.rev.RevSitHrlyPlyTot;
import kr.adnetwork.utils.Util;

@Entity
@Table(name="KNL_MEDIA")
public class KnlMedium {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEDIUM_ID")
	private int id;
	
	// 매체ID
	@Column(name = "SHORT_NAME", nullable = false, length = 50, unique = true)
	private String shortName;
	
	// 매체명
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 화면 해상도
	//
	//     매체에 포함된 화면의 해상도 크기 목록을 구분자로 구분
	//     화면에 대한 전체 목록에서 선택된 항목으로 제한
	//       ex) 1080x1920|3840x1080
	//
	@Column(name = "RESOLUTIONS", nullable = false, length = 200)
	private String resolutions;
	
	// API 키
	//
	//   매체 화면에서 광고 서버 연결 시 매체의 소속임을 증명할 수 있는 문자열, 총 22 bytes
	//
	@Column(name = "API_KEY", nullable = false, length = 22, unique = true)
	private String apiKey = "";
	
	
	// 기본 재생시간
	@Column(name = "DEFAULT_DUR_SECS", nullable = false)
	private int defaultDurSecs = 15;
	
	// 범위 재생시간 허용
	@Column(name = "RANGE_DUR_ALLOWED", nullable = false)
	private boolean rangeDurAllowed = false;
	
	// 최저 재생시간
	@Column(name = "MIN_DUR_SECS", nullable = false)
	private int minDurSecs = 10;
	
	// 최고 재생시간
	@Column(name = "MAX_DUR_SECS", nullable = false)
	private int maxDurSecs = 20;
	
	
	// 매체 디폴트 운영 시간(24x7=168)
	@Column(name = "BIZ_HOUR", nullable = false, length = 168)
	private String bizHour = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";
	
	
	// 유효시작일
	@Column(name = "EFFECTIVE_START_DATE", nullable = false)
	private Date effectiveStartDate;
	
	// 유효종료일
	@Column(name = "EFFECTIVE_END_DATE")
	private Date effectiveEndDate;
	
	
	// 동기화등급 A (ms 단위)
	@Column(name = "A_GRADE_MS", nullable = false)
	private int aGradeMillis = 120;
	
	// 동기화등급 B (ms 단위)
	@Column(name = "B_GRADE_MS", nullable = false)
	private int bGradeMillis = 250;

	// 동기화등급 C (ms 단위)
	@Column(name = "C_GRADE_MS", nullable = false)
	private int cGradeMillis = 400;

	
	// 운영자 메모
	@Column(name = "MEMO", length = 300)
	private String memo;


	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private int whoLastUpdatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 매체 1주일 운영 시간
	@Transient
	private int bizHours = 0;
	
	
	// 다른 개체 연결(S)
	
	// 하위 개체: 사이트
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvSite> sites = new HashSet<InvSite>(0);
	
	// 하위 개체: 화면
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvScreen> screens = new HashSet<InvScreen>(0);
	
	// 하위 개체: 업로드 임시 저장
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdnExcelRow> excelRows = new HashSet<AdnExcelRow>(0);
	
	// 하위 개체: 광고 소재
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreative> creatives = new HashSet<AdcCreative>(0);
	
	// 하위 개체: 광고 소재 파일
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreatFile> creatFiles = new HashSet<AdcCreatFile>(0);
	
	// 하위 개체: 광고 선택
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevAdSelect> adSelects = new HashSet<RevAdSelect>(0);
	
	// 하위 개체: 캠페인
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCampaign> campaigns = new HashSet<AdcCampaign>(0);
	
	// 하위 개체: 광고
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAd> ads = new HashSet<AdcAd>(0);
	
	// 하위 개체: 광고 방송 소재
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAdCreative> adCreatives = new HashSet<AdcAdCreative>(0);
	
	// 하위 개체: 시간당 화면/광고 재생
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHourlyPlay> scrHourlyPlays = new HashSet<RevScrHourlyPlay>(0);
	
	// 하위 개체: 광고 소재 승인/거절 판단
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevCreatDecn> creatDecns = new HashSet<RevCreatDecn>(0);
	
	// 하위 개체: 광고 소재 인벤토리 타겟팅
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcCreatTarget> creatTargets = new HashSet<AdcCreatTarget>(0);
	
	// 하위 개체: 매체 옵션
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgMediumOpt> mediumOpts = new HashSet<OrgMediumOpt>(0);
	
	// 하위 개체: 광고 인벤토리 타겟팅
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAdTarget> adTargets = new HashSet<AdcAdTarget>(0);
	
	// 하위 개체: 시간당 화면 재생 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyPlyTot> scrHrlyPlyTots = new HashSet<RevScrHrlyPlyTot>(0);
	
	// 하위 개체: 시간당 사이트 재생 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevSitHrlyPlyTot> sitHrlyPlyTots = new HashSet<RevSitHrlyPlyTot>(0);
	
	// 하위 개체: 인벤 요청
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevInvenRequest> invenRequests = new HashSet<RevInvenRequest>(0);
	
	// 하위 개체: 인벤 요청
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgAdvertiser> advertisers = new HashSet<OrgAdvertiser>(0);
	
	// 하위 개체: 시간당 화면 실패 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyFailTot> scrHrlyFailTots = new HashSet<RevScrHrlyFailTot>(0);
	
	// 하위 개체: 시간당 화면 광고없음 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyNoAdTot> scrHrlyNoAdTots = new HashSet<RevScrHrlyNoAdTot>(0);
	
	// 하위 개체: 시간당 화면 대체광고 합계
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevScrHrlyFbTot> scrHrlyFbTots = new HashSet<RevScrHrlyFbTot>(0);
	
	// 하위 개체: 일별/광고별/광고 소재별 하루 재생
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevHourlyPlay> hourlyPlays = new HashSet<RevHourlyPlay>(0);
	
	// 하위 개체: 화면 묶음
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvScrPack> scrPacks = new HashSet<InvScrPack>(0);
	
	// 하위 개체: 동기화 묶음
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvSyncPack> syncPacks = new HashSet<InvSyncPack>(0);
	
	// 하위 개체: 일별 달성 기록
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevDailyAchv> dailyAchves = new HashSet<RevDailyAchv>(0);
	
	// 하위 개체: 재생목록
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcPlaylist> playlists = new HashSet<AdcPlaylist>(0);
	
	// 하위 개체: 채널
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgChannel> channels = new HashSet<OrgChannel>(0);
	
	// 하위 개체: 이벤트 보고
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<RevEventReport> eventReports = new HashSet<RevEventReport>(0);
	
	// 하위 개체: 지도의 원 반경 지역
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgRadRegion> radRegions = new HashSet<OrgRadRegion>(0);
	
	// 하위 개체: 광고 모바일 타겟
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcMobTarget> mobTargets = new HashSet<AdcMobTarget>(0);
	
	// 하위 개체: 알림톡
	@OneToMany(mappedBy = "medium", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgAlimTalk> alimTalks = new HashSet<OrgAlimTalk>(0);
	
	// 다른 개체 연결(E)

	
	public KnlMedium() {}
	
	public KnlMedium(String shortName, String name, String resolutions, String apiKey,
			int defaultDurSecs, boolean rangeDurAllowed, int minDurSecs, int maxDurSecs,
			Date effectiveStartDate, Date effectiveEndDate, String memo, HttpSession session) {
		
		this.shortName = shortName;
		this.name = name;
		this.resolutions = resolutions;
		this.apiKey = apiKey;
		this.defaultDurSecs = defaultDurSecs;
		this.rangeDurAllowed = rangeDurAllowed;
		this.minDurSecs = minDurSecs;
		this.maxDurSecs = maxDurSecs;
		
		this.effectiveStartDate = Util.removeTimeOfDate(effectiveStartDate == null ? new Date() : effectiveStartDate);
		this.effectiveEndDate = Util.setMaxTimeOfDate(effectiveEndDate);
		this.memo = memo;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdatedBy = Util.loginUserId(session);
		this.whoLastUpdateDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(Date effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public Date getEffectiveEndDate() {
		return effectiveEndDate;
	}

	public void setEffectiveEndDate(Date effectiveEndDate) {
		this.effectiveEndDate = effectiveEndDate;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public Date getWhoLastUpdateDate() {
		return whoLastUpdateDate;
	}

	public void setWhoLastUpdateDate(Date whoLastUpdateDate) {
		this.whoLastUpdateDate = whoLastUpdateDate;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdatedBy() {
		return whoLastUpdatedBy;
	}

	public void setWhoLastUpdatedBy(int whoLastUpdatedBy) {
		this.whoLastUpdatedBy = whoLastUpdatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	public String getResolutions() {
		return resolutions;
	}

	public void setResolutions(String resolutions) {
		this.resolutions = resolutions;
	}

	@JsonIgnore
	public Set<InvSite> getSites() {
		return sites;
	}

	public void setSites(Set<InvSite> sites) {
		this.sites = sites;
	}

	@JsonIgnore
	public Set<InvScreen> getScreens() {
		return screens;
	}

	public void setScreens(Set<InvScreen> screens) {
		this.screens = screens;
	}

	public int getDefaultDurSecs() {
		return defaultDurSecs;
	}

	public void setDefaultDurSecs(int defaultDurSecs) {
		this.defaultDurSecs = defaultDurSecs;
	}

	public boolean isRangeDurAllowed() {
		return rangeDurAllowed;
	}

	public void setRangeDurAllowed(boolean rangeDurAllowed) {
		this.rangeDurAllowed = rangeDurAllowed;
	}

	public int getMinDurSecs() {
		return minDurSecs;
	}

	public void setMinDurSecs(int minDurSecs) {
		this.minDurSecs = minDurSecs;
	}

	public int getMaxDurSecs() {
		return maxDurSecs;
	}

	public void setMaxDurSecs(int maxDurSecs) {
		this.maxDurSecs = maxDurSecs;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	@JsonIgnore
	public Set<AdnExcelRow> getExcelRows() {
		return excelRows;
	}

	public void setExcelRows(Set<AdnExcelRow> excelRows) {
		this.excelRows = excelRows;
	}

	@JsonIgnore
	public Set<AdcCreative> getCreatives() {
		return creatives;
	}

	public void setCreatives(Set<AdcCreative> creatives) {
		this.creatives = creatives;
	}

	@JsonIgnore
	public Set<AdcCreatFile> getCreatFiles() {
		return creatFiles;
	}

	public void setCreatFiles(Set<AdcCreatFile> creatFiles) {
		this.creatFiles = creatFiles;
	}

	@JsonIgnore
	public Set<RevAdSelect> getAdSelects() {
		return adSelects;
	}

	public void setAdSelects(Set<RevAdSelect> adSelects) {
		this.adSelects = adSelects;
	}

	@JsonIgnore
	public Set<AdcCampaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(Set<AdcCampaign> campaigns) {
		this.campaigns = campaigns;
	}

	@JsonIgnore
	public Set<AdcAd> getAds() {
		return ads;
	}

	public void setAds(Set<AdcAd> ads) {
		this.ads = ads;
	}

	@JsonIgnore
	public Set<AdcAdCreative> getAdCreatives() {
		return adCreatives;
	}

	public void setAdCreatives(Set<AdcAdCreative> adCreatives) {
		this.adCreatives = adCreatives;
	}

	@JsonIgnore
	public Set<RevScrHourlyPlay> getScrHourlyPlays() {
		return scrHourlyPlays;
	}

	public void setScrHourlyPlays(Set<RevScrHourlyPlay> scrHourlyPlays) {
		this.scrHourlyPlays = scrHourlyPlays;
	}

	@JsonIgnore
	public Set<RevCreatDecn> getCreatDecns() {
		return creatDecns;
	}

	public void setCreatDecns(Set<RevCreatDecn> creatDecns) {
		this.creatDecns = creatDecns;
	}

	@JsonIgnore
	public Set<AdcCreatTarget> getCreatTargets() {
		return creatTargets;
	}

	public void setCreatTargets(Set<AdcCreatTarget> creatTargets) {
		this.creatTargets = creatTargets;
	}

	@JsonIgnore
	public Set<OrgMediumOpt> getMediumOpts() {
		return mediumOpts;
	}

	public void setMediumOpts(Set<OrgMediumOpt> mediumOpts) {
		this.mediumOpts = mediumOpts;
	}

	@JsonIgnore
	public Set<AdcAdTarget> getAdTargets() {
		return adTargets;
	}

	public void setAdTargets(Set<AdcAdTarget> adTargets) {
		this.adTargets = adTargets;
	}

	public String getBizHour() {
		return bizHour;
	}

	public void setBizHour(String bizHour) {
		this.bizHour = bizHour;
	}

	public int getBizHours() {
		return bizHours;
	}

	public void setBizHours(int bizHours) {
		this.bizHours = bizHours;
	}

	@JsonIgnore
	public Set<RevScrHrlyPlyTot> getScrHrlyPlyTots() {
		return scrHrlyPlyTots;
	}

	public void setScrHrlyPlyTots(Set<RevScrHrlyPlyTot> scrHrlyPlyTots) {
		this.scrHrlyPlyTots = scrHrlyPlyTots;
	}

	@JsonIgnore
	public Set<RevSitHrlyPlyTot> getSitHrlyPlyTots() {
		return sitHrlyPlyTots;
	}

	public void setSitHrlyPlyTots(Set<RevSitHrlyPlyTot> sitHrlyPlyTots) {
		this.sitHrlyPlyTots = sitHrlyPlyTots;
	}

	@JsonIgnore
	public Set<RevInvenRequest> getInvenRequests() {
		return invenRequests;
	}

	public void setInvenRequests(Set<RevInvenRequest> invenRequests) {
		this.invenRequests = invenRequests;
	}

	@JsonIgnore
	public Set<OrgAdvertiser> getAdvertisers() {
		return advertisers;
	}

	public void setAdvertisers(Set<OrgAdvertiser> advertisers) {
		this.advertisers = advertisers;
	}

	@JsonIgnore
	public Set<RevScrHrlyFailTot> getScrHrlyFailTots() {
		return scrHrlyFailTots;
	}

	public void setScrHrlyFailTots(Set<RevScrHrlyFailTot> scrHrlyFailTots) {
		this.scrHrlyFailTots = scrHrlyFailTots;
	}

	@JsonIgnore
	public Set<RevScrHrlyNoAdTot> getScrHrlyNoAdTots() {
		return scrHrlyNoAdTots;
	}

	public void setScrHrlyNoAdTots(Set<RevScrHrlyNoAdTot> scrHrlyNoAdTots) {
		this.scrHrlyNoAdTots = scrHrlyNoAdTots;
	}

	@JsonIgnore
	public Set<RevScrHrlyFbTot> getScrHrlyFbTots() {
		return scrHrlyFbTots;
	}

	public void setScrHrlyFbTots(Set<RevScrHrlyFbTot> scrHrlyFbTots) {
		this.scrHrlyFbTots = scrHrlyFbTots;
	}

	@JsonIgnore
	public Set<RevHourlyPlay> getHourlyPlays() {
		return hourlyPlays;
	}

	public void setHourlyPlays(Set<RevHourlyPlay> hourlyPlays) {
		this.hourlyPlays = hourlyPlays;
	}

	@JsonIgnore
	public Set<InvScrPack> getScrPacks() {
		return scrPacks;
	}

	public void setScrPacks(Set<InvScrPack> scrPacks) {
		this.scrPacks = scrPacks;
	}

	@JsonIgnore
	public Set<InvSyncPack> getSyncPacks() {
		return syncPacks;
	}

	public void setSyncPacks(Set<InvSyncPack> syncPacks) {
		this.syncPacks = syncPacks;
	}

	@JsonIgnore
	public Set<RevDailyAchv> getDailyAchves() {
		return dailyAchves;
	}

	public void setDailyAchves(Set<RevDailyAchv> dailyAchves) {
		this.dailyAchves = dailyAchves;
	}

	@JsonIgnore
	public Set<AdcPlaylist> getPlaylists() {
		return playlists;
	}

	public void setPlaylists(Set<AdcPlaylist> playlists) {
		this.playlists = playlists;
	}

	@JsonIgnore
	public Set<RevEventReport> getEventReports() {
		return eventReports;
	}

	public void setEventReports(Set<RevEventReport> eventReports) {
		this.eventReports = eventReports;
	}

	@JsonIgnore
	public Set<OrgRadRegion> getRadRegions() {
		return radRegions;
	}

	public void setRadRegions(Set<OrgRadRegion> radRegions) {
		this.radRegions = radRegions;
	}

	@JsonIgnore
	public Set<AdcMobTarget> getMobTargets() {
		return mobTargets;
	}

	public void setMobTargets(Set<AdcMobTarget> mobTargets) {
		this.mobTargets = mobTargets;
	}

	public int getaGradeMillis() {
		return aGradeMillis;
	}

	public void setaGradeMillis(int aGradeMillis) {
		this.aGradeMillis = aGradeMillis;
	}

	public int getbGradeMillis() {
		return bGradeMillis;
	}

	public void setbGradeMillis(int bGradeMillis) {
		this.bGradeMillis = bGradeMillis;
	}

	public int getcGradeMillis() {
		return cGradeMillis;
	}

	public void setcGradeMillis(int cGradeMillis) {
		this.cGradeMillis = cGradeMillis;
	}

	@JsonIgnore
	public Set<OrgChannel> getChannels() {
		return channels;
	}

	public void setChannels(Set<OrgChannel> channels) {
		this.channels = channels;
	}

	@JsonIgnore
	public Set<OrgAlimTalk> getAlimTalks() {
		return alimTalks;
	}

	public void setAlimTalks(Set<OrgAlimTalk> alimTalks) {
		this.alimTalks = alimTalks;
	}

}
