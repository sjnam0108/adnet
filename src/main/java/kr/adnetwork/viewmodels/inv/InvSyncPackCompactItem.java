package kr.adnetwork.viewmodels.inv;

public class InvSyncPackCompactItem {

	// 동기화 그룹 일련번호
	private int id;
	
	
	// 매체 일련번호
	private int mediumId;
	
	// 동기화 묶음 ID
	private String spShortName;
	
	// 전체 화면 수
	private int cnt;
	
	// 레인 최소 번호
	private int minLaneId;
	
	// 첫번째 화면 ID
	private String scrShortName;
	
	// 첫번째 화면 id
	private int screenId;
	
	
	public InvSyncPackCompactItem(int mediumId, String spShortName, int cnt, int id, int minLaneId, String scrShortName, int screenId) {
		this.id = id;
		this.spShortName = spShortName;
		this.cnt = cnt;
		this.mediumId = mediumId;
		this.minLaneId = minLaneId;
		this.scrShortName = scrShortName;
		this.screenId = screenId;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMediumId() {
		return mediumId;
	}

	public void setMediumId(int mediumId) {
		this.mediumId = mediumId;
	}

	public String getSpShortName() {
		return spShortName;
	}

	public void setSpShortName(String spShortName) {
		this.spShortName = spShortName;
	}

	public int getCnt() {
		return cnt;
	}

	public void setCnt(int cnt) {
		this.cnt = cnt;
	}

	public int getMinLaneId() {
		return minLaneId;
	}

	public void setMinLaneId(int minLaneId) {
		this.minLaneId = minLaneId;
	}

	public String getScrShortName() {
		return scrShortName;
	}

	public void setScrShortName(String scrShortName) {
		this.scrShortName = scrShortName;
	}

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}
	
}
