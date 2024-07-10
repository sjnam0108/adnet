package kr.adnetwork.models.adn;

import javax.servlet.http.HttpServletRequest;

import kr.adnetwork.models.UserCookie;

public class AdnUserCookie extends UserCookie {

	public AdnUserCookie() {}
	
	public AdnUserCookie(HttpServletRequest request) {
		
		super(request);
		
		// 이후 작업 명시
	}
}
