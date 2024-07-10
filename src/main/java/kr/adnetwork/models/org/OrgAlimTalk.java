package kr.adnetwork.models.org;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.utils.Util;

@Entity
@Table(name="ORG_ALIM_TALKS", uniqueConstraints = {
		@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "SHORT_NAME"}),
})
public class OrgAlimTalk {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ALIM_TALK_ID")
	private int id;
	
	//
	// 알림톡
	//

	// 알림톡ID
	@Column(name = "SHORT_NAME", nullable = false, length = 70)
	private String shortName;
	
	
	// 알림톡 운영/이벤트 점검 시간(24x7=168)
	@Column(name = "BIZ_HOUR", nullable = false, length = 168)
	private String bizHour = "111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";

	// 이벤트 유형
	//
	//   - ActScr	활성 화면수
	//
	@Column(name = "EVENT_TYPE", nullable = false, length = 10)
	private String eventType = "";

	// 활성화 여부
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus;

	// 알림톡 구독자(휴대폰 번호 + "|"). 
	//   휴대폰 번호 + 구분자 = 12자리이기 때문에 총 20명 이상 가능
	@Column(name = "SUBSCRIBERS", nullable = false, length = 250)
	private String subscribers = "";
	
	// 동일 내용 전송일 경우 대기 시간(분단위)
	//
	//   30, 60, 120, 180 중 하나
	//
	//   동일 내용에 대한 판단은 이벤트 유형에 따라 달라짐
	//   ActScr - 장애 기기 수 및 장애 기기 목록 문자열, 2항목으로 판단 
	//
	@Column(name = "WAIT_MINS", nullable = false)
	private int waitMins = 30;

	// 점검을 시작하기 전 지연 시간(분단위)
	//
	//   0 이상, 60 미만의 값
	//
	//   보통 STB가 켜지면서 정상화되기 전까지의 시간은 점검은 보류되어야 함
	//
	@Column(name = "DELAY_CHK_MINS", nullable = false)
	private int delayChkMins = 5;


	// 현재 상태
	//
	//   현재 알림톡 항목의 체크 결과
	//
	//     - S		성공상태. 알림톡 발송 조건이 아님
	//     - F		실패상태. 알림톡 발송 조건에 부합
	//
	@Column(name = "STATUS", nullable = false, length = 1)
	private String status = "S";
	
	// 체크 일시
	//
	//   한번도 체크하지 않은 경우에는 null, 그 이후에는 체크를 한 일시가 저장되어,
	//   결국 가장 최근 체크한 일시를 가지게 됨.
	//   비활성화 상태에서는 체크하지 않음.
	//
	@Column(name = "CHECK_DATE")
	private Date checkDate;
	
	// 대기 일시
	//
	//   활성화 상태 && 상태 체크의 변경에 대해
	//     S -> F : 알림톡 발송, 현재시간 + waitMins의 최종 결과 일시를 설정
	//     S -> S : null. 추가 설정 필요없음
	//     F -> S : null. null로 설정해야 함
	//     F -> F : 현재 시간이 대기 시간보다 이전이면 pass, 이후면
	//              알림톡 새로 발송하고, 현재시간 + waitMins의 최종 결과 일시를 설정
	//
	@Column(name = "WAIT_DATE")
	private Date waitDate;
	
	
	// 설정용 문자열
	//
	//   각 이벤트 유형에 따라 설정 가능한 항목의 수가 다름
	//
	//   - ActScr:
	//     1분 주기로 점검하는 동안, 활성 화면수가 {1번} 미만인 상태로 
	//     {2번} 회 연속 확인되면 알림톡을 발송합니다.
	//
	//     1: 활성 화면수 (1이상 직접 입력)
	//     2: 연속 몇 회 (1, 3, 5, 10 중 하나)
	//
	// 설정용 문자열 1
	@Column(name = "CF_STR_1", length = 20)
	private String cfStr1;

