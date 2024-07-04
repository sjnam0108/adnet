package net.doohad.models.inv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="INV_SYNC_PACKS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class InvSyncPack {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SYNC_PACK_ID")
	private int id;
	
	// 묶음ID
	@Column(name = "SHORT_NAME", nullable = false, length = 50, unique = true)
	private String shortName;
	
	// 묶음명
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 서비스중 여부
	//
	//   더 이상 목록에서 보이고 싶지 않을 경우, 삭제 전 단계
	//
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus = true; 

	// 운영자 메모
	@Column(name = "MEMO", length = 300)
	private String memo;
	
	
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
	
	
	// 10분내 요청 화면의 갯수
	@Transient
	private int activeScreenCount = 0;
	
	// 화면의 갯수
	@Transient
	private int screenCount = 0;
	
	// 요청 상태
	@Transient
	private String reqStatus = "0";
	
	// 최근 광고 요청(ad / playlist API)
	@Transient
	private Date lastAdReq = null;
	
	// 최근 광고 시작
	@Transient
	private Date lastAdBegin = null;
	
	// 최근 광고
	@Transient
	private String lastAd;
	
	
	// 최근 5분 동기화 점수
	@Transient
	private float gradeOf5Mins = 0;
	
	// 최근 5분 Reset 1 횟수
	@Transient
	private int reset1CountOf5Mins = 0;
	
	// 최근 5분 Reset 2 횟수
	@Transient
	private int reset2CountOf5Mins = 0;
	
	// 최근 1시간 동기화 점수
	@Transient
	private float gradeOfHour = 0;
	
	// 최근 1시간 Reset 1 횟수
	@Transient
	private int reset1CountOfHour = 0;
	
	// 최근 1시간 Reset 2 횟수
	@Transient
	private int reset2CountOfHour = 0;
	
	// 최근 24시간 동기화 점수
	@Transient
	private float gradeOfDay = 0;
	
	// 최근 24시간 Reset 1 횟수
	@Transient
	private int reset1CountOfDay = 0;
	
	// 최근 24시간 Reset 2 횟수
	@Transient
	private int reset2CountOfDay = 0;
	
	// 오늘 동기화 점수
	@Transient
	private float gradeOfToday = 0;
	
	// 오늘 Reset 1 횟수
	@Transient
	private int reset1CountOfToday = 0;
	
	// 오늘 Reset 2 횟수
	@Transient
	private int reset2CountOfToday = 0;
	
	
	// 최근 최고-최저간 차이(밀리초 단위)
	@Transient
	private Integer diff = 0;
	
	// 최근 등급 큐
	@Transient
	private String gradeQueue = "";
	
	// 최근 보고 기기 수 큐
	@Transient
	private String countQueue = "";
	
	// 광고 채널 ID
	@Transient
	private String channel = "";
	
	// 재생목록
	@Transient
	private String playlist = "";
	
	
	// 채널 광고 일련 번호
	@Transient
	private Integer seq;
	
	// 채널 광고와의 시작 시간 차이(초 단위)
	@Transient
	private Integer seqDiff;
	
	
	// 구독 광고 채널
	@Transient
	private String subChannels = "";

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 하위 개체: 동기화 묶음 항목
	@OneToMany(mappedBy = "syncPack", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvSyncPackItem> syncPackItems = new HashSet<InvSyncPackItem>(0);
	
	// 다른 개체 연결(E)

	
	public InvSyncPack() {}
	
	public InvSyncPack(KnlMedium medium, String shortName, String name,
			String memo, boolean activeStatus, HttpSession session) {
		
		this.medium = medium;
		
		this.shortName = shortName;
		this.name = name;
		this.memo = memo;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
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

	public int getScreenCount() {
		return screenCount;
	}

	public void setScreenCount(int screenCount) {
		this.screenCount = screenCount;
	}

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	@JsonIgnore
	public Set<InvSyncPackItem> getSyncPackItems() {
		return syncPackItems;
	}

	public void setSyncPackItems(Set<InvSyncPackItem> syncPackItems) {
		this.syncPackItems = syncPackItems;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public String getReqStatus() {
		return reqStatus;
	}

	public void setReqStatus(String reqStatus) {
		this.reqStatus = reqStatus;
	}

	public Date getLastAdReq() {
		return lastAdReq;
	}

	public void setLastAdReq(Date lastAdReq) {
		this.lastAdReq = lastAdReq;
	}

	public int getActiveScreenCount() {
		return activeScreenCount;
	}

	public void setActiveScreenCount(int activeScreenCount) {
		this.activeScreenCount = activeScreenCount;
	}

	public float getGradeOf5Mins() {
		return gradeOf5Mins;
	}

	public void setGradeOf5Mins(float gradeOf5Mins) {
		this.gradeOf5Mins = gradeOf5Mins;
	}

	public int getReset1CountOf5Mins() {
		return reset1CountOf5Mins;
	}

	public void setReset1CountOf5Mins(int reset1CountOf5Mins) {
		this.reset1CountOf5Mins = reset1CountOf5Mins;
	}

	public int getReset2CountOf5Mins() {
		return reset2CountOf5Mins;
	}

	public void setReset2CountOf5Mins(int reset2CountOf5Mins) {
		this.reset2CountOf5Mins = reset2CountOf5Mins;
	}

	public float getGradeOfHour() {
		return gradeOfHour;
	}

	public void setGradeOfHour(float gradeOfHour) {
		this.gradeOfHour = gradeOfHour;
	}

	public int getReset1CountOfHour() {
		return reset1CountOfHour;
	}

	public void setReset1CountOfHour(int reset1CountOfHour) {
		this.reset1CountOfHour = reset1CountOfHour;
	}

	public int getReset2CountOfHour() {
		return reset2CountOfHour;
	}

	public void setReset2CountOfHour(int reset2CountOfHour) {
		this.reset2CountOfHour = reset2CountOfHour;
	}

	public float getGradeOfDay() {
		return gradeOfDay;
	}

	public void setGradeOfDay(float gradeOfDay) {
		this.gradeOfDay = gradeOfDay;
	}

	public int getReset1CountOfDay() {
		return reset1CountOfDay;
	}

	public void setReset1CountOfDay(int reset1CountOfDay) {
		this.reset1CountOfDay = reset1CountOfDay;
	}

	public int getReset2CountOfDay() {
		return reset2CountOfDay;
	}

	public void setReset2CountOfDay(int reset2CountOfDay) {
		this.reset2CountOfDay = reset2CountOfDay;
	}

	public float getGradeOfToday() {
		return gradeOfToday;
	}

	public void setGradeOfToday(float gradeOfToday) {
		this.gradeOfToday = gradeOfToday;
	}

	public int getReset1CountOfToday() {
		return reset1CountOfToday;
	}

	public void setReset1CountOfToday(int reset1CountOfToday) {
		this.reset1CountOfToday = reset1CountOfToday;
	}

	public int getReset2CountOfToday() {
		return reset2CountOfToday;
	}

	public void setReset2CountOfToday(int reset2CountOfToday) {
		this.reset2CountOfToday = reset2CountOfToday;
	}

	public Integer getDiff() {
		return diff;
	}

	public void setDiff(Integer diff) {
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

	public Date getLastAdBegin() {
		return lastAdBegin;
	}

	public void setLastAdBegin(Date lastAdBegin) {
		this.lastAdBegin = lastAdBegin;
	}

	public String getLastAd() {
		return lastAd;
	}

	public void setLastAd(String lastAd) {
		this.lastAd = lastAd;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPlaylist() {
		return playlist;
	}

	public void setPlaylist(String playlist) {
		this.playlist = playlist;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeqDiff() {
		return seqDiff;
	}

	public void setSeqDiff(Integer seqDiff) {
		this.seqDiff = seqDiff;
	}

	public String getSubChannels() {
		return subChannels;
	}

	public void setSubChannels(String subChannels) {
		this.subChannels = subChannels;
	}

}
