package kr.adnetwork.models.inv;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="INV_RT_SYNC_PACKS")
public class InvRTSyncPack {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RT_SYNC_PACK_ID")
	private int id;
	
	//
	// 런타임 동기화 화면 묶음
	//

	// 동기화 화면 묶음 번호
	//
	//   동기화 화면 묶음의 시퀀스 번호로 별도의 PK/FK 관계를 만들지 않음
	//
	@Column(name = "SYNC_PACK_ID", nullable = false, unique = true)
	private int syncPackId;

	
	// 최근 광고 요청
	@Column(name = "LAST_AD_REQ_DATE")
	private Date lastAdReqDate; 

	// 최근 광고 시작
	@Column(name = "LAST_AD_BEGIN_DATE")
	private Date lastAdBeginDate; 

	
	// 최고-최저간 차이(밀리초 단위)
	@Column(name = "DIFF")
	private Integer diff;
	
	
	// 최근 광고
	@Column(name = "LAST_AD", length = 200)
	private String lastAd = "";

	// 최근 등급 큐
	@Column(name = "GRADE_QUEUE", length = 15)
	private String gradeQueue = "";

	// 최근 기기 수 큐
	@Column(name = "COUNT_QUEUE", length = 15)
	private String countQueue = "";

	
	// 채널 정보(채널ID)
	@Column(name = "CHANNEL", length = 50)
	private String channel = "";
	
	// 재생목록(자율 광고 선택이 아닌 경우)
	@Column(name = "PLAYLIST", length = 200)
	private String playlist = "";
	
	
	// 채널 광고 일련 번호
	@Column(name = "SEQ")
	private Integer seq;
	
	// 채널 광고와의 시작 시간 차이(초 단위)
	@Column(name = "SEQ_DIFF")
	private Integer seqDiff;

	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	// WHO 컬럼들(E)
	

	public InvRTSyncPack() {}

	public InvRTSyncPack(int syncPackId) {
		
		this.syncPackId = syncPackId;
		
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

	public int getSyncPackId() {
		return syncPackId;
	}

	public void setSyncPackId(int syncPackId) {
		this.syncPackId = syncPackId;
	}

	public Date getLastAdReqDate() {
		return lastAdReqDate;
	}

	public void setLastAdReqDate(Date lastAdReqDate) {
		this.lastAdReqDate = lastAdReqDate;
	}

	public Date getLastAdBeginDate() {
		return lastAdBeginDate;
	}

	public void setLastAdBeginDate(Date lastAdBeginDate) {
		this.lastAdBeginDate = lastAdBeginDate;
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

	public Integer getDiff() {
		return diff;
	}

	public void setDiff(Integer diff) {
		this.diff = diff;
	}

	public String getLastAd() {
		return lastAd;
	}

	public void setLastAd(String lastAd) {
		this.lastAd = lastAd;
	}

	public String getGradeQueue() {
		return gradeQueue;
	}

	public void setGradeQueue(String gradeQueue) {
		this.gradeQueue = gradeQueue;
	}

	public String getCountQueue() {
		return countQueue;
	}

	public void setCountQueue(String countQueue) {
		this.countQueue = countQueue;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPlaylist() {
		return playlist;
	}

	public void setPlaylist(String playlist) {
		this.playlist = playlist;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeqDiff() {
		return seqDiff;
	}

	public void setSeqDiff(Integer seqDiff) {
		this.seqDiff = seqDiff;
	}

}
