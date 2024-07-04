package net.doohad.models.rev;

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

import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;

@Entity
@Table(name="REV_EVENT_REPORTS")
public class RevEventReport {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EVENT_REPORT_ID")
	private int id;


	// 보고 유형
	//
	//   - I: 정보
	//   - W: 경고
	//   - E: 오류
	//
	@Column(name = "REPORT_TYPE", nullable = false, length = 1)
	private String reportType;
	
	// 범주(색상으로 구분됨)
	//
	//   - R: 빨간색
	//   - O: 주황색
	//   - Y: 노란색
	//   - G: 초록색
	//   - B: 파란색
	//   - P: 보라색
	//
	@Column(name = "CATEGORY", nullable = false, length = 1)
	private String category;
	
	// 기기/장비 유형
	//
	//   - P: STB(Player)
	//
	@Column(name = "EQUIP_TYPE", nullable = false, length = 1)
	private String equipType;

	// 기기 번호
	//
	//   기기의 시퀀스 번호로 별도의 PK/FK 관계를 만들지 않음
	//
	@Column(name = "EQUIP_ID", nullable = false)
	private int equipId;
	
	// 기기명
	@Column(name = "EQUIP_NAME", nullable = false, length = 200)
	private String equipName;
	
	// 이벤트명(등록할 이벤트의 이름)
	//
	//   - Restart: 재시작
	//   - Reboot: 리부트
	//
	@Column(name = "EVENT", nullable = false, length = 20)
	private String event;
	
	// 이벤트에 대한 추가 설명
	@Column(name = "DETAILS", nullable = false, length = 300)
	private String details = "";

	// 트리거 유형
	//
	//   - P: 내부 프로세스
	//   - C: 명령
	//   - A: 외부 앱
	//   - E: 기타
	//
	@Column(name = "TRIGGER_TYPE", nullable = false, length = 1)
	private String triggerType = "P";
	

	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 매체
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MEDIUM_ID", nullable = false)
	private KnlMedium medium;
	
	// 다른 개체 연결(E)

	
	public RevEventReport() {}
	
	public RevEventReport(InvScreen screen, String reportType, String category, String event, 
			String triggerType, String details) {
		
		this.medium = screen.getMedium();
		
		this.reportType = reportType;
		this.category = category;
		this.triggerType = triggerType;
		
		this.equipType = "P";
		this.equipId = screen.getId();
		this.equipName = screen.getName();
		
		this.event = event;
		this.details = details;
		
		
		this.whoCreationDate = new Date();
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getEquipType() {
		return equipType;
	}

	public void setEquipType(String equipType) {
		this.equipType = equipType;
	}

	public int getEquipId() {
		return equipId;
	}

	public void setEquipId(int equipId) {
		this.equipId = equipId;
	}

	public String getEquipName() {
		return equipName;
	}

	public void setEquipName(String equipName) {
		this.equipName = equipName;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Date getWhoCreationDate() {
		return whoCreationDate;
	}

	public void setWhoCreationDate(Date whoCreationDate) {
		this.whoCreationDate = whoCreationDate;
	}

	public KnlMedium getMedium() {
		return medium;
	}

	public void setMedium(KnlMedium medium) {
		this.medium = medium;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

}
