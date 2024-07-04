package net.doohad.models.inv;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="INV_RT_SCREENS")
public class InvRTScreen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RT_SCREEN_ID")
	private int id;
	
	//
	// 런타임 화면
	//

	// 화면 번호
	//
	//   화면의 시퀀스 번호로 별도의 PK/FK 관계를 만들지 않음
	//
	@Column(name = "SCREEN_ID", nullable = false, unique = true)
	private int screenId;

	
	// 플레이어 버전
	@Column(name = "PLAYER_VER", nullable = false, length = 30)
	private String playerVer = ""; 
	
	// 키퍼 버전
	@Column(name = "KEEPER_VER", nullable = false, length = 30)
	private String keeperVer = "";

	// 다음 명령(다음에 수행될 기기 명령)
	//
	//   명령				한글					이벤트
	//   ------------------	-----------------------	-----------------------
	//   Reboot				기기 재시작(리부팅)		Reboot
	//   Update				플레이어 업데이트		X(실패 시에만 UpdateError를 따로 등록)
	//   Restart            플레이어 재시작			Restart
	//   Log				로그 업로드				X
	//   ------------------	-----------------------	-----------------------
	//
	@Column(name = "NEXT_CMD", nullable = false, length = 30)
	private String nextCmd = "";
	
	// 명령 실행 결과 실패
	@Column(name = "CMD_FAILED", nullable = false)
	private boolean cmdFailed;
	
	// 명령 등록한 사용자 id(-1은 시스템)
	@Column(name = "CMD_BY", nullable = false)
	private int cmdBy = -1;
	
	
	// 재생목록 A: 확인 시간
	@Column(name = "A_PLAYLIST_DATE")
	private Date aPlaylistDate;

	// 재생목록 A: 이름
	@Column(name = "A_PLAYLIST", nullable = false, length = 200)
	private String aPlaylist = "";

	// 재생목록 B: 확인 시간
	@Column(name = "B_PLAYLIST_DATE")
	private Date bPlaylistDate;

	// 재생목록 B: 이름
	@Column(name = "B_PLAYLIST", nullable = false, length = 200)
	private String bPlaylist = "";

	// 재생목록 C: 확인 시간
	@Column(name = "C_PLAYLIST_DATE")
	private Date cPlaylistDate;

	// 재생목록 C: 이름
	@Column(name = "C_PLAYLIST", nullable = false, length = 200)
	private String cPlaylist = "";

	// 재생목록 D: 확인 시간
	@Column(name = "D_PLAYLIST_DATE")
	private Date dPlaylistDate;

	// 재생목록 D: 이름
	@Column(name = "D_PLAYLIST", nullable = false, length = 200)
	private String dPlaylist = "";

	
	// 화면 GPS 위치
	@Column(name = "GPS_TIME")
	private Date gpsTime;
	
	// 위도
	@Column(name = "GPS_LAT", precision = 17, scale = 14)
	private Double gpsLat;
	
	// 경도
	@Column(name = "GPS_LNG", precision = 17, scale = 14)
	private Double gpsLng;
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	// WHO 컬럼들(E)
	

	public InvRTScreen() {}

	public InvRTScreen(int screenId) {
		
		this.screenId = screenId;
		
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

	public int getScreenId() {
		return screenId;
	}

	public void setScreenId(int screenId) {
		this.screenId = screenId;
	}

	public String getPlayerVer() {
		return playerVer;
	}

	public void setPlayerVer(String playerVer) {
		this.playerVer = playerVer;
	}

	public String getNextCmd() {
		return nextCmd;
	}

	public void setNextCmd(String nextCmd) {
		this.nextCmd = nextCmd;
	}

	public boolean isCmdFailed() {
		return cmdFailed;
	}

	public void setCmdFailed(boolean cmdFailed) {
		this.cmdFailed = cmdFailed;
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

	public int getCmdBy() {
		return cmdBy;
	}

	public void setCmdBy(int cmdBy) {
		this.cmdBy = cmdBy;
	}

	public Date getaPlaylistDate() {
		return aPlaylistDate;
	}

	public void setaPlaylistDate(Date aPlaylistDate) {
		this.aPlaylistDate = aPlaylistDate;
	}

	public String getaPlaylist() {
		return aPlaylist;
	}

	public void setaPlaylist(String aPlaylist) {
		this.aPlaylist = aPlaylist;
	}

	public Date getbPlaylistDate() {
		return bPlaylistDate;
	}

	public void setbPlaylistDate(Date bPlaylistDate) {
		this.bPlaylistDate = bPlaylistDate;
	}

	public String getbPlaylist() {
		return bPlaylist;
	}

	public void setbPlaylist(String bPlaylist) {
		this.bPlaylist = bPlaylist;
	}

	public Date getcPlaylistDate() {
		return cPlaylistDate;
	}

	public void setcPlaylistDate(Date cPlaylistDate) {
		this.cPlaylistDate = cPlaylistDate;
	}

	public String getcPlaylist() {
		return cPlaylist;
	}

	public void setcPlaylist(String cPlaylist) {
		this.cPlaylist = cPlaylist;
	}

	public Date getdPlaylistDate() {
		return dPlaylistDate;
	}

	public void setdPlaylistDate(Date dPlaylistDate) {
		this.dPlaylistDate = dPlaylistDate;
	}

	public String getdPlaylist() {
		return dPlaylist;
	}

	public void setdPlaylist(String dPlaylist) {
		this.dPlaylist = dPlaylist;
	}

	public Date getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(Date gpsTime) {
		this.gpsTime = gpsTime;
	}

	public Double getGpsLat() {
		return gpsLat;
	}

	public void setGpsLat(Double gpsLat) {
		this.gpsLat = gpsLat;
	}

	public Double getGpsLng() {
		return gpsLng;
	}

	public void setGpsLng(Double gpsLng) {
		this.gpsLng = gpsLng;
	}

	public String getKeeperVer() {
		return keeperVer;
	}

	public void setKeeperVer(String keeperVer) {
		this.keeperVer = keeperVer;
	}

}
