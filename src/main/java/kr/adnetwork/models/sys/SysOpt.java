package kr.adnetwork.models.sys;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SYS_OPTS")
public class SysOpt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "OPT_ID")
	private int id;
	
	//
	// 시스템 옵션
	//

	// 코드
	@Column(name = "CODE", nullable = false, length = 200, unique = true)
	private String code;
	
	// 값
	@Column(name = "VALUE", nullable = false, length = 500)
	private String value;
	
	// 시간
	@Column(name = "DATE", nullable = false)
	private Date date;

	
	// WHO 컬럼들(S)

	// WHO 컬럼들(E)

	
	public SysOpt() {}
	
	public SysOpt(String code, String value) {
		this.code = code;
		this.value = value;
		this.date = new Date();
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
