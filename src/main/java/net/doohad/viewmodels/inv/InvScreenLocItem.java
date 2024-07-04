package net.doohad.viewmodels.inv;

import java.util.Date;

import net.doohad.models.inv.InvScrLoc;
import net.doohad.utils.Util;

public class InvScreenLocItem {

	private double lat;
	private double lng;
	
	private Date time;
	
	private String title;
	
	
	public InvScreenLocItem() { }
	
	public InvScreenLocItem(InvScrLoc scrLoc) {
		
		this.lat = scrLoc.getLat();
		this.lng = scrLoc.getLng();
		
		this.time = scrLoc.getTime1();
		
		if (scrLoc.getTime2() == null) {
			this.title = Util.toSimpleString(scrLoc.getTime1(), "HH:mm:ss");
		} else {
			this.title = Util.toSimpleString(scrLoc.getTime1(), "HH:mm:ss") +
					" - " + Util.toSimpleString(scrLoc.getTime2(), "HH:mm:ss");
		}
	}

	
	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
