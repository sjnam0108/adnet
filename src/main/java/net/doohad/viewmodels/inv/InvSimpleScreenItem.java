package net.doohad.viewmodels.inv;

import net.doohad.models.inv.InvScreen;

public class InvSimpleScreenItem {

	private int id;
	private int screenId;
	
	private String name;
	private String shortName;
	
	private String siteName;
	private String siteShortName;
	
	private boolean activeStatus; 
	
	private int laneId;
	
	
	public InvSimpleScreenItem() {}
	
	public InvSimpleScreenItem(int id, InvScreen screen) {
		if (screen != null) {
			this.id = id;
			
			this.screenId = screen.getId();
			this.name = screen.getName();
			this.shortName = screen.getShortName();
			
			this.siteName = screen.getSite().getName();
			this.siteShortName = screen.getSite().getShortName();
			
			this.activeStatus = screen.isActiveStatus();
		}
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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteShortName() {
		return siteShortName;
	}

	public void setSiteShortName(String siteShortName) {
		this.siteShortName = siteShortName;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public int getLaneId() {
		return laneId;
	}

	public void setLaneId(int laneId) {
		this.laneId = laneId;
	}
	
}
