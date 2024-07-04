package net.doohad.viewmodels.rev;

import java.util.Date;

import net.doohad.models.rev.RevDailyAchv;
import net.doohad.utils.Util;

public class RevDailyAchvItem {

	// 노출 일시
	private Date playDate;
	
	// 집행 노출량
	private int actualValue;
	
	// 집행 금액
	private int actualAmount;
	
	// 집행 방법(목표 구분)
	//
	//   A		광고예산(AD spend)
	//   I		노출량(Impressions)
	//
	private String goalType;
	
	// 하루 목표
	private int tgtToday;
	
	// 달성률
	private double achvRatio;
	
	
	private int dbId;
	private int dbActualValue;
	private int dbActualAmount;
	
	private double dbAchvRatio;
	
	private String dbGoalType;
	
	private int dbTgtToday;
	
	
	public RevDailyAchvItem() {}
	
	public RevDailyAchvItem(Date playDate, int actualValue, int actualAmount, String goalType) {
		
		this.playDate = playDate;
		this.actualValue = actualValue;
		this.actualAmount = actualAmount;
		this.goalType = goalType;
	}

	
	public void setDBData(RevDailyAchv item) {
		if (item != null) {
			this.dbId = item.getId();
			this.dbActualAmount = item.getActualAmount();
			this.dbActualValue = item.getActualValue();
			this.dbAchvRatio = item.getAchvRatio();
			this.dbGoalType = item.getGoalType();
			this.dbTgtToday = item.getTgtToday();
		}
	}
	
	public boolean isUpdateRequired() {
		
		return getDiffRatio() > 7.5d;
	}
	
	public double getDiffRatio() {
		
		if (Util.isNotValid(dbGoalType) || !dbGoalType.equals(goalType) || dbTgtToday == 0) {
			return 100d;
		} else {
			return Math.abs((double)tgtToday / (double)dbTgtToday * 100d - 100d);
		}
	}
	
	public int getFinalTgtToday() {
		if (isUpdateRequired()) {
			return tgtToday;
		} else {
			return dbTgtToday;
		}
	}
	
	
	public Date getPlayDate() {
		return playDate;
	}

	public void setPlayDate(Date playDate) {
		this.playDate = playDate;
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

	public double getAchvRatio() {
		return achvRatio;
	}

	public void setAchvRatio(double achvRatio) {
		this.achvRatio = achvRatio;
	}

	public int getDbActualValue() {
		return dbActualValue;
	}

	public void setDbActualValue(int dbActualValue) {
		this.dbActualValue = dbActualValue;
	}

	public int getDbActualAmount() {
		return dbActualAmount;
	}

	public void setDbActualAmount(int dbActualAmount) {
		this.dbActualAmount = dbActualAmount;
	}

	public double getDbAchvRatio() {
		return dbAchvRatio;
	}

	public void setDbAchvRatio(double dbAchvRatio) {
		this.dbAchvRatio = dbAchvRatio;
	}

	public String getDbGoalType() {
		return dbGoalType;
	}

	public void setDbGoalType(String dbGoalType) {
		this.dbGoalType = dbGoalType;
	}

	public int getDbTgtToday() {
		return dbTgtToday;
	}

	public void setDbTgtToday(int dbTgtToday) {
		this.dbTgtToday = dbTgtToday;
	}

	public int getDbId() {
		return dbId;
	}

	public void setDbId(int dbId) {
		this.dbId = dbId;
	}
	
}
