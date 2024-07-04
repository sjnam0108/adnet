package net.doohad.viewmodels.inv;

import net.doohad.models.inv.InvSyncPack;

public class InvSimpleSyncPackItem {

	private int id;
	private int syncPackId;
	
	private String name;
	private String shortName;
	
	private int screenCount;
	private boolean activeStatus;
	
	
	public InvSimpleSyncPackItem() {}
	
	public InvSimpleSyncPackItem(int id, InvSyncPack syncPack) {
		if (syncPack != null) {
			this.id = id;
			
			this.syncPackId = syncPack.getId();
			this.name = syncPack.getName();
			this.shortName = syncPack.getShortName();
			
			this.activeStatus = syncPack.isActiveStatus();
		}
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSyncPackId() {
		return syncPackId;
	}

	public void setSyncPackId(int syncPackId) {
		this.syncPackId = syncPackId;
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

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public int getScreenCount() {
		return screenCount;
	}

	public void setScreenCount(int screenCount) {
		this.screenCount = screenCount;
	}
}
