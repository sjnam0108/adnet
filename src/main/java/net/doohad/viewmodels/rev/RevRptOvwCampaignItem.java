package net.doohad.viewmodels.rev;

import java.util.Date;

import net.doohad.models.adc.AdcCampaign;

public class RevRptOvwCampaignItem {

	private String name;
	private String status;
	private String advertiser;
	
	private int id;
	private int adCount;
	
	private Date startDate;
	private Date endDate;
	
	private int budget;
	private int goalValue;
	private int sysValue;
	private int actualValue;
	private int actualAmount;
	private int actualCpm;
	
	private double achvRatio;
	
	private String goalType = "U";
	private String impDailyType;
	private String impHourlyType;
	
	private boolean selfManaged;

	private int proposedSysValue = 0;
	
	private String tgtTodayDisp = "";

	
	public RevRptOvwCampaignItem(AdcCampaign campaign, int adCount) {
		
		this.id = campaign.getId();
		this.name = campaign.getName();
		this.status = campaign.getStatus();
		this.startDate = campaign.getStartDate();
		this.endDate = campaign.getEndDate();
		this.advertiser = campaign.getAdvertiser().getName();
		
		this.adCount = adCount;
		
		this.budget = campaign.getBudget();
		this.goalValue = campaign.getGoalValue();
		this.sysValue = campaign.getSysValue();
		this.actualValue = campaign.getActualValue();
		this.actualAmount = campaign.getActualAmount();
		this.actualCpm = campaign.getActualCpm();
		
		this.achvRatio = campaign.getAchvRatio();
		this.goalType = campaign.getGoalType();
		this.selfManaged = campaign.isSelfManaged();
		this.impDailyType = campaign.getImpDailyType();
		this.impHourlyType = campaign.getImpHourlyType();
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdCount() {
		return adCount;
	}

	public void setAdCount(int adCount) {
		this.adCount = adCount;
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

	public String getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
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

	public String getTgtTodayDisp() {
		return tgtTodayDisp;
	}

	public void setTgtTodayDisp(String tgtTodayDisp) {
		this.tgtTodayDisp = tgtTodayDisp;
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
	
}
