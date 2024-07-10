package kr.adnetwork.viewmodels.sys;

import kr.adnetwork.utils.SolUtil;

public class SysAuditTrailValueItem {

	private String itemName = "";		// 항목명
	private String itemText = "";		// 항목 출력 문자열
	private String oldValue = "";		// 이전 값
	private String newValue = "";		// 이후 값
	private String oldText = "";		// 이전 출력값
	private String newText = "";		// 이후 출력값
	
	
	public SysAuditTrailValueItem(String itemText, String itemName) {
		this.itemText = itemText;
		this.itemName = itemName;
	}
	
	public SysAuditTrailValueItem(String itemText, String oldValue, String newValue) {
		this.itemText = itemText;
		this.oldValue = oldValue;
		this.newValue = newValue;
		
		if (itemText.equals("광고명")) {
			this.itemName = "Name";
		} else if (itemText.equals("구매 유형")) {
			this.itemName = "PurchType";
		} else if (itemText.equals("시작일")) {
			this.itemName = "StartDate";
		} else if (itemText.equals("종료일")) {
			this.itemName = "EndDate";
		} else if (itemText.equals("우선 순위")) {
			this.itemName = "Priority";
		} else if (itemText.equals("광고 예산")) {
			this.itemName = "Budget";
		} else if (itemText.equals("보장 노출량")) {
			this.itemName = "GoalValue";
		} else if (itemText.equals("목표 노출량")) {
			this.itemName = "SysValue";
		} else if (itemText.equals("집행 방법")) {
			this.itemName = "GoalType";
		} else if (itemText.equals("일별 광고 분산")) {
			this.itemName = "ImpDailyType";
		} else if (itemText.equals("1일 광고 분산")) {
			this.itemName = "ImpHourlyType";
		} else if (itemText.equals("현재 노출량 추가 제어")) {
			this.itemName = "ImpAddRatio";
		} else if (itemText.equals("게시 유형")) {
			this.itemName = "ViewType";
		} else if (itemText.equals("CPM")) {
			this.itemName = "CPM";
		} else if (itemText.equals("하루 노출한도")) {
			this.itemName = "DailyCap";
		} else if (itemText.equals("동일 광고 송출 금지 시간")) {
			this.itemName = "FreqCap";
		} else if (itemText.equals("화면당 하루 노출한도")) {
			this.itemName = "DailyScrCap";
		} else if (itemText.equals("재생 시간")) {
			this.itemName = "Duration";
		} else if (itemText.equals("광고 소재간 가중치")) {
			this.itemName = "Weight";
		} else if (itemText.equals("타겟팅 연산")) {
			this.itemName = "TargetOper";
		} else if (itemText.equals("광고 소재명")) {
			this.itemName = "Name";
		} else if (itemText.equals("범주")) {
			this.itemName = "Category";
		} else if (itemText.equals("소재 유형")) {
			this.itemName = "CreatType";
		} else if (itemText.equals("매체화면 재생시간 무시")) {
			this.itemName = "durPolicy";
		} else if (itemText.equals("대체 광고간 가중치")) {
			this.itemName = "Weight";
		}
		
		if (itemName.equals("PurchType") || itemName.equals("GoalType") || itemName.equals("ImpDailyType") || 
				itemName.equals("ImpHourlyType") || itemName.equals("ImpAddRatio") || itemName.equals("CPM") ||
				itemName.equals("DailyCap") || itemName.equals("FreqCap") || itemName.equals("DailyScrCap") ||
				itemName.equals("Duration") || itemName.equals("TargetOper")) {
			
			this.oldText = SolUtil.getAuditTrailValueCodeText("A", itemName, oldValue);
			this.newText = SolUtil.getAuditTrailValueCodeText("A", itemName, newValue);
			
		} else if (itemName.equals("Category") || itemName.equals("CreatType") || itemName.equals("durPolicy")) {
			
			this.oldText = SolUtil.getAuditTrailValueCodeText("C", itemName, oldValue);
			this.newText = SolUtil.getAuditTrailValueCodeText("C", itemName, newValue);
			
		}
	}

	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemText() {
		return itemText;
	}

	public void setItemText(String itemText) {
		this.itemText = itemText;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getOldText() {
		return oldText;
	}

	public void setOldText(String oldText) {
		this.oldText = oldText;
	}

	public String getNewText() {
		return newText;
	}

	public void setNewText(String newText) {
		this.newText = newText;
	}
	
}
