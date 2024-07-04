package net.doohad.models.adc;

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
import javax.servlet.http.HttpSession;

import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.Util;

@Entity
@Table(name="ADC_MOB_TARGETS")
public class AdcMobTarget {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MOB_TARGET_ID")
	private int id;
	
	//
	// 광고 모바일 타겟
	//

	// 삭제 여부
	//
	//   별도의 플래그 컬럼은 존재하지 않음. 상위인 Ad의 상태에 따라 살아있거나, 죽어있거나.
	//   Ad의 표시없이는 이 값들은 표시되지 않기 때문
	//
	
	// 필터 유형
	//
	//   A		And
	//   O		Or
	//
	@Column(name = "FILTER_TYPE", nullable = false, length = 1)
	private String filterType = "O";
	
	// 모바일 유형
	//
	//   RG      모바일 타겟팅 지역 - ReGion
	//   CR      원 반경 지역 - Circle Radius region
	//
	@Column(name = "MOB_TYPE", nullable = false, length = 2)
	private String mobType = "";

	// 대상 id(RG, CR 등의 id)
	@Column(name = "TGT_ID", nullable = false)
	private int tgtId;
	
	// 동일 광고에서 순서
	@Column(name = "SIBLING_SEQ", nullable = false)
	private int siblingSeq = 0;
	
	
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
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 상위 개체: 광고
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "AD_ID", nullable = false)
	private AdcAd ad;
	
	// 다른 개체 연결(E)
	
	
	public AdcMobTarget() {}
	
	public AdcMobTarget(AdcAd ad, String mobType, int tgtId, int siblingSeq, HttpSession session) {
		this.medium = ad.getMedium();
		this.ad = ad;
		
		//this.filterType = "O";	// 기본값 그대로
		this.mobType = mobType;
		this.tgtId = tgtId;
		this.siblingSeq = siblingSeq;
		
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

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getMobType() {
		return mobType;
	}

	public void setMobType(String mobType) {
		this.mobType = mobType;
	}

	public int getTgtId() {
		return tgtId;
	}

	public void setTgtId(int tgtId) {
		this.tgtId = tgtId;
	}

	public int getSiblingSeq() {
		return siblingSeq;
	}

	public void setSiblingSeq(int siblingSeq) {
		this.siblingSeq = siblingSeq;
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

	public AdcAd getAd() {
		return ad;
	}

	public void setAd(AdcAd ad) {
		this.ad = ad;
	}

}
