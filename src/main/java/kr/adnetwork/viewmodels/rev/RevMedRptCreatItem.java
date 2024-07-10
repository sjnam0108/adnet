package kr.adnetwork.viewmodels.rev;

import kr.adnetwork.models.adc.AdcCreative;

public class RevMedRptCreatItem {

	private int creatId;						// 광고 소재 id
	private int campId;							// 캠페인 id
	
	private String name;						// 광고 소재명
	private String fileResolutions = "";		// 등록된 해상도
	private String category = "";				// 광고 소재 범주
	private String advertiserName = "";			// 광고주
	
	private String viewTypeCode = "";			// 등록된 해상도sub
	
	private boolean paused;						// 잠시멈춤여부(오늘만) - 광고명sub
	private boolean invenTargeted;				// 인벤타겟여부 - 광고명sub
	private boolean timeTargeted;				// 시간타겟여부 - 광고명sub
	
	private boolean totalRow;
	
	
	public RevMedRptCreatItem() {}
	
	public RevMedRptCreatItem(AdcCreative creative, int campaignId, boolean isTodayData) {
		if (creative != null) {
			this.creatId = creative.getId();
			this.campId = campaignId;
			
			this.name = creative.getName();
			this.viewTypeCode = creative.getViewTypeCode();
			this.category = creative.getCategory();
			this.advertiserName = creative.getAdvertiser().getName();
			
			this.timeTargeted = creative.isTimeTargeted();
			// invenTargeted는 별도로 설정
			
			if (isTodayData) {
				// 오늘 날짜 자료에만 필요
				this.paused = creative.isPaused();
			}
		}
	}

	public int getCreatId() {
		return creatId;
	}

	public void setCreatId(int creatId) {
		this.creatId = creatId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileResolutions() {
		return fileResolutions;
	}

	public void setFileResolutions(String fileResolutions) {
		this.fileResolutions = fileResolutions;
	}

	public boolean isTotalRow() {
		return totalRow;
	}

	public void setTotalRow(boolean totalRow) {
		this.totalRow = totalRow;
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

	public String getViewTypeCode() {
		return viewTypeCode;
	}

	public void setViewTypeCode(String viewTypeCode) {
		this.viewTypeCode = viewTypeCode;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public void setAdvertiserName(String advertiserName) {
		this.advertiserName = advertiserName;
	}

	public int getCampId() {
		return campId;
	}

	public void setCampId(int campId) {
		this.campId = campId;
	}

}
