package net.doohad.viewmodels.rev;

import java.util.Date;

import net.doohad.models.adc.AdcAd;

public class RevMedRptAdItem {
	
	private int id;								// 광고 id
	private int campId;							// 캠페인 id

	private String name;						// 광고명
	
	private Date startDate;						// 시작일
	private Date endDate;						// 종료일
	
	private boolean paused;						// 잠시멈춤여부(오늘만) - 광고명sub
	private boolean mobTargeted;				// 모바일타겟여부 - 광고명sub
	private boolean invenTargeted;				// 인벤타겟여부 - 광고명sub
	private boolean timeTargeted;				// 시간타겟여부 - 광고명sub
	
	private String goalType;					// 집행방법
	private String purchType;					// 구매유형
	
	private String impDailyType;				// 일별 광고 분산 정책
	private String impHourlyType;				// 하루 광고 분산 정책
	
	private int priority;						// 구매유형
	private int budget;							// 예산
	private int goalValue;						// 보장
	private int sysValue;						// 목표
	private int dailyScrCap;					// 화면하루한도
	
	private int proposedDailyScrCap = 0;		// 화면하루한도(proposed)
	private int impAddRatio = 0;				// 현재 노출량 추가 제어(오늘만) - 광고명sub
	
	private boolean totalRow;
	private boolean sysValueProposed;			// 목표(proposed)
	
	
	public RevMedRptAdItem() {}
	
	public RevMedRptAdItem(AdcAd ad, boolean isTodayData) {
		if (ad != null) {
			this.id = ad.getId();
			this.campId = ad.getCampaign().getId();
			
			this.name = ad.getName();
			this.startDate = ad.getStartDate();
			this.endDate = ad.getEndDate();
			
			this.purchType = ad.getPurchType();
			this.goalType = ad.getGoalType();
			this.priority = ad.getPriority();
			this.goalValue = ad.getGoalValue();
			this.budget = ad.getBudget();
			this.sysValue = ad.getSysValue();
			this.dailyScrCap = ad.getDailyScrCap();
			
			this.impDailyType = ad.getImpDailyType();
			this.impHourlyType = ad.getImpHourlyType();
			
			this.timeTargeted = ad.isTimeTargeted();
			// invenTargeted는 별도로 설정
			
			if (isTodayData) {
				// 오늘 날짜 자료에만 필요
				this.paused = ad.isPaused();
				this.impAddRatio = ad.getImpAddRatio();
			}
		}
	}

	
	public String getImpAddRatioDisp() {
		
		if (impAddRatio == 0) {
			return "0";
		} else if (impAddRatio > 0) {
			return "+" + String.valueOf(impAddRatio) + "%";
		} else {
			return String.valueOf(impAddRatio) + "%";
		}
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isInvenTargeted() {
		return invenTargeted;
	}

	public void setInvenTargeted(boolean invenTargeted) {
		this.invenTargeted = invenTargeted;
	}

	public boolean isTimeTargeted() {
		return timeTargeted;
	}

	public void setTimeTargeted(boolean timeTargeted) {
		this.timeTargeted = timeTargeted;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
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

	public int getDailyScrCap() {
		return dailyScrCap;
	}

	public void setDailyScrCap(int dailyScrCap) {
		this.dailyScrCap = dailyScrCap;
	}

	public int getProposedDailyScrCap() {
		return proposedDailyScrCap;
	}

	public void setProposedDailyScrCap(int proposedDailyScrCap) {
		this.proposedDailyScrCap = proposedDailyScrCap;
	}

	public int getImpAddRatio() {
		return impAddRatio;
	}

	public void setImpAddRatio(int impAddRatio) {
		this.impAddRatio = impAddRatio;
	}

	public boolean isTotalRow() {
		return totalRow;
	}

	public void setTotalRow(boolean totalRow) {
		this.totalRow = totalRow;
	}

	public boolean isSysValueProposed() {
		return sysValueProposed;
	}

	public void setSysValueProposed(boolean sysValueProposed) {
		this.sysValueProposed = sysValueProposed;
	}

	public int getCampId() {
		return campId;
	}

	public void setCampId(int campId) {
		this.campId = campId;
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

	public boolean isMobTargeted() {
		return mobTargeted;
	}

	public void setMobTargeted(boolean mobTargeted) {
		this.mobTargeted = mobTargeted;
	}

}
