package net.doohad.viewmodels.adc;

import java.util.Date;
import java.util.List;

import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.utils.Util;

public class AdcJsonFileObject {

	private int adId;
	
	private String adName;
	private String advertiserName;
	private String adUuid;
	private String httpFilename;
	private String mimeType;
	private String resolution;
	private String hash;
	
	private long fileLength;
	
	private Date creationDate;
	
	private String filename;

	
	// 실제 재생시간 millis 단위 int
	// 실제 광고의 노출 시간
	//   1) 광고 설정값(5초 이상인 경우): 이미지 유형 포함
	//   2) 재생 시간 미설정(이미지 유형)이라면, 화면의 기본 재생 시간, 매체의 기본 재생 시간 순으로 설정
	//   3) 광고 소재의 재생 시간
	private int durMillis;

	private AdcCreative creative;

	
	public AdcJsonFileObject() {}
	
	public AdcJsonFileObject(AdcCreatFile creatFile) {
		this.adId = creatFile.getCreative().getId();
		this.adName = creatFile.getCreative().getName();
		this.advertiserName = creatFile.getCreative().getAdvertiser().getName();
		this.adUuid = creatFile.getUuid().toString();
		this.httpFilename = creatFile.getHttpFilename();
		this.fileLength = creatFile.getFileLength();
		this.mimeType = creatFile.getMimeType();
		this.hash = creatFile.getHash();
		this.resolution = creatFile.getResolution();
		this.creationDate = creatFile.getWhoCreationDate();
		
		this.filename = creatFile.getFilename();
		
		this.durMillis = (int)Math.round(creatFile.getSrcDurSecs() * 1000d);
	}
	
	
	public String getUuidDurFilename() {
		
		// for "local_filename"
		
		return adUuid + "_(" + (Math.round(durMillis / 100d) / 10d) + "s)." + Util.getFileExt(filename);
	}
	
	public int getWidth() {
		if (Util.isValid(resolution)) {
			List<String> items = Util.tokenizeValidStr(resolution, "x");
			return Util.parseInt(items.get(0));
		}
		return 0;
	}
	
	public int getHeight() {
		if (Util.isValid(resolution)) {
			List<String> items = Util.tokenizeValidStr(resolution, "x");
			return Util.parseInt(items.get(1));
		}
		return 0;
	}

	public int getDurSecs() {
		
		// for "duration"

		return (int)Math.round(durMillis / 1000d);
	}
	
	public int getFormalDurMillis() {
		
		// for "duration_millis"

		return durMillis;
	}
	
	public String getCreationDateStr() {
		return Util.toSimpleString(creationDate, "yyyyMMdd HHmmss");
	}

	public String getMimeType() {
		return mimeType;
	}

	public long getFileLength() {
		return fileLength;
	}

	public String getHttpFilename() {
		return httpFilename;
	}

	public String getAdvertiserName() {
		return advertiserName;
	}

	public String getAdUuid() {
		return adUuid;
	}

	public int getAdId() {
		return adId;
	}

	public String getAdName() {
		return adName;
	}

	public AdcCreative getCreative() {
		return creative;
	}

	public void setCreative(AdcCreative creative) {
		this.creative = creative;
	}

	public int getDurMillis() {
		return durMillis;
	}

	public void setDurMillis(int durMillis) {
		this.durMillis = durMillis;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
