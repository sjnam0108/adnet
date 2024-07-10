package kr.adnetwork.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="REV_SYNC_PACK_IMPS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"SHORT_NAME", "START_DATE"}),
})
public class RevSyncPackImp {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SYNC_PACK_IMP_ID")
	private int id;
	
	//
	// 동기화 묶음 노출
	//
	
	// 동기화 묶음 보고 ID
	@Column(name = "SHORT_NAME", nullable = false, length = 50)
	private String shortName;
	
	// 시작 일시
	@Column(name = "START_DATE", nullable = false)
	private Date startDate;

	
	// 유형
	//
	//   L		로그(기기의 보고를 기록)
	//   C		제어(기록에 따른 조치 제어 형태를 기록)
	//
	@Column(name = "TYPE", nullable = false, length = 1)
	private String type;

	// 유형명
	//
	//   CMPL	동기화 그룹에 속한 모든 기기가 정해진 시간(예: 5s)내에 모두 보고된 상태
	//   EXP	동기화 그룹에 속한 모든 기기가 보고되지 않아 정해진 시간을 경과한 상태
	//   RST1	그룹 내 모든 기기 리셋 type 1
	//
	@Column(name = "TYPE_NAME", nullable = false, length = 10)
	private String typeName;

	
	// 등급
	//
	//   A		가장 좋은 상태
	//   B		동기화 상태가 어긋나는 것이 느껴지나 놔두어도 크게 문제가 없을 것 같은 상태
	//   C		동기화가 상당히 어긋나, 지속적인 이 등급의 상태라면 제어가 필요한 상태
	//
	//   D		동기화가 아주 많이 어긋나, 즉각적인 조치가 필요한 상태
	//
	@Column(name = "GRADE", length = 1)
	private String grade;
	
	// 기기간 최대 오차(밀리초)
	@Column(name = "DIFF")
	private Integer diff;
	
	// 보고된 기기 수
	@Column(name = "RPT_CNT")
	private Integer reportCnt;
	
	// 전체 기기 수
	@Column(name = "TOT_CNT")
	private Integer totalCnt;
	
	// 광고명
	@Column(name = "AD", length = 100)
	private String ad;

	
	// 등급 큐(최근 등급에 대한 연속 문자열, 최근의 값이 제일 앞에 위치)
	@Column(name = "GRADE_QUEUE", nullable = false, length = 15)
	private String gradeQueue = "";

	// 보고 기기 수 큐(최근 등급에 대한 연속 문자열, 최근의 값이 제일 앞에 위치)
	//
	//   - 1-9: 1 / 2 / 3 / 4 / 5 / 6 / 7 / 8 / 9
	//   - 10: A
	//   - 11: B
	//   ...
	//   - 19: J
	//   - 나머지: Z
	//
	@Column(name = "COUNT_QUEUE", nullable = false, length = 15)
	private String countQueue = "";
	

	// WHO 컬럼들(S)
	
	//   기기에 의해 자동 생성되고, 꼭 필요한 생성일시가 포함되어 있으므로 WHO 컬럼 생략

	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 다른 개체 연결(E)
	
	
	public RevSyncPackImp() {}
	
	public RevSyncPackImp(Date startDate, String shortName, String typeName, String grade, int diff, int reportCnt, int totalCnt, 
			String ad, String gradeQueue, String countQueue) {
		this.startDate = startDate;
		this.shortName = shortName;
		
		this.type = "L";
		
		this.typeName = typeName;
		this.grade = grade;
		this.diff = diff;
		this.reportCnt = reportCnt;
		this.totalCnt = totalCnt;
		this.ad = ad;
		this.gradeQueue = gradeQueue;
		this.countQueue = countQueue;
	}
	
	public RevSyncPackImp(Date startDate, String shortName, String typeName) {
		this.startDate = startDate;
		this.shortName = shortName;
		
		this.type = "C";
		
		this.typeName = typeName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}

	public Integer getDiff() {
		return diff;
	}

	public void setDiff(Integer diff) {
		this.diff = diff;
	}

	public Integer getReportCnt() {
		return reportCnt;
	}

	public void setReportCnt(Integer reportCnt) {
		this.reportCnt = reportCnt;
	}

	public Integer getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(Integer totalCnt) {
		this.totalCnt = totalCnt;
	}

	public String getAd() {
		return ad;
	}

	public void setAd(String ad) {
		this.ad = ad;
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
}
