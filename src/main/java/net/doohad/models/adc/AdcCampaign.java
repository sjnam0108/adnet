package net.doohad.models.adc;

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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAdvertiser;
import net.doohad.utils.Util;

@Entity
@Table(name="ADC_CAMPAIGNS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class AdcCampaign {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CAMPAIGN_ID")
	private int id;
	
	// 캠페인명
	@Column(name = "NAME", nullable = false, length = 220)
	private String name;
	
	// 최근 상태
	//  
	//   가능한 상태로는,
	//
	//     - U		시작전		Upcoming
	//     - R		진행		Running
	//     - C		완료		Completed
	//
	//     - V      보관		Archived(변경 불가, 관계 설정 불가)
	//     - T      삭제		Deleted
	//
	//   광고에 의해 설정된 시작 종료일에 따라, 혹은 날짜 변경에 따라 U R C 상태 자동 변경 필요
	//
	//   보관 해제시에는 하위의 광고 시작/종료일에 따라 U R C 설정
	//
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status = "U";
	
	// 삭제 여부
	//
	//   소프트 삭제 플래그
	//
	//   삭제 요청 시 아래 값처럼 변경하고 실제 삭제는 진행하지 않음
	//
	//        name = name + '_yyyyMMdd_HHmm'
	//
	@Column(name = "DELETED", nullable = false)
	private boolean deleted; 
	
	// 동일 광고주 광고 송출 금지 시간
	//
	//  동일 캠페인의 광고가 방송되지 않도록 유지될 수 있는 초단위 시간
	//
	@Column(name = "FREQ_CAP", nullable = false)
	private int freqCap = 0;
	
	
	// 예산 및 노출량 관련
	//
	
	// 예산
	@Column(name = "BUDGET", nullable = false)
	private int budget = 0;
	
	// 보장 노출량
	// 
	//   계약된, 혹은 공식적으로 보장되는 노출량.
	//   이 값이 "예산"값보다 우선 순위가 높으며, 이 값이 입력되면(0초과) 무조건 노출량 기반으로
	//   이 광고는 진행된다.
	// 
	@Column(name = "GOAL_VALUE", nullable = false)
	private int goalValue = 0;
	
	// 목표 노출량
	//
	//   솔루션(시스템)에서 우선적으로 노출량의 목표로 설정되는 값
	//
	@Column(name = "SYS_VALUE", nullable = false)
	private int sysValue = 0;
	
	// 집행 노출량
	//
	//   목표로 설정한 노출량(목표 혹은 보장)에 대한 실제 진행된 노출량
	//   목표 및 보장 노출량이 0이라도, 이 값은 0 초과의 실제 값을 가지게 됨
	//
	@Column(name = "ACTUAL_VALUE", nullable = false)
	private int actualValue = 0;
	
	// 집행 금액
	//
	//   예산이 입력되지 않았다고 하더라도, 화면 혹은 광고의 CPM 설정에 의해 이 금액을 계산하는 것이 가능
	//
	@Column(name = "ACTUAL_AMOUNT", nullable = false)
	private int actualAmount = 0;
	
	// 집행 CPM
	//
	//   집행 금액과 집행 노출량을 통한 계산값
	//
	@Column(name = "ACTUAL_CPM", nullable = false)
	private int actualCpm = 0;
	
	// 달성률
	//
	//   %값으로, 보통은 100에 수렴함. 집행정책에 따라 그 대상의 항목이 다름.
	//
	//   if 집행정책 == 노출량, 
	//      if 보장 노출량 == 0, 달성률 == 0
	//      else 집행 노출량 / 보장 노출량
	//   else if 집행정책 == 광고예산, 집행 금액 / 예산
	//   else 달성률 == 0
	//     
	@Column(name = "ACHV_RATIO", nullable = false)
	private double achvRatio = 0;

	// 집행 방법
	//
	//   자체 목표 관리 상태와 상관없이 외부 요인에 의해 이 값이 달라짐
	//
	//   if "자체 목표 관리"
	//   	if "목표 노출량" > 0 || "보장 노출량" > 0  --> 노출량(I)
	//   	else if "예산" > 0      --> 광고예산(A)
	//   	else                    --> 무제한 노출(U). 이 값은 정책상 존재할 수 없음
	//   else
	//      if 포함된 광고의 수 == 0				--> 무제한 노출(U)
	//      else if 포함된 광고의 집행 방법이 하나	--> 그 값(A/I/U)
	//   	else  									--> 여러 방법(M)
	//
	//
	//   A		광고예산(AD spend)
	//   I		노출량(Impressions)
	//   U		무제한 노출(Unlimited budget)
	//   M      여러 방법(Multiple) - 캠페인 자체 목표 관리가 아닐 경우에, 포함된 광고의 집행 방법이 2개 이상
	//
	//
	@Column(name = "GOAL_TYPE", nullable = false, length = 1)
	private String goalType = "U";

	// 자체 목표 관리
	//
	//   캠페인에서 예산/노출량 설정
	//
	//   값			예산	보장	목표	노출량/집행금액/집행CPM/달성률		집행방법
	//   ---------------------------------------------------------------------------------------
	//   if true	변경가	변경가	변경가	bg로 업데이트						변경시에 설정(bg에서 제외)
	//   if false	3항목불가(bg로 sum)		bg로 업데이트(위와 동일)			bg로 업데이트
	//   
	@Column(name = "SELF_MANAGED", nullable = false)
	private boolean selfManaged;

	
	// 오늘 목표
	//
	//   매체에 포함된 전체 화면에서의 목표 합(양 또는 금액). 백그라운드 계산.
	//
	//   값 설정 조건
	//   - 상태: 진행(R)
	//   - 구매 유형: 목표 보장(G), 목표 비보장(N)
	//   - 이외의 상황: tgtToday = 0
	//
	//   설정 정책: 일단 linear(시작일부터 종료일까지 균등)로 접근. 이후 여러 유형 선택 가능하게
	//   - 기준 시점은 오늘 현재가 아닌 오늘 0시 기준(집행량, 집행금액 등은 모두 어제까지의 합)
	//   - 잔여일수에 오늘 포함
	//     if 집행정책 == 노출량, (목표or보장 노출량 - 집행 노출량) / (종료일까지 잔여일수)
	//     else if 집행정책 == 광고예산, (예산 - 집행 금액) / (종료일까지 잔여일수)
	//     else (집행정책 == 무제한 노출) 오늘 목표 == 0 
	//
	//   백그라운드 계산 시점
	//   - 매일 0시 즈음
	//   - WAS 구동 시
	//
	@Column(name = "TGT_TODAY", nullable = false)
	private int tgtToday = 0;
	
	
	// 일별 광고 분산 정책
	//
	//     - E		모든 날짜 균등
	//     - W		통계 기반 요일별 차등
	//
	@Column(name = "IMP_DAILY_TYPE", nullable = false, length = 1)
	private String impDailyType = "E";
	
	// 하루 광고 분산 정책
	// 
	//   이 값은 포함된 광고의 해당 항목(하루 광고 분산 정책)이 적용되며,
	//   광고와의 설정 일관성을 위해 항목은 남겨둔다.
	//
	//     - E		모든 시간 균등
	//     - D		일과 시간 집중
	//     - A		포함된 광고 설정에 따름
	//
	@Column(name = "IMP_HOURLY_TYPE", nullable = false, length = 1)
	private String impHourlyType = "A";
	
	//
	// / 예산 및 노출량 관련

	
	// 대행사
	@Column(name = "AD_AGENCY", nullable = false, length = 50)
	private String adAgency = "";

	// 미디어 렙사
	@Column(name = "MEDIA_REP", nullable = false, length = 50)
	private String mediaRep = "";

	
	// 캠페인 시작일과 종료일
	//
	//   시작일과 종료일은 표시 목적이며, 사용자의 입력을 필요로 하지 않는다.
	//   하위에 속하는 광고"들"의 시작/종료일에 따라 코드에 의해 설정된다
	//
	// 시작일
	@Column(name = "START_DATE")
	private Date startDate;
	
	// 종료일
	@Column(name = "END_DATE")
	private Date endDate;
	
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
	
	
	// 광고 수
	@Transient
	private int adCount = 0;

	// 광고의 상태 카드 문자열(옐로, 레드 카드 처리를 위한)
	@Transient
	private String statusCard = "";
	
	// 자동 설정된 목표횟수
	@Transient
	private int proposedSysValue = 0;
	
	// 오늘 목표(하루 목표)
	@Transient
	private String tgtTodayDisp = "";
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 광고주
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ADVERTISER_ID", nullable = false)
	private OrgAdvertiser advertiser;
	
	// 하위 개체: 광고
	@OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<AdcAd> ads = new HashSet<AdcAd>(0);
	
	// 다른 개체 연결(E)

	
	public AdcCampaign() {}
	
	public AdcCampaign(OrgAdvertiser advertiser, String name, int freqCap, 
			String memo, HttpSession session) {
		
		this.medium = advertiser.getMedium();
		this.advertiser = advertiser;
		
		this.name = name;
		this.freqCap = freqCap;
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

	
	public String getDispPeriod() {
		if (startDate == null || endDate == null) {
			return "-";
		} else if (Util.isSameDate(startDate, endDate)) {
			if (Util.isThisYear(startDate)) {
				return "<small>" + Util.toSimpleString(startDate, "yyyy") + "</small> " + Util.toSimpleString(startDate, "M/d");
			} else {
				return Util.toSimpleString(startDate, "yyyy M/d");
			}
		} else if (Util.isSameYear(startDate, endDate)) {
			if (Util.isThisYear(startDate)) {
				return "<small>" + Util.toSimpleString(startDate, "yyyy") + "</small> " + Util.toSimpleString(startDate, "M/d") + 
						" ~ " + Util.toSimpleString(endDate, "M/d");
			} else {
				return Util.toSimpleString(startDate, "yyyy M/d") + 
						" ~ " + Util.toSimpleString(endDate, "M/d");
			}
		} else {
			return Util.toSimpleString(startDate, "yyyy M/d") + 
					" ~ " + Util.toSimpleString(endDate, "yyyy M/d");
		}
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getFreqCap() {
		return freqCap;
	}

	public void setFreqCap(int freqCap) {
		this.freqCap = freqCap;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
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

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public OrgAdvertiser getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(OrgAdvertiser advertiser) {
		this.advertiser = advertiser;
	}

	@JsonIgnore
	public Set<AdcAd> getAds() {
		return ads;
	}

	public void setAds(Set<AdcAd> ads) {
		this.ads = ads;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getAdCount() {
		return adCount;
	}

	public void setAdCount(int adCount) {
		this.adCount = adCount;
	}

	public String getStatusCard() {
		return statusCard;
	}

	public void setStatusCard(String statusCard) {
		this.statusCard = statusCard;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public int getGoalValue() {
		return goalValue;
	}

	public void setGoalValue(int goalValue) {
		this.goalValue = goalValue;
	}

	public int getSysValue() {
		return sysValue;
	}

	public void setSysValue(int sysValue) {
		this.sysValue = sysValue;
	}

	public int getActualValue() {
		return actualValue;
	}

	public void setActualValue(int actualValue) {
		this.actualValue = actualValue;
	}

	public int getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(int actualAmount) {
		this.actualAmount = actualAmount;
	}

	public int getActualCpm() {
		return actualCpm;
	}

	public void setActualCpm(int actualCpm) {
		this.actualCpm = actualCpm;
	}

	public double getAchvRatio() {
		return achvRatio;
	}

	public void setAchvRatio(double achvRatio) {
		this.achvRatio = achvRatio;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public boolean isSelfManaged() {
		return selfManaged;
	}

	public void setSelfManaged(boolean selfManaged) {
		this.selfManaged = selfManaged;
	}

	public int getProposedSysValue() {
		return proposedSysValue;
	}

	public void setProposedSysValue(int proposedSysValue) {
		this.proposedSysValue = proposedSysValue;
	}

	public int getTgtToday() {
		return tgtToday;
	}

	public void setTgtToday(int tgtToday) {
		this.tgtToday = tgtToday;
	}

	public String getImpDailyType() {
		return impDailyType;
	}

	public void setImpDailyType(String impDailyType) {
		this.impDailyType = impDailyType;
	}

	public String getImpHourlyType() {
		return impHourlyType;
	}

	public void setImpHourlyType(String impHourlyType) {
		this.impHourlyType = impHourlyType;
	}

	public String getTgtTodayDisp() {
		return tgtTodayDisp;
	}

	public void setTgtTodayDisp(String tgtTodayDisp) {
		this.tgtTodayDisp = tgtTodayDisp;
	}

	public String getAdAgency() {
		return adAgency;
	}

	public void setAdAgency(String adAgency) {
		this.adAgency = adAgency;
	}

	public String getMediaRep() {
		return mediaRep;
	}

	public void setMediaRep(String mediaRep) {
		this.mediaRep = mediaRep;
	}
}
