package net.doohad.viewmodels.rev;

import java.util.Date;

public class RevObjEventTimeItem {

	private int objId;
	private Date date;

	// 유형
	//
	//   - 11: 화면(S)의 파일정보 요청(file)
	//   - 12: 화면(S)의 현재광고 요청(ad, playlist)
	//   - 13: 화면(S)의 방송완료 보고(report, directReport)
	//   - 14: 화면(S)의 Retry Report API 요청(X)
	//   - 14: 화면(S)의 플레이어 시작(info)
	//   - 15: 화면(S)의 명령 확인(command)
	//	 - 16: 화면(S)의 명령결과 보고(commandReport)
	//	 - 17: 화면(S)의 이벤트 보고(event)
	//   - [삭제]18: 화면(S)의 재생목록 확인 보고(playlist rcv)
	//   - 21: 광고 소재(C)의 송출 완료
	//   - 31: 동기화 화면 묶음(P)의 파일 정보 요청(file)
	//   - 32: 동기화 화면 묶음(P)의 현재 광고 요청(ad, recplaylist)
	//
	private int type;
	
	
	public RevObjEventTimeItem() {}
	
	public RevObjEventTimeItem(int objId, Date date, int type) {
		this.objId = objId;
		this.date = date;
		this.type = type;
	}

	
	public int getObjId() {
		return objId;
	}

	public void setObjId(int screenId) {
		this.objId = screenId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
