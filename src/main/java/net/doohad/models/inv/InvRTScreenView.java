package net.doohad.models.inv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import net.doohad.utils.Util;

@Entity
@Immutable
@Subselect(
	"SELECT s.screen_id as id, s.name, s.short_name, s.resolution, s.medium_id, st.region_name, " +
			"IFNULL(rt.player_ver, '') as player_ver, IFNULL(rt.keeper_ver, '') as keeper_ver, " +
			"IFNULL(rt.next_cmd, '') as next_cmd, IFNULL(rt.cmd_failed, false) as cmd_failed, " +
			"ot.date1 as last_file_date, ot.date2 as last_ad_request_date, ot.date3 as last_ad_report_date, ot.date4 as last_info_date, " +
			"ot.date5 as last_command_date, ot.date6 as last_command_report_date, ot.date7 as last_event_date, " +
			"rt.gps_time as gps_time " +
	"FROM inv_sites st, inv_screens s LEFT OUTER JOIN inv_rt_screens rt ON s.screen_id = rt.screen_id " +
			"LEFT OUTER JOIN rev_obj_touches ot ON s.screen_id = ot.obj_id AND ot.type = 'S' " +
	"WHERE s.effective_start_date <= CURRENT_TIMESTAMP() " +
	"AND (s.effective_end_date IS NULL OR s.effective_end_date >= CURRENT_TIMESTAMP()) " +
	"AND s.deleted = 0 AND s.active_status = 1 AND s.ad_server_available = 1 " +
	"AND s.site_id = st.site_id "
)
public class InvRTScreenView {
	// 화면 id와 동일
	@Id
	@Column(name = "ID")
	private int id;

	// 매체 id
	@Column(name = "MEDIUM_ID")
	private int mediumId;

	// 화면명
	@Column(name = "NAME")
	private String name;

	// 화면ID
	@Column(name = "SHORT_NAME")
	private String shortName;

	// 화면 해상도
	@Column(name = "RESOLUTION")
	private String resolution;

	
	// 지역명(시/군/구 이름)
	@Column(name = "REGION_NAME")
	private String regionName;
	
	
	// 플레이어 버전
	@Column(name = "PLAYER_VER")
	private String playerVer = "";
	
	// 키퍼 버전
	@Column(name = "KEEPER_VER")
	private String keeperVer = "";

	// 다음 명령(다음에 수행될 기기 명령)
	@Column(name = "NEXT_CMD")
	private String nextCmd = "";
	
	// 명령 실행 결과 실패
	@Column(name = "CMD_FAILED")
	private boolean cmdFailed;

	
	// 최근 파일 목록 요청일시
	@Column(name = "LAST_FILE_DATE")
	private Date lastFileDate;

	// 최근 광고 요청일시
	@Column(name = "LAST_AD_REQUEST_DATE")
	private Date lastAdRequestDate;

	// 최근 광고노출 결과 보고일시
	@Column(name = "LAST_AD_REPORT_DATE")
	private Date lastAdReportDate;

	// 최근 플레이어 시작일시
	@Column(name = "LAST_INFO_DATE")
	private Date lastInfoDate;

	// 최근 명령 확인일시
	@Column(name = "LAST_COMMAND_DATE")
	private Date lastCommandDate;

	// 최근 명령결과 보고일시
	@Column(name = "LAST_COMMAND_REPORT_DATE")
	private Date lastCommandReportDate;

	// 최근 이벤트 보고일시
	@Column(name = "LAST_EVENT_DATE")
	private Date lastEventDate;

	
	// 화면 GPS 위치
	@Column(name = "GPS_TIME")
	private Date gpsTime;

	
	// 요청 상태(기존의 최근 상태 lastStatus)
	@Transient
	private String reqStatus = "0";
	
	// 포함된 화면묶음명
	@Transient
	private String scrPackName;
	
