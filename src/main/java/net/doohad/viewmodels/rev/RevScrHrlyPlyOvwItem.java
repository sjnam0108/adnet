package net.doohad.viewmodels.rev;

import net.doohad.models.inv.InvScreen;

public class RevScrHrlyPlyOvwItem {

	private int id;
	
	private String name;
	private String reqStatus = "0";
	
	private int succTotal = 0;
	private int failTotal = 0;
	private int fbTotal = 0;
	private int noAdTotal = 0;
	
	private boolean deleted = false;
	
	public RevScrHrlyPlyOvwItem(InvScreen screen) {
		if (screen != null) {
			this.id = screen.getId();
			this.name = screen.getName();
			this.deleted = screen.isDeleted();
			this.reqStatus = screen.getReqStatus();
		}
	}
	
	
	public int getTotal() {
		return succTotal + failTotal + fbTotal + noAdTotal;
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

	public String getReqStatus() {
		return reqStatus;
	}

	public void setReqStatus(String reqStatus) {
		this.reqStatus = reqStatus;
	}

	public int getSuccTotal() {
		return succTotal;
	}

	public void setSuccTotal(int succTotal) {
		this.succTotal = succTotal;
	}

	public int getFailTotal() {
		return failTotal;
	}

	public void setFailTotal(int failTotal) {
		this.failTotal = failTotal;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public int getFbTotal() {
		return fbTotal;
	}

	public void setFbTotal(int fbTotal) {
		this.fbTotal = fbTotal;
	}

	public int getNoAdTotal() {
		return noAdTotal;
	}

	public void setNoAdTotal(int noAdTotal) {
		this.noAdTotal = noAdTotal;
	}
	
}
