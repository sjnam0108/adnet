package net.doohad.viewmodels.rev;

public class RevChanAdItem {

	// 광고/광고 소재 id
	private int adCreatId;
	
	// 광고 소재 id
	private int creatId;
	
	// 광고 소재명
	private String creatName;
	
	// 광고 id
	private int adId;
	
	// 광고명
	private String adName;
	
	// 묶음 광고 소재
	private String adPackIds;
	
	// 매체 id
	private int mediumId;
	
	// 화면 해상도
	private String resolution;
	
	// 재생 시간(밀리초 단위)
	private int duration;
	
	// 유효성 여부
	private boolean effective;
	
	
	public RevChanAdItem(int mediumId, int adCreatId, int creatId, String creatName, int adId, 
			String adName, String adPackIds, String resolution, int duration, boolean effective) {
		this.mediumId = mediumId;
		this.adCreatId = adCreatId;
		this.creatId = creatId;
		this.creatName = creatName;
		this.adId = adId;
		this.adName = adName;
		this.adPackIds = adPackIds;
		this.resolution = resolution;
		this.duration = duration;
		this.effective = effective;
	}

	
	public int getAdCreatId() {
		return adCreatId;
	}

	public void setAdCreatId(int adCreatId) {
		this.adCreatId = adCreatId;
	}

	public int getCreatId() {
		return creatId;
	}

	public void setCreatId(int creatId) {
		this.creatId = creatId;
	}

	public String getCreatName() {
		return creatName;
	}

	public void setCreatName(String creatName) {
		this.creatName = creatName;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getAdPackIds() {
		return adPackIds;
	}

	public void setAdPackIds(String adPackIds) {
		this.adPackIds = adPackIds;
	}

	public int getMediumId() {
		return mediumId;
	}

	public void setMediumId(int mediumId) {
		this.mediumId = mediumId;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public boolean isEffective() {
		return effective;
	}

	public void setEffective(boolean effective) {
		this.effective = effective;
	}
	
}
