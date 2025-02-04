package kr.adnetwork.viewmodels.rev;

import java.math.BigDecimal;

import javax.persistence.Tuple;

import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.utils.Util;

public class RevMedRptDataItem {
	
	private int dataId;							// id
	private int parentId;						// 상위 개체 id

	private String name;						// 광고명
	
	private int total;							// 노출량
	private int cntScreen;						// 화면수
	
	private int actualCpm;						// 집행CPM
	private long actualAmount;					// 집행금액
	
	private int cnt00, cnt01, cnt02, cnt03, cnt04, cnt05, cnt06, cnt07;
	private int cnt08, cnt09, cnt10, cnt11, cnt12, cnt13, cnt14, cnt15;
	private int cnt16, cnt17, cnt18, cnt19, cnt20, cnt21, cnt22, cnt23;
	

	// 집행 방법(목표 구분)
	//
	//   A			광고예산(AD spend)
	//   I			노출량(Impressions)
	//   [blank] 	하루 목표 없음
	//
	private String goalType = "";
	
	// 하루 목표
	private int tgtToday;
	
	// 목표 달성률(%)
	private double achvRatio;
	
	
	private boolean totalRow;
	
	
	public RevMedRptDataItem() {}
	
	public RevMedRptDataItem(AdcCampaign camp) {
		if (camp != null) {
			this.dataId = camp.getId();
			this.parentId = 0;

			this.name = camp.getName();
			
			// 별도로 설정:
			// 		actualCpm, total, actualAmount, 00-23, cntScreen
		}
	}
	
	public RevMedRptDataItem(AdcAd ad) {
		if (ad != null) {
			this.dataId = ad.getId();
			this.parentId = ad.getCampaign().getId();

			this.name = ad.getName();
			this.actualCpm = ad.getActualCpm();
			
			// 별도로 설정:
			// 		total, actualAmount, 00-23, cntScreen
		}
	}
	
	public RevMedRptDataItem(AdcCreative creat, int campaignId) {
		if (creat != null) {
			this.dataId = creat.getId();
			this.parentId = campaignId;

			this.name = creat.getName();
			
			// 별도로 설정:
			// 		actualCpm, total, actualAmount, 00-23, cntScreen
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
	
	public String getColorCode() {
		if (!totalRow && Util.isValid(goalType)) {
			if (achvRatio < 50) { return "#df1616"; }
			else if (achvRatio < 80) { return "#ff7325"; }
			else if (achvRatio < 90) { return "#af52fe"; }
			else if (achvRatio < 100) { return "#70cc33"; }
			else if (achvRatio >= 100) { return "#487df2"; }
		}
		
		return "";
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public int getActualCpm() {
		return actualCpm;
	}

	public void setActualCpm(int actualCpm) {
		this.actualCpm = actualCpm;
	}

	public long getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(long actualAmount) {
		this.actualAmount = actualAmount;
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

	public boolean isTotalRow() {
		return totalRow;
	}

	public void setTotalRow(boolean totalRow) {
		this.totalRow = totalRow;
	}

	public int getDataId() {
		return dataId;
	}

	public void setDataId(int dataId) {
		this.dataId = dataId;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public double getAchvRatio() {
		return achvRatio;
	}

	public void setAchvRatio(double achvRatio) {
		this.achvRatio = achvRatio;
	}

	public int getTgtToday() {
		return tgtToday;
	}

	public void setTgtToday(int tgtToday) {
		this.tgtToday = tgtToday;
	}

}
