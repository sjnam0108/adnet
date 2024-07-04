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
@Table(name="INV_SCR_PACKS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"MEDIUM_ID", "NAME"}),
})
public class InvScrPack {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SCR_PACK_ID")
	private int id;
	
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
	
	
	// 화면의 갯수
	@Transient
	private int screenCount = 0;

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 하위 개체: 화면 묶음 항목
	@OneToMany(mappedBy = "scrPack", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<InvScrPackItem> scrPackItems = new HashSet<InvScrPackItem>(0);
	
	// 다른 개체 연결(E)

	
	public InvScrPack() {}
	
	public InvScrPack(KnlMedium medium, String name, String memo, boolean activeStatus, HttpSession session) {
		
		this.medium = medium;
		
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

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
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
	public Set<InvScrPackItem> getScrPackItems() {
		return scrPackItems;
	}

	public void setScrPackItems(Set<InvScrPackItem> scrPackItems) {
		this.scrPackItems = scrPackItems;
	}

}
