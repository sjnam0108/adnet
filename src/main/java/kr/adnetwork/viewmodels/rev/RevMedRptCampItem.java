package kr.adnetwork.viewmodels.rev;

import java.util.Date;

import kr.adnetwork.models.adc.AdcCampaign;

public class RevMedRptCampItem {
	
	private int id;								// 캠페인 id

	private String name;						// 켐페인명
	
	private Date startDate;						// 시작일
	private Date endDate;						// 종료일
	
	private String advertiserName = "";			// 광고주
	private String statusCard;					// 상태 카드(오늘만) - 캠페인명sub
	
	private int budget;							// 예산
	private int goalValue;						// 보장
	private int sysValue;						// 목표

	private String goalType;					// 집행 방법(내부 처리 목적)
	
	private boolean selfManaged;				// 자체 목표 관리
	private boolean totalRow;
	private boolean sysValueProposed;			// 목표(proposed)
	
	
	public RevMedRptCampItem() {}
	
	public RevMedRptCampItem(AdcCampaign camp) {
		if (camp != null) {
			this.id = camp.getId();
			
			this.name = camp.getName();
			this.advertiserName = camp.getAdvertiser().getName();
			
			this.startDate = camp.getStartDate();
			this.endDate = camp.getEndDate();
			
			this.budget = camp.getBudget();
			this.goalValue = camp.getGoalValue();
			this.sysValue = camp.getSysValue();
			
			this.goalType = camp.getGoalType();
			
			this.selfManaged = camp.isSelfManaged();
			
			// statusCard는 별도로 설정
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

	public String getStatusCard() {
		return statusCard;
	}

	public void setStatusCard(String statusCard) {
		this.statusCard = statusCard;
	}

	public boolean isTotalRow() {
		return totalRow;
	}

	public void setTotalRow(boolean totalRow) {
		this.totalRow = totalRow;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public void setAdvertiserName(String advertiserName) {
		this.advertiserName = advertiserName;
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

	public boolean isSelfManaged() {
		return selfManaged;
	}

	public void setSelfManaged(boolean selfManaged) {
		this.selfManaged = selfManaged;
	}

	public boolean isSysValueProposed() {
		return sysValueProposed;
	}

	public void setSysValueProposed(boolean sysValueProposed) {
		this.sysValueProposed = sysValueProposed;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

}
