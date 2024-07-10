package kr.adnetwork.viewmodels.inv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.adnetwork.utils.Util;

public class InvScreenLocData {

	private String playDate = "";
	
	private List<InvScreenLocItem> locItems = new ArrayList<InvScreenLocItem>();
	
	
	public InvScreenLocData() {}
	
	public InvScreenLocData(Date playDate) {
		
		this.playDate = Util.toDateString(playDate);
	}

	
	public String getPlayDate() {
		return playDate;
	}

	public void setPlayDate(String playDate) {
		this.playDate = playDate;
	}

	public List<InvScreenLocItem> getLocItems() {
		return locItems;
	}

	public void setLocItems(List<InvScreenLocItem> locItems) {
		this.locItems = locItems;
	}
	
}
