package net.doohad.viewmodels.adc;

import java.text.DecimalFormat;
import java.util.Date;

import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.utils.Util;

public class AdcAdCreatDragItem {
	
	private int adCreatId;
	
	private String creatName;
	private String thumbUri;
	private String mediaType;
	private String fileLength;
	private String duration;
	
	private int intDuration;
	
	private String adName;
	private String advertiser;
	
	private Date date;
	
	private boolean valid = true;
	
	public AdcAdCreatDragItem(AdcAdCreative adCreat, boolean valid) {
		
		this.adCreatId = adCreat.getId();
		
		this.adName = adCreat.getAd().getName();
		this.advertiser = adCreat.getAd().getCampaign().getAdvertiser().getName();
		
		this.valid = valid;
	}
	
	public AdcAdCreatDragItem(AdcAdCreative adCreat, AdcCreatFile creatFile, boolean valid) {
		
		this.adCreatId = adCreat.getId();
		
		this.adName = adCreat.getAd().getName();
		this.advertiser = adCreat.getAd().getCampaign().getAdvertiser().getName();
		
		this.creatName = creatFile.getCreative().getName();
		
		this.thumbUri = creatFile.getCtntFolder().getName() + "/" + creatFile.getThumbFilename();
		this.mediaType = creatFile.getMediaType();
		this.fileLength = Util.getSmartFileLength(creatFile.getFileLength());
		this.duration = new DecimalFormat("###,##0.00").format(creatFile.getSrcDurSecs()) + "s";
		this.intDuration = creatFile.getDurSecs();
		
		this.date = creatFile.getWhoCreationDate();
		
		this.valid = valid;
	}
	
	
	public String getOrderCode() {
		
		String ret = valid ? "1" : "2";
		ret += creatName;
		
		return ret;
	}

	
	public int getAdCreatId() {
		return adCreatId;
	}

	public void setAdCreatId(int adCreatId) {
		this.adCreatId = adCreatId;
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

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public String getAdvertiser() {
		return advertiser;
	}

	public void setAdvertiser(String advertiser) {
		this.advertiser = advertiser;
	}

	public int getIntDuration() {
		return intDuration;
	}

	public void setIntDuration(int intDuration) {
		this.intDuration = intDuration;
	}
	
}
