package kr.adnetwork.viewmodels.rev;

import java.util.Date;

public class RevSyncPackMinMaxItem {

	// 동기화 그룹 ID
	private String groupID = "";
	
	// 광고 ID
	private String adID = "";
	
	// 화면의 총 갯수
	private int maxCnt;
	
	// 보고된 화면 수
	private int cnt;
	
	// 최소 시간
	private long min;
	
	// 최대 시간
	private long max;
	
	// 생성 시간
	private Date date;
	
	// 매체 ID
	private String mediumID = "";
	
	// 새로고침 필요
	private boolean refresh = false;
	
	
	public RevSyncPackMinMaxItem(String mediumID, String groupID, String adID, int maxCnt, 
			long time, boolean refresh) {
		this.mediumID = mediumID;
		this.groupID = groupID;
		this.adID = adID;
		this.maxCnt = maxCnt;
		this.cnt = 1;
		this.min = time;
		this.max = time;
		this.date = new Date();
		this.refresh = refresh;
	}

	
	public boolean reportNext(long time) {
		cnt ++;
		if (this.min > time) {
			this.min = time;
		}
		if (this.max < time) {
			this.max = time;
		}
		
		return (maxCnt == cnt);
	}
	
	public int getDiff() {
		return (int)(max - min);
	}
	
	public int getDateDiff() {
		return ((int)(new Date().getTime()) - (int)date.getTime());
	}
	
	
	public int getMaxCnt() {
		return maxCnt;
	}

	public void setMaxCnt(int maxCnt) {
		this.maxCnt = maxCnt;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public String getAdID() {
		return adID;
	}

	public void setAdID(String adID) {
		this.adID = adID;
	}

	public String getMediumID() {
		return mediumID;
	}

	public void setMediumID(String mediumID) {
		this.mediumID = mediumID;
	}

	public boolean isRefresh() {
		return refresh;
	}

	public void setRefresh(boolean refresh) {
		this.refresh = refresh;
	}
	
}
