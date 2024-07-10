package kr.adnetwork.models.sys;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.annotation.JsonIgnore;

import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.utils.Util;

@Entity
@Table(name="SYS_AUDIT_TRAILS")
public class SysAuditTrail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUDIT_TRAIL_ID")
	private int id;
	
	//
	// 감사 추적
	//

	// 매체 번호
	@Column(name = "MEDIUM_ID", nullable = false)
	private int mediumId;

	// 유형
	//
	//    A - 광고
	//    C - 광고 소재
	//    S - 화면
	//
	@Column(name = "OBJ_TYPE", length = 1, nullable = false)
	private String objType;

	// 개체(광고 등의) id
	@Column(name = "OBJ_ID", nullable = false)
	private int objId;

	
	//
	// 액션유형   대상유형   대상이름           대상 값		기타
	// ---------- ---------- ----------         ----------	----------
	// N          [blank]
	// X          [blank]
	// E          P                                         AuditTrailValue 에서 상세 항목값 변경 기록
	// S          Creat      240313_마스터즈_B  875			AuditTrailValue 에서 시작일, 종료일, 가중치 항목값 기록
	// S          CPack                         32|34|35    AuditTrailValue에 추가 항목 없으며, 대상 값은 "광고"가 아닌 "소재 파일" id
	// S          Time       48                 01101011... AuditTrailValue에 추가 항목 없음
	// S          Mobil      강남구             RG          AuditTrailValue에 추가 항목 없음
	// S          Mobil      롯데 잠실 1km      CR          AuditTrailValue에 추가 항목 없음
	// U          Creat      240313_마스터즈_B  875
	// U          Time       
	// U          Mobil      강남구             RG
	// U          Mobil      롯데 잠실 1km      CR
	// E          Mobil      롯데 잠실 1km      CR          AuditTrailValue 에서 상세 항목값 변경 기록(타겟팅 연산)
	// E          Mobil      순서 변경                      AuditTrailValue 에서 순서별로 타겟팅 나열
	//
	// Case
	//  - NA1: 광고 > 광고 - 추가
	//  - NA2: 광고 > 광고 - 복사추가
	//  - NA3: 광고 > 캠페인 - 캠페인과 광고를 함께
	//  - NC1: 광고 > 광고 소재 - 추가
	//  - NC2: 광고 > 광고 소재 - 다른 매체로 복사
	//
	//  - XA1: 광고 > 광고 - 삭제
	//  - XA2: 광고 > 캠페인 - 삭제
	//  - XC1: 광고 > 광고 소재 - 삭제
	//
	//  - EA1: 광고 > 광고 - 변경
	//  - EA2: 광고 > 특정 광고 > 광고 소재 - 변경
	//  - EA3: 광고 > 특정 광고 > 모바일 타겟팅 - And / Or
	//  - EA4: 광고 > 특정 광고 > 모바일 타겟팅 - 순서 변경
	//  - EA5: 광고 > 특정 광고 > 인벤 타겟팅 - And / Or
	//  - EA6: 광고 > 특정 광고 > 인벤 타겟팅 - 순서 변경
	//  - EA7: 광고 > 특정 광고 > 인벤 타겟팅 - 특정 타겟팅 변경
	//  - EA8: 광고 > 특정 광고 > 광고 목록 - 진행
	//  - EC1: 광고 > 광고 소재 - 변경
	//  - EC2: 광고 > 특정 광고 소재 > 소재 상세 - 수정
	//  - EC3: 광고 > 특정 광고 소재 > 인벤 타겟팅 - And / Or
	//  - EC4: 광고 > 특정 광고 소재 > 인벤 타겟팅 - 순서 변경
	//  - EC5: 광고 > 특정 광고 소재 > 인벤 타겟팅 - 특정 타겟팅 변경
	//  - EC6: 광고 > 특정 광고 소재 > 소재 목록 - 진행
	//
	//  - SA1: 광고 > 특정 광고 > 광고 소재 - 소재와 연결
	//  - SA2: 광고 > 특정 광고 > 광고 소재 - 묶음 광고 소재 변경
	//  - SA3: 광고 > 특정 광고 > 시간 타겟팅 - 등록
	//  - SA4: 광고 > 특정 광고 > 모바일 타겟팅 - 타겟팅 추가
	//  - SA5: 광고 > 특정 광고 > 인벤 타겟팅 - 타겟팅 추가
	//  - SC1: 광고 > 특정 광고 소재 > 소재 파일 - 업로드
	//  - SC2: 광고 > 특정 광고 소재 > 시간 타겟팅 - 등록
	//  - SC3: 광고 > 특정 광고 소재 > 인벤 타겟팅 - 타겟팅 추가
	//
	//  - UA1: 광고 > 특정 광고 > 광고 소재 - 연결 해제
	//  - UA2: 광고 > 특정 광고 > 시간 타겟팅 - 삭제
	//  - UA3: 광고 > 특정 광고 > 모바일 타겟팅 - 삭제
	//  - UA4: 광고 > 특정 광고 > 인벤 타겟팅 - 삭제
	//  - UC1: 광고 > 특정 광고 소재 > 소재 파일 - 삭제
	//  - UC2: 광고 > 특정 광고 소재 > 시간 타겟팅 - 삭제
	//  - UC3: 광고 > 특정 광고 소재 > 인벤 타겟팅 - 삭제
	//
	
	// 액션 유형
	//
	//    N - 생성(New)
	//    X - 삭제
	//    E - 변경(Edit)
	//    S - 대상 항목의 설정/추가(Set)
	//    U - 대상 항목의 설정 해제(Unset)
	//
	@Column(name = "ACT_TYPE", length = 1, nullable = false)
	private String actType;

	// 방법
	//
	//    F - 웹 애플리케이션 기본 폼
	//    B - 일괄 업로드
	//
	@Column(name = "METHOD", length = 1, nullable = false)
	private String method;
	
	
	// 대상 유형
	//
	//    [blank] 	- N/A			액션 유형 N / X
	//    P 		- 속성			액션 유형 E
	//    Creat     - 광고 소재		액션 유형 S or U
	//
	@Column(name = "TGT_TYPE", nullable = false, length = 5)
	private String tgtType = "";
	
	// 대상 이름
	@Column(name = "TGT_NAME", nullable = false, length = 100)
	private String tgtName = "";
	
	// 대상 값
	@Column(name = "TGT_VALUE", nullable = false, length = 200)
	private String tgtValue = "";
	
	
	// 액션 수행자 ID
	@Transient
	private String actedByShortName = "";
	
	// 대상: 여러 상황에 따른 대상을 간단한 문자열로 처리
	@Transient
	private String target = "";
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 다른 개체 연결(S)

	// 하위 개체: 감사 추적 항목 값
	@OneToMany(mappedBy = "auditTrail", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	private Set<SysAuditTrailValue> auditTrailValues = new HashSet<SysAuditTrailValue>(0);
	
	// 다른 개체 연결(E)
	
	
	public SysAuditTrail() {}
	
	public SysAuditTrail(AdcAd ad, String actType, String method, HttpSession session) {
		
		this(ad, actType, "", method, session);
	}
	
	public SysAuditTrail(AdcAd ad, String actType, String tgtType, String method, HttpSession session) {
		
		this.mediumId = ad.getMedium().getId();
		
		this.objType = "A";
		this.objId = ad.getId();
		
		this.actType = actType;
		this.method = method;
		
		this.tgtType = tgtType;
		this.tgtName = "";
		this.tgtValue = "";
		
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}
	
	public SysAuditTrail(AdcCreative creat, String actType, String method, HttpSession session) {
		
		this(creat, actType, "", method, session);
	}
	
	public SysAuditTrail(AdcCreative creat, String actType, String tgtType, String method, HttpSession session) {
		
		this.mediumId = creat.getMedium().getId();
		
		this.objType = "C";
		this.objId = creat.getId();
		
		this.actType = actType;
		this.method = method;
		
		this.tgtType = tgtType;
		this.tgtName = "";
		this.tgtValue = "";
		
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

	public int getMediumId() {
		return mediumId;
	}

	public void setMediumId(int mediumId) {
		this.mediumId = mediumId;
	}

	public String getObjType() {
		return objType;
	}

	public void setObjType(String objType) {
		this.objType = objType;
	}

	public int getObjId() {
		return objId;
	}

	public void setObjId(int objId) {
		this.objId = objId;
	}

	public String getActType() {
		return actType;
	}

	public void setActType(String actType) {
		this.actType = actType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTgtType() {
		return tgtType;
	}

	public void setTgtType(String tgtType) {
		this.tgtType = tgtType;
	}

	public String getTgtName() {
		return tgtName;
	}

	public void setTgtName(String tgtName) {
		this.tgtName = tgtName;
	}

	public String getTgtValue() {
		return tgtValue;
	}

	public void setTgtValue(String tgtValue) {
		this.tgtValue = tgtValue;
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

	@JsonIgnore
	public Set<SysAuditTrailValue> getAuditTrailValues() {
		return auditTrailValues;
	}

	public void setAuditTrailValues(Set<SysAuditTrailValue> auditTrailValues) {
		this.auditTrailValues = auditTrailValues;
	}

	public String getActedByShortName() {
		return actedByShortName;
	}

	public void setActedByShortName(String actedByShortName) {
		this.actedByShortName = actedByShortName;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

}
