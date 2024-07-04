package net.doohad.models.rev;

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

import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.knl.KnlMedium;

@Entity
@Table(name="REV_DAILY_ACHVES", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"TYPE", "OBJ_ID", "PLAY_DATE"}),
})
public class RevDailyAchv {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "DAILY_ACHV_ID")
	private int id;
	

	// 유형
	//
	//    C - 캠페인
	//    A - 광고
	//
	@Column(name = "TYPE", length = 1, nullable = false)
	private String type;

	// 개체(캠페인 또는 광고) id
	@Column(name = "OBJ_ID", nullable = false)
	private int objId;
	
	// 방송 일시
	@Column(name = "PLAY_DATE", nullable = false)
	private Date playDate;
	

	// 집행 방법(목표 구분)
	//
	//   A		광고예산(AD spend)
	//   I		노출량(Impressions)
	//
	@Column(name = "GOAL_TYPE", nullable = false, length = 1)
	private String goalType;
	
	// 하루 목표
	@Column(name = "TGT_TODAY", nullable = false)
	private int tgtToday = 0;

	// 집행 노출량
	@Column(name = "ACTUAL_VALUE", nullable = false)
	private int actualValue = 0;
	
	// 집행 금액
	@Column(name = "ACTUAL_AMOUNT", nullable = false)
	private int actualAmount = 0;
	
	// 달성률
	//
	//   %값으로, 보통은 100에 수렴함. 집행정책에 따라 그 대상의 항목이 다름.
	//
	//   if 집행정책 == 노출량, 집행 노출량 / 하루 목표
	//   else if 집행정책 == 광고예산, 집행 금액 / 하루 목표
	//   else 달성률 == 0
	//     
	@Column(name = "ACHV_RATIO", nullable = false)
	private double achvRatio = 0;


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
	
	// 다른 개체 연결(E)

	
	public RevDailyAchv() {}
	
	public RevDailyAchv(AdcAd ad, Date playDate) {
		
		this.medium = ad.getMedium();
		
		this.type = "A";
		this.objId = ad.getId();
		this.playDate = playDate;
		
		this.goalType = ad.getGoalType();
		this.tgtToday = ad.getTgtToday();
		
		
		Date now = new Date();
		this.whoCreationDate = now;
		this.whoLastUpdateDate = now;
	}
	
	public RevDailyAchv(AdcCampaign camp, Date playDate) {
		
		this.medium = camp.getMedium();
		
		this.type = "C";
		this.objId = camp.getId();
		this.playDate = playDate;
		
		this.goalType = camp.getGoalType();
		this.tgtToday = camp.getTgtToday();
		
		
		Date now = new Date();
		this.whoCreationDate = now;
		this.whoLastUpdateDate = now;
	}

	public void touch() {
		this.whoLastUpdateDate = new Date();
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public Date getPlayDate() {
		return playDate;
	}

	public void setPlayDate(Date playDate) {
		this.playDate = playDate;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public int getTgtToday() {
		return tgtToday;
	}

	public void setTgtToday(int tgtToday) {
		this.tgtToday = tgtToday;
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

	public double getAchvRatio() {
		return achvRatio;
	}

	public void setAchvRatio(double achvRatio) {
		this.achvRatio = achvRatio;
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
	
}
