package kr.adnetwork.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="REV_CHAN_AD_RPTS")
public class RevChanAdRpt {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CHAN_AD_RPT_ID")
	private int id;
	
	//
	// 채널 광고 보고
	//
	
	// 광고 채널 번호
	@Column(name = "CHANNEL_ID", nullable = false)
	private int channelId;
	
	// 일련번호(광고 채널 내에서만 거의 unique)
	@Column(name = "SEQ", nullable = false)
	private int seq;


	// 유형
	//
	//    P - 동기화 화면 묶음
	//
	@Column(name = "TYPE", length = 1, nullable = false)
	private String type;

	// 개체(동기화 화면 묶음) id
	@Column(name = "OBJ_ID", nullable = false)
	private int objId;
	
	
	// 광고명
	@Column(name = "AD_NAME", length = 200, nullable = false)
	private String adName = "";
	
	// 광고 소재명
	@Column(name = "CREAT_NAME", length = 200, nullable = false)
	private String creatName = "";
	
	// 묶음 광고 소재
	@Column(name = "AD_PACK_IDS", length = 50, nullable = false)
	private String adPackIds = "";

	
	// 재생 시작 일시
	@Column(name = "PLAY_BEGIN_DATE", nullable = false)
	private Date playBeginDate;

	// 실제 시작 일시 = 생성일시
	@Column(name = "REAL_BEGIN_DATE", nullable = false)
	private Date realBeginDate;

	
	// 오차(초단위)
	//
	//   양수: 채널 광고 제안보다 이후
	//   음수: 채널 광고 제안보다 먼저(문제있음)
	//
	@Column(name = "DIFF", nullable = false)
	private int diff;
	
	// 보고 화면수
	@Column(name = "SCR_COUNT", nullable = false)
	private int scrCount = 0;

	
	// WHO 컬럼들(S)

	// WHO 컬럼들(E)
	
	
	// 채널 이름
	@Transient
	private String chanName = "";

	// 묶음 유형
	@Transient
	private String adPackType = "";


	
	public RevChanAdRpt() {}
	
	public RevChanAdRpt(RevChanAd chanAd, String type, int objId, Date date, int scrCount) {
		
		this.channelId = chanAd.getChannelId();
		this.seq = chanAd.getSeq();
		
		this.type = type;
		this.objId = objId;
		
		this.adName = chanAd.getAdName();
		this.playBeginDate = chanAd.getPlayBeginDate();
		
		this.creatName = chanAd.getCreatName();
		this.adPackIds = chanAd.getAdPackIds();
		
		this.realBeginDate = date;
		this.scrCount = scrCount;
		
		this.diff = (int)Math.round((realBeginDate.getTime() - playBeginDate.getTime()) / 1000);
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

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
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

	public String getAdName() {
		return adName;
	}

	public void setAdName(String adName) {
		this.adName = adName;
	}

	public Date getPlayBeginDate() {
		return playBeginDate;
	}

	public void setPlayBeginDate(Date playBeginDate) {
		this.playBeginDate = playBeginDate;
	}

	public Date getRealBeginDate() {
		return realBeginDate;
	}

	public void setRealBeginDate(Date realBeginDate) {
		this.realBeginDate = realBeginDate;
	}

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}

	public String getChanName() {
		return chanName;
	}

	public void setChanName(String chanName) {
		this.chanName = chanName;
	}

	public String getCreatName() {
		return creatName;
	}

	public void setCreatName(String creatName) {
		this.creatName = creatName;
	}

	public String getAdPackType() {
		return adPackType;
	}

	public void setAdPackType(String adPackType) {
		this.adPackType = adPackType;
	}

	public int getScrCount() {
		return scrCount;
	}

	public void setScrCount(int scrCount) {
		this.scrCount = scrCount;
	}

	public String getAdPackIds() {
		return adPackIds;
	}

	public void setAdPackIds(String adPackIds) {
		this.adPackIds = adPackIds;
	}
	
}
