package kr.adnetwork.models.fnd;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.servlet.http.HttpSession;

import kr.adnetwork.utils.Util;


@Entity
@Table(name="FND_SETUP_FILES")
public class FndSetupFile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SETUP_FILE_ID")
	private int id;
	
	
	// 파일명
	//
	//   > 설치 파일의 이름 구조
	//
	//   {제품 대분류}_{제품 keyword}[.{분기 keyword}]_{version}[_{추가 설명 문자열}].{파일확장명}
	//
	//   - {제품 대분류}
	//     AdNetPlayer: 고정, 대소문자구분
	//   - {제품 keyword}: 현재까지 3가지 가능
	//     lite / sync / keeper
	//   - {분기 keyword}
	//     제품에 따른 세부 분기를 지정. 포함 시 반드시 '.'로 시작해야 함. alpha numeric 만 허용
	//     서명 앱의 경우는 플랫폼 키워드가 될 수도 있고 - 예) keep.qb2
	//     커스텀 앱의 분기 문자열이 될 수도 있음 - 예) lite.egs
	//   - {version}
	//     버전은 반드시 {major_ver}.{minor_ver}.{build_ver}의 형태를 취해야 한다.
	//     {major_ver}의 범위: 0 <= 숫자 <= 99
	//     {minor_ver}의 범위: 0 <= 숫자 <= 99
	//     {build_ver}의 범위: 0 <= 숫자 <= 999
	//     허용 예)
	//       1.0.0
	//	     2.10.105
	//     허용하지 않는 예)
	//       1      <- 형식 맞지 않음
	//	     1.2    <- 형식 맞지 않음
	//	     1.-1.1 <- 버전 범위 맞지 않음
	//	     1.2.a  <- 버전 범위 혹은 허용 문자가 맞지 않음
	//	     325.1.1  <- 허용 범위 벗어남
	//	     1.0.1012 <- 허용 범위 벗어남
	//   - {추가 설명 문자열}
	//     필요에 따라 추가되는 설명 문자열. 포함되는 것은 옵션이며, 포함 시 반드시 '_'로 시작해야 함
	//     설명 문자열로 '_' 혹은 '.'도 포함 가능함
	//   - {파일확장명}: 현재까지 2가지 가능
	//     apk / exe
	//     각각 Android용 및 Window용
	//   ex)
	//     AdNetPlayer_lite_2.1.2.apk
	//     AdNetPlayer_keep.qb_1.0.2.apk
	//     AdNetPlayer_sync.qb2_2.10.112.apk  <- sync 제품은 현재까지 루팅 필요가 없기 때문에 qb2의 키워드는 운영정책적으로는 불가능, 기술적으로는 가능
	//     AdNetPlayer_lite_2.1.2_신한은행용.apk
	//
	//   {추가 설명 문자열}의 다양한 이용이 가능하기 때문에 설치 파일의 이름은 다음의 방법으로 파싱
	//   1) 파일명에서 {파일확장명} 분리. 선행 '.' 제거
	//      잔여 형태: {제품 대분류}_{제품 keyword}[.{분기 keyword}]_{version}[_{추가 설명 문자열}]
	//   2) 나머지 문자열을 '_'로 구분(tokenizing), 최소 3개 그룹 이상이어야 함
	//      1st 그룹을 {제품 대분류},
	//      2nd 그룹은 {제품 keyword}만 있거나, 제품 keyword에 {분기 keyword}가 옵션 정보로 포함될 수 있음
	//      3rd 그룹은 버전 정보만 있거나, 버전 정보에 {추가 설명 문자열}이 옵션 정보로 포함될 수 있음
	//      잔여 형태: {version}[_{추가 설명 문자열}]
	//   3) 나머지 문자열을 '.'로 구분(tokenizing), 최소 1개 이상 그룹 존재
	//      1개 그룹 - 플랫폼 keyword 포함 안됨
	//      2개 그룹이상 - 마지막 위치 그룹(2개 그룹이면 2nd, 5개 그룹이면 5th)을 {플랫폼 keyword}로 인식
	//      잔여 형태: {version}[_{추가 설명 문자열}]
	//   4) 나머지 문자열을 '_'로 구분(tokenizing), 최소 1개 이상 그룹 존재
	//      1개 그룹 - 추가 설명 문자열 포함 안됨
	//      2개 그룹이상 - 1st 그룹이 버전 정보, 2nd 이하는 추가 설명 문자열
	//
	//
	@Column(name = "FILENAME", nullable = false, length = 100, unique = true)
	private String filename = "";
	
	// 파일크기
	@Column(name = "FILE_LENGTH", nullable = false)
	private long fileLength = 0;
	
	// 개선/변경 목록
	@Column(name = "UPDATE_LIST", nullable = false, length = 2000)
	private String updateList = "";
	
	// UUID
	//
	//   컨텐츠 폴더 하단, 직접적인 예측 접근 방지를 위해
	//
	@Column(name = "UUID", nullable = false, columnDefinition = "binary(16)")
	private UUID uuid;

	// 서비스중 여부
	//
	//   파일 업로드 후 준비되면, 실제 서비스 여부 플래그. 게시 개념
	//
	@Column(name = "ACTIVE_STATUS", nullable = false)
	private boolean activeStatus = false; 
	
	// 제품 keyword
	@Column(name = "PROD_KEYWORD", nullable = false, length = 20)
	private String prodKeyword = "";
	
	// 버전 번호
	//
	//   세자리 수 버전에 따른 약속된 정수형 버전 번호
	//     {major_ver}의 범위: 0 <= 숫자 <= 99
	//     {minor_ver}의 범위: 0 <= 숫자 <= 99
	//     {build_ver}의 범위: 0 <= 숫자 <= 999
	//
	@Column(name = "VER_NUMBER", nullable = false)
	private int verNumber = 0;
	
	// 플랫폼 keyword
	@Column(name = "PLAT_KEYWORD", nullable = false, length = 10)
	private String platKeyword = "";
	
	// 파일 해쉬 문자열
	//
	//  sha-256: 64byte
	//
	@Column(name = "HASH", nullable = false, length = 64)
	private String hash = "";
	
	
	// WHO 컬럼들(S)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date whoCreationDate;
	
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	private Date whoLastUpdateDate;
	
	@Column(name = "CREATED_BY", nullable = false)
	private int whoCreatedBy;
	
	@Column(name = "LAST_UPDATED_BY", nullable = false)
	private int whoLastUpdatedBy;
	
	@Column(name = "LAST_UPDATE_LOGIN", nullable = false)
	private int whoLastUpdateLogin;
	// WHO 컬럼들(E)
	
	
	// 제품 대분류
	@Transient
	private String majorCat = "";
	
	// 제품 버전
	@Transient
	private String version = "";
	
	
	// 다른 개체 연결(S)
	
	// 상위 개체: 컨텐츠 폴더
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "CTNT_FOLDER_ID", nullable = false)
	private FndCtntFolder ctntFolder;
	
	// 다른 개체 연결(E)
	

	public FndSetupFile() {}
	
	public FndSetupFile(FndCtntFolder ctntFolder, String filename, long fileLength, String updateList, UUID uuid, 
			String prodKeyword, int verNumber, String platKeyword, HttpSession session) {
		
		this.ctntFolder = ctntFolder;
		
		this.filename = filename;
		this.fileLength = fileLength;
		this.updateList = updateList;
		this.uuid = uuid;
		
		this.prodKeyword = prodKeyword;
		this.verNumber = verNumber;
		this.platKeyword = platKeyword;
		
		touchWhoC(session);
	}

	private void touchWhoC(HttpSession session) {
		this.whoCreatedBy = Util.loginUserId(session);
		this.whoCreationDate = new Date();
		touchWho(session);
	}
	
	public void touchWho(HttpSession session) {
		this.whoLastUpdatedBy = Util.loginUserId(session);
		this.whoLastUpdateDate = new Date();
		this.whoLastUpdateLogin = Util.loginId(session);
	}

	public String getSmartLength() {
		return Util.getSmartFileLength(fileLength);
	}
	
	public String getDispFileLength() {
		return new DecimalFormat("##,###,###,##0").format(fileLength) + " bytes";
	}
	
	public String getHttpFilename() {
		return ctntFolder.getWebPath() + "/" + ctntFolder.getName() + "/" + uuid.toString() + "/" + filename;
	}

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getUpdateList() {
		return updateList;
	}

	public void setUpdateList(String updateList) {
		this.updateList = updateList;
	}

	public boolean isActiveStatus() {
		return activeStatus;
	}

	public void setActiveStatus(boolean activeStatus) {
		this.activeStatus = activeStatus;
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

	public int getWhoCreatedBy() {
		return whoCreatedBy;
	}

	public void setWhoCreatedBy(int whoCreatedBy) {
		this.whoCreatedBy = whoCreatedBy;
	}

	public int getWhoLastUpdatedBy() {
		return whoLastUpdatedBy;
	}

	public void setWhoLastUpdatedBy(int whoLastUpdatedBy) {
		this.whoLastUpdatedBy = whoLastUpdatedBy;
	}

	public int getWhoLastUpdateLogin() {
		return whoLastUpdateLogin;
	}

	public void setWhoLastUpdateLogin(int whoLastUpdateLogin) {
		this.whoLastUpdateLogin = whoLastUpdateLogin;
	}

	public FndCtntFolder getCtntFolder() {
		return ctntFolder;
	}

	public void setCtntFolder(FndCtntFolder ctntFolder) {
		this.ctntFolder = ctntFolder;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getMajorCat() {
		return majorCat;
	}

	public void setMajorCat(String majorCat) {
		this.majorCat = majorCat;
	}

	public String getProdKeyword() {
		return prodKeyword;
	}

	public void setProdKeyword(String prodKeyword) {
		this.prodKeyword = prodKeyword;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPlatKeyword() {
		return platKeyword;
	}

	public void setPlatKeyword(String platKeyword) {
		this.platKeyword = platKeyword;
	}

	public int getVerNumber() {
		return verNumber;
	}

	public void setVerNumber(int verNumber) {
		this.verNumber = verNumber;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

}
