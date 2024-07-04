package net.doohad.models.rev;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import net.doohad.models.adc.AdcAdCreative;
import net.doohad.utils.Util;
import net.doohad.viewmodels.rev.RevChanAdItem;

@Entity
@Table(name="REV_REC_PLAYLISTS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"SYNC_PACK_ID", "PLAY_BEGIN_DATE"}),
})
public class RevRecPlaylist {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "REC_PLAYLIST_ID")
	private int id;
	
	//
	// 기준(혹은 권장) 재생목록
	//   - Standard / Server / Recommended playlist
	//
	
	// 동기화 그룹 번호: 추가적인 PK/FK 관계는 맺지 않음
	@Column(name = "SYNC_PACK_ID", nullable = false)
	private int syncPackId;
	
	// 일련번호
	//
	//   - 초기화에 의해 {동기화그룹} + {일련번호}의 값이 중복될 가능성도 있다.
	//   - 초기화의 쿨타임은 24시간
	//
	@Column(name = "SEQ", nullable = false)
	private int seq;
	
	
	// 광고 소재 번호
	//
	//   - API를 통해 서비스를 할 때의 레이블: adId
	//
	@Column(name = "CREAT_ID", nullable = false)
	private int creatId;
	
	// 광고 소재명
	@Column(name = "CREAT_NAME", length = 200, nullable = false)
	private String creatName;
	
	// 광고/광고 소재 번호
	//
	//   - API를 통해 서비스를 할 때의 레이블: acId
	//
	@Column(name = "AD_CREAT_ID", nullable = false)
	private int adCreatId;
	
	// 광고 번호
	@Column(name = "AD_ID", nullable = false)
	private int adId;
	
	// 광고명
	@Column(name = "AD_NAME", length = 200, nullable = false)
	private String adName = "";

	
	// 재생 시작 일시
	@Column(name = "PLAY_BEGIN_DATE", nullable = false)
	private Date playBeginDate;
	
	// 재생 종료 일시
	@Column(name = "PLAY_END_DATE", nullable = false)
	private Date playEndDate;
	
	// 재생 시간(밀리초 단위)
	@Column(name = "DURATION", nullable = false)
	private int duration;

	
	// 기기 실제 시작 일시
	@Column(name = "REAL_BEGIN_DATE", nullable = true)
	private Date realBeginDate;

	
	// 묶음 광고 소재
	//
	//  - 예: 707  or  706|705|704
	//
	@Column(name = "AD_PACK_IDS", nullable = false, length = 50)
	private String adPackIds = "";

	// 힌트(도움정보)
	//
	//   - 재생목록 유형: 해당 항목의 재생목록 번호 및 인덱스 번호 등(예: 13_9)
	//
	@Column(name = "HINT", length = 200)
	private String hint = "";
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	// WHO 컬럼들(E)

	
	public RevRecPlaylist() {}
	
	public RevRecPlaylist(int syncPackId, int seq, RevChanAdItem adItem, Date startDate, int duration, String hint) {
		
		this.syncPackId = syncPackId;
		this.seq = seq;
		
		this.creatId = adItem.getCreatId();
		this.creatName = adItem.getCreatName();
		this.adCreatId = adItem.getAdCreatId();
		this.adId = adItem.getAdId();
		this.adName = adItem.getAdName();
		
		this.playBeginDate = startDate;
		this.playEndDate = Util.addMilliseconds(playBeginDate, duration);
		this.duration = duration;
		
		this.adPackIds = adItem.getAdPackIds();
		this.hint = hint;
		
		this.whoCreationDate = new Date();
	}
	
	public RevRecPlaylist(int syncPackId, int seq, AdcAdCreative adCreat, Date startDate, int duration, String hint) {
		
		this.syncPackId = syncPackId;
		this.seq = seq;
		
		this.creatId = adCreat.getCreative().getId();
		this.creatName = adCreat.getCreative().getName();
		this.adCreatId = adCreat.getId();
		this.adId = adCreat.getAd().getId();
		this.adName = adCreat.getAd().getName();
		
		this.playBeginDate = startDate;
		this.playEndDate = Util.addMilliseconds(playBeginDate, duration);
		this.duration = duration;
		
		this.adPackIds = adCreat.getAd().getAdPackIds();
		this.hint = hint;
		
		this.whoCreationDate = new Date();
	}

	
	public String getDurDisp() {
		
		return String.valueOf((int)Math.floor((double)duration / 1000d)) + 
				" <small>" + String.format("%03d", duration % 1000) + "</small>";
	}
	
	public String getDiffDisp() {
		if (playBeginDate == null || realBeginDate == null) {
			return "";
		} else if(realBeginDate.after(playBeginDate)) {
			return "+" + Util.getHumanTime(Math.round((realBeginDate.getTime() - playBeginDate.getTime()) / 1000));
		} else {
			return "-" + Util.getHumanTime(Math.round((playBeginDate.getTime() - realBeginDate.getTime()) / 1000));
		}
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

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getCreatId() {
		return creatId;
	}

	public void setCreatId(int creatId) {
		this.creatId = creatId;
	}

	public String getCreatName() {
		return creatName;
	}

	public void setCreatName(String creatName) {
		this.creatName = creatName;
	}

	public int getAdCreatId() {
		return adCreatId;
	}

	public void setAdCreatId(int adCreatId) {
		this.adCreatId = adCreatId;
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

	public Date getPlayBeginDate() {
		return playBeginDate;
	}

	public void setPlayBeginDate(Date playBeginDate) {
		this.playBeginDate = playBeginDate;
	}

	public Date getPlayEndDate() {
		return playEndDate;
	}

	public void setPlayEndDate(Date playEndDate) {
		this.playEndDate = playEndDate;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public String getAdPackIds() {
		return adPackIds;
	}

	public void setAdPackIds(String adPackIds) {
		this.adPackIds = adPackIds;
	}

	public Date getRealBeginDate() {
		return realBeginDate;
	}

	public void setRealBeginDate(Date realBeginDate) {
		this.realBeginDate = realBeginDate;
	}

}
