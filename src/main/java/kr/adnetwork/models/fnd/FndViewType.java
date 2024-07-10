package kr.adnetwork.models.fnd;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.servlet.http.HttpSession;

import kr.adnetwork.utils.Util;


@Entity
@Table(name="FND_VIEW_TYPES", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"VIEW_TYPE_CODE", "RESOLUTION"}),
})
public class FndViewType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "VIEW_TYPE_ID")
	private int id;
	
	// 게시 유형ID
	@Column(name = "VIEW_TYPE_CODE", nullable = false, length = 15)
	private String code;
	
	// 게시 유형명
	@Column(name = "NAME", nullable = false, length = 100, unique = true)
	private String name;

	
	// 화면 해상도
	@Column(name = "RESOLUTION", nullable = false, length = 20)
	private String resolution;
	
	// 적용 대상 매체
	@Column(name = "DEST_MEDIA", nullable = false)
	private String destMedia = "";

	// 묶음 광고 단위로 이용
	//
	//   묶음 광고 단위로 기록 및 통계 계산 여부
	//
	@Column(name =  "AD_PACK_USED", nullable = false)
	private boolean adPackUsed; 
	
	
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
	
	
	public FndViewType() {}
	
	public FndViewType(String code, String name, String resolution, String destMedia, 
			boolean adPackUsed, HttpSession session) {
		this.code = code;
		this.name = name;
		this.resolution = resolution;
		this.destMedia = destMedia;
		this.adPackUsed = adPackUsed;
		
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getDestMedia() {
		return destMedia;
	}

	public void setDestMedia(String destMedia) {
		this.destMedia = destMedia;
	}

	public boolean isAdPackUsed() {
		return adPackUsed;
	}

	public void setAdPackUsed(boolean adPackUsed) {
		this.adPackUsed = adPackUsed;
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

}
