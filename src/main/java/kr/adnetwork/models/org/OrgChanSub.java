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
import javax.servlet.http.HttpSession;

import kr.adnetwork.utils.Util;

@Entity
@Table(name="ORG_CHAN_SUBS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"CHANNEL_ID", "TYPE", "OBJ_ID"}),
})
public class OrgChanSub {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CHAN_SUB_ID")
	private int id;
	
	//
	// 채널 구독자
	//

	// 유형
	//
	//    S - 화면
	//    P - 동기화 화면 묶음
	//
	@Column(name = "TYPE", length = 1, nullable = false)
	private String type;

	// 개체(화면 또는 동기화 화면 묶음) id
	@Column(name = "OBJ_ID", nullable = false)
	private int objId;
	
	
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
	@JoinColumn(name = "CHANNEL_ID", nullable = false)
	private OrgChannel channel;
	
	// 다른 개체 연결(E)

	
	public OrgChanSub() {}
	
	public OrgChanSub(OrgChannel channel, String type, int objId, HttpSession session) {
		
		this.channel = channel;
		
		this.type = type;
		this.objId = objId;
		
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

	public OrgChannel getChannel() {
		return channel;
	}

	public void setChannel(OrgChannel channel) {
		this.channel = channel;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

}
