package kr.adnetwork.models.adc;

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
@Table(name="ADC_PLAYLISTS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"CHANNEL_ID", "START_DATE"}),
})
public class AdcPlaylist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "PLAYLIST_ID")
	private int id;
	
	// 재생 목록 이름
	@Column(name = "NAME", nullable = false, length = 200)
	private String name;
	
	// 광고 갯수
	@Column(name = "AD_COUNT", nullable = false)
	private int adCount = 0;
	
	// 표출 시간
	@Column(name = "TOT_DUR_SEC", nullable = false)
	private int totDurSecs = 0;
	
	// 광고 id 값('|' 구분자 - 실제로는 광고/광고 소재 - adCreative)
	@Column(name = "AD_VALUE", nullable = false, length = 2000)
	private String adValue = "";
	
	
	// 서비스중 여부
	//
	//   재생 목록에 대한 작업 완료 후, 실제 서비스 여부 플래그. 게시 개념
	//   이 값이 활성화(true) 되어야만, 광고 채널에 사용될 수 있는 후보가 됨
	//
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus = false;
	
	// 시작일시(꼭 0시가 아닐 수 있기 때문)
	@Column(name = "START_DATE", nullable = false)
	private Date startDate;
	
	// 종료일시(꼭 0시가 아닐 수 있기 때문)
	@Column(name = "END_DATE")
	private Date endDate;

	
	// 채널 번호
	//
	//   생성되는 현재는 재생목록 유형을 완전 대체할 수 없기 때문에 nullable 상태이나,
	//   이후 재생목록 유형이 완전히 삭제될 시점에는 int 및 NN 상태로 변경 예정
	//   이후에는 PK/FK 관계는 맺지 않을 계획(channel - playlist)
	//
	@Column(name = "CHANNEL_ID", nullable = false)
	private int channelId;
	
	
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

	
	// 광고 채널
	@Transient
	private String channel = "";
	
	// 해상도
	@Transient
	private String resolution = "";

	// 게시유형
	@Transient
	private String viewTypeCode = "";

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 다른 개체 연결(E)

	
	public AdcPlaylist() {}
	
	public AdcPlaylist(KnlMedium medium, String name, int channelId, Date startDate, 
			boolean activeStatus, HttpSession session) {
		
		this.medium = medium;
		this.channelId = channelId;
		
		this.name = name;
		this.startDate = startDate;
		
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

	public int getAdCount() {
		return adCount;
	}

	public void setAdCount(int adCount) {
		this.adCount = adCount;
	}

	public int getTotDurSecs() {
		return totDurSecs;
	}

	public void setTotDurSecs(int totDurSecs) {
		this.totDurSecs = totDurSecs;
	}

	public String getAdValue() {
		return adValue;
	}

	public void setAdValue(String adValue) {
		this.adValue = adValue;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
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

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
