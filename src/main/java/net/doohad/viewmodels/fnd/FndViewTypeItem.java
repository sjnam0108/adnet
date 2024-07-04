package net.doohad.viewmodels.fnd;

import net.doohad.models.fnd.FndViewType;

public class FndViewTypeItem {

	// 게시 유형ID
	private String code;

	// 묶음 광고 단위로 이용
	private boolean adPackUsed; 

	
	public FndViewTypeItem(FndViewType viewType) {
		this.code= viewType.getCode();
		this.adPackUsed = viewType.isAdPackUsed();
	}


	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isAdPackUsed() {
		return adPackUsed;
	}

	public void setAdPackUsed(boolean adPackUsed) {
		this.adPackUsed = adPackUsed;
	}
	
}
