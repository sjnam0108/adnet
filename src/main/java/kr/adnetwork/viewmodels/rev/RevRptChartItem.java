package kr.adnetwork.viewmodels.rev;

import java.util.Date;

import kr.adnetwork.utils.Util;

public class RevRptChartItem {

	// 노출 시간(시간포함 날짜형)
	private Date playTime;
	
	// 노출량(집행 금액 제외 - 시간 단위의 자료는 노출량뿐)
	private int value;
	
	// 차트 도움말의 key 문자열에 시간 포함 여부
	private boolean timeIncluded = false;
	

	public RevRptChartItem() {}
	
	public RevRptChartItem(Date playTime) {
		this.playTime = playTime;
	}
	
	public RevRptChartItem(Date playTime, boolean timeIncluded) {
		this.playTime = playTime;
		this.timeIncluded = true;
	}

	
	public String getPlayTimeDisp() {
		if (timeIncluded) {
			return Util.toSimpleString(playTime, "M/d(EEE) HH") + "시";
		} else {
			return Util.toSimpleString(playTime, "M/d(EEE)");
		}
	}
	
	public Date getPlayTime() {
		return playTime;
	}

	public void setPlayTime(Date playTime) {
		this.playTime = playTime;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
