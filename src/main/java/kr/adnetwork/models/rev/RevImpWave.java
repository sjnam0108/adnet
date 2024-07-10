package kr.adnetwork.models.rev;

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
@Table(name="REV_IMP_WAVES")
public class RevImpWave {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "IMP_WAVE_ID")
	private int id;

	
	// 화면 번호
	@Column(name = "SCREEN_ID", nullable = false)
	private int screenId;
	
	// 화면명
	@Column(name = "SCREEN_NAME", length = 200, nullable = false)
	private String screenName;
	
	// 광고 번호
	@Column(name = "AD_ID", nullable = false)
	private int adId;
	
	// 광고명
	@Column(name = "AD_NAME", length = 200, nullable = false)
	private String adName;
	
	// 광고 소재 번호
	@Column(name = "CREATIVE_ID", nullable = false)
	private int creativeId;
	
	// 광고 소재명
	@Column(name = "CREATIVE_NAME", length = 200, nullable = false)
	private String creativeName;
	
	
	// 광고/광고 소재 번호
	@Column(name = "AD_CREATIVE_ID")
	private Integer adCreativeId;


	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)

	
	public RevImpWave() {}
	
	public RevImpWave(int screenId, String screenName, int adId, String adName,
			int creativeId, String creativeName, Integer adCreativeId, HttpSession session) {
		this.screenId = screenId;
		this.screenName = screenName;
		this.adId = adId;
		this.adName = adName;
		this.creativeId = creativeId;
		this.creativeName = creativeName;
		this.adCreativeId = adCreativeId;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	public int getAdId() {
		return adId;
	}

	public void setAdId(int adId) {
		this.adId = adId;
	}

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	public int getCreativeId() {
		return creativeId;
	}

	public void setCreativeId(int creativeId) {
		this.creativeId = creativeId;
	}

	public String getCreativeName() {
		return creativeName;
	}

	public void setCreativeName(String creativeName) {
		this.creativeName = creativeName;
	}

	public Integer getAdCreativeId() {
		return adCreativeId;
	}

	public void setAdCreativeId(Integer adCreativeId) {
		this.adCreativeId = adCreativeId;
	}

}
