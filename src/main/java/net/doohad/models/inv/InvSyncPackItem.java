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
@Table(name="INV_SYNC_PACK_ITEMS")
public class InvSyncPackItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SYNC_PACK_ITEM_ID")
	private int id;

	// 화면 id
	@Column(name = "SCREEN_ID", nullable = false, unique = true)
	private int screenId;
	
	// 레인 번호(1부터 시작)
	@Column(name = "LANE_ID", nullable = false)
	private int laneId;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)

	
	// 다른 개체 연결(S)
	
	// 상위 개체: 동기화 묶음
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SYNC_PACK_ID", nullable = false)
	private InvSyncPack syncPack;
	
	// 다른 개체 연결(E)

	
	public InvSyncPackItem() {}
	
	public InvSyncPackItem(InvSyncPack syncPack, int screenId, int laneId, HttpSession session) {
		
		this.syncPack = syncPack;
		
		this.screenId = screenId;
		this.laneId = laneId;
		
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

	public int getLaneId() {
		return laneId;
	}

	public void setLaneId(int laneId) {
		this.laneId = laneId;
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

	public InvSyncPack getSyncPack() {
		return syncPack;
	}

	public void setSyncPack(InvSyncPack syncPack) {
		this.syncPack = syncPack;
	}

}
