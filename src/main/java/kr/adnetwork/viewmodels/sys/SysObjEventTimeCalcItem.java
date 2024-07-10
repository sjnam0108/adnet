package kr.adnetwork.viewmodels.sys;

import java.util.Date;

public class SysObjEventTimeCalcItem {

	private int objId;
	private String type;
	
	private Date date1;
	private Date date2;
	private Date date3;
	private Date date4;
	private Date date5;
	private Date date6;
	private Date date7;
	private Date date8;
	
	private Date lastUpdateDate;
	
	
	public SysObjEventTimeCalcItem() {}
	
	public SysObjEventTimeCalcItem(String typeS, int objId, Date date, int type) {
		this.type = typeS;
		this.objId = objId;
		
		if (type == 11 || type == 21 || type == 31) {
			this.date1 = date;
		} else if (type == 12 || type == 32) {
			this.date2 = date;
		} else if (type == 13) {
			this.date3 = date;
		} else if (type == 14) {
			this.date4 = date;
		} else if (type == 15) {
			this.date5 = date;
		} else if (type == 16) {
			this.date6 = date;
		} else if (type == 17) {
			this.date7 = date;
		}
	}

	
	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDate1() {
		return date1;
	}

	public void setDate1(Date date1) {
		this.date1 = date1;
	}

	public Date getDate2() {
		return date2;
	}

	public void setDate2(Date date2) {
		this.date2 = date2;
	}

	public Date getDate3() {
		return date3;
	}

	public void setDate3(Date date3) {
		this.date3 = date3;
	}

	public Date getDate4() {
		return date4;
	}

	public void setDate4(Date date4) {
		this.date4 = date4;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Date getDate5() {
		return date5;
	}

	public void setDate5(Date date5) {
		this.date5 = date5;
	}

	public Date getDate6() {
		return date6;
	}

	public void setDate6(Date date6) {
		this.date6 = date6;
	}

	public Date getDate7() {
		return date7;
	}

	public void setDate7(Date date7) {
		this.date7 = date7;
	}

	public Date getDate8() {
		return date8;
	}

	public void setDate8(Date date8) {
		this.date8 = date8;
	}
	
}
