package kr.adnetwork.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.knl.KnlMedium;

@Entity
@Table(name="REV_SCR_HOURLY_PLAYS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"PLAY_DATE", "AD_CREATIVE_ID", "SCREEN_ID"}),
})
public class RevScrHourlyPlay {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SCR_HOURLY_PLAY_ID")
	private int id;
	
	//
	// 시간당 화면/광고 재생
	//
	
	// 방송 일시
	@Column(name = "PLAY_DATE", nullable = false)
	private Date playDate;
	
	
	// 시간별 성공 카운트
	@Column(name = "CNT_00", nullable = false)
	private int cnt00 = 0;

	@Column(name = "CNT_01", nullable = false)
	private int cnt01 = 0;

	@Column(name = "CNT_02", nullable = false)
	private int cnt02 = 0;

	@Column(name = "CNT_03", nullable = false)
	private int cnt03 = 0;

	@Column(name = "CNT_04", nullable = false)
	private int cnt04 = 0;

	@Column(name = "CNT_05", nullable = false)
	private int cnt05 = 0;

	@Column(name = "CNT_06", nullable = false)
	private int cnt06 = 0;

	@Column(name = "CNT_07", nullable = false)
	private int cnt07 = 0;

	@Column(name = "CNT_08", nullable = false)
	private int cnt08 = 0;

	@Column(name = "CNT_09", nullable = false)
	private int cnt09 = 0;

	@Column(name = "CNT_10", nullable = false)
	private int cnt10 = 0;

	@Column(name = "CNT_11", nullable = false)
	private int cnt11 = 0;

	@Column(name = "CNT_12", nullable = false)
	private int cnt12 = 0;

	@Column(name = "CNT_13", nullable = false)
	private int cnt13 = 0;

	@Column(name = "CNT_14", nullable = false)
	private int cnt14 = 0;

	@Column(name = "CNT_15", nullable = false)
	private int cnt15 = 0;

	@Column(name = "CNT_16", nullable = false)
	private int cnt16 = 0;

	@Column(name = "CNT_17", nullable = false)
	private int cnt17 = 0;

	@Column(name = "CNT_18", nullable = false)
	private int cnt18 = 0;

	@Column(name = "CNT_19", nullable = false)
	private int cnt19 = 0;

	@Column(name = "CNT_20", nullable = false)
	private int cnt20 = 0;

	@Column(name = "CNT_21", nullable = false)
	private int cnt21 = 0;

	@Column(name = "CNT_22", nullable = false)
	private int cnt22 = 0;

	@Column(name = "CNT_23", nullable = false)
	private int cnt23 = 0;
	
	
	// 하루 성공 카운트 합계
	@Column(name = "SUCC_TOT", nullable = false)
	private int succTotal = 0;
	
	// 하루 실패 카운트 합계
	@Column(name = "FAIL_TOT", nullable = false)
	private int failTotal = 0;

	
	// 하루 카운트 합계
	@Column(name = "DATE_TOT", nullable = false)
	private int dateTotal = 0;
	
	
	// 현재 시간(시간 기반) 송출 목표량
	@Column(name = "CURR_HOUR_GOAL")
	private Integer currHourGoal;
	
	// 화면 최저 CPM
	@Column(name = "FLOOR_CPM", nullable = false)
	private int floorCpm = 0;
	
	// 광고 CPM
	@Column(name = "AD_CPM", nullable = false)
	private int adCpm = 0;
	

	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 광고
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AD_ID", nullable = false)
	private AdcAd ad;
	
	// 상위 개체: 광고/광고 소재
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AD_CREATIVE_ID", nullable = false)
	private AdcAdCreative adCreative;
	
	// 상위 개체: 화면
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCREEN_ID", nullable = false)
	private InvScreen screen;
	
	// 다른 개체 연결(E)

	
	public RevScrHourlyPlay() {}
	
	public RevScrHourlyPlay(InvScreen screen, AdcAdCreative adCreative, Date playDate) {
		
		this.medium = screen.getMedium();
		this.screen = screen;
		this.ad = adCreative.getAd();
		this.adCreative = adCreative;
		this.playDate = playDate;
		
		this.floorCpm = screen.getFloorCpm();
		if (adCreative != null && adCreative.getAd() != null) {
			if (adCreative.getAd().getGoalType().equals("I") && adCreative.getAd().getBudget() > 0 
					&& adCreative.getAd().getGoalValue() > 0) {
				this.adCpm = (int)((long)adCreative.getAd().getBudget() * 1000l / (long)adCreative.getAd().getGoalValue());
			} else {
				this.adCpm = adCreative.getAd().getCpm();
			}
		}
		
		this.whoCreationDate = new Date();

		touchWho();
	}
	
	public void touchWho() {
		this.whoLastUpdateDate = new Date();
	}

	
	public void calcTotal() {
		this.succTotal = cnt00 + cnt01 + cnt02 + cnt03 + cnt04 + cnt05 + cnt06 + cnt07 + cnt08 + cnt09 + cnt10 + cnt11 + cnt12 +
				cnt13 + cnt14 + cnt15 + cnt16 + cnt17 + cnt18 + cnt19 + cnt20 + cnt21 + cnt22 + cnt23;
		this.dateTotal = this.succTotal + this.failTotal;
	}
	
