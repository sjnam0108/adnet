package net.doohad.viewmodels.adc;

import java.util.Date;

import net.doohad.models.adc.AdcPlaylist;

public class AdcPlaylistItem {

	// 재생목록 id
	private int id;
	
	// 재생목록 이름
	private String name;
	
	// 광고 갯수
	private int adCount = 0;
	
	// 표출 시간
	private int totDurSecs = 0;
	
	// 광고 id 값('|' 구분자 - 실제로는 광고/광고 소재 - adCreative)
	private String adValue = "";
	
	// 서비스중 여부
	private boolean activeStatus = false;
	
	// 시작일시(꼭 0시가 아닐 수 있기 때문)
	private Date startDate;
	
	// 종료일시
	private Date endDate;
	
	// 상태 코드
	//
	//   - C: 현재 날짜에 의한 편성 가능한, 현재 재생목록
	//   - OC : 광고 편성 중 + "C" 속성
	//
	private String code = "";

	
	public AdcPlaylistItem(AdcPlaylist playlist) {
		
		this.id = playlist.getId();
		this.name = playlist.getName();
		this.adCount = playlist.getAdCount();
		this.totDurSecs = playlist.getTotDurSecs();
		this.adValue = playlist.getAdValue();
		this.activeStatus = playlist.isActiveStatus();
		this.startDate = playlist.getStartDate();
		this.endDate = playlist.getEndDate();
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAdCount() {
		return adCount;
	}

	public void setAdCount(int adCount) {
		this.adCount = adCount;
	}

	public int getTotDurSecs() {
		return totDurSecs;
	}

	public void setTotDurSecs(int totDurSecs) {
		this.totDurSecs = totDurSecs;
	}

	public String getAdValue() {
		return adValue;
	}

	public void setAdValue(String adValue) {
		this.adValue = adValue;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
