package kr.adnetwork.viewmodels.adc;

import java.text.DecimalFormat;
import java.util.Date;

import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.utils.Util;

public class AdcCreatFileThumbItem {

	private int id;
	private int advId;
	private int creatId;
	
	private String creatName;
	private String thumbUri;
	private String mediaType;
	private String fileLength;
	private String duration;
	private String resolution;
	private String link;
	
	// 모든, 진행중, 최근 등록 유형 처리
	private String type = "A";
	
	private Date date;
	
	
	public AdcCreatFileThumbItem(AdcCreatFile creatFile) {
		
		this.id = creatFile.getId();
		this.advId = creatFile.getCreative().getAdvertiser().getId();
		this.creatId = creatFile.getCreative().getId();
		
		this.creatName = creatFile.getCreative().getName();
		
		this.thumbUri = creatFile.getCtntFolder().getName() + "/" + creatFile.getThumbFilename();
		this.mediaType = creatFile.getMediaType();
		this.fileLength = Util.getSmartFileLength(creatFile.getFileLength());
		this.duration = new DecimalFormat("###,##0.00").format(creatFile.getSrcDurSecs()) + "s";
		this.resolution = creatFile.getResolution();
		this.link = creatFile.getCtntFolder().getWebPath() + "/" + creatFile.getCtntFolder().getName() + "/" + 
				creatFile.getUuid().toString() + "/" + creatFile.getFilename();
		
		this.date = creatFile.getWhoCreationDate();
	}
	
	
	public String getDispRegDate() {
		
		if (Util.isToday(this.date)) {
			return Util.toSimpleString(this.date, "HH:mm:ss");
		} else if (Util.isThisYear(this.date)) {
			return Util.toSimpleString(this.date, "M/d");
		} else {
			return Util.toSimpleString(this.date, "yyyy M/d");
		}
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreatName() {
		return creatName;
	}

	public void setCreatName(String creatName) {
		this.creatName = creatName;
	}

	public String getThumbUri() {
		return thumbUri;
	}

	public void setThumbUri(String thumbUri) {
		this.thumbUri = thumbUri;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getFileLength() {
		return fileLength;
	}

	public void setFileLength(String fileLength) {
		this.fileLength = fileLength;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAdvId() {
		return advId;
	}

	public void setAdvId(int advId) {
		this.advId = advId;
	}

	public int getCreatId() {
		return creatId;
	}

	public void setCreatId(int creatId) {
		this.creatId = creatId;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
}