	public int getActualAmount() {
		
		return (adCpm == 0 ? floorCpm : adCpm) * succTotal / 1000;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getPlayDate() {
		return playDate;
	}

	public void setPlayDate(Date playDate) {
		this.playDate = playDate;
	}

	public int getCnt00() {
		return cnt00;
	}

	public void setCnt00(int cnt00) {
		this.cnt00 = cnt00;
	}

	public int getCnt01() {
		return cnt01;
	}

	public void setCnt01(int cnt01) {
		this.cnt01 = cnt01;
	}

	public int getCnt02() {
		return cnt02;
	}

	public void setCnt02(int cnt02) {
		this.cnt02 = cnt02;
	}

	public int getCnt03() {
		return cnt03;
	}

	public void setCnt03(int cnt03) {
		this.cnt03 = cnt03;
	}

	public int getCnt04() {
		return cnt04;
	}

	public void setCnt04(int cnt04) {
		this.cnt04 = cnt04;
	}

	public int getCnt05() {
		return cnt05;
	}

	public void setCnt05(int cnt05) {
		this.cnt05 = cnt05;
	}

	public int getCnt06() {
		return cnt06;
	}

	public void setCnt06(int cnt06) {
		this.cnt06 = cnt06;
	}

	public int getCnt07() {
		return cnt07;
	}

	public void setCnt07(int cnt07) {
		this.cnt07 = cnt07;
	}

	public int getCnt08() {
		return cnt08;
	}

	public void setCnt08(int cnt08) {
		this.cnt08 = cnt08;
	}

	public int getCnt09() {
		return cnt09;
	}

	public void setCnt09(int cnt09) {
		this.cnt09 = cnt09;
	}

	public int getCnt10() {
		return cnt10;
	}

	public void setCnt10(int cnt10) {
		this.cnt10 = cnt10;
	}

	public int getCnt11() {
		return cnt11;
	}

	public void setCnt11(int cnt11) {
		this.cnt11 = cnt11;
	}

	public int getCnt12() {
		return cnt12;
	}

	public void setCnt12(int cnt12) {
		this.cnt12 = cnt12;
	}

	public int getCnt13() {
		return cnt13;
	}

	public void setCnt13(int cnt13) {
		this.cnt13 = cnt13;
	}

	public int getCnt14() {
		return cnt14;
	}

	public void setCnt14(int cnt14) {
		this.cnt14 = cnt14;
	}

	public int getCnt15() {
		return cnt15;
	}

	public void setCnt15(int cnt15) {
		this.cnt15 = cnt15;
	}

	public int getCnt16() {
		return cnt16;
	}

	public void setCnt16(int cnt16) {
		this.cnt16 = cnt16;
	}

	public int getCnt17() {
		return cnt17;
	}

	public void setCnt17(int cnt17) {
		this.cnt17 = cnt17;
	}

	public int getCnt18() {
		return cnt18;
	}

	public void setCnt18(int cnt18) {
		this.cnt18 = cnt18;
	}

	public int getCnt19() {
		return cnt19;
	}

	public void setCnt19(int cnt19) {
		this.cnt19 = cnt19;
	}

	public int getCnt20() {
		return cnt20;
	}

	public void setCnt20(int cnt20) {
		this.cnt20 = cnt20;
	}

	public int getCnt21() {
		return cnt21;
	}

	public void setCnt21(int cnt21) {
		this.cnt21 = cnt21;
	}

	public int getCnt22() {
		return cnt22;
	}

	public void setCnt22(int cnt22) {
		this.cnt22 = cnt22;
	}

	public int getCnt23() {
		return cnt23;
	}

	public void setCnt23(int cnt23) {
		this.cnt23 = cnt23;
	}

	public int getSuccTotal() {
		return succTotal;
	}

	public void setSuccTotal(int succTotal) {
		this.succTotal = succTotal;
	}

	public int getFailTotal() {
		return failTotal;
	}

	public void setFailTotal(int failTotal) {
		this.failTotal = failTotal;
	}

	public int getDateTotal() {
		return dateTotal;
	}

	public void setDateTotal(int dateTotal) {
		this.dateTotal = dateTotal;
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

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public AdcAd getAd() {
		return ad;
	}

	public void setAd(AdcAd ad) {
		this.ad = ad;
	}

	public AdcAdCreative getAdCreative() {
		return adCreative;
	}

	public void setAdCreative(AdcAdCreative adCreative) {
		this.adCreative = adCreative;
	}

	public InvScreen getScreen() {
		return screen;
	}

	public void setScreen(InvScreen screen) {
		this.screen = screen;
	}

	public Integer getCurrHourGoal() {
		return currHourGoal;
	}

	public void setCurrHourGoal(Integer currHourGoal) {
		this.currHourGoal = currHourGoal;
	}

	public int getFloorCpm() {
		return floorCpm;
	}

	public void setFloorCpm(int floorCpm) {
		this.floorCpm = floorCpm;
	}

	public int getAdCpm() {
		return adCpm;
	}

	public void setAdCpm(int adCpm) {
		this.adCpm = adCpm;
	}

}
