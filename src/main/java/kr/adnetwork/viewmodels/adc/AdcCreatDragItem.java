package kr.adnetwork.viewmodels.adc;

import java.text.DecimalFormat;
import java.util.Date;

import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.utils.Util;

public class AdcCreatDragItem {

	private int creatId;
	
	private String creatName;
	private String thumbUri;
	private String mediaType;
	private String fileLength;
	private String duration;
	
	private Date date;
	
	private boolean valid = true;
	
	
	public AdcCreatDragItem(AdcCreatFile creatFile) {
		
		this.creatId = creatFile.getCreative().getId();
		this.creatName = creatFile.getCreative().getName();
		
		this.thumbUri = creatFile.getCtntFolder().getName() + "/" + creatFile.getThumbFilename();
		this.mediaType = creatFile.getMediaType();
		this.fileLength = Util.getSmartFileLength(creatFile.getFileLength());
		this.duration = new DecimalFormat("###,##0.00").format(creatFile.getSrcDurSecs()) + "s";
		
		this.date = creatFile.getWhoCreationDate();
	}
	
	
	public String getOrderCode() {
		
		String ret = valid ? "1" : "2";
		if (valid) {
			ret += creatName;
		} else {
			ret += Util.toSimpleString(date, "yyyyMMdd HHmmss");
		}
		
		return ret;
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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
