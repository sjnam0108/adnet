package kr.adnetwork.viewmodels.rev;

import java.text.DecimalFormat;
import java.util.Date;

import kr.adnetwork.utils.Util;

public class RevUploadFileItem {

	private int id;
    
	private String filename = "";
	private String date;

	private long length;
	private long lastModified;
	
	
	public RevUploadFileItem(String filename, long length, long lastModified,
			String date, int id) {
		
		this.filename = filename;
		this.length = length;
		this.lastModified = lastModified;
		this.date = date;
		this.id = id;
	}

	public RevUploadFileItem(String filename, long length, long lastModified,
			String stb, String date, int id) {
		this.filename = filename;
		this.length = length;
		this.lastModified = lastModified;
		this.date = date;
		this.id = id;
	}

	
	public String getSmartLength() {
		return Util.getSmartFileLength(length);
	}
	
	public String getDispFileLength() {
		return new DecimalFormat("##,###,###,##0").format(length) + " bytes";
	}
	
	public Date getUploadDate() {
		return new Date(lastModified);
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

}
