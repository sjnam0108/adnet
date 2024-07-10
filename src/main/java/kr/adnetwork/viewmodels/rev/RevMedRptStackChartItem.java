package kr.adnetwork.viewmodels.rev;

public class RevMedRptStackChartItem {

	// 표시 시간(00 - 23)
	private String hour;
	
	// 성공 합계
	private long succ;
	
	// 실패 합계
	private long fail;
	
	// 대체광고 합계
	private long fb;
	
	// 광고없음 합계
	private long noAd;
	
	// 툴팁 제목
	private String title;
	
	
	public RevMedRptStackChartItem() {}
	
	public RevMedRptStackChartItem(String title, String hour, long succ, long fail, long fb, long noAd) {
		this.title = title;
		this.hour = hour;
		this.succ = succ;
		this.fail = fail;
		this.fb = fb;
		this.noAd = noAd;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public long getSucc() {
		return succ;
	}

	public void setSucc(long succ) {
		this.succ = succ;
	}

	public long getFail() {
		return fail;
	}

	public void setFail(long fail) {
		this.fail = fail;
	}

	public long getFb() {
		return fb;
	}

	public void setFb(long fb) {
		this.fb = fb;
	}

	public long getNoAd() {
		return noAd;
	}

	public void setNoAd(long noAd) {
		this.noAd = noAd;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
