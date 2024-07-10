package kr.adnetwork.viewmodels.rev;

import java.text.DecimalFormat;
import java.util.Date;

import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.utils.SolUtil;

public class RevRptOvwAdItem {

	private String name;
	private String status;
	private String goalType;
	private String purchType;
	private String impDailyType;
	private String impHourlyType;
	
	private int id;
	private int campaignId;

	private boolean paused;
	private boolean mobTargeted;
	private boolean invenTargeted;
	private boolean timeTargeted;
	
	private int priority;
	private int goalValue;
	private int budget;
	private int sysValue;
	private int actualValue;
	private int actualCpm;
	private int actualAmount;

	private int proposedSysValue = 0;
	private int impAddRatio = 0;
	
	private boolean sysValueProposed;
	
	private double achvRatio;
	
	private String tgtTodayDisp = "";
	
	private Date startDate;
	private Date endDate;
	
	
	public RevRptOvwAdItem(AdcAd ad) {
		
		this.id = ad.getId();
		this.campaignId = ad.getCampaign().getId();
		
		this.name = ad.getName();
		this.status = ad.getStatus();
		this.goalType = ad.getGoalType();
		this.purchType = ad.getPurchType();
		
		this.paused = ad.isPaused();
		this.timeTargeted = ad.isTimeTargeted();
		
		this.startDate = ad.getStartDate();
		this.endDate = ad.getEndDate();

		this.priority = ad.getPriority();
		this.goalValue = ad.getGoalValue();
		this.budget = ad.getBudget();
		this.sysValue = ad.getSysValue();
		
		this.actualValue = ad.getActualValue();
		this.actualAmount = ad.getActualAmount();
		this.actualCpm = ad.getActualCpm();
		this.achvRatio = ad.getAchvRatio();
		
		this.impDailyType = ad.getImpDailyType();
		this.impHourlyType = ad.getImpHourlyType();
		this.impAddRatio = ad.getImpAddRatio();
		
		// 오늘/하루 목표
		if (ad.getTgtToday() > 0) {
			if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
					(ad.getStatus().equals("A") || ad.getStatus().equals("R")) &&
					(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
					(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
				
				String tgtTodayDisp = String.format("%s: ", ad.getStatus().equals("A") ? "하루 목표" : "오늘 목표");
				tgtTodayDisp += String.format("%s %s %s",
						ad.getGoalType().equals("A") ? "광고 예산" : "노출량",
						new DecimalFormat("###,###,##0").format(ad.getTgtToday()),
						ad.getGoalType().equals("A") ? "원" : "회");
				
				this.setTgtTodayDisp(tgtTodayDisp);
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

	public int getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
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

	public int getProposedSysValue() {
		return proposedSysValue;
	}

	public void setProposedSysValue(int proposedSysValue) {
		this.proposedSysValue = proposedSysValue;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getGoalValue() {
		return goalValue;
	}

	public void setGoalValue(int goalValue) {
		this.goalValue = goalValue;
	}

	public int getBudget() {
		return budget;
	}

	public void setBudget(int budget) {
		this.budget = budget;
	}

	public int getSysValue() {
		return sysValue;
	}

	public void setSysValue(int sysValue) {
		this.sysValue = sysValue;
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

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	public int getActualValue() {
		return actualValue;
	}

	public void setActualValue(int actualValue) {
		this.actualValue = actualValue;
	}

	public int getActualCpm() {
		return actualCpm;
	}

	public void setActualCpm(int actualCpm) {
		this.actualCpm = actualCpm;
	}

	public boolean isSysValueProposed() {
		return sysValueProposed;
	}

	public void setSysValueProposed(boolean sysValueProposed) {
		this.sysValueProposed = sysValueProposed;
	}

	public double getAchvRatio() {
		return achvRatio;
	}

	public void setAchvRatio(double achvRatio) {
		this.achvRatio = achvRatio;
	}

	public int getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(int actualAmount) {
		this.actualAmount = actualAmount;
	}

	public int getImpAddRatio() {
		return impAddRatio;
	}

	public void setImpAddRatio(int impAddRatio) {
		this.impAddRatio = impAddRatio;
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

	public boolean isMobTargeted() {
		return mobTargeted;
	}

	public void setMobTargeted(boolean mobTargeted) {
		this.mobTargeted = mobTargeted;
	}

}
