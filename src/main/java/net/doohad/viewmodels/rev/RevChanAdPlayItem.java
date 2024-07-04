package net.doohad.viewmodels.rev;

import java.util.Date;

public class RevChanAdPlayItem {

	// 일련번호
	private int seq;
	
	// 재생 시작 일시
	private Date playBeginDate;
	
	// 재생 종료 일시
	private Date playEndDate;

	// 힌트(도움정보)
	private String hint = "";
	
	
	public RevChanAdPlayItem(int seq, Date playBeginDate, Date playEndDate, String hint) {
		this.seq = seq;
		this.playBeginDate = playBeginDate;
		this.playEndDate = playEndDate;
		this.hint = hint;
	}


	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public Date getPlayBeginDate() {
		return playBeginDate;
	}

	public void setPlayBeginDate(Date playBeginDate) {
		this.playBeginDate = playBeginDate;
	}

	public Date getPlayEndDate() {
		return playEndDate;
	}

	public void setPlayEndDate(Date playEndDate) {
		this.playEndDate = playEndDate;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}
	
}