	// 최근 기록
	@Transient
	private String lastTouches;
	
	
	public InvRTScreenView() {}
	
	
	public String getLastTouches() {
		
		ArrayList<ScreenAction> actions = new ArrayList<ScreenAction>();
		
		// 값이 null이더라도 정렬을 통해 뒤로 보내기 때문에 수용 가능
		actions.add(new ScreenAction("file", lastFileDate));
		actions.add(new ScreenAction("ad", lastAdRequestDate));
		actions.add(new ScreenAction("rpt", lastAdReportDate));
		actions.add(new ScreenAction("info", lastInfoDate));
		
		// 명령 관련은 둘(명령 확인일시, 명령결과 보고일시) 중 가장 최근 것만 포함
		if (lastCommandDate == null) {
			actions.add(new ScreenAction("cmd2", lastCommandReportDate));
		} else if (lastCommandReportDate == null) {
			actions.add(new ScreenAction("cmd1", lastCommandDate));
		} else {
			if (lastCommandReportDate.after(lastCommandDate)) {
				actions.add(new ScreenAction("cmd2", lastCommandReportDate));
			} else {
				actions.add(new ScreenAction("cmd1", lastCommandDate));
			}
		}

		actions.add(new ScreenAction("evt", lastEventDate));
		
		
		Collections.sort(actions, new Comparator<ScreenAction>() {
	    	public int compare(ScreenAction item1, ScreenAction item2) {
	    		if (item1.getDate() != null && item2.getDate() != null) {
	    			return item2.getDate().compareTo(item1.getDate());
	    		} else if (item1.getDate() == null) {
	    			return 1;
	    		} else if (item2.getDate() == null) {
	    			return -1;
	    		} else {
	    			return 0;
	    		}
	    	}
	    });

		String ret = "";
		int cnt = 0;
		for(ScreenAction act : actions) {
			if (act.getDate() != null) {
				if (Util.isValid(ret)) {
					ret += "|";
				}
				ret += act.getCode();
				cnt++;
				if (cnt >= 3) {
					break;
				}
			}
		}
		
		return ret;
	}
	
	
	private class ScreenAction {
		private String code;
		private Date date;
		
		public ScreenAction(String code, Date date) {
			this.code = code;
			this.date = date;
		}

		public String getCode() {
			return code;
		}

		public Date getDate() {
			return date;
		}
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMediumId() {
		return mediumId;
	}

	public void setMediumId(int mediumId) {
		this.mediumId = mediumId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNextCmd() {
		return nextCmd;
	}

	public void setNextCmd(String nextCmd) {
		this.nextCmd = nextCmd;
	}

	public String getReqStatus() {
		return reqStatus;
	}

	public void setReqStatus(String reqStatus) {
		this.reqStatus = reqStatus;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getPlayerVer() {
		return playerVer;
	}

	public void setPlayerVer(String playerVer) {
		this.playerVer = playerVer;
	}

	public boolean isCmdFailed() {
		return cmdFailed;
	}

	public void setCmdFailed(boolean cmdFailed) {
		this.cmdFailed = cmdFailed;
	}

	public String getRegionName() {
		return regionName;
	}

	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public String getScrPackName() {
		return scrPackName;
	}

	public void setScrPackName(String scrPackName) {
		this.scrPackName = scrPackName;
	}

	public Date getLastFileDate() {
		return lastFileDate;
	}

	public void setLastFileDate(Date lastFileDate) {
		this.lastFileDate = lastFileDate;
	}

	public Date getLastAdRequestDate() {
		return lastAdRequestDate;
	}

	public void setLastAdRequestDate(Date lastAdRequestDate) {
		this.lastAdRequestDate = lastAdRequestDate;
	}

	public Date getLastAdReportDate() {
		return lastAdReportDate;
	}

	public void setLastAdReportDate(Date lastAdReportDate) {
		this.lastAdReportDate = lastAdReportDate;
	}

	public Date getLastInfoDate() {
		return lastInfoDate;
	}

	public void setLastInfoDate(Date lastInfoDate) {
		this.lastInfoDate = lastInfoDate;
	}

	public Date getLastCommandDate() {
		return lastCommandDate;
	}

	public void setLastCommandDate(Date lastCommandDate) {
		this.lastCommandDate = lastCommandDate;
	}

	public Date getLastCommandReportDate() {
		return lastCommandReportDate;
	}

	public void setLastCommandReportDate(Date lastCommandReportDate) {
		this.lastCommandReportDate = lastCommandReportDate;
	}

	public Date getLastEventDate() {
		return lastEventDate;
	}

	public void setLastEventDate(Date lastEventDate) {
		this.lastEventDate = lastEventDate;
	}

	public void setLastTouches(String lastTouches) {
		this.lastTouches = lastTouches;
	}

	public Date getGpsTime() {
		return gpsTime;
	}

	public void setGpsTime(Date gpsTime) {
		this.gpsTime = gpsTime;
	}

	public String getKeeperVer() {
		return keeperVer;
	}

	public void setKeeperVer(String keeperVer) {
		this.keeperVer = keeperVer;
	}

}
