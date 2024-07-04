package net.doohad.models.inv;

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

import net.doohad.utils.Util;

@Entity
@Table(name="INV_SCR_PACK_ITEMS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"SCR_PACK_ID", "SCREEN_ID"}),
})
public class InvScrPackItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SCR_PACK_ITEM_ID")
	private int id;

	// 화면 id
	@Column(name = "SCREEN_ID", nullable = false)
	private int screenId;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 화면 묶음
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SCR_PACK_ID", nullable = false)
	private InvScrPack scrPack;
	
	// 다른 개체 연결(E)

	
	public InvScrPackItem() {}
	
	public InvScrPackItem(InvScrPack scrPack, int screenId, HttpSession session) {
		
		this.scrPack = scrPack;
		
		this.screenId = screenId;
		
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

	public InvScrPack getScrPack() {
		return scrPack;
	}

	public void setScrPack(InvScrPack scrPack) {
		this.scrPack = scrPack;
	}

}
