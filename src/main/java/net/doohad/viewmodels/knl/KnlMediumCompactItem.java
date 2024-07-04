package net.doohad.viewmodels.knl;

import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

public class KnlMediumCompactItem {
	
	// 매체 일련번호
	private int id;
	
	
	// 동일 광고 송출 금지 시간(미 설정 시 -1)
	private int adFreqCap;
	
	// 동일 광고주 송출 금지 시간(미 설정 시 -1)
	private int advFreqCap;
	
	// 동일 광고 범주 송출 금지 시간(미 설정 시 -1)
	private int catFreqCap;
	
	// 화면당 하루 노출 한도(미 설정 시 -1)
	private int dailyScrCap;
	
	
	// 동기화등급 A (ms 단위)
	private int aGradeMillis;
	
	// 동기화등급 B (ms 단위)
	private int bGradeMillis;

	// 동기화등급 C (ms 단위)
	private int cGradeMillis;
	
	
	public KnlMediumCompactItem(KnlMedium medium) {
		this.id = medium.getId();
		this.aGradeMillis = medium.getaGradeMillis();
		this.bGradeMillis = medium.getbGradeMillis();
		this.cGradeMillis = medium.getcGradeMillis();
		
		this.adFreqCap = Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.ad"));
		this.advFreqCap = Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.advertiser"));
		this.catFreqCap = Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.category"));
		this.dailyScrCap = Util.parseInt(SolUtil.getOptValue(medium.getId(), "freqCap.daily.screen"));
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getaGradeMillis() {
		return aGradeMillis;
	}

	public void setaGradeMillis(int aGradeMillis) {
		this.aGradeMillis = aGradeMillis;
	}

	public int getbGradeMillis() {
		return bGradeMillis;
	}

	public void setbGradeMillis(int bGradeMillis) {
		this.bGradeMillis = bGradeMillis;
	}

	public int getcGradeMillis() {
		return cGradeMillis;
	}

	public void setcGradeMillis(int cGradeMillis) {
		this.cGradeMillis = cGradeMillis;
	}

	public int getAdFreqCap() {
		return adFreqCap;
	}

	public void setAdFreqCap(int adFreqCap) {
		this.adFreqCap = adFreqCap;
	}

	public int getAdvFreqCap() {
		return advFreqCap;
	}

	public void setAdvFreqCap(int advFreqCap) {
		this.advFreqCap = advFreqCap;
	}

	public int getCatFreqCap() {
		return catFreqCap;
	}

	public void setCatFreqCap(int catFreqCap) {
		this.catFreqCap = catFreqCap;
	}

	public int getDailyScrCap() {
		return dailyScrCap;
	}

	public void setDailyScrCap(int dailyScrCap) {
		this.dailyScrCap = dailyScrCap;
	}

}
