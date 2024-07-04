package net.doohad.models.org;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ORG_RT_CHANNELS")
public class OrgRTChannel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RT_CHANNEL_ID")
	private int id;
	
	//
	// 런타임 채널
	//

	// 채널 번호
	//
	//   채널의 시퀀스 번호로 별도의 PK/FK 관계를 만들지 않음
	//
	@Column(name = "CHANNEL_ID", nullable = false, unique = true)
	private int channelId;

	
	// 최근 광고 요청
	@Column(name = "LAST_AD_REQ_DATE")
	private Date lastAdReqDate;

	// 최근 광고 편성
	@Column(name = "LAST_AD_APP_DATE")
	private Date lastAdAppDate; 

	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	// WHO 컬럼들(E)
	

	public OrgRTChannel() {}

	public OrgRTChannel(int channelId) {
		
		this.channelId = channelId;
		
		Date now = new Date();
		this.whoCreationDate = now;
		this.whoLastUpdateDate = now;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
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



}
