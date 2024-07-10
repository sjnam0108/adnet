package kr.adnetwork.models.org;

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

import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.utils.Util;

@Entity
@Table(name="ORG_CHANNELS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "SHORT_NAME"}),
})
public class OrgChannel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CHANNEL_ID")
	private int id;
	
	//
	// 채널
	//

	// 채널ID
	@Column(name = "SHORT_NAME", nullable = false, length = 50)
	private String shortName;
	
	// 채널 이름
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 화면 해상도
	@Column(name = "RESOLUTION", nullable = false, length = 20)
	private String resolution;
	
	// 게시 유형
	@Column(name = "VIEW_TYPE_CODE", nullable = false, length = 15)
	private String viewTypeCode = "";
	
	// 우선 순위
	//
	//  최고 1에서 최저 10 사이의 값으로, 높은 우선 순위의 채널이 먼저 선택됨
	//
	@Column(name = "PRIORITY", nullable = false)
	private int priority = 5;
	
	// 광고 추가 모드
	//
	//   - A: 자율 광고선택
	//   - P: 재생목록
	//
	@Column(name = "APPEND_MODE", nullable = false, length = 1)
	private String appendMode = "P";
	
	
	// 자율 광고선택 기준 화면id
	@Column(name = "REQ_SCREEN_ID")
	private Integer reqScreenId;
	
	
	// 활성화 여부
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus = true;
	
	// 광고 편성중 여부
	@Column(name = "AD_APPENDED", nullable = false)
	private boolean adAppended = false;
	
	
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
	
	
	// 구독수
	@Transient
	private int subCount = 0;
	
	// 자율 광고선택 기준 화면
	@Transient
	private String reqScreen = "";
	
	// 최근 광고 요청
	@Transient
	private Date lastAdReqDate;
	
	// 최근 광고 편성
	@Transient
	private Date lastAdAppDate;

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	
	// 하위 개체: 채널 구독자
	@OneToMany(mappedBy = "channel", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<OrgChanSub> chanSubs = new HashSet<OrgChanSub>(0);
	
	// 다른 개체 연결(E)

	
	public OrgChannel() {}
	
	public OrgChannel(KnlMedium medium, String shortName, String name, String resolution, String viewTypeCode, 
			int priority, String appendMode, boolean activeStatus, HttpSession session) {
		
		this.medium = medium;
		
		this.shortName = shortName;
		this.name = name;
		this.resolution = resolution;
		this.viewTypeCode = viewTypeCode;
		this.priority = priority;
		this.appendMode = appendMode;
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
	
	
	public String getResolutionDisp() {
		return resolution.replace("x", " x ");
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getViewTypeCode() {
		return viewTypeCode;
	}

	public void setViewTypeCode(String viewTypeCode) {
		this.viewTypeCode = viewTypeCode;
	}

	public String getAppendMode() {
		return appendMode;
	}

	public void setAppendMode(String appendMode) {
		this.appendMode = appendMode;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
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

	@JsonIgnore
	public Set<OrgChanSub> getChanSubs() {
		return chanSubs;
	}

	public void setChanSubs(Set<OrgChanSub> chanSubs) {
		this.chanSubs = chanSubs;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getSubCount() {
		return subCount;
	}

	public void setSubCount(int subCount) {
		this.subCount = subCount;
	}

	public String getReqScreen() {
		return reqScreen;
	}

	public void setReqScreen(String reqScreen) {
		this.reqScreen = reqScreen;
	}

	public Integer getReqScreenId() {
		return reqScreenId;
	}

	public void setReqScreenId(Integer reqScreenId) {
		this.reqScreenId = reqScreenId;
	}

	public boolean isAdAppended() {
		return adAppended;
	}

	public void setAdAppended(boolean adAppended) {
		this.adAppended = adAppended;
	}

	public Date getLastAdReqDate() {
		return lastAdReqDate;
	}

	public void setLastAdReqDate(Date lastAdReqDate) {
		this.lastAdReqDate = lastAdReqDate;
	}

	public Date getLastAdAppDate() {
		return lastAdAppDate;
	}

	public void setLastAdAppDate(Date lastAdAppDate) {
		this.lastAdAppDate = lastAdAppDate;
	}

}
