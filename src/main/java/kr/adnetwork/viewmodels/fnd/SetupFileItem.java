package kr.adnetwork.viewmodels.fnd;

import java.util.List;

import kr.adnetwork.utils.Util;

public class SetupFileItem {

	private String majorCat = "";				// 제품 대분류
	private String prodKeyword = "";			// 제품 keyword
	private String version = "";				// 제품 버전
	private String desc = "";					// 추가 설명 문자열
	private String platKeyword = "";			// 플랫폼 keyword -> 분기 keyword 로 변경
	private String fileExt = "";				// 파일 확장명
	
	private int verNumber = 0;					// 버전 넘버링, 
												// 이 값이 > 0 이면 유효한 파일명으로 판단
	
	private String filename;					// 초기 입력된 값 그대로
	private String lengthStr;					// 출력용 파일 크기
	
	private String errorMsg;					// 에러 메시지
	
	public SetupFileItem() {}
	
	public SetupFileItem(String filename) {
		
		//
		//   이전 방식:
		//
		//   {추가 설명 문자열}의 다양한 이용이 가능하기 때문에 설치 파일의 이름은 다음의 방법으로 파싱
		//   1) 파일명에서 {파일확장명} 분리. 선행 '.' 제거
		//      잔여 형태: {제품 대분류}_{제품 keyword}_{version}[_{추가 설명 문자열}][.{플랫폼 keyword}]
		//   2) 나머지 문자열을 '_'로 구분(tokenizing), 최소 3개 그룹 이상이어야 함
		//      1st 그룹을 {제품 대분류}, 2nd 그룹을 {제품 keyword}로 인식
		//      3rd 그룹은 버전 정보만 있거나, 버전 정보에 {추가 설명 문자열}, {플랫폼 keyword}가 옵션 정보로 포함될 수 있음
		//      잔여 형태: {version}[_{추가 설명 문자열}][.{플랫폼 keyword}]
		//   3) 나머지 문자열을 '.'로 구분(tokenizing), 최소 1개 이상 그룹 존재
		//      1개 그룹 - 플랫폼 keyword 포함 안됨
		//      2개 그룹이상 - 마지막 위치 그룹(2개 그룹이면 2nd, 5개 그룹이면 5th)을 {플랫폼 keyword}로 인식
		//      잔여 형태: {version}[_{추가 설명 문자열}]
		//   4) 나머지 문자열을 '_'로 구분(tokenizing), 최소 1개 이상 그룹 존재
		//      1개 그룹 - 추가 설명 문자열 포함 안됨
		//      2개 그룹이상 - 1st 그룹이 버전 정보, 2nd 이하는 추가 설명 문자열
		//
		//   새 방식:
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
		
		this.filename = filename;
		
		if (Util.isValid(filename) && 
				filename.indexOf(".") > 0 && filename.indexOf("_") > 0 && 
				!filename.endsWith(".")) {
			
			fileExt = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
			String part = filename.substring(0, filename.lastIndexOf("."));
			
			List<String> cats = Util.tokenizeValidStr(part, "_");
			if (cats.size() >= 3) {
				majorCat = cats.get(0);
				prodKeyword = cats.get(1);
				version = cats.get(2);
				
				if (cats.size() >= 3) {
					List<String> vers = Util.tokenizeValidStr(version, ".");
					if (vers.size() >= 3) {
						if (vers.size() > 3) {
							version = vers.get(0) + "." + vers.get(1) + "." + vers.get(2);
						}

						int v1 = Util.parseInt(vers.get(0));
						int v2 = Util.parseInt(vers.get(1));
						int v3 = Util.parseInt(vers.get(2));
						
						if (v1 > -1 && v2 > -1 && v3 > -1 && v1 < 100 && v2 < 100 && v3 < 1000) {
							verNumber = v1 * 100000 + v2 * 1000 + v3;
						}
					}
					
					List<String> prods = Util.tokenizeValidStr(prodKeyword, ".");
					if (prods.size() >= 2) {
						prodKeyword = prods.get(0);
						platKeyword = prods.get(1);
					}
					
					if (cats.size() > 3) {
						desc = cats.get(3);
					}
				}
			}
		}
	}

	
	public boolean isRightFormatted() {
		return verNumber > 0;
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

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getLengthStr() {
		return lengthStr;
	}

	public void setLengthStr(String lengthStr) {
		this.lengthStr = lengthStr;
	}
	
}