	// 설정용 문자열 2
	@Column(name = "CF_STR_2", length = 20)
	private String cfStr2;

	
	// 운행용 문자열
	//
	//   각 이벤트 유형에 따라 운행을 위한 항목의 수가 다름
	//
	//   - ActScr: 총 2개
	//     1: 장애 기기 수
	//     lg1: 장애기기 목록
	//
	// 운행용 문자열 1
	@Column(name = "OP_STR_1", length = 20)
	private String opStr1;

	// 운행용 문자열 1
	@Column(name = "OP_LG_STR_1", length = 500)
	private String opLgStr1;
	
	
	// 운행용 숫자
	//
	//   각 이벤트 유형에 따라 운행을 위한 항목의 수와 값이 다름
	//
	//   - ActScr: 총 1개
	//     1: 장애 등록 전 카운팅
	//
	// 운행용 숫자 1
	@Column(name = "OP_INT_1")
	private Integer opInt1;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private int whoLastUpdatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 알림톡 수신 번호 수
	@Transient
	private int subCount = 0;
	
	// 알림톡 운영/이벤트 점검 총 시간
	@Transient
	private int bizHours = 0;
	
	// 알림톡 주요 점검 내용
	@Transient
	private String checkList = "";

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 다른 개체 연결(E)

	
	public OrgAlimTalk() {}
	
	public OrgAlimTalk(KnlMedium medium, String shortName, String bizHour, String eventType, boolean activeStatus, HttpSession session) {
		
		this.medium = medium;
		
		this.shortName = shortName;
		this.bizHour = bizHour;
		this.eventType = eventType;
		this.activeStatus = activeStatus;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdatedBy = Util.loginUserId(session);
		this.whoLastUpdateDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
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

	public String getBizHour() {
		return bizHour;
	}

	public void setBizHour(String bizHour) {
		this.bizHour = bizHour;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(String subscribers) {
		this.subscribers = subscribers;
	}

	public int getWaitMins() {
		return waitMins;
	}

	public void setWaitMins(int waitMins) {
		this.waitMins = waitMins;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public Date getWaitDate() {
		return waitDate;
	}

	public void setWaitDate(Date waitDate) {
		this.waitDate = waitDate;
	}

	public String getCfStr1() {
		return cfStr1;
	}

	public void setCfStr1(String cfStr1) {
		this.cfStr1 = cfStr1;
	}

	public String getCfStr2() {
		return cfStr2;
	}

	public void setCfStr2(String cfStr2) {
		this.cfStr2 = cfStr2;
	}

	public String getOpStr1() {
		return opStr1;
	}

	public void setOpStr1(String opStr1) {
		this.opStr1 = opStr1;
	}

	public String getOpLgStr1() {
		return opLgStr1;
	}

	public void setOpLgStr1(String opLgStr1) {
		this.opLgStr1 = opLgStr1;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public Date getWhoLastUpdateDate() {
		return whoLastUpdateDate;
	}

	public void setWhoLastUpdateDate(Date whoLastUpdateDate) {
		this.whoLastUpdateDate = whoLastUpdateDate;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdatedBy() {
		return whoLastUpdatedBy;
	}

	public void setWhoLastUpdatedBy(int whoLastUpdatedBy) {
		this.whoLastUpdatedBy = whoLastUpdatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public int getSubCount() {
		return subCount;
	}

	public void setSubCount(int subCount) {
		this.subCount = subCount;
	}

	public int getBizHours() {
		return bizHours;
	}

	public void setBizHours(int bizHours) {
		this.bizHours = bizHours;
	}

	public String getCheckList() {
		return checkList;
	}

	public void setCheckList(String checkList) {
		this.checkList = checkList;
	}

	public Integer getOpInt1() {
		return opInt1;
	}

	public void setOpInt1(Integer opInt1) {
		this.opInt1 = opInt1;
	}

	public int getDelayChkMins() {
		return delayChkMins;
	}

	public void setDelayChkMins(int delayChkMins) {
		this.delayChkMins = delayChkMins;
	}

}
