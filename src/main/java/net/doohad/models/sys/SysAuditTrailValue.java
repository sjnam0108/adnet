package net.doohad.models.sys;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import net.doohad.utils.Util;
import net.doohad.viewmodels.sys.SysAuditTrailValueItem;

@Entity
@Table(name="SYS_AUDIT_TRAIL_VALUES")
public class SysAuditTrailValue {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUDIT_TRAIL_VALUE_ID")
	private int id;
	
	//
	// 감사 추적 항목 값
	//
	
	// 항목명
	@Column(name = "ITEM_NAME", nullable = false, length = 100)
	private String itemName = "";

	// 항목 출력 문자열
	@Column(name = "ITEM_TEXT", nullable = false, length = 100)
	private String itemText = "";

	
	// 이전 값
	@Column(name = "OLD_VAL", nullable = false, length = 200)
	private String oldValue = "";

	// 이후 값
	@Column(name = "NEW_VAL", nullable = false, length = 200)
	private String newValue = "";

	// 이전 출력값
	@Column(name = "OLD_TEXT", nullable = false, length = 200)
	private String oldText = "";

	// 이후 출력값
	@Column(name = "NEW_TEXT", nullable = false, length = 200)
	private String newText = "";

	
	// WHO 컬럼들(S)

	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 감사 추적
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AUDIT_TRAIL_ID", nullable = false)
	private SysAuditTrail auditTrail;
	
	// 다른 개체 연결(E)
	
	
	public SysAuditTrailValue() {}
	
	public SysAuditTrailValue(SysAuditTrail auditTrail, SysAuditTrailValueItem item) {
		this.auditTrail = auditTrail;
		
		this.itemName = item.getItemName();
		this.itemText = item.getItemText();
		
		this.oldValue = item.getOldValue();
		this.oldText = item.getOldText();
		
		this.newValue = item.getNewValue();
		this.newText = item.getNewText();
	}
	
	
	public String getOldDispText() {
		return Util.isValid(oldText) ? oldText : oldValue;
	}
	
	public String getNewDispText() {
		return Util.isValid(newText) ? newText : newValue;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public SysAuditTrail getAuditTrail() {
		return auditTrail;
	}

	public void setAuditTrail(SysAuditTrail auditTrail) {
		this.auditTrail = auditTrail;
	}

}
