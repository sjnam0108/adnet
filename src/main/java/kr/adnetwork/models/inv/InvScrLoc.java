package kr.adnetwork.models.inv;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="INV_SCR_LOCS", uniqueConstraints = {
	@javax.persistence.UniqueConstraint(columnNames = {"SCREEN_ID", "TIME_1"}),
})
public class InvScrLoc {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SCR_LOC_ID")
	private int id;
	
	//
	// 화면 위치(GPS)
	//

	// 화면 번호
	//
	//   화면의 시퀀스 번호로 별도의 PK/FK 관계를 만들지 않음
	//
	@Column(name = "SCREEN_ID", nullable = false)
	private int screenId;
	
	// 시간1(등록 시간)
	@Column(name = "TIME_1", nullable = false)
	private Date time1;
	
	// 시간2(현재/마지막 시간)
	//
	//   - 동일 위치라 판단될 때의 현재 시간을 기록
	//   - time1, lat, lng 변경없이 time2만 업데이트
	//   - 결국 time1, time2는 기기 존재 from, to time이 됨
	//   - 행 자료 등록 시에 이 값은 null
	//   - 이 컬럼값은 null이거나 값이 있거나
	//
	@Column(name = "TIME_2")
	private Date time2;
	
	
	// 위도
	//
	//   - site에서의 문자열 처리와는 달리, 
	//     여기에 등록되는 자료는 null이나 빈 값이 될 수 없기 때문
	//
	@Column(name = "LAT", nullable = false, precision = 17, scale = 14)
	private double lat;
	
	// 경도
	@Column(name = "LNG", nullable = false, precision = 17, scale = 14)
	private double lng;

	
	// WHO 컬럼들(S)
	//
	//   업무 프로세스 특성 상 필요치 않아 생략함
	//
	// WHO 컬럼들(E)

	
	public InvScrLoc() {}
	
	public InvScrLoc(int screenId, Date time, double lat, double lng) {
		
		this.screenId = screenId;
		this.time1 = time;
		this.lat = lat;
		this.lng = lng;
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

	public Date getTime1() {
		return time1;
	}

	public void setTime1(Date time1) {
		this.time1 = time1;
	}

	public Date getTime2() {
		return time2;
	}

	public void setTime2(Date time2) {
		this.time2 = time2;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

}
