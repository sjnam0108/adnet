package kr.adnetwork.viewmodels.rev;

import java.util.Date;

public class RevSyncPackImpItem {

	// 동기화 묶음 ID
	private String groupID = "";
	
	// 기기간 최대 오차(밀리초)
	private int diff;
	
	// 등급 큐(최근 등급에 대한 연속 문자열, 최근의 값이 제일 앞에 위치)
	private String gradeQueue = "";

	// 보고 기기 수 큐(최근 등급에 대한 연속 문자열, 최근의 값이 제일 앞에 위치)
	private String countQueue = "";
	
	// 기준 재생목록에서의 일련번호
	//
	//    이 값이 -1이면, 일련번호 정보가 전달되지 않았음을 의미함
	//
	private int seq;
	
	// 광고 시작일시
	private Date beginDate = null;

	
	public RevSyncPackImpItem(String groupID, int diff, String gradeQueue, String countQueue, int seq, Date beginDate) {
		this.groupID = groupID;
		this.diff = diff;
		this.gradeQueue = gradeQueue;
		this.countQueue = countQueue;
		this.seq = seq;
		this.beginDate = beginDate;
	}

	
	public String getGroupID() {
		return groupID;
	}

	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}

	public String getGradeQueue() {
		return gradeQueue;
	}

	public void setGradeQueue(String gradeQueue) {
		this.gradeQueue = gradeQueue;
	}

	public String getCountQueue() {
		return countQueue;
	}

	public void setCountQueue(String countQueue) {
		this.countQueue = countQueue;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}
}
