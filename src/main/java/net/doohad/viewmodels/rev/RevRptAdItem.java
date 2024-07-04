package net.doohad.viewmodels.rev;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Tuple;

import net.doohad.models.adc.AdcAd;
import net.doohad.utils.SolUtil;

public class RevRptAdItem {

	private String name;
	private String status;
	
	private Date startDate;
	private Date endDate;
	
	private int cnt00, cnt01, cnt02, cnt03, cnt04, cnt05, cnt06, cnt07;
	private int cnt08, cnt09, cnt10, cnt11, cnt12, cnt13, cnt14, cnt15;
	private int cnt16, cnt17, cnt18, cnt19, cnt20, cnt21, cnt22, cnt23;
	
	private int total;
	private int cntScreen;
	
	private boolean paused;
	private boolean mobTargeted;
	private boolean invenTargeted;
	private boolean timeTargeted;
	
	private String goalType;
	private String purchType;
	private String impDailyType;				// 일별 광고 분산 정책
	private String impHourlyType;				// 하루 광고 분산 정책
	
	private int id;
	private int priority;
	private int budget;
	private int goalValue;
	private int sysValue;
	private int dailyScrCap;
	private int actualCpm;
	
	private int proposedDailyScrCap = 0;
	private int impAddRatio = 0;
	
	private long actualAmount;
	
	private boolean totalRow;
	private boolean sysValueProposed;
	private boolean singleAdFiltered;
	
	private double achvRatio;
	
	private String tgtTodayDisp = "";
	
	
	public RevRptAdItem() {}
	
	public RevRptAdItem(AdcAd ad) {
		if (ad != null) {
			this.id = ad.getId();
			this.name = ad.getName();
			this.status = ad.getStatus();
			this.startDate = ad.getStartDate();
			this.endDate = ad.getEndDate();
			this.paused = ad.isPaused();
			this.timeTargeted = ad.isTimeTargeted();
			
			this.purchType = ad.getPurchType();
			this.goalType = ad.getGoalType();
			this.priority = ad.getPriority();
			this.goalValue = ad.getGoalValue();
			this.budget = ad.getBudget();
			this.sysValue = ad.getSysValue();
			this.dailyScrCap = ad.getDailyScrCap();
			
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
	}
	
	
	public void setTupleData(Tuple tuple) {
		if (tuple != null) {
			this.total = ((BigDecimal)tuple.get(1)).intValue();
			this.cnt00 = ((BigDecimal)tuple.get(2)).intValue();
			this.cnt01 = ((BigDecimal)tuple.get(3)).intValue();
			this.cnt02 = ((BigDecimal)tuple.get(4)).intValue();
			this.cnt03 = ((BigDecimal)tuple.get(5)).intValue();
			this.cnt04 = ((BigDecimal)tuple.get(6)).intValue();
			this.cnt05 = ((BigDecimal)tuple.get(7)).intValue();
			this.cnt06 = ((BigDecimal)tuple.get(8)).intValue();
			this.cnt07 = ((BigDecimal)tuple.get(9)).intValue();
			this.cnt08 = ((BigDecimal)tuple.get(10)).intValue();
			this.cnt09 = ((BigDecimal)tuple.get(11)).intValue();
			this.cnt10 = ((BigDecimal)tuple.get(12)).intValue();
			this.cnt11 = ((BigDecimal)tuple.get(13)).intValue();
			this.cnt12 = ((BigDecimal)tuple.get(14)).intValue();
			this.cnt13 = ((BigDecimal)tuple.get(15)).intValue();
			this.cnt14 = ((BigDecimal)tuple.get(16)).intValue();
			this.cnt15 = ((BigDecimal)tuple.get(17)).intValue();
			this.cnt16 = ((BigDecimal)tuple.get(18)).intValue();
			this.cnt17 = ((BigDecimal)tuple.get(19)).intValue();
			this.cnt18 = ((BigDecimal)tuple.get(20)).intValue();
			this.cnt19 = ((BigDecimal)tuple.get(21)).intValue();
			this.cnt20 = ((BigDecimal)tuple.get(22)).intValue();
			this.cnt21 = ((BigDecimal)tuple.get(23)).intValue();
			this.cnt22 = ((BigDecimal)tuple.get(24)).intValue();
			this.cnt23 = ((BigDecimal)tuple.get(25)).intValue();
			this.actualAmount = ((BigDecimal)tuple.get(26)).intValue();
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getCntScreen() {
		return cntScreen;
	}

	public void setCntScreen(int cntScreen) {
		this.cntScreen = cntScreen;
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

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public long getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(long actualAmount) {
		this.actualAmount = actualAmount;
	}

	public boolean isTotalRow() {
		return totalRow;
	}

	public void setTotalRow(boolean totalRow) {
		this.totalRow = totalRow;
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

	public boolean isSysValueProposed() {
		return sysValueProposed;
	}

	public void setSysValueProposed(boolean sysValueProposed) {
		this.sysValueProposed = sysValueProposed;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getTgtTodayDisp() {
		return tgtTodayDisp;
	}

	public void setTgtTodayDisp(String tgtTodayDisp) {
		this.tgtTodayDisp = tgtTodayDisp;
	}

	public boolean isSingleAdFiltered() {
		return singleAdFiltered;
	}

	public void setSingleAdFiltered(boolean singleAdFiltered) {
		this.singleAdFiltered = singleAdFiltered;
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
