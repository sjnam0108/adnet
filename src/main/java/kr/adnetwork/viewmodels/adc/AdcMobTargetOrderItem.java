package kr.adnetwork.viewmodels.adc;

import kr.adnetwork.models.adc.AdcMobTarget;
import kr.adnetwork.utils.Util;

public class AdcMobTargetOrderItem {

	private int id;
	private int seq;
	
	private String mobType;
	private String tgtDisplay;
	
	public AdcMobTargetOrderItem(AdcMobTarget mobTarget, String tgtDisplay, int seq) {
		this.id = mobTarget.getId();
		this.mobType = mobTarget.getMobType();
		this.tgtDisplay = tgtDisplay;
		this.seq = seq;
	}

	
	public String getTgtDisplayShort() {
		if (Util.isValid(tgtDisplay) && tgtDisplay.length() >= 20) {
			return tgtDisplay.substring(0, 20) + "...";
		} else {
			return tgtDisplay;
		}
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getMobType() {
		return mobType;
	}

	public void setMobType(String mobType) {
		this.mobType = mobType;
	}

	public String getTgtDisplay() {
		return tgtDisplay;
	}

	public void setTgtDisplay(String tgtDisplay) {
		this.tgtDisplay = tgtDisplay;
	}

}
