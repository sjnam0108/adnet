package net.doohad.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.doohad.info.GlobalInfo;
import net.doohad.models.CustomComparator;
import net.doohad.models.UserCookie;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.fnd.FndViewType;
import net.doohad.models.inv.InvScrPackItem;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.knl.KnlAccount;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.knl.KnlUser;
import net.doohad.models.rev.RevChanAd;
import net.doohad.models.rev.RevDailyAchv;
import net.doohad.models.rev.RevHourlyPlay;
import net.doohad.models.rev.RevScrHourlyPlay;
import net.doohad.models.rev.RevSyncPackImp;
import net.doohad.models.service.AdcService;
import net.doohad.models.service.FndService;
import net.doohad.models.service.InvService;
import net.doohad.models.service.KnlService;
import net.doohad.models.service.OrgService;
import net.doohad.models.service.RevService;
import net.doohad.models.service.SysService;
import net.doohad.viewmodels.DropDownListItem;
import net.doohad.viewmodels.adc.AdcAdCreatFileObject;
import net.doohad.viewmodels.adc.AdcJsonFileObject;
import net.doohad.viewmodels.fnd.FndViewTypeItem;
import net.doohad.viewmodels.inv.InvSyncPackCompactItem;
import net.doohad.viewmodels.knl.KnlAccountItem;
import net.doohad.viewmodels.knl.KnlMediumCompactItem;
import net.doohad.viewmodels.knl.KnlMediumItem;
import net.doohad.viewmodels.rev.RevDailyAchvItem;
import net.doohad.viewmodels.rev.RevScrHrlyPlyAdStatItem;
import net.doohad.viewmodels.rev.RevSyncPackMinMaxItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

@Component
public class SolUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(SolUtil.class);
	
	static KnlService sKnlService;
	static AdcService sAdcService;
	static OrgService sOrgService;
	static RevService sRevService;
	static InvService sInvService;
	static FndService sFndService;
	static SysService sSysService;
	
	
	@Autowired
	public void setStaticKnlService(KnlService knlService) {
		SolUtil.sKnlService = knlService;
	}
	
	@Autowired
	public void setStaticAdcService(AdcService adcService) {
		SolUtil.sAdcService = adcService;
	}
	
	@Autowired
	public void setStaticOrgService(OrgService orgService) {
		SolUtil.sOrgService = orgService;
	}
	
	@Autowired
	public void setStaticRevService(RevService revService) {
		SolUtil.sRevService = revService;
	}
	
	@Autowired
	public void setStaticRevService(InvService invService) {
		SolUtil.sInvService = invService;
	}
	
	@Autowired
	public void setStaticFndService(FndService fndService) {
		SolUtil.sFndService = fndService;
	}
	
	@Autowired
	public void setStaticSysService(SysService sysService) {
		SolUtil.sSysService = sysService;
	}
	

	/**
	 * 사이트 설정 String 값의 동일 여부 반환(session 값으로)
	 */
	public static boolean propEqVal(HttpSession session, String code, String value) {
		String tmp = getProperty(session, code);
		
		return (Util.isValid(tmp) && tmp.equals(value));
	}

	/**
	 * 사이트 설정 String 값의 동일 여부 반환(사이트 번호로)
	 */
	public static boolean propEqVal(int siteId, String code, String value) {
		String tmp = getProperty(siteId, code);
		
		return (Util.isValid(tmp) && tmp.equals(value));
	}
	
	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code) {
		return getProperty(session, code, null, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code, Locale locale) {
		return getProperty(session, code, locale, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(HttpSession session, String code, Locale locale, 
			String defaultValue) {
		String value = getPropertyValue(Util.getSessionSiteId(session), code, locale);
		
		return Util.isValid(value) ? value : defaultValue;
	}
	
	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code) {
		return getProperty(siteId, code, null, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code, Locale locale) {
		return getProperty(siteId, code, locale, "");
	}

	/**
	 * 사이트 설정 String 값 획득
	 */
	public static String getProperty(int siteId, String code, Locale locale, 
			String defaultValue) {
		String value = getPropertyValue(siteId, code, locale);
		
		return Util.isValid(value) ? value : defaultValue;
	}
	
	private static String getPropertyValue(int siteId, String code, Locale locale) {
		
    	// [WAB] --------------------------------------------------------------------------
		//
		if (Util.isValid(code) && 
				(code.equals("logo.title") || code.equals("quicklink.max.menu"))) {
			return Util.getFileProperty(code);
		} else {
			return "";
		}
		//
    	// [WAB] --------------------------------------------------------------------------
    	// [SignCast] ext ----------------------------------------------------------- start
    	//
    	//
		
		/*
		return sOptService.getSiteOption(siteId, code, locale);
		*/
		
    	//
    	//
    	// [SignCast] ext ------------------------------------------------------------- end
	}

	

	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid) {
		return getPhysicalRoot(ukid, "");
	}

	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid, String medium) {
		if (Util.isNotValid(ukid)) {
			return null;
		}
		
		String rootDirPath = Util.getFileProperty("dir.rootPath");
		
		//
		//   Thumb: 썸네일 파일
		//   UpCtntTemp: 광고 소재 파일 업로드 서버 폴더
		//   UpTemp: 업로드 임시 폴더
		//   XlsTemplate: 인벤 일괄업로드용 엑셀 템플릿 파일 폴더
		//
		if (ukid.equals("Thumb")) {
            return Util.getValidRootDir(rootDirPath) + "thumbs";
		} else if (ukid.equals("UpCtntTemp")) {
            return Util.getValidRootDir(rootDirPath) + "upctnttemp";
		} else if (ukid.equals("UpTemp")) {
            return Util.getValidRootDir(rootDirPath) + "uptemp";
		} else if (ukid.equals("XlsTemplate")) {
            return Util.getValidRootDir(rootDirPath) + "templates";
		} else if (ukid.equals("Log")) {
            return Util.getValidRootDir(rootDirPath) + "logs";
		}

        return Util.getPhysicalRoot(ukid);
	}

	/**
	 * 물리적인 루트 디렉토리 획득
	 */
	public static String getPhysicalRoot(String ukid, String site, String repos) {
		if (ukid == null || ukid.isEmpty()) {
			return null;
		}
		
		/*
		String rootDirPath = Util.getFileProperty("dir.rootPath");
		
		if (ukid.equals("Repository")) {
			if (repos == null || repos.isEmpty()) {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site;
			} else {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site + "/Additional/" + repos;
			}
		} else if (ukid.equals("Schedule")) {
			if (repos == null || repos.isEmpty()) {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site + "/Schedule";
			} else {
				return Util.getValidRootDir(rootDirPath) + "repositories/" + site + "/Additional/" + repos
						+ "/Schedule";
			}
		}
		*/
		
		return null;
	}

	/**
	 * 관리 가능한 매체 목록 획득
	 */
	public static List<KnlMediumItem> getAvailMediumListByUserId(int userId) {
		
		ArrayList<KnlMediumItem> list = new ArrayList<KnlMediumItem>();
		
		KnlUser user = sKnlService.getUser(userId);
		if (user != null) {
			
			// 연결된 계정의 관리 영역 확인
			//   - "커널 관리 가능" -> 유효한 모든 매체
			//   - "매체 관리 가능" -> 계정의 관리 대상 매체 목록
			if (user.getAccount().isScopeKernel()) {
				List<KnlMedium> mediumList = sKnlService.getValidMediumList();
				for(KnlMedium medium : mediumList) {
					list.add(new KnlMediumItem(medium.getId(), medium.getShortName(),
							medium.getName()));
				}
			} else if (user.getAccount().isScopeMedium()) {
				List<String> media = Util.tokenizeValidStr(user.getAccount().getDestMedia());
				for(String m : media) {
					KnlMedium medium = sKnlService.getMedium(m);
					if (medium != null) {
						list.add(new KnlMediumItem(medium.getId(), medium.getShortName(),
								medium.getName()));
					}
				}
			}
			
			if (list.size() > 0) {
				Collections.sort(list, new Comparator<KnlMediumItem>() {
	    	    	public int compare(KnlMediumItem item1, KnlMediumItem item2) {
	    	    		return item1.getShortName().toLowerCase().compareTo(item2.getShortName().toLowerCase());
	    	    	}
	    	    });
			}
		}

		return list;
	}

	/**
	 * 관리 가능한 광고 제공 계정 목록 획득
	 */
	public static List<KnlAccountItem> getAvailAdAccountListByUserId(int userId) {
		
		ArrayList<KnlAccountItem> list = new ArrayList<KnlAccountItem>();
		
		KnlUser user = sKnlService.getUser(userId);
		if (user != null) {
			
			// 연결된 계정의 관리 영역 확인
			//   - "커널 관리 가능" -> 유효한 모든 광고 제공 계정
			//   - "광고 제공 가능" -> 연결된 현재 계정만
			if (user.getAccount().isScopeKernel()) {
				List<KnlAccount> accountList = sKnlService.getValidAccountList();
				for(KnlAccount account : accountList) {
					list.add(new KnlAccountItem(account.getId(), account.getName()));
				}
			} else if (user.getAccount().isScopeAd()) {
				list.add(new KnlAccountItem(user.getAccount().getId(), user.getAccount().getName()));
			}
			
			if (list.size() > 0) {
				Collections.sort(list, new Comparator<KnlAccountItem>() {
	    	    	public int compare(KnlAccountItem item1, KnlAccountItem item2) {
	    	    		return item1.getName().toLowerCase().compareTo(item2.getName().toLowerCase());
	    	    	}
	    	    });
			}
		}

		return list;
	}

	/**
	 * 유효 시작 및 종료일에 대한 유효성 검사 결과 획득
	 */
	public static boolean isEffectiveDate(Date startDate, Date endDate) {
		
		Date now = new Date();
		if (startDate == null || now.before(startDate)) {
			return false;
		}
		
		if (endDate != null && now.after(endDate)) {
			return false;
		}
		
		return true;
	}
	

	/**
	 * Hibernate: 표준 count 메소드
	 */
	public static int getCount(org.hibernate.Session session, Class<?> clazz) {
		
		if (session == null) return 0;
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(clazz)));

		return (session.createQuery(cq).getSingleResult()).intValue();
	}
	

	/**
	 * Hibernate: 표준 delete 메소드
	 */
	public static void delete(org.hibernate.Session session, Class<?> clazz, int id) {
		
		if (session != null) {
			session.delete(session.load(clazz, id));
		}
	}

	
	/**
	 * 시스템 백그라운드 작업에서 필요한 일련번호 획득
	 */
	public static int getBgNextSeq(String key) {
		
		Integer maxVal = GlobalInfo.BgMaxValueMap.get(Util.getFirstToken(key, "_"));
		if (maxVal == null || maxVal.intValue() == 0) {
			maxVal = 10;
			GlobalInfo.BgMaxValueMap.put(Util.getFirstToken(key, "_"), maxVal);
		}
		
		Integer currVal = GlobalInfo.BgCurrValueMap.get(key);
		if (currVal == null) {
			currVal = 1;
		} else {
			currVal ++;
			if (currVal > maxVal) {
				currVal = 1;
			}
		}
		
		GlobalInfo.BgCurrValueMap.put(key, currVal);
		
		return currVal;
	}

	
	/**
	 * 시스템 백그라운드 작업에서 필요한 일련번호 최고값 설정
	 */
	public static void setBgMaxSeq(String key, int value) {
		
		if (Util.isNotValid(key)) {
			return;
		}
		
		GlobalInfo.BgMaxValueMap.put(key, value <= 0 ? 10 : value * 3);
	}

	
	/**
	 * 주간 및 시간별 문자열을 통해 시간 합계를 획득
	 */
	public static int getHourCnt(String hourStr) {
		
		if (Util.isNotValid(hourStr) || hourStr.length() != 168) {
			return 0;
		}
		
		int cnt = 0;
		for(int i = 0; i < 168; i ++) {
			if (hourStr.substring(i, i + 1).equals("1")) {
				cnt ++;
			}
		}
		
		return cnt;
	}

	
	/**
	 * 24x7 시간 문자열이 현재 시간 포함 여부 획득
	 */
	public static boolean isCurrentOpHours(String opHour) {
		
		return isCurrentOpHours(opHour, new Date());
	}

	
	/**
	 * 24x7 시간 문자열이 현재 시간 포함 여부 획득
	 */
	public static boolean isCurrentOpHours(String opHour, Date date) {
		
		if (Util.isNotValid(opHour) || opHour.length() != 168 || date == null) {
			return false;
		}
		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int offDate = cal.get(Calendar.HOUR_OF_DAY);
		
		// 월요일 0, ..., 일요일 6
		int offDay = cal.get(Calendar.DAY_OF_WEEK);
		offDay -= 2;
		if (offDay < 0) {
			offDay = 6;
		}
		
		int offset = offDay * 24 + offDate;
		
		return opHour.substring(offset, offset + 1).equals("1");
		
	}

	
	/**
	 * 현재 상태 표시 문자열에 해당 시간(분)의 상태를 기록한 문자열 획득
	 */
	public static String getScrStatusLine(String prevStatusLine, Date date, String currStatus) {
		
		return getScrStatusLine(prevStatusLine, date, currStatus, false);
	}

	
	/**
	 * 현재 상태 표시 문자열에 해당 시간(분)의 상태를 기록한 문자열 획득
	 */
	public static String getScrStatusLine(String prevStatusLine, Date date, String currStatus, boolean forcedMode) {
		
		if (Util.isNotValid(prevStatusLine) || prevStatusLine.length() != 1440) {
			prevStatusLine = String.format("%1440s", "2").replace(' ', '2');
		}
		
		if (Util.isNotValid(currStatus) || currStatus.length() != 1) {
			return prevStatusLine;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int pos = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
		if (pos < 0 || pos > 1439) {
			return prevStatusLine;
		}

		boolean goAhead = true;
		if (!forcedMode) {
			String prevStatus = prevStatusLine.substring(pos, pos + 1);
			if (prevStatus.compareTo(currStatus) >= 0) {
				goAhead = false;
			}
		}
		
		if (goAhead) {
			if (pos == 0) {
				return currStatus + prevStatusLine.substring(1);
			} else if (pos == 1439) {
				return prevStatusLine.substring(0, 1439) + currStatus;
			}
			
			return prevStatusLine.substring(0, pos) + currStatus + prevStatusLine.substring(pos + 1);
		}
		
		return prevStatusLine;
	}

	
	/**
	 * 현재 상태 표시 문자열에 해당 시간(분)의 상태를 기록한 문자열 획득
	 */
	public static String getTodayScrStatusLine(String statusLine) {
		
		String defaultStr = String.format("%1440s", "9").replace(' ', '9');
		
		if (Util.isNotValid(statusLine) || statusLine.length() != 1440) {
			return defaultStr;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		int pos = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

		if (pos < 0 || pos > 1439) {
			return defaultStr;
		} else if (pos == 1439) {
			return statusLine;
		}
		
		pos ++;
		
		return statusLine.substring(0, pos) + defaultStr.substring(0, 1440 - pos);
	}

	
	/**
	 * 두 날짜에 걸쳐진 모든 분단위 날짜(시간) 목록 획득
	 */
	public static List<Date> getOnTimeMinuteDateListBetween(Date date1, Date date2) {
		
		ArrayList<Date> retList = new ArrayList<Date>();

		if (date1 == null || date2 == null) {
			return retList;
		}
		
		if (date1.compareTo(date2) > 0) {
			Date tmp = date1;
			date1 = date2;
			date2 = tmp;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date1);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		date1 = cal.getTime();
		
		cal.setTime(date2);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		date2 = cal.getTime();
		
		while (date1.compareTo(date2) <= 0) {
			retList.add(date1);
			date1 = Util.addMinutes(date1, 1);
		}
		
		return retList;
	}

	
	/**
	 * 광고 순서 문자열을 바탕으로 방송 소재 선택 및 그룹 내 순서 조정 문자열 획득
	 */
	public static String selectAdSeqList(String ordStr) {
		
		if (Util.isNotValid(ordStr)) {
			return "";
		}
		
		String ret = "";
		List<String> grpList = Util.tokenizeValidStr(ordStr, "|");
		for (String grpStr : grpList) {
			if (Util.isNotValid(grpStr)) {
				continue;
			}
			List<String> adList = Util.tokenizeValidStr(grpStr, ",");
			if (adList.size() == 0) {
				continue;
			} else {
				Collections.shuffle(adList);
				for(String ad : adList) {
					ret += ad + "|";
				}
			}
		}
		
		grpList = Util.tokenizeValidStr(ret, "|");
		ret = "";
		for (String s : grpList) {
			if (s.indexOf("_") > -1) {
				List<String> creatList = Util.tokenizeValidStr(s, "_");
				if (creatList.size() > 1) {
					ArrayList<String> ids = new ArrayList<String>();
					for (String c : creatList) {
						List<String> cp = Util.tokenizeValidStr(c, ":");
						if (cp.size() == 2) {
							int w = Util.parseInt(cp.get(1));
							if (w > 0) {
								for (int i = 0; i < w; i ++) {
									ids.add(cp.get(0));
								}
							}
						}
					}
					Collections.shuffle(ids);
					ret += ids.get(0) + "|";
				}
			} else {
				ret += s + "|";
			}
		}
		
		if (ret.equals("|")) {
			return "";
		} else {
			return Util.removeTrailingChar(ret);
		}
	}

	
	/**
	 * 광고 순서 문자열에 포함된 모든 광고(실제로는 광고/광고 소재) id 문자열 획득
	 */
	public static String getAllAdSeqList(String ordStr) {
		
		if (Util.isNotValid(ordStr)) {
			return "";
		}
		
		String ret = "";
		List<String> grpList = Util.tokenizeValidStr(ordStr, "|");
		for (String grpStr : grpList) {
			if (Util.isNotValid(grpStr)) {
				continue;
			}
			List<String> adList = Util.tokenizeValidStr(grpStr, ",");
			if (adList.size() == 0) {
				continue;
			} else {
				Collections.shuffle(adList);
				for(String ad : adList) {
					ret += ad + "|";
				}
			}
		}
		
		grpList = Util.tokenizeValidStr(ret, "|");
		ret = "";
		for (String s : grpList) {
			if (s.indexOf("_") > -1) {
				List<String> creatList = Util.tokenizeValidStr(s, "_");
				if (creatList.size() > 1) {
					ArrayList<String> ids = new ArrayList<String>();
					for (String c : creatList) {
						List<String> cp = Util.tokenizeValidStr(c, ":");
						if (cp.size() == 2) {
							int w = Util.parseInt(cp.get(1));
							if (w > 0) {
								for (int i = 0; i < w; i ++) {
									ids.add(cp.get(0));
								}
							}
						}
					}
					for(String s1 : ids) {
						ret += s1 + "|";
					}
				}
			} else {
				ret += s + "|";
			}
		}
		
		if (ret.equals("|")) {
			return "";
		} else {
			return Util.removeTrailingChar(ret);
		}
	}

	
	/**
	 * 두 날짜에 걸쳐진 모든 분단위 날짜(시간) 목록 획득
	 */
	public static int getCurrHourCount(RevScrHourlyPlay hourlyPlay) {

		if (hourlyPlay == null) {
			return 0;
		}
		
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());		
		
        switch (calendar.get(Calendar.HOUR_OF_DAY)) {
        case 0: return hourlyPlay.getCnt00();
        case 1: return hourlyPlay.getCnt01();
        case 2: return hourlyPlay.getCnt02();
        case 3: return hourlyPlay.getCnt03();
        case 4: return hourlyPlay.getCnt04();
        case 5: return hourlyPlay.getCnt05();
        case 6: return hourlyPlay.getCnt06();
        case 7: return hourlyPlay.getCnt07();
        case 8: return hourlyPlay.getCnt08();
        case 9: return hourlyPlay.getCnt09();
        case 10: return hourlyPlay.getCnt10();
        case 11: return hourlyPlay.getCnt11();
        case 12: return hourlyPlay.getCnt12();
        case 13: return hourlyPlay.getCnt13();
        case 14: return hourlyPlay.getCnt14();
        case 15: return hourlyPlay.getCnt15();
        case 16: return hourlyPlay.getCnt16();
        case 17: return hourlyPlay.getCnt17();
        case 18: return hourlyPlay.getCnt18();
        case 19: return hourlyPlay.getCnt19();
        case 20: return hourlyPlay.getCnt20();
        case 21: return hourlyPlay.getCnt21();
        case 22: return hourlyPlay.getCnt22();
        case 23: return hourlyPlay.getCnt23();
        }

		return 0;
	}

	
	/**
	 * 24x7 시간 문자열을 통해 남은 시간을 획득
	 */
	public static int getRemainOpHours(String opHour) {
		
		if (Util.isNotValid(opHour) || opHour.length() != 168) {
			return 0;
		}
		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		int offDate = cal.get(Calendar.HOUR_OF_DAY);
		
		// 월요일 0, ..., 일요일 6
		int offDay = cal.get(Calendar.DAY_OF_WEEK);
		offDay -= 2;
		if (offDay < 0) {
			offDay = 6;
		}
		
		int offset = offDay * 24 + offDate;
		int offsetEnd = (offDay + 1) * 24;
		
		int cnt = 0;
		for(int i = offset; i < offsetEnd; i++) {
			if (opHour.substring(i, i + 1).equals("1")) {
				cnt++;
			}
		}
		
		return cnt;
	}
	
	
    /**
	 * 현재 시간 기반 30분 정각의 시간 목록 획득
	 */
    public static List<DropDownListItem> get30MonitMinsDropDownList() {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		Date now = new Date();
		ArrayList<Date> dates = new ArrayList<Date>();
		dates.add(now);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);

		if (cal.get(Calendar.MINUTE) >= 30) {
			cal.set(Calendar.MINUTE, 30);
		} else {
			cal.set(Calendar.MINUTE, 0);
		}
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		Date firstDt = cal.getTime();
		for (int i = 0; i < 48; i ++) {
			dates.add(Util.addMinutes(firstDt, (-1) * i * 30));
		}
		
		String nowDate = Util.toSimpleString(now, "dd");
		String display = "", currDate = "";
		boolean isFirst = true;
		for(Date date : dates) {
			if (isFirst) {
				isFirst = false;
				display = "현재";
				
				list.add(new DropDownListItem(display, String.valueOf(-1)));
			} else {
				currDate = Util.toSimpleString(date, "dd");
				if (nowDate.equals(currDate)) {
					display = "오늘 " + Util.toSimpleString(date, "HH:mm");
				} else {
					display = "어제 " + Util.toSimpleString(date, "HH:mm");
				}
				
				list.add(new DropDownListItem(display, String.valueOf(date.getTime())));
			}
		}
		
		return list;
    }
	
	
    /**
     * 유효 시간 변수로 설정된 값 획득(중요)
     * 
	 * 		매체의 옵션 값 획득(유효 시간 30초)
	 */
    public static String getOptValue(int mediumId, String code) {

		if (Util.isNotValid(code) || mediumId < 0) {
			return "";
		}
		
		Date now = new Date();
		String key = "M" + mediumId + code;
		
		String value = getAutoExpVarValue(key, now);
		if (Util.isValid(value)) {
			return value;
		}
		
		value = sOrgService.getMediumOptValue(mediumId, code);
		
		value = putAutoExpVarValue(key, value, Util.addSeconds(now, 30));
		if (Util.isValid(value)) {
			return value;
		}
		
		// 값이 없거나, 아직 저장이 되지 않음
		if (Util.isValid(code)) {
			value = "";
			if (code.equals("impress.per.hour")) {
				value = "6";
			} else if (code.equals("inven.default")) {
				value = "{ }";
			} else if (code.equals("opt.list")) {
				value = "tester,테스터";
			}
			
			value = putAutoExpVarValue(key, value, Util.addSeconds(now, 30));
			if (Util.isValid(value)) {
				return value;
			}
		}
    	
		return "";
    }
	
	
    /**
	 * 유효 시간 있는 변수 값 획득
	 */
    public static String getAutoExpVarValue(String key) {

    	return getAutoExpVarValue(key, new Date());
    }
	
	
    /**
	 * 유효 시간 있는 변수 값 획득
	 */
    public static String getAutoExpVarValue(String key, Date now) {

		String value = GlobalInfo.AutoExpVarMap.get(key);
		if (Util.isValid(value)) {
			Date date = GlobalInfo.AutoExpVarTimeMap.get(key);
			if (date != null && date.after(now)) {
				return value;
			} else {
				GlobalInfo.AutoExpVarMap.put(key,  null);
			}
		}
    	
		return "";
    }
	
	
    /**
	 * 유효 시간 있는 변수 값 설정
	 */
    public static String putAutoExpVarValue(String key, String value, Date expTime) {
    	
    	if (Util.isValid(value)) {
        	GlobalInfo.AutoExpVarMap.put(key,  value);
        	GlobalInfo.AutoExpVarTimeMap.put(key, expTime);
    	}
    	
    	return value;
    }
	
	
    /**
	 * 유효 시간 있는 변수 값 설정
	 */
    public static void removeAutoExpVarValue(String key) {
    	
    	if (Util.isValid(key)) {
        	GlobalInfo.AutoExpVarMap.put(key,  null);
        	GlobalInfo.AutoExpVarTimeMap.put(key, null);
    	}
    }
	
	
    /**
	 * 위치 마커 이미지 URL 획득 - 모든 매체에 공통
	 */
    public static String getLocMarkerUrl() {
    	
    	return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-loc.png";
    }
	
	
    /**
	 * 화면(실제로는 사이트)의 아이콘 마커 이미지 URL 획득
	 */
    public static String getIconMarkerUrl(String venueType) {

		if (Util.webFileExists(GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-" + venueType + "-ico.png")) {
			return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-" + venueType + "-ico.png";
		}

		return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-default-ico.png";
	}
	
	
    /**
	 * 화면(실제로는 사이트)의 써클 마커 이미지 URL 획득
	 */
    public static String getCircleMarkerUrl(String catType) {

		if (Util.webFileExists(GlobalInfo.ApiTestServer + "/resources/shared/images/marker/cir-marker-" + catType + ".png")) {
			return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/cir-marker-" + catType + ".png";
		}

		return GlobalInfo.ApiTestServer + "/resources/shared/images/marker/marker-default-cir.png";
    }
	
	
    /**
	 * 서비스 응답시간의 체크 시작일시 획득
	 */
    public static Date getSvcRespTimeCheckDate(int timeMillis) {
    	
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.MILLISECOND, timeMillis * -1);
		
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
    }

	
	/**
	 * 상태 표시행 두 개를 하나로 합친 결과 획득
	 */
	public static String mergeScrStatusLines(String statusLine1, String statusLine2) {
		
		if (Util.isNotValid(statusLine1) && Util.isNotValid(statusLine2)) {
			return "";
		} else if (Util.isNotValid(statusLine1)) {
			return statusLine2;
		} else if (Util.isNotValid(statusLine2)) {
			return statusLine1;
		} else if (statusLine1.length() != 1440 || statusLine2.length() != 1440) {
			return statusLine1;
		}
		
		StringBuffer sb = new StringBuffer();
		
		for(int i = 0; i < 1440; i ++) {
			String s1 = statusLine1.substring(i, i + 1);
			String s2 = statusLine2.substring(i, i + 1);
			
			if (s1.compareTo(s2) < 0) {
				sb.append(s2);
			} else {
				sb.append(s1);
			}
		}
		
		return sb.toString();
	}
	
	
    /**
	 * 요청에 포함된 쿠키 중 currAdId의 값을 획득
	 */
	public static int getCurrAdIdFromRequest(HttpServletRequest request) {
		
		int ret = -1;
		UserCookie userCookie = new UserCookie(request);
		if (userCookie != null) {
			ret = userCookie.getCurrAdId();
		}
		
		return ret;
	}
	
	
    /**
	 * 세션에 "현재" 광고 정보를 기록
	 */
	public static int saveCurrAdsToSession(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			int campaignId, int adId) {
		
		int currAdId = adId;
		if (adId < 0) {
	    	currAdId = Util.parseInt(Util.cookieValue(request, "currAdId"));
		}
    	
    	boolean goAhead = false;
    	List<AdcAd> adList = sAdcService.getAdListByCampaignId(campaignId);
    	ArrayList<DropDownListItem> currAds = new ArrayList<DropDownListItem>();
    	for(AdcAd ad : adList) {
			String icon = "fa-audio-description";
			String text = ad.getName();
			String value = String.valueOf(ad.getId());
			String subIcon = ad.getStatus().equals("R") ? "fa-bolt-lightning" : "fa-blank";
			
			currAds.add(new DropDownListItem(icon, subIcon, text, value));
    		if (ad.getId() == currAdId) {
    			goAhead = true;
    		}
    	}
		Collections.sort(currAds, CustomComparator.DropDownListItemTextComparator);
    	
    	if (!goAhead) {
    		if (adList.size() == 0) {
    			currAdId = -1;
    		} else {
    			currAdId = Util.parseInt(currAds.get(0).getValue());
    		}
    	}
    	if ((goAhead && adId > 0) || !goAhead) {
    		response.addCookie(Util.cookie("currAdId", String.valueOf(currAdId)));
    	}
    	
		session.setAttribute("currAdId", String.valueOf(currAdId));
		session.setAttribute("currAds", currAds);
		
		return currAdId;
	}

	
    /**
	 * 전달된 캠페인의 상태카드를 설정
	 */
	public static void setCampaignStatusCard(AdcCampaign campaign) {
		
		// 진행 중인 캠페인에 대해서만 상태 카드 처리
		if (campaign != null && campaign.getStatus().equals("R")) {
			
			List<AdcAdCreative> adCreatList = sAdcService.getActiveAdCreativeListByCampaignId(campaign.getId());
			if (adCreatList.size() == 0) {
				campaign.setStatusCard("R");
			} else {
    			
    			boolean hasEffActive = false;
    			for(AdcAdCreative adCreative : adCreatList) {
    				if (Util.isBetween(Util.removeTimeOfDate(new Date()), adCreative.getStartDate(), adCreative.getEndDate())) {
    					hasEffActive = true;
    					break;
    				}
    			}
    			if (!hasEffActive) {
    				campaign.setStatusCard("Y");
    			}
			}
		}
	}
	
	
    /**
	 * 전달된 광고의 상태카드를 설정
	 */
	public static void setAdStatusCard(AdcAd ad) {
		
		// 진행 중인 광고에 대해서만 상태 카드 처리
		if (ad != null && ad.getStatus().equals("R")) {
			
			List<AdcAdCreative> adCreatList = sAdcService.getActiveAdCreativeListByAdId(ad.getId());
			if (adCreatList.size() == 0) {
				ad.setStatusCard("R");
			} else {
    			
    			boolean hasEffActive = false;
    			for(AdcAdCreative adCreative : adCreatList) {
    				if (Util.isBetween(Util.removeTimeOfDate(new Date()), adCreative.getStartDate(), adCreative.getEndDate())) {
    					hasEffActive = true;
    					break;
    				}
    			}
    			if (!hasEffActive) {
    				ad.setStatusCard("Y");
    			}
			}
		}
	}
	
	
    /**
	 * 광고 내의 오늘 기준 비중 획득
	 */
	public static float getCreatWeight(int adId, int adCreatId, Date date) {
		
		List<AdcAdCreative> adCreatList = sAdcService.getActiveAdCreativeListByAdId(adId);
		if (adCreatList.size() == 0) {
			return 0f;
		} else {
			
			int sum = 0;
			float myWeight = 0f;
			for(AdcAdCreative adCreative : adCreatList) {
				if (Util.isBetween(date, adCreative.getStartDate(), adCreative.getEndDate())) {
					sum += adCreative.getWeight();
					
					if (adCreative.getId() == adCreatId) {
						myWeight = adCreative.getWeight();
					}
				}
			}
			
			if (sum > 0) {
				return myWeight / (float) sum;
			}
		}
		
		return 0f;
	}
	
	
    /**
	 * 전달된 광고의 인벤 타겟팅 여부를 설정
	 */
	public static void setAdInvenTargeted(AdcAd ad) {
		
		if (ad != null) {
			ArrayList<Integer> targetIds = new ArrayList<Integer>();
			
			List<Tuple> countList = sAdcService.getAdTargetCountGroupByMediumAdId(ad.getMedium().getId());
			for(Tuple tuple : countList) {
				targetIds.add((Integer) tuple.get(0));
			}

			if (targetIds.contains(ad.getId())) {
				ad.setInvenTargeted(true);
			}
			
			// 모바일 타겟팅 여부 설정
			if (sAdcService.getMobTargetCountByAdId(ad.getId()) > 0) {
				ad.setMobTargeted(true);
			}
		}
	}
	
	
    /**
	 * 전달된 광고의 광고 소재 해상도를 설정
	 */
	public static void setAdResolutions(AdcAd ad) {
		
		if (ad != null) {
			List<AdcAdCreative> list = sAdcService.getAdCreativeListByAdId(ad.getId());
			ArrayList<String> resolutions = new ArrayList<String>();
			
			// 이 값이 유효하다는 것: 게시 유형이 지정되어 있고, 유효한 게시 크기(해상도) 존재
			String fixedReso = "";
			if (Util.isValid(ad.getViewTypeCode())) {
				fixedReso = sFndService.getViewTypeResoByCode(ad.getViewTypeCode());
			}
			
			for(AdcAdCreative adCreate : list) {
				List<AdcCreatFile> fileList = sAdcService.getCreatFileListByCreativeId(adCreate.getCreative().getId());
				for(AdcCreatFile creatFile : fileList) {
        			
        			// 20% 범위로 적합도 판정
					int fitness = Util.isValid(fixedReso) ?
							SolUtil.measureResolutionWith(creatFile.getResolution(), fixedReso, 20) :
							sAdcService.measureResolutionWithMedium(
		            				creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    				
        			String value = String.valueOf(fitness) + ":" + creatFile.getResolution();
        			if (!resolutions.contains(value)) {
        				resolutions.add(value);
        			}
				}
			}
			
			String reso = "";
			for(String r : resolutions) {
				if (Util.isValid(reso)) {
					reso += "|" + r;
				} else {
					reso = r;
				}
				
			}
			
			ad.setResolutions(reso);
		}
	}
	
	
    /**
	 * 전달된 광고의 고정 해상도(게시 유형 지정에 따른)를 설정
	 */
	public static void setAdFixedResolution(AdcAd ad) {
		
		if (ad != null) {
			ad.setFixedResolution(sFndService.getViewTypeResoByCode(ad.getViewTypeCode()));
		}
	}
	
	
    /**
	 * 전달된 광고의 일별 및 하루의 매체 분산 정책을 설정
	 */
	public static void setAdMediumImpTypes(AdcAd ad) {
		
		if (ad != null) {
			// 임의의 기본값으로 설정
			ad.setMediumImpDailyType("E");
			ad.setMediumImpHourlyType("E");
		}
	}
	
	
    /**
	 * 세션에 "현재" 광고 소재 정보를 기록
	 */
	public static int saveCurrCreativesToSession(HttpServletRequest request, HttpServletResponse response, HttpSession session,
			int advertiserId, int creativeId) {
		
		int currCreatId = creativeId;
		if (creativeId < 0) {
			currCreatId = Util.parseInt(Util.cookieValue(request, "currCreatId"));
		}
    	
    	boolean goAhead = false;
    	List<AdcCreative> creatList = sAdcService.getCreativeListByAdvertiserId(advertiserId);
    	ArrayList<DropDownListItem> currCreatives = new ArrayList<DropDownListItem>();
    	for(AdcCreative creative : creatList) {
			String icon = creative.getStatus().equals("A") ? "fa-clapperboard-play" : "fa-circle-dashed";
			String text = creative.getName();
			String value = String.valueOf(creative.getId());
			
			currCreatives.add(new DropDownListItem(icon, text, value));
    		if (creative.getId() == currCreatId) {
    			goAhead = true;
    		}
    	}
    	Collections.sort(currCreatives, CustomComparator.DropDownListItemTextComparator);
    	
    	if (!goAhead) {
    		if (creatList.size() == 0) {
    			currCreatId = -1;
    		} else {
    			currCreatId = Util.parseInt(currCreatives.get(0).getValue());
    		}
    	}
    	if ((goAhead && creativeId > 0) || !goAhead) {
    		response.addCookie(Util.cookie("currCreatId", String.valueOf(currCreatId)));
    	}
    	
		session.setAttribute("currCreatId", String.valueOf(currCreatId));
		session.setAttribute("currCreatives", currCreatives);
		
		return currCreatId;
	}
	
	
    /**
	 * 전달된 광고 소재의 인벤 타겟팅 여부를 설정
	 */
	public static void setCreativeInvenTargeted(AdcCreative creative) {
		
		if (creative != null) {
			ArrayList<Integer> targetIds = new ArrayList<Integer>();
			
			List<Tuple> countList = sAdcService.getCreatTargetCountGroupByMediumCreativeId(creative.getMedium().getId());
			for(Tuple tuple : countList) {
				targetIds.add((Integer) tuple.get(0));
			}

			if (targetIds.contains(creative.getId())) {
				creative.setInvenTargeted(true);
			}
		}
	}
	
	
    /**
	 * 전달된 광고 소재의 해상도를 설정
	 */
	public static void setCreativeResolutions(AdcCreative creative) {
		
		if (creative != null) {
			List<AdcCreatFile> fileList = sAdcService.getCreatFileListByCreativeId(creative.getId());
			
			String resolutions = "";
			
			// 이 값이 유효하다는 것: 게시 유형이 지정되어 있고, 유효한 게시 크기(해상도) 존재
			String fixedReso = "";
			if (Util.isValid(creative.getViewTypeCode())) {
				fixedReso = sFndService.getViewTypeResoByCode(creative.getViewTypeCode());
			}

			for(AdcCreatFile creatFile : fileList) {
    			
    			// 20% 범위로 적합도 판정
				int fitness = Util.isValid(fixedReso) ?
						SolUtil.measureResolutionWith(creatFile.getResolution(), fixedReso, 20) :
						sAdcService.measureResolutionWithMedium(
	            				creatFile.getResolution(), creatFile.getMedium().getId(), 20);
				
				if (Util.isValid(resolutions)) {
					resolutions += "|" + String.valueOf(fitness) + ":" + creatFile.getResolution();
				} else {
					resolutions = String.valueOf(fitness) + ":" + creatFile.getResolution();
				}
			}
			
			creative.setFileResolutions(resolutions);
		}
	}
	
	
    /**
	 * 전달된 광고 소재의 고정 해상도(게시 유형 지정에 따른)를 설정
	 */
	public static void setCreativeFixedResolution(AdcCreative creative) {
		
		if (creative != null) {
			creative.setFixedResolution(sFndService.getViewTypeResoByCode(creative.getViewTypeCode()));
		}
	}
	
	
    /**
	 * 하루 동안의 광고 노출량을 계산
	 */
	public static List<Integer> calcOneDayAdImpression(Date playDate, boolean initValSet) {
		
		//HashMap<String, Integer> map = new HashMap<String, Integer>();
		if (initValSet) {
			
			//
			//		SELECT hp.scr_hourly_play_id, s.floor_cpm, a.cpm, 
			//             hp.floor_cpm, hp.ad_cpm, a.budget, a.goal_value, a.goal_type
			//
    		List<Tuple> cpmList = sRevService.getScrHourlyPlayUpdateCpmListByPlayDate(playDate);
    		for(Tuple tuple : cpmList) {
    			// 기존의 변경 cpm에 대한 자료의 갱신뿐만 아니라,
    			//   광고의 예산, 보장횟수의 지정으로 인한 확정 cpm을 기록
    			int id = (Integer) tuple.get(0);
    			int floorCpm = (Integer) tuple.get(1);
    			int adCpm = (Integer) tuple.get(2);
    			
    			int currFloorCpm = (Integer) tuple.get(3);
    			int currAdCpm = (Integer) tuple.get(4);
    			int budget = (Integer) tuple.get(5);
    			int goalValue = (Integer) tuple.get(6);
    			String goalType = (String) tuple.get(7);

    			if (goalType.equals("I") && budget > 0 && goalValue > 0) {
    				int newAdCpm = (int)((long)budget * 1000l / (long)goalValue);
    				if (floorCpm != currFloorCpm || adCpm != newAdCpm) {
    	    			sRevService.updateScrHourlyPlayCpmValues(id, floorCpm, newAdCpm);
    				}
    			} else {
    				if (floorCpm != currFloorCpm || adCpm != currAdCpm) {
    	    			sRevService.updateScrHourlyPlayCpmValues(id, floorCpm, adCpm);
    				}
    			}
    		}
    		
    		/*
			List<InvScreen> scrList = sInvService.getScreenList();
			for(InvScreen screen : scrList) {
				map.put("S" + screen.getId(), screen.getFloorCpm());
			}
			
			List<AdcAd> adList = sAdcService.getAdList();
			for(AdcAd ad : adList) {
				map.put("A" + ad.getId(), ad.getCpm());
			}
			
			List<RevScrHourlyPlay> hpList = sRevService.getScrHourlyPlayListByPlayDate(playDate);
			for(RevScrHourlyPlay hp : hpList) {
				Integer floorCpm = map.get("S" + hp.getScreen().getId());
				Integer adCpm = map.get("A" + hp.getAd().getId());
				if (floorCpm != null && adCpm != null && 
						(hp.getFloorCpm() != floorCpm.intValue() || hp.getAdCpm() != adCpm.intValue())) {
					hp.setFloorCpm(floorCpm.intValue());
					hp.setAdCpm(adCpm.intValue());
					
					hp.touchWho();
					
					sRevService.saveOrUpdate(hp);
				}
			}
			*/
		}
		
		int addCnt = 0;
		int updCnt = 0;
		
		List<Tuple> list = sRevService.getScrHourlyPlayStatGroupByAdCreatPlayDate(playDate);
		for(Tuple tuple : list) {
			AdcAd ad = sAdcService.getAd((int) tuple.get(0));
			AdcCreative creative = sAdcService.getCreative((int) tuple.get(1));
			if (ad != null && creative != null) {
				RevHourlyPlay hp = sRevService.getHourlyPlay(ad, creative, playDate);
				if (hp == null) {
					hp = new RevHourlyPlay(playDate, ad, creative);
					addCnt++;
				} else {
					hp.touchWho();
					updCnt ++;
				}
				
				hp.setCnt00(((BigDecimal) tuple.get(2)).intValue());
				hp.setCnt01(((BigDecimal) tuple.get(3)).intValue());
				hp.setCnt02(((BigDecimal) tuple.get(4)).intValue());
				hp.setCnt03(((BigDecimal) tuple.get(5)).intValue());
				hp.setCnt04(((BigDecimal) tuple.get(6)).intValue());
				hp.setCnt05(((BigDecimal) tuple.get(7)).intValue());
				hp.setCnt06(((BigDecimal) tuple.get(8)).intValue());
				hp.setCnt07(((BigDecimal) tuple.get(9)).intValue());
				hp.setCnt08(((BigDecimal) tuple.get(10)).intValue());
				hp.setCnt09(((BigDecimal) tuple.get(11)).intValue());
				hp.setCnt10(((BigDecimal) tuple.get(12)).intValue());
				hp.setCnt11(((BigDecimal) tuple.get(13)).intValue());
				hp.setCnt12(((BigDecimal) tuple.get(14)).intValue());
				hp.setCnt13(((BigDecimal) tuple.get(15)).intValue());
				hp.setCnt14(((BigDecimal) tuple.get(16)).intValue());
				hp.setCnt15(((BigDecimal) tuple.get(17)).intValue());
				hp.setCnt16(((BigDecimal) tuple.get(18)).intValue());
				hp.setCnt17(((BigDecimal) tuple.get(19)).intValue());
				hp.setCnt18(((BigDecimal) tuple.get(20)).intValue());
				hp.setCnt19(((BigDecimal) tuple.get(21)).intValue());
				hp.setCnt20(((BigDecimal) tuple.get(22)).intValue());
				hp.setCnt21(((BigDecimal) tuple.get(23)).intValue());
				hp.setCnt22(((BigDecimal) tuple.get(24)).intValue());
				hp.setCnt23(((BigDecimal) tuple.get(25)).intValue());

				hp.setFailTotal(((BigDecimal) tuple.get(26)).intValue());
				hp.setCntScreen(((BigInteger) tuple.get(27)).intValue());
				hp.setActualAmount(((BigDecimal) tuple.get(28)).intValue());
				
				hp.calcTotal();

				sRevService.saveOrUpdate(hp);
			}
		}
		
		// 광고의 일별 달성값 기록
		list = sRevService.getHourlyPlayActualStatGroupByAdIdByPlayDate(playDate);
		for(Tuple tuple : list) {
			int adId = (Integer) tuple.get(0);
			int actualValue = ((BigDecimal) tuple.get(1)).intValue();
			int actualAmount = ((BigDecimal) tuple.get(2)).intValue();
			
			AdcAd ad = sAdcService.getAd(adId);
			if (ad != null && SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
					(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
					(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
				
				RevDailyAchv dailyAchv = SolUtil.checkDailyArch(ad, playDate);
				if (dailyAchv != null) {

					if (Util.isToday(playDate)) {
						dailyAchv.setGoalType(ad.getGoalType());
						dailyAchv.setTgtToday(ad.getTgtToday());
					}
					dailyAchv.setActualValue(actualValue);
					dailyAchv.setActualAmount(actualAmount);
					
					double achvRatio = 0;
					// 달성률
					//
					//   %값으로, 보통은 100에 수렴함. 집행정책에 따라 그 대상의 항목이 다름.
					//
					//   if 집행정책 == 노출량, 집행 노출량 / 하루 목표
					//   else if 집행정책 == 광고예산, 집행 금액 / 하루 목표
					//   else 달성률 == 0
					//
					if (dailyAchv.getTgtToday() > 0) {
						if (dailyAchv.getGoalType().equals("I")) {
							achvRatio = Math.round((double)actualValue * 10000d / (double)dailyAchv.getTgtToday()) / 100d;
						} else if (dailyAchv.getGoalType().equals("A")) {
							achvRatio = Math.round((double)actualAmount * 10000d / (double)dailyAchv.getTgtToday()) / 100d;
						}
					}
					dailyAchv.setAchvRatio(achvRatio);
					dailyAchv.touch();
					
					sRevService.saveOrUpdate(dailyAchv);
				}
			}
		}
		
		// 캠페인의 일별 달성값 기록
		list = sRevService.getHourlyPlayActualStatGroupByCampaignIdByPlayDate(playDate);
		for(Tuple tuple : list) {
			int campaignId = (Integer) tuple.get(0);
			int actualValue = ((BigDecimal) tuple.get(1)).intValue();
			int actualAmount = ((BigDecimal) tuple.get(2)).intValue();
			
			AdcCampaign campaign = sAdcService.getCampaign(campaignId);
			if (campaign != null && campaign.isSelfManaged() && 
					(campaign.getGoalType().equals("A") || campaign.getGoalType().equals("I"))) {
				
				RevDailyAchv dailyAchv = SolUtil.checkDailyArch(campaign, playDate);
				if (dailyAchv != null) {

					if (Util.isToday(playDate)) {
						dailyAchv.setGoalType(campaign.getGoalType());
						dailyAchv.setTgtToday(campaign.getTgtToday());
					}
					dailyAchv.setActualValue(actualValue);
					dailyAchv.setActualAmount(actualAmount);
					
					double achvRatio = 0;
					// 달성률
					//
					//   %값으로, 보통은 100에 수렴함. 집행정책에 따라 그 대상의 항목이 다름.
					//
					//   if 집행정책 == 노출량, 집행 노출량 / 하루 목표
					//   else if 집행정책 == 광고예산, 집행 금액 / 하루 목표
					//   else 달성률 == 0
					//
					if (dailyAchv.getTgtToday() > 0) {
						if (dailyAchv.getGoalType().equals("I")) {
							achvRatio = Math.round((double)actualValue * 10000d / (double)dailyAchv.getTgtToday()) / 100d;
						} else if (dailyAchv.getGoalType().equals("A")) {
							achvRatio = Math.round((double)actualAmount * 10000d / (double)dailyAchv.getTgtToday()) / 100d;
						}
					}
					dailyAchv.setAchvRatio(achvRatio);
					dailyAchv.touch();
					
					sRevService.saveOrUpdate(dailyAchv);
				}
			}
		}
		
		
		return Arrays.asList(addCnt, updCnt);
		
	}
	
	
    /**
	 * 대상 광고의 오늘 목표치 계산
	 */
	public static boolean calcAdTodayTargetValue(AdcAd ad) {

		if (ad == null) {
			return false;
		}
		
		int tgtToday = 0;
		
		if (SolUtil.isEffectiveDate(ad.getMedium().getEffectiveStartDate(), ad.getMedium().getEffectiveEndDate()) &&
				(ad.getStatus().equals("A") || ad.getStatus().equals("R")) &&
				(ad.getPurchType().equals("G") || ad.getPurchType().equals("N")) &&
				(ad.getGoalType().equals("A") || ad.getGoalType().equals("I"))) {
			
			//
			//   SELECT SUM(succ_tot), SUM(actual_amount)
			//
			Tuple tuple = sAdcService.getAdAccStatBeforePlayDate(ad.getId(), Util.removeTimeOfDate(new Date()));
			if (tuple != null) {
				BigDecimal sumView = (BigDecimal) tuple.get(0);
				BigDecimal sumAmount = (BigDecimal) tuple.get(1);
				
				int numerator = 0;
				if (ad.getGoalType().equals("I")) {
					// 노출량 based
					numerator = ad.getGoalValue();
					
					// 보장량이 설정되어 있을 경우, 매체 설정값 기반으로 목표량 설정
					int sysValue = ad.getSysValue();
					if (numerator > 0 && sysValue == 0) {
						// 보장량 설정되어 있고, 목표량이 미설정
						int sysValuePct = Util.parseInt(SolUtil.getOptValue(ad.getMedium().getId(), "sysValue.pct"));
						if (sysValuePct > 0) {
							sysValue = (int)Math.ceil((float)numerator * (float)sysValuePct / 100f);
						}
					}
					if (sysValue > numerator) {
						numerator = sysValue;
					}
					
					if (sumView != null) {
						numerator -= sumView.intValue();
					}
				} else if (ad.getGoalType().equals("A")) {
					// 광고예산 based
					numerator = ad.getBudget();
					
					if (sumAmount != null) {
						numerator -= sumAmount.intValue();
					}
				}
				
				if (numerator > 0) {
					tgtToday = getDayTargetOfProgressingAd(ad, numerator, 
							Util.removeTimeOfDate(new Date()));
				}
				
				try {
					ad.setTgtToday(tgtToday);
					sAdcService.saveOrUpdate(ad);
				} catch (Exception e) {
					logger.error("calcAdTodayTargetValue", e);
					
					return false;
				}
			}
		}
		
		return true;
	}
	
	
    /**
	 * 대상 캠페인의 오늘 목표치 계산
	 */
	public static boolean calcCampTodayTargetValue(AdcCampaign campaign) {

		if (campaign == null) {
			return false;
		}
		
		int tgtToday = 0;
		
		if (campaign.isSelfManaged() && (campaign.getStatus().equals("U") || campaign.getStatus().equals("R")) &&
				(campaign.getGoalType().equals("A") || campaign.getGoalType().equals("I"))) {
			
			List<AdcAd> list = sAdcService.getAdListByCampaignId(campaign.getId());
			ArrayList<Integer> ids = new ArrayList<Integer>();
			for(AdcAd ad : list) {
				ids.add(ad.getId());
			}
			
			//
			//   SELECT SUM(succ_tot), SUM(actual_amount)
			//
			Tuple tuple = sAdcService.getAdAccStatBeforePlayDate(ids, Util.removeTimeOfDate(new Date()));
			if (tuple != null) {
				BigDecimal sumView = (BigDecimal) tuple.get(0);
				BigDecimal sumAmount = (BigDecimal) tuple.get(1);
				
				int numerator = 0;
				if (campaign.getGoalType().equals("I")) {
					// 노출량 based
					numerator = campaign.getGoalValue();
					
					// 보장량이 설정되어 있을 경우, 매체 설정값 기반으로 목표량 설정
					int sysValue = campaign.getSysValue();
					if (numerator > 0 && sysValue == 0) {
						// 보장량 설정되어 있고, 목표량이 미설정
						int sysValuePct = Util.parseInt(SolUtil.getOptValue(campaign.getMedium().getId(), "sysValue.pct"));
						if (sysValuePct > 0) {
							sysValue = (int)Math.ceil((float)numerator * (float)sysValuePct / 100f);
						}
					}
					if (sysValue > numerator) {
						numerator = sysValue;
					}
					
					if (sumView != null) {
						numerator -= sumView.intValue();
					}
				} else if (campaign.getGoalType().equals("A")) {
					// 광고예산 based
					numerator = campaign.getBudget();
					
					if (sumAmount != null) {
						numerator -= sumAmount.intValue();
					}
				}
				
				if (numerator > 0) {
					tgtToday = getDayTargetOfProgressingCampaign(campaign, numerator, 
							Util.removeTimeOfDate(new Date()));
				}
				
				try {
					campaign.setTgtToday(tgtToday);
					sAdcService.saveOrUpdate(campaign);
				} catch (Exception e) {
					logger.error("calcCampTodayTargetValue", e);
					
					return false;
				}
			}
		}
		
		return true;
	}
	
	
    /**
	 * 광고/화면별 시간 목표치 획득
	 */
	public static Integer getScrAdHourlyGoalValue(RevScrHourlyPlay hourlyPlay, float weight) {
		
		if (hourlyPlay == null) {
			return null;
		}
		
		// 광고의 하루 노출한도
		int dailyScrCapMedium = Util.parseInt(SolUtil.getOptValue(hourlyPlay.getMedium().getId(), "freqCap.daily.screen"));
		
		// 활성 화면 수
		int activeScrCnt = Util.parseInt(SolUtil.getOptValue(hourlyPlay.getMedium().getId(), "activeCount.screen"));
		if (activeScrCnt < 1) {
			activeScrCnt = sInvService.getActiveScreenCountByMediumId(hourlyPlay.getMedium().getId());
		}
		
		// 시간당 노출계획(등록되지 않았을 경우, 시스템 내정값 6)
		int impPlanPerHour = Util.parseInt(SolUtil.getOptValue(hourlyPlay.getMedium().getId(), "impress.per.hour"), 6);
		
		
		return getScrAdHourlyGoalValue(hourlyPlay, dailyScrCapMedium, activeScrCnt, impPlanPerHour, weight);
	}	
	
	
    /**
	 * 광고/화면별 시간 목표치 획득
	 */
	public static Integer getScrAdHourlyGoalValue(RevScrHourlyPlay hourlyPlay, int dailyScrCapMedium, 
			int activeScrCnt, int impPlanPerHour, float weight) {
		
		if (hourlyPlay == null) {
			return null;
		}
		if (weight <= 0f) {
			return 0;
		}
		
		
		int ret = 0;
		int impAddRatio = hourlyPlay.getAd().getImpAddRatio();  // 현재 노출량 추가 제어
		
		int tgtToday = hourlyPlay.getAd().getTgtToday();			// 오늘 목표
		int finalRet = 0;
		if (tgtToday > 0) {
			String goalType = hourlyPlay.getAd().getGoalType();	// 집행 방법(A 광고 예산)
			
			// 1차 중간값
			float intrimVal = (float)tgtToday + (float) Math.ceil((float)tgtToday * (float)impAddRatio / 100f);
			if (activeScrCnt > 0) {
				intrimVal = (float)Math.ceil(intrimVal / (float)activeScrCnt);
			}
			//-

			// 2차 중간값: 만약 금액 기준이라면, 금액을 cpm 기반 횟수로 변경
			if (goalType.equals("A")) {
				if (hourlyPlay.getAdCpm() > 0) {
					intrimVal = intrimVal * 1000f / (float)hourlyPlay.getAdCpm();
				} else if (hourlyPlay.getFloorCpm() > 0) {
					intrimVal = intrimVal * 1000f / (float)hourlyPlay.getFloorCpm();
				} else {
					// 기준이 되는 두 값이 모두 0이므로
					return null;
				}
			}
			
			
			// 시간당 노출 계획이 아닌 경우에는, 기수행한 횟수를 고려하여 시간당 목표 횟수가 달라진다.
			//
			//   - intrimVal: 오늘 목표치 및 현재 노출량 추가 제어까지 계산된 값(오늘 기기당 노출 필요한 총량)
			//   - reqValues: 오늘 기 노출량을 제외한 노출 필요 잔량 총합(이번 시간에 노출량은 포함안됨)
			//
			//   - hourValue: 최종 계산값. 시간당 수행되어야 하는 목표횟수
			//   - currHourCount: 이번 시간에 노출된 횟수
			//
			//   - valueFromCurrHour0ToMidnight: 이번 시간 정각부터 오늘 끝까지 노출되어야 하는 총량
			//                                     이미 노출된 횟수도 포함되나 계산이 시간단위로 이루어지기 때문에 필요
			//     valueFromCurrHour0ToMidnight = reqValues + currHourCount
			//
			//int reqValues = (int)intrimVal - hourlyPlay.getSuccTotal();
			//int currHourCount = SolUtil.getCurrHourCount(hourlyPlay);
			//int valueFromCurrHour0ToMidnight = reqValues + currHourCount;
			int valueFromCurrHour0ToMidnight = (int)intrimVal - hourlyPlay.getSuccTotal() + SolUtil.getCurrHourCount(hourlyPlay);
			int hourValue = 0;
			
			// valueFromCurrHour0ToMidnight의 값이 음수(intrimVal 보다 기존 성공 수량이 더 많음)일 경우 처리
			if (valueFromCurrHour0ToMidnight < 0) {
				valueFromCurrHour0ToMidnight = 0;
			}

			InvScreen screen = hourlyPlay.getScreen();
			
			
			if (hourlyPlay.getAd().getImpHourlyType().equals("D")) {
				float factor = getCurrHourTargetFactor(hourlyPlay.getAd().getImpHourlyType(), 
						(Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
								? screen.getBizHour() : screen.getMedium().getBizHour());
				hourValue = (int)Math.round((float)valueFromCurrHour0ToMidnight * factor);
			} else {
				// 다른 값 설정이 없으면, 모든 시간 균등으로.
				int remainHours = SolUtil.getRemainOpHours((Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
						? screen.getBizHour() : screen.getMedium().getBizHour());
				
				if (remainHours <= 1) {
					hourValue = valueFromCurrHour0ToMidnight;
				} else {
					hourValue = valueFromCurrHour0ToMidnight / remainHours + (valueFromCurrHour0ToMidnight % remainHours > 0 ? 1 : 0);
				}
			}
			
			finalRet = (int)Math.round((float)hourValue * weight);
			
			// 시간당 진행되어야 하는 횟수가 0 초과(진행 대상 광고)이나,
			// 비중(weight)이 1 미만(여러 소재 등록)일 경우, 하나는 살린다
			if (hourValue > 0 && finalRet == 0) {
				finalRet = 1;
			}
			
		} else if (hourlyPlay.getAd().getDailyScrCap() > 0) {
			
			//
			// 매체의 값을 적용할 때가 아닌 광고 자체의 [화면당 하루 노출한도]의 값일 경우에만 적용
			//   이유: 매체의 값은 매체에 속한 모든 광고에 적용되기 때문에 특정 값의 지정이 어려운 경우가 많음
			//

			//
			// 오늘 목표치(tgtToday) == 0 && 화면당 하루 노출한도(dailyScrCap) > 0
			// 
			
			int valueFromCurrHour0ToMidnight = hourlyPlay.getAd().getDailyScrCap() - hourlyPlay.getSuccTotal() + SolUtil.getCurrHourCount(hourlyPlay);
			int hourValue = 0;
			
			// valueFromCurrHour0ToMidnight의 값이 음수(intrimVal 보다 기존 성공 수량이 더 많음)일 경우 처리
			if (valueFromCurrHour0ToMidnight < 0) {
				valueFromCurrHour0ToMidnight = 0;
			}

			InvScreen screen = hourlyPlay.getScreen();
			
			
			if (hourlyPlay.getAd().getImpHourlyType().equals("D")) {
				float factor = getCurrHourTargetFactor(hourlyPlay.getAd().getImpHourlyType(), 
						(Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
								? screen.getBizHour() : screen.getMedium().getBizHour());
				hourValue = (int)Math.round((float)valueFromCurrHour0ToMidnight * factor);
			} else {
				// 다른 값 설정이 없으면, 모든 시간 균등으로.
				int remainHours = SolUtil.getRemainOpHours((Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
						? screen.getBizHour() : screen.getMedium().getBizHour());
				
				if (remainHours <= 1) {
					hourValue = valueFromCurrHour0ToMidnight;
				} else {
					hourValue = valueFromCurrHour0ToMidnight / remainHours + (valueFromCurrHour0ToMidnight % remainHours > 0 ? 1 : 0);
				}
			}
			
			finalRet = (int)Math.round((float)hourValue * weight);
			
			// 시간당 진행되어야 하는 횟수가 0 초과(진행 대상 광고)이나,
			// 비중(weight)이 1 미만(여러 소재 등록)일 경우, 하나는 살린다
			if (hourValue > 0 && finalRet == 0) {
				finalRet = 1;
			}
			
		} else {
			ret = impPlanPerHour;
			if (ret < 1) {
				ret = 6;
			}
			
			// impAddRatio 적용
			finalRet = (int) Math.ceil(((float)ret + (float)ret * (float)impAddRatio / 100f) * weight);
		}
		
		
		// 시간당 최대 노출 수
		//
		// - 광고마다 시간별 최대값 설정 필요하지 않음
		// - 각 시간에 대한 시간별 최대값도 큰 의미 없음
		// - 현재 활성 기기 수가 매우 적다면(기기의 처음 시작 시간 즈음), 왜곡된(매우 큰) 값이 적용
		// - 광고가 타겟팅이 되었을 경우, 정확한 활성 기기 수 확인이 불가능
		// 
		// - 시간당 광고 노출 계획의 횟수에 특정값(2.5f)의 배수로 최고값을 제한
		//
		int maxRet = (int)Math.round((impPlanPerHour < 1 ? 6 : impPlanPerHour) * 2.5f);
		
		return finalRet > maxRet ? maxRet : finalRet;
	}
	
	
	private static float getCurrHourImpHourlyValue(String type, int idx) {
		
		if (Util.isNotValid(type) || !type.equals("D") || idx < 0 || idx > 23) {
			return 0f;
		}
		
		float[] values = {
				0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f,
				1f, 2f, 2.5f, 2.5f, 2f, 2.1f, 2.2f, 2.2f,
				2f, 2f, 2f, 0.3f, 0.1f, 0.1f, 0.1f, 0.1f
			};
		
		return values[idx];
	}
	
	/**
	 * 현재 시간에 필요한 노출량의 잔량 대비 비율 획득
	 */
	public static float getCurrHourTargetFactor(String type, String opHour) {
		
		if (Util.isNotValid(opHour) || opHour.length() != 168 || Util.isNotValid(type)) {
			return 1f;
		}
		
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
		int offDate = cal.get(Calendar.HOUR_OF_DAY);
		
		// 월요일 0, ..., 일요일 6
		int offDay = cal.get(Calendar.DAY_OF_WEEK);
		offDay -= 2;
		if (offDay < 0) {
			offDay = 6;
		}
		
		int offset = offDay * 24 + offDate;
		int offsetEnd = (offDay + 1) * 24;
		
		float sum = 0f;
		float thisValue = 0f;
		for(int i = offset; i < offsetEnd; i++) {
			if (opHour.substring(i, i + 1).equals("1")) {
				if (type.equals("D")) {
					sum += getCurrHourImpHourlyValue("D", i - offDay * 24);
				} else {
					sum += 1f;
				}
				
				if (i == offset) {
					if (type.equals("D")) {
						thisValue = getCurrHourImpHourlyValue("D", i - offDay * 24);
					} else {
						thisValue = 1f;
					}
				}
			}
		}
		
		if (sum == 0f) {
			return 1f;
		}
		
		return thisValue / sum;
	}
	
	
    /**
	 * 화면이 포함된 화면 묶음의 이름을 획득
	 */
	public static String getScreenPackNamesByScreenId(int screenId) {
		
		List<InvScrPackItem> items = sInvService.getScrPackItemListByScreenId(screenId);
		if (items == null || items.size() == 0) {
			return "";
		}

		ArrayList<String> list = new ArrayList<String>();
		for(InvScrPackItem item : items) {
			list.add(item.getScrPack().getName());
		}
		
		Collections.sort(list);
		
		String ret = "";
		for(String s : list) {
			if (Util.isValid(ret)) {
				ret += "|";
			}
			ret += s;
		}
		
		return ret;
	}
	
	
    /**
	 * 화면의 요청 상태 설정
	 */
	public static void setScreenReqStatus(InvScreen screen) {
		
		if (screen != null) {
			String status = GlobalInfo.InvenLastStatusMap.get("SC" + String.valueOf(screen.getId()));
			if (Util.isNotValid(status)) {
				status = "0";
			}
			
			screen.setReqStatus(status);
		}
	}
	
	
    /**
	 * 사이트의 요청 상태 설정
	 */
	public static void setSiteReqStatus(InvSite site) {
		
		if (site != null) {
			String status = GlobalInfo.InvenLastStatusMap.get("ST" + String.valueOf(site.getId()));
			if (Util.isNotValid(status)) {
				status = "0";
			}
			
			site.setReqStatus(status);
		}
	}
	
	
    /**
	 * 매체에서 사용가능한 모든 게시 유형 ID 목록 획득
	 */
	public static List<String> getViewTypeListByMediumId(int mediumId) {

		ArrayList<String> retList = new ArrayList<String>();
		
		List<FndViewType> viewTypeList = sFndService.getViewTypeList();
		for(FndViewType viewType : viewTypeList) {
			String ID = viewType.getCode();
			List<String> media = Util.tokenizeValidStr(viewType.getDestMedia());
			for(String m : media) {
				KnlMedium medium = sKnlService.getMedium(m);
				if (medium != null) {
					if (medium.getId() == mediumId && !retList.contains(ID)) {
						retList.add(ID);
					}
				}
			}
		}

		Collections.sort(retList);
		
		return retList;
	}
	
	
    /**
	 * 전달된 해상도와 정해진 해상도의 값을 비교
	 */
	public static int measureResolutionWith(String resolution, String fixedResolution, int boundVal) {
		
		//  반환값:
		//
		//     1  -  가장 적합(정해진 해상도와 정확히 일치)
		//     0  -  정해진 해상도와 boundVal 범위 내 비율
		//    -1  -  정해진 해상도와 boundVal 범위를 벗어남
		
		if (Util.isValid(resolution) && Util.isValid(fixedResolution)) {
			if (resolution.equals(fixedResolution)) {
				return 1;
			}
			
			float ratio = Util.getResolutionRatio(resolution);
			if (ratio > 0f) {
				float rt = Util.getResolutionRatio(fixedResolution);
				if (rt > 0f) {
					if (Util.getPctDifference(ratio, rt) <= boundVal) {
						return 0;
					}
				}
			}
		}
		
		return -1;
	}
	
	
    /**
	 * 대상 광고의 해당 일의 하루 목표치 반환
	 */
	public static int getDayTargetOfProgressingAd(AdcAd ad, int remainTotal, Date date) {
		
		if (ad == null || remainTotal <= 0) {
			return 0;
		}
		
		
		// 광고 카운팅 시작일
		//Date startCntDate = ad.getStartDate();
		
		// 전체 광고 일수
		int days = Util.getDaysBetween(ad.getStartDate(), ad.getEndDate());
		
		// 현재 일수 인덱스(1..전체 광고 일수)
		//
		//   잔여 일수 = (전체 광고 일수) - (현재 일수 인덱스) + 1;
		//   현재 일수 인덱스 = (전체 광고 일수) - (잔여 일수) + 1;
		//
		int idxDays = 1;
		
		// 현재 일수 인덱스를 광고의 상태에서 가져가기 보다, 날짜의 실제 계산을 통해 획득
		/*
		// 광고가 예약(A) 혹은 진행(R)의 경우에만 진행
		if (ad.getStatus().equals("A")) {
		} else if (ad.getStatus().equals("R")) {
			idxDays = days - Util.getDaysBetween(new Date(), ad.getEndDate()) + 1;
		} else {
			return 0;
		}
		*/
		if (date.before(ad.getStartDate())) {
			// 기본 값 1
		} else if (date.after(ad.getEndDate())) {
			idxDays = days;
		} else {
			idxDays = days - Util.getDaysBetween(date, ad.getEndDate()) + 1;
		}

		
		// 일별 광고 분산 정책이 모든 날짜 균등(E), 통계 기반 요일별 차등(W)
		if (ad.getImpDailyType().equals("W")) {
			
			if (idxDays == days) {
				return remainTotal;
			}
			
			List<Integer> calcStatList = sRevService.getPctValueListByMeidumIdDayOfWeek( ad.getMedium().getId(),
					 Util.addDays(Util.removeTimeOfDate(new Date()), -15),  
					 Util.addDays(Util.removeTimeOfDate(new Date()), -2));
			
			Calendar cal = Calendar.getInstance();
			long sum = 0l, currValue = 0;
			for(int i = idxDays - 1; i < days; i++) {
				Date d = Util.addDays(ad.getStartDate(), i);
				cal.setTime(d);
				switch(cal.get(Calendar.DAY_OF_WEEK)) {
				case 1: sum += calcStatList.get(6); break;
				case 2: sum += calcStatList.get(0); break;
				case 3: sum += calcStatList.get(1); break;
				case 4: sum += calcStatList.get(2); break;
				case 5: sum += calcStatList.get(3); break;
				case 6: sum += calcStatList.get(4); break;
				case 7: sum += calcStatList.get(5); break;
				}
				
				if (i == idxDays - 1) {
					currValue = (int)sum;
				}
			}
			
			return (int)((double)remainTotal / (double)sum * (double)currValue);
		}
		
		return (int)Math.ceil(remainTotal / (days - idxDays + 1));
	}
	
	
    /**
	 * 대상 캠페인의 해당 일의 하루 목표치 반환
	 */
	public static int getDayTargetOfProgressingCampaign(AdcCampaign campaign, int remainTotal, Date date) {
		
		if (campaign == null || remainTotal <= 0) {
			return 0;
		}
		
		
		// 광고 카운팅 시작일
		//Date startCntDate = ad.getStartDate();
		
		// 전체 광고 일수
		int days = Util.getDaysBetween(campaign.getStartDate(), campaign.getEndDate());
		if (days < 1) {
			return 0;
		}
		
		// 현재 일수 인덱스(1..전체 광고 일수)
		//
		//   잔여 일수 = (전체 광고 일수) - (현재 일수 인덱스) + 1;
		//   현재 일수 인덱스 = (전체 광고 일수) - (잔여 일수) + 1;
		//
		int idxDays = 1;
		
		// 현재 일수 인덱스를 캠페인의 상태에서 가져가기 보다, 날짜의 실제 계산을 통해 획득
		/*
		// 캠페인이 시작전(U) 혹은 진행(R)의 경우에만 진행
		if (campaign.getStatus().equals("U")) {
		} else if (campaign.getStatus().equals("R")) {
			idxDays = days - Util.getDaysBetween(new Date(), campaign.getEndDate()) + 1;
		} else {
			return 0;
		}
		*/
		if (date.before(campaign.getStartDate())) {
			// 기본 값 1
		} else if (date.after(campaign.getEndDate())) {
			idxDays = days;
		} else {
			idxDays = days - Util.getDaysBetween(date, campaign.getEndDate()) + 1;
		}

		
		// 일별 광고 분산 정책이 모든 날짜 균등(E), 통계 기반 요일별 차등(W)
		if (campaign.getImpDailyType().equals("W")) {
			
			if (idxDays == days) {
				return remainTotal;
			}
			
			List<Integer> calcStatList = sRevService.getPctValueListByMeidumIdDayOfWeek( campaign.getMedium().getId(),
					 Util.addDays(Util.removeTimeOfDate(new Date()), -15),  
					 Util.addDays(Util.removeTimeOfDate(new Date()), -2));
			
			Calendar cal = Calendar.getInstance();
			long sum = 0l, currValue = 0;
			for(int i = idxDays - 1; i < days; i++) {
				Date d = Util.addDays(campaign.getStartDate(), i);
				cal.setTime(d);
				switch(cal.get(Calendar.DAY_OF_WEEK)) {
				case 1: sum += calcStatList.get(6); break;
				case 2: sum += calcStatList.get(0); break;
				case 3: sum += calcStatList.get(1); break;
				case 4: sum += calcStatList.get(2); break;
				case 5: sum += calcStatList.get(3); break;
				case 6: sum += calcStatList.get(4); break;
				case 7: sum += calcStatList.get(5); break;
				}
				
				if (i == idxDays - 1) {
					currValue = (int)sum;
				}
			}
			
			return (int)((double)remainTotal / (double)sum * (double)currValue);
		}
		
		return (int)Math.ceil(remainTotal / (days - idxDays + 1));
	}
    
	
    /**
	 * 일별 달성 기록 생성 확인(캠페인)
	 */
	public static RevDailyAchv checkDailyArch(AdcCampaign campaign, Date playDate) {
		
		RevDailyAchv ret = null;
		if (playDate != null && campaign != null && campaign.isSelfManaged() && campaign.getTgtToday() > 0) {
			ret = sRevService.getDailyAchvByTypeIdPlayDate("C", campaign.getId(), playDate);
			if (ret == null && Util.isToday(playDate)) {
				ret = new RevDailyAchv(campaign, playDate);
				sRevService.saveOrUpdate(ret);
			}
		}
		
		return ret;
	}
	
	
    /**
	 * 일별 달성 기록 생성 확인(광고)
	 */
	public static RevDailyAchv checkDailyArch(AdcAd ad, Date playDate) {
		
		RevDailyAchv ret = null;
		if (playDate != null && ad != null && ad.getTgtToday() > 0) {
			ret = sRevService.getDailyAchvByTypeIdPlayDate("A", ad.getId(), playDate);
			if (ret == null && Util.isToday(playDate)) {
				ret = new RevDailyAchv(ad, playDate);
				sRevService.saveOrUpdate(ret);
			}
		}
		
		return ret;
	}
	
	
    /**
	 * 지정된 광고의 일별 달성 기록 계산
	 */
	public static boolean calcDailyAchvesByAdId(int adId) {
		
		AdcAd ad = sAdcService.getAd(adId);
		if (ad == null) {
			return false;
		}
		
		
		ArrayList<RevDailyAchvItem> achvList = new ArrayList<RevDailyAchvItem>();
		
		List<Tuple> list = sRevService.getHourlyPlayStatGroupByPlayDateAdIdInBetween(
				new ArrayList<>( Arrays.asList(ad.getId())), ad.getStartDate(), ad.getEndDate());
		int sumValue = 0, sumAmount = 0;
		for (Tuple tuple : list) {
			
			//		SELECT play_date, SUM(succ_tot), 
			//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
			//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
			//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
			//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
			//             sum(actual_amount)
			
			Date date = (Date) tuple.get(0);
			int actualValue = ((BigDecimal) tuple.get(1)).intValue();
			int actualAmount = ((BigDecimal) tuple.get(26)).intValue();
			
			
			int tgtToday = 0;
			int numerator = 0;
			int actual = 0;
			
			if (ad.getGoalType().equals("I")) {
				// 노출량 based
				numerator = ad.getGoalValue();
				
				// 보장량이 설정되어 있을 경우, 매체 설정값 기반으로 목표량 설정
				int sysValue = ad.getSysValue();
				if (numerator > 0 && sysValue == 0) {
					// 보장량 설정되어 있고, 목표량이 미설정
					int sysValuePct = Util.parseInt(SolUtil.getOptValue(ad.getMedium().getId(), "sysValue.pct"));
					if (sysValuePct > 0) {
						sysValue = (int)Math.ceil((float)numerator * (float)sysValuePct / 100f);
					}
				}
				if (sysValue > numerator) {
					numerator = sysValue;
				}
				numerator -= sumValue;
				
				actual = actualValue;
			} else if (ad.getGoalType().equals("A")) {
				// 광고예산 based
				numerator = ad.getBudget();
				numerator -= sumAmount;
				
				actual = actualAmount;
			}
			
			if (numerator > 0) {
				tgtToday = SolUtil.getDayTargetOfProgressingAd(ad, numerator, date);
			}
			
			
			RevDailyAchv dailyAchv = sRevService.getDailyAchvByTypeIdPlayDate("A", ad.getId(), date);
			RevDailyAchvItem item = new RevDailyAchvItem(date, actualValue, actualAmount, ad.getGoalType());
			item.setTgtToday(tgtToday);
			item.setDBData(dailyAchv);
			
			if (item.getFinalTgtToday() != 0) {
				item.setAchvRatio(Math.round((double)actual * 10000d / (double)item.getFinalTgtToday()) / 100d);
			}
			
			achvList.add(item);

			
			sumValue += actualValue;
			sumAmount += actualAmount;
			
			if (dailyAchv == null) {
				dailyAchv = new RevDailyAchv(ad, date);
			} else {
				dailyAchv.touch();
			}
			
			dailyAchv.setActualAmount(item.getActualAmount());
			dailyAchv.setActualValue(item.getActualValue());
			dailyAchv.setGoalType(item.getGoalType());
			dailyAchv.setTgtToday(item.getFinalTgtToday());
			dailyAchv.setAchvRatio(item.getAchvRatio());
			
			sRevService.saveOrUpdate(dailyAchv);
		}

		/*
		logger.info("------------------");
		for(RevDailyAchvItem item : achvList) {
			logger.info("{}  type:{}  {}회  {}원  -  계산: {}, DB: {}, last={}  -  {}, upd={}", 
					Util.toSimpleString(item.getPlayDate(), "yyyy-MM-dd"), 
					item.getGoalType(), item.getActualValue(), item.getActualAmount(),
					item.getTgtToday(),
					item.getDbTgtToday(),
					item.isUpdateRequired() ? item.getTgtToday() : item.getDbTgtToday(),
					item.getAchvRatio(), item.isUpdateRequired());
		}
		logger.info("------------------");
		*/
		
		return true;
	}
	
	
    /**
	 * 지정된 캠페인의 일별 달성 기록 계산
	 */
	public static boolean calcDailyAchvesByCampId(int campId) {
		
		AdcCampaign camp = sAdcService.getCampaign(campId);
		if (camp == null || !camp.isSelfManaged()) {
			return false;
		}
		
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		List<AdcAd> adList = sAdcService.getAdListByCampaignId(campId);
		for(AdcAd ad : adList) {
			ids.add(ad.getId());
		}
		
		ArrayList<RevDailyAchvItem> achvList = new ArrayList<RevDailyAchvItem>();
		
		List<Tuple> list = sRevService.getHourlyPlayStatGroupByPlayDateAdIdInBetween(
				ids, camp.getStartDate(), camp.getEndDate());
		int sumValue = 0, sumAmount = 0;
		for (Tuple tuple : list) {
			
			//		SELECT play_date, SUM(succ_tot), 
			//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
			//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
			//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
			//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
			//             sum(actual_amount)
			
			Date date = (Date) tuple.get(0);
			int actualValue = ((BigDecimal) tuple.get(1)).intValue();
			int actualAmount = ((BigDecimal) tuple.get(26)).intValue();
			
			
			int tgtToday = 0;
			int numerator = 0;
			int actual = 0;
			
			if (camp.getGoalType().equals("I")) {
				// 노출량 based
				numerator = camp.getGoalValue();
				
				// 보장량이 설정되어 있을 경우, 매체 설정값 기반으로 목표량 설정
				int sysValue = camp.getSysValue();
				if (numerator > 0 && sysValue == 0) {
					// 보장량 설정되어 있고, 목표량이 미설정
					int sysValuePct = Util.parseInt(SolUtil.getOptValue(camp.getMedium().getId(), "sysValue.pct"));
					if (sysValuePct > 0) {
						sysValue = (int)Math.ceil((float)numerator * (float)sysValuePct / 100f);
					}
				}
				if (sysValue > numerator) {
					numerator = sysValue;
				}
				numerator -= sumValue;
				
				actual = actualValue;
			} else if (camp.getGoalType().equals("A")) {
				// 광고예산 based
				numerator = camp.getBudget();
				numerator -= sumAmount;
				
				actual = actualAmount;
			}
			
			if (numerator > 0) {
				tgtToday = SolUtil.getDayTargetOfProgressingCampaign(camp, numerator, date);
			}
			
			
			RevDailyAchv dailyAchv = sRevService.getDailyAchvByTypeIdPlayDate("C", camp.getId(), date);
			RevDailyAchvItem item = new RevDailyAchvItem(date, actualValue, actualAmount, camp.getGoalType());
			item.setTgtToday(tgtToday);
			item.setDBData(dailyAchv);
			
			if (item.getFinalTgtToday() != 0) {
				item.setAchvRatio(Math.round((double)actual * 10000d / (double)item.getFinalTgtToday()) / 100d);
			}
			
			achvList.add(item);

			
			sumValue += actualValue;
			sumAmount += actualAmount;
			
			if (dailyAchv == null) {
				dailyAchv = new RevDailyAchv(camp, date);
			} else {
				dailyAchv.touch();
			}
			
			dailyAchv.setActualAmount(item.getActualAmount());
			dailyAchv.setActualValue(item.getActualValue());
			dailyAchv.setGoalType(item.getGoalType());
			dailyAchv.setTgtToday(item.getFinalTgtToday());
			dailyAchv.setAchvRatio(item.getAchvRatio());
			
			sRevService.saveOrUpdate(dailyAchv);
		}

		/*
		logger.info("------------------");
		for(RevDailyAchvItem item : achvList) {
			logger.info("{}  type:{}  {}회  {}원  -  계산: {}, DB: {}, last={}  -  {}, upd={}", 
					Util.toSimpleString(item.getPlayDate(), "yyyy-MM-dd"), 
					item.getGoalType(), item.getActualValue(), item.getActualAmount(),
					item.getTgtToday(),
					item.getDbTgtToday(),
					item.isUpdateRequired() ? item.getTgtToday() : item.getDbTgtToday(),
					item.getAchvRatio(), item.isUpdateRequired());
		}
		logger.info("------------------");
		*/
		
		return true;
	}
	
	
    /**
	 * 프로세스 lock 진행
	 */
	public static void lockProcess(String lockKey) {

		// 최대 1초 대기에, 체크 타임을 200ms로
		lockProcess(lockKey, 1000, 200);
	}
	
	
    /**
	 * 프로세스 lock 진행
	 */
	public static void lockProcess(String lockKey, int maxTimeoutMillis, int stepMillis) {
		
		if (Util.isNotValid(lockKey)) {
			return;
		}
		
		boolean goAhead = false;
		
		int accTime = 0;
		while(!goAhead) {
			if (Util.isValid(SolUtil.getAutoExpVarValue(lockKey))) {
		        try {
		            Thread.sleep(stepMillis);
		            logger.info("--- LOCK sleep " + stepMillis + " lockKey = " + lockKey);
		        } catch (Exception e) { }
		        accTime += stepMillis;
		        if (accTime >= maxTimeoutMillis) {
		        	goAhead = true;
		        }
			} else {
				goAhead = true;
			}
		}
		
		GlobalInfo.AutoExpVarMap.put(lockKey, "Y");
		GlobalInfo.AutoExpVarTimeMap.put(lockKey,  Util.addSeconds(new Date(), 1));
	}
	
	
    /**
	 * 프로세스 unlock 진행
	 */
	public static void unlockProcess(String lockKey) {
		
		if (Util.isNotValid(lockKey)) {
			return;
		}
		
		removeAutoExpVarValue(lockKey);
	}
	
	
    /**
	 * 구글 파이어베이스에 요청해서 결과를 되돌려 준다
	 */
	public static String getFirebaseResponse(String uri) {
		
		if (Util.isNotValid(uri)) {
			return "";
		}
		
		String server = Util.getFileProperty("firebase.baseUrl");
		if (Util.isNotValid(server) || server.toLowerCase().startsWith("firebase.")) {
			return "";
		}
		
		return Util.getHttpGetResponse(server + "/" + uri);
	}
	
	
    /**
	 * 구글 파이어베이스에 요청해서 결과를 되돌려 준다(Lock 적용)
	 *   - 동일 명령을 lockSecs 시간 동안은 재호출되지 않도록
	 */
	public static int requestRunnableFirebaseFunc(String gid, String uri, String lockKey) {
		
		return requestRunnableFirebaseFunc(gid, uri, lockKey, 10);
	}
	
	
    /**
	 * 구글 파이어베이스에 요청해서 결과를 되돌려 준다(Lock 적용)
	 *   - 동일 명령을 lockSecs 시간 동안은 재호출되지 않도록
	 */
	public static int requestRunnableFirebaseFunc(String gid, String uri, String lockKey, int lockSecs) {
		
		//
		// 결과값:
		//   -1: 인자 오류
		//    0: 최근 lockSecs 내 동일 요청이 존재하여 요청을 하지 않음
		//    0: refresh를 하는데, 대상 재생목록이 비어있는 경우. 요청을 하지 않음
		//    1: 정상적인 API 호출(쓰레드)하고, 결과 대기, 혹은 
		//       결과 받기 전에 반환(결과는 쓰레드로 실행되나 결과 받지 못함)
		//
		//  - 결과값이 1: success
		//  - 결과값 < 1: failure
		//
		int result = -1;
		if (Util.isValid(uri) && Util.isValid(lockKey)) {
			String server = Util.getFileProperty("firebase.baseUrl");
			if (Util.isValid(server) && !server.toLowerCase().startsWith("firebase.")) {
				
				if (uri.startsWith("refresh?") && uri.indexOf("&list=%5B%5D") > -1) {
					
					// refresh를 하는데, 대상 재생목록이 비어있는 경우. 요청을 하지 않음
					result = 0;
					logger.info("* requestRunnableFirebaseFunc " + uri + " IGNORED!!");
					
				} else if (Util.isNotValid(SolUtil.getAutoExpVarValue(lockKey))) {
					
					// 동일 동기화 그룹에 잠금 시간(lockSecs) 내 동일 명령 실행 금지
		    		GlobalInfo.AutoExpVarMap.put(lockKey, "Y");
		    		GlobalInfo.AutoExpVarTimeMap.put(lockKey,  Util.addSeconds(new Date(), lockSecs));
		    		
		    		result = 1;
					
		    		new Thread(lockKey){
		    			public void run(){
							String res = Util.getHttpGetResponse(server + "/" + uri);
				    		
							logger.info("****** syncAD:[Firebase result] " + res);
		    			}
		    		}.start();		    		
				} else {
					// pass
					// 이전에 전송하고나서, 아직 lockSecs 가 경과하지 않은 상태
					result = 0;
					
				}
			}
		}
		
		return result;
	}
	
	
    /**
	 * 도움말 모드로 동작 중인 동기화 묶음 그룹명 획득
	 */
	public static String getHelpModeSyncPackIDs() {
		
		String resp = SolUtil.getFirebaseResponse("getHelpModeGIDs");
		
		if (Util.isNotValid(resp)) {
			return "";
		}
		
		try {
			JSONObject respObj = JSONObject.fromObject(JSONSerializer.toJSON(resp));
			if (respObj != null) {
				return respObj.getString("showGroup");
			}
		} catch (Exception e) {
			logger.error("getHelpModeSyncPackIDs - json parsing", e);
		}
		
		return "";
	}


	private static void setNaverAPIAuth(HttpURLConnection http) {
		
        http.setRequestProperty("X-NCP-APIGW-API-KEY-ID", "j8mpo9mxip");
        http.setRequestProperty("X-NCP-APIGW-API-KEY", "CnxRzvrWUtlwPpFTKb4d69a3hey66yNsXaTn6CXG");
	}

	
    /**
	 * Reverse Geocoding을 수행하여 광역시/구/동까지의 문자열 획득
	 */
	public static String reverseGCRegion(double lat, double lng) {
		
		return reverseGCRegion(String.valueOf(lat), String.valueOf(lng));
	}

	
    /**
	 * Reverse Geocoding을 수행하여 광역시/구/동까지의 문자열 획득
	 */
	public static String reverseGCRegion(String lat, String lng) {
		
		HttpURLConnection http = null;
		BufferedReader in = null;
		
		String url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?orders=addr,roadaddr&output=json&coords=" +
					String.valueOf(lng) + "," + String.valueOf(lat);

		String respStr = "";
		try {
			URL serverUrl = new URL(url);
            http = (HttpURLConnection) serverUrl.openConnection();

            http.setRequestMethod("GET");   // HTTP GET 메소드 설정
            setNaverAPIAuth(http);

            int responseCode = http.getResponseCode();
            in = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
            	response.append(inputLine);
            }
            in.close();
            
            if (responseCode == 200) {
            	respStr = response.toString();
            }
		} catch (Exception e) {
			logger.error("reverseGCRegion", e);
		} finally {
			if (in != null) {
            	try {
                	in.close();
                } catch (IOException ex) {
                	logger.error("reverseGCRegion - finally", ex);
                }
            }
            if (http != null) {
            	http.disconnect();
            }
		}
		
		if (Util.isValid(respStr)) {
			try {
				
				JSONObject jObj = JSONObject.fromObject(JSONSerializer.toJSON(respStr));
				if (jObj != null) {
					
					JSONArray results = jObj.getJSONArray("results");
					for(int i = 0; i < results.size(); i++) {
						
						JSONObject addr = results.getJSONObject(i);
						
						JSONObject region = addr.getJSONObject("region");
						
						JSONObject area1 = region.getJSONObject("area1");
						JSONObject area2 = region.getJSONObject("area2");
						JSONObject area3 = region.getJSONObject("area3");
						
						String name1 = (String)area1.get("name");
						String name2 = (String)area2.get("name");
						String name3 = (String)area3.get("name");
						
						if (Util.isNotValid(name1) || Util.isNotValid(name2) || Util.isNotValid(name3)) {
							continue;
						}
						
						return name1 + " " + name2 + " " + name3;
					}
				}
			} catch (Exception e) {
				logger.error("reverseGCRegion - parsing json", e);
			}
		}

		return "";	
	}

	
    /**
	 * Reverse Geocoding을 수행하여 의미있는 지역 위치 획득
	 *   우선순위: 1) 지번 주소, 2) 도로명 주소
	 */
	public static String reverseGCRegion2(double lat, double lng) {
		
		return reverseGCRegion2(String.valueOf(lat), String.valueOf(lng));
	}

	
    /**
	 * Reverse Geocoding을 수행하여 의미있는 지역 위치 획득
	 *   우선순위: 1) 지번 주소, 2) 도로명 주소
	 */
	public static String reverseGCRegion2(String lat, String lng) {
		
		HttpURLConnection http = null;
		BufferedReader in = null;
		
		String url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?orders=addr,roadaddr&output=json&coords=" +
					String.valueOf(lng) + "," + String.valueOf(lat);

		String respStr = "";
		try {
			URL serverUrl = new URL(url);
            http = (HttpURLConnection) serverUrl.openConnection();

            http.setRequestMethod("GET");   // HTTP GET 메소드 설정
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            setNaverAPIAuth(http);

            int responseCode = http.getResponseCode();
            in = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
            	response.append(inputLine);
            }
            in.close();

            if (responseCode == 200) {
            	respStr = response.toString();
            }
		} catch (Exception e) {
			logger.error("reverseGCRegion", e);
		} finally {
			if (in != null) {
            	try {
                	in.close();
                } catch (IOException ex) {
                	logger.error("reverseGCRegion2 - finally", ex);
                }
            }
            if (http != null) {
            	http.disconnect();
            }
		}
		
		if (Util.isValid(respStr)) {
			try {
				
				JSONObject jObj = JSONObject.fromObject(JSONSerializer.toJSON(respStr));
				if (jObj != null) {
					
					String ret = "";
					JSONArray results = jObj.getJSONArray("results");
					for(int i = 0; i < results.size(); i++) {
						
						JSONObject addr = results.getJSONObject(i);
						
						String addrName = (String)addr.get("name");
						if (Util.isValid(addrName)) {
							if (addrName.equals("roadaddr")) {
								{
									JSONObject region = addr.getJSONObject("region");
									
									JSONObject area1 = region.getJSONObject("area1");
									JSONObject area2 = region.getJSONObject("area2");
									
									String name1 = (String)area1.get("name");
									String name2 = (String)area2.get("name");
									
									
									JSONObject land = addr.getJSONObject("land");
									String landName = (String)land.get("name");
									
									if (Util.isNotValid(name1) || Util.isNotValid(name2) || Util.isNotValid(landName)) {
										continue;
									}
									
									ret = name1 + " " + name2 + " " + landName;
									
									String number1 = (String)land.get("number1");
									if (Util.isValid(number1)) {
										ret += " " + number1;
									}
									
									JSONObject addition0 = land.getJSONObject("addition0");
									if (addition0 != null) {
										String type = (String) addition0.get("type");
										String value = (String) addition0.get("value");
										
										if (Util.isValid(type) && Util.isValid(value) && type.equals("building")) {
											ret += " " + value;
										}
									}
								}
							} else if (addrName.equals("addr")) {
								{
									JSONObject region = addr.getJSONObject("region");
									
									JSONObject area1 = region.getJSONObject("area1");
									JSONObject area2 = region.getJSONObject("area2");
									JSONObject area3 = region.getJSONObject("area3");
									
									String name1 = (String)area1.get("name");
									String name2 = (String)area2.get("name");
									String name3 = (String)area3.get("name");
									
									if (Util.isNotValid(name1) || Util.isNotValid(name2) || Util.isNotValid(name3)) {
										continue;
									}
									
									ret = name1 + " " + name2 + " " + name3;
									
									
									JSONObject land = addr.getJSONObject("land");
									
									String number1 = (String)land.get("number1");
									String number2 = (String)land.get("number2");
									
									if (Util.isValid(number1)) {
										ret += " " + number1;
									}
									if (Util.isValid(number2)) {
										ret += "-" + number2;
									}
								}
							}
						}
						
						if (Util.isNotValid(ret)) {
							continue;
						}
						
						return ret;
					}
				}
			} catch (Exception e) {
				logger.error("reverseGCRegion2 - parsing json", e);
			}
		}

		return "";	
	}

	
    /**
	 * 현재의 위경도의 위치가 우리나라 범주 내의 위치 여부 획득
	 */
	public static boolean isAcceptableLocation(String lat, String lng) {
		
		return isAcceptableLocation(Util.parseDouble(lat), Util.parseDouble(lng));
	}

	
    /**
	 * 현재의 위경도의 위치가 우리나라 범주 내의 위치 여부 획득
	 */
	public static boolean isAcceptableLocation(double lat, double lng) {
		
		// 위도: N 33˚06′40″ ~ N 43˚00′39″
		// 경도: E 124˚11′00″ ~ E 131˚52′42″
		
		return (lat >= 33d && lat <= 43d) && (lng >= 124d || lng <= 131d);
	}

	
    /**
	 * 게시 유형이 묶음 광고 단위로 이용 여부를 획득(DB 사용하지 않음)
	 */
	public static boolean isViewTypeAdPackUsed(String viewType) {
		
		if (Util.isValid(viewType)) {
			FndViewTypeItem item = GlobalInfo.ViewTypeGlobalMap.get(viewType);
			if (item != null) {
				return item.isAdPackUsed();
			}
		}
		
		return false;
	}

	
    /**
	 * 동기화 기기간 시작 시간의 차이를 등급으로 획득
	 */
	public static String getSyncPackGrade(String mediumShortName, String groupID, int diff, int cnt) {
		
		// 등급 설정
		//
		if (Util.isNotValid(groupID) || diff < 0 || Util.isNotValid(mediumShortName)) {
			return "?";
		}
		
		
		KnlMediumCompactItem item = GlobalInfo.MediaMap.get(mediumShortName);
		if (item == null) {
			return "?";
		}
		
		// 현재는 매체의 값을 바로 이용하지만, 향후에는 매체 정보 hashmap으로부터의 값 적용 예정
		int levelA = item.getaGradeMillis();
		int levelB = item.getbGradeMillis();
		int levelC = item.getcGradeMillis();

		String grade = "?";
		if (diff <= levelA) {
			grade = "A";
		} else if (diff <= levelB) {
			grade = "B";
		} else if (diff <= levelC) {
			grade = "C";
		} else {
			grade = "D";
		}
		
		
		final int MAX_QUEUE_SIZE = 10;
		//
		// cnt가 2기 이상일 경우에만 등급 큐에 추가. 하지만 카운트 큐에는 1기라도 추가.
		//
		if (cnt > 1) {
			// 큐 관리
			String qStr = GlobalInfo.SyncPackImpGradeMap.get(groupID);
			if (Util.isValid(qStr)) {
				qStr = grade + qStr;
				
				if (qStr.length() > MAX_QUEUE_SIZE) {
					qStr = qStr.substring(0, MAX_QUEUE_SIZE);
				}
			} else {
				qStr = grade;
			}
			
			GlobalInfo.SyncPackImpGradeMap.put(groupID, qStr);
		}
		
		// 노출 기기 수
		String cntS = "Z";
		if (cnt >= 0 && cnt < 10) { cntS = String.valueOf(cnt); }
		else if (cnt == 10) { cntS = "A"; }
		else if (cnt == 11) { cntS = "B"; }
		else if (cnt == 12) { cntS = "C"; }
		else if (cnt == 13) { cntS = "D"; }
		else if (cnt == 14) { cntS = "E"; }
		else if (cnt == 15) { cntS = "F"; }
		else if (cnt == 16) { cntS = "G"; }
		else if (cnt == 17) { cntS = "H"; }
		else if (cnt == 18) { cntS = "I"; }
		else if (cnt == 19) { cntS = "J"; }
		
		String cntStr = GlobalInfo.SyncPackImpCntMap.get(groupID);
		if (Util.isValid(cntStr)) {
			cntStr = cntS + cntStr;
			
			if (cntStr.length() > MAX_QUEUE_SIZE) {
				cntStr = cntStr.substring(0, MAX_QUEUE_SIZE);
			}
		} else {
			cntStr = cntS;
		}
		
		GlobalInfo.SyncPackImpCntMap.put(groupID, cntStr);
		
		
		return grade;
	}
	
	
    /**
	 * 동기화 기기의 노출 등급 큐 문자열을 획득
	 */
	private static String getSyncPackGradeQueue(String groupID, int length) {
		
		if (Util.isNotValid(groupID)) {
			return "";
		}
		
		String queue = GlobalInfo.SyncPackImpGradeMap.get(groupID);
		if (Util.isNotValid(queue)) {
			return "";
		}
		
		
		if (length < 1 || (length != 999 && queue.length() < length)) {
			return "";
		}

		
		if (length == 999) {
			return queue;
		} else {
			return queue.substring(0, length);
		}
	}

	
    /**
	 * 동기화 기기의 노출 등급 큐 문자열을 획득
	 */
	public static String getSyncPackGradeQueue(String groupID) {
		
		return getSyncPackGradeQueue(groupID, 999);
	}
	
	
    /**
	 * 동기화 기기의 참여 기기 수 큐 문자열을 획득
	 */
	private static String getSyncPackCntQueue(String groupID, int length) {
		
		if (Util.isNotValid(groupID)) {
			return "";
		}
		
		String queue = GlobalInfo.SyncPackImpCntMap.get(groupID);
		if (Util.isNotValid(queue)) {
			return "";
		}
		
		
		if (length < 1 || (length != 999 && queue.length() < length)) {
			return "";
		}

		
		if (length == 999) {
			return queue;
		} else {
			return queue.substring(0, length);
		}
	}

	
    /**
	 * 동기화 기기의 참여 기기 수 큐 문자열을 획득
	 */
	public static String getSyncPackCntQueue(String groupID) {
		
		return getSyncPackCntQueue(groupID, 999);
	}

	
    /**
	 * 동기화 기기의 현재 시간 대비 90초 동안의 재생 목록 문자열을 획득
	 */
	public static String get90SecChannelAds(String groupID) {
		
		JSONArray playlists = new JSONArray();
		
		InvSyncPackCompactItem spCompactItem = GlobalInfo.SyncPackMap.get(groupID);
		if (spCompactItem != null) {
    		
    		// 기존 RevRecPlaylist에서 RevChanAd로 변경됨
    		//
			ArrayList<RevChanAd> retList = new ArrayList<RevChanAd>();
			
			List<RevChanAd> adList = sRevService.getCurrChanAdListByChannelId(
					SolUtil.getFirstPriorityChannelByTypeObjId("P", spCompactItem.getId()));
			int durSum = 0;
			for(RevChanAd chanAd : adList) {
				durSum += chanAd.getDuration();
				retList.add(chanAd);
				if (durSum > 90000) {
					break;
				}
			}
			
			for(RevChanAd chanAd : retList) {
				
				JSONObject obj = new JSONObject();
				obj.put("seq", chanAd.getSeq());
				obj.put("ad_id", chanAd.getCreatId());
				obj.put("ac_id", chanAd.getAdCreatId());
				obj.put("ad_pack_ids", chanAd.getAdPackIds());
				
				obj.put("begin", Util.toSimpleString(chanAd.getPlayBeginDate(), "yyyyMMdd HHmmss.SSS"));
				obj.put("end", Util.toSimpleString(chanAd.getPlayEndDate(), "yyyyMMdd HHmmss.SSS"));
				
				playlists.add(obj);
			}
			
		}
		
		
		//return Util.getObjectToJson(playlists, false);
		return URLEncoder.encode(Util.getObjectToJson(playlists, false), StandardCharsets.UTF_8);
	}
	
    /**
	 * 동기화 그룹에 [새로고침] 액션 진행
	 */
	private static void proceedSyncPackControlRulesRefreshCommand(String gid, String adID, String ID) {
		
		if (Util.isValid(ID)) {
			
			String uri = "refresh?groupId=" + gid + "&list=" + 
					SolUtil.get90SecChannelAds(gid);
			String logStr = "****** syncAd:[" + gid + "] - " + ID;
			
			sRevService.saveOrUpdate(new RevSyncPackImp(new Date(), gid, ID));
			
			
			logger.info(logStr + " : uri = " + uri);
			int resInt = SolUtil.requestRunnableFirebaseFunc(gid, uri, gid + "Refresh");
			
			if (resInt < 1) {
				logger.info(logStr + " : result = " + resInt);
			}
			
			logger.info(logStr + " : adId = " + adID);
			
			GlobalInfo.SyncPackImpGradeMap.remove(gid);
		}
	}
	
    /**
	 * 동기화 기기에 대한 제어 규칙을 확인하여, 필요 시 액션 진행
	 */
	public static void proceedSyncPackControlRules(RevSyncPackMinMaxItem item, String grade, boolean cmplMode, Date now) {
		
		if (item != null && Util.isValid(item.getGroupID()) && Util.isValid(grade) && !grade.equals("?")) {
			// 최근의 것으로 갱신되었음을 확인함
			
			// Rule 1. 노출 점수가 최근 7회 연속 C 이하일 경우(RST1)
			String rule1Str = getSyncPackGradeQueue(item.getGroupID(), 7);
			if (Util.isValid(rule1Str)) {
				
				boolean goodGradeFound = false;
				for(int i = 0; i < 7; i ++) {
					char c = rule1Str.charAt(i);
					if (c != 'C' && c != 'D') {
						goodGradeFound = true;
						break;
					}
				}
				
				if (!goodGradeFound) {
					proceedSyncPackControlRulesRefreshCommand(item.getGroupID(), item.getAdID(), "RST1");
					return;
				}
			}

			
			// Rule 2. 기기 수가 지그재그 연속 3회 발생 혹은 특정 숫자 패턴인 경우(RST2)
			String rule2Str = getSyncPackCntQueue(item.getGroupID(), 6);
			if (Util.isValid(rule2Str)) {
				if (rule2Str.charAt(0) == rule2Str.charAt(2) && rule2Str.charAt(0) == rule2Str.charAt(4) &&
						rule2Str.charAt(1) == rule2Str.charAt(3) && rule2Str.charAt(1) == rule2Str.charAt(5) &&
						rule2Str.charAt(0) != rule2Str.charAt(1)) {
					
					proceedSyncPackControlRulesRefreshCommand(item.getGroupID(), item.getAdID(), "RST2");
					return;
				} else if (!cmplMode && GlobalInfo.SyncPackProhibitedPatterns.contains(rule2Str)) {
					
					proceedSyncPackControlRulesRefreshCommand(item.getGroupID(), item.getAdID(), "RST2");
					return;
				}
			}
			
			
			// Rule 3. 대기 유효시간 초과(EXP) 카운팅이 특정한 숫자를 넘을 경우(RST3)
			//
			//    특정한 숫자 = (item.getMaxCnt() / 2) + 1
			//
			//        기기 수가 3    -> 2
			//        기기 수가 4, 5 -> 3
			//        기기 수가 6, 7 -> 4
			//        기기 수가 8, 9 -> 5
			//        기기 수가 10, 11 -> 6
			//        기기 수가 12, 13 -> 7
			//
			Date lastReportDt = GlobalInfo.SyncPackReportTimeMap.get(item.getGroupID());
			if (lastReportDt != null && Util.addMinutes(lastReportDt, 30).before(now)) {
				// 30분 이상 보고 없었다면,
				GlobalInfo.SyncPackExpCountMap.put(item.getGroupID(), 0);
			}
			GlobalInfo.SyncPackReportTimeMap.put(item.getGroupID(), now);
			
				
			if (cmplMode) {
				GlobalInfo.SyncPackExpCountMap.put(item.getGroupID(), 0);
			} else {
				Integer cnt = GlobalInfo.SyncPackExpCountMap.get(item.getGroupID());
				Integer newCnt = null;
				if (cnt == null) {
					newCnt = 1;
				} else if (cnt.intValue() >= 0) {
					newCnt = cnt + 1;
				}
				
				if (newCnt != null) {
					
					int trVal = (int)(item.getMaxCnt() / 2) + 1;
					
					if (newCnt > 0) {
						// 최소 3개 이상일 경우에만 동작하도록
						if (trVal == 1) {
							trVal = 0;
						}
					}

					// trVal == 1 이란, 그룹의 등록 화면 수가 2 이하
					if (trVal <= 1) {
						newCnt = -1;
					} else if (trVal > 1 && newCnt >= trVal) {
						newCnt = -1;
						
						proceedSyncPackControlRulesRefreshCommand(item.getGroupID(), item.getAdID(), "RST3");
						GlobalInfo.SyncPackExpCountMap.put(item.getGroupID(), -1);
						return;
					}
					
					GlobalInfo.SyncPackExpCountMap.put(item.getGroupID(), newCnt.intValue());
				}
			}

		}
	}
	
	
    /**
	 * 광고 선출 하위 private
	 */
    private static Date getLastCacheDate(List<Tuple> cacheList, int adId, int advertiserId, String catCode) {
    	
    	if (cacheList == null) {
    		return null;
    	}

    	// SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID, AC.AD_ID, C.ADVERTISER_ID, C.CATEGORY
		for(Tuple tuple : cacheList) {
			if (adId > 0) {
				if ((int)tuple.get(3) == adId) {
					return (Date) tuple.get(1);
				}
			} else if (advertiserId > 0) {
				if ((int)tuple.get(4) == advertiserId) {
					return (Date) tuple.get(1);
				}
			} else if (Util.isValid(catCode)) {
				if (Util.isValid((String)tuple.get(5)) && catCode.equals((String)tuple.get(5))) {
					return (Date) tuple.get(1);
				}
			}
		}

		return null;
    }
    
    
    /**
	 * 대상 시점에서의 하나의 광고 선택(광고 선출)
	 */
	public static AdcAdCreatFileObject selectAdFromCandiList(InvScreen screen, String viewType, double lat, double lng, Date targetDt) {
		
		if (screen == null || Util.isNotValid(screen.getResolution())) {
			return null;
		}
		
		HashMap<String, AdcAdCreative> candiMap = null;
		
		String key = GlobalInfo.AdCandiAdCreatVerKey.get("M" + screen.getMedium().getId());
		if (Util.isValid(key)) {
			candiMap = GlobalInfo.AdRealAdCreatMap.get(key);
		}
		if (candiMap == null) {
			return null;
		}
		
		ArrayList<AdcCreatFile> mapCreatFileList = null; 
		key = GlobalInfo.FileCandiCreatFileVerKey.get("S" + screen.getId());
		if (Util.isValid(key)) {
			mapCreatFileList = GlobalInfo.FileCandiCreatFileMap.get(key);
		}

		KnlMediumCompactItem mediumItem = GlobalInfo.ApiKeyMediaMap.get(screen.getMedium().getApiKey());
		if (mediumItem == null) {
			return null;
		}
		
		Date now = targetDt == null ? new Date() : targetDt;
		
		
		// 매체 화면의 운영 시간 확인
		if (!SolUtil.isCurrentOpHours((Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
					? screen.getBizHour() : screen.getMedium().getBizHour(), now)) {
			return null;
		}
		
		
		
		AdcCreatFile creatFile = null;
		
		ArrayList<AdcAdCreative> orderedList = new ArrayList<AdcAdCreative>();

		String orderStr = SolUtil.selectAdSeqList(GlobalInfo.AdRealAdCreatIdsMap.get("M" + screen.getMedium().getId()));
		List<String> seqList = Util.tokenizeValidStr(orderStr);
		
		boolean impWaveMode = false;
		
		// 강제 노출 등록 확인
		if (seqList.size() > 0) {
			int impWaveAdCreatId = Util.parseInt(SolUtil.getAutoExpVarValue("Imp" + String.valueOf(screen.getId())));
			if (impWaveAdCreatId > 0) {
    			AdcAdCreative adCreat = candiMap.get("AC" + String.valueOf(impWaveAdCreatId));
    			if (adCreat != null) {
	    			orderedList.add(adCreat);
	    			impWaveMode = true;
    			}
			}
		}
		
		for(String s : seqList) {
			AdcAdCreative adCreat = candiMap.get("AC" + s);
			if (adCreat != null) {
    			orderedList.add(adCreat);
			}
		}

		
		// 해당 화면에 대한 오늘의 총 송출 자료 획득
		List<Tuple> shpList = sRevService.getScrHourlyPlayAdStatListByScreenIdPlayDate(screen.getId(), 
				Util.removeTimeOfDate(new Date()));
		HashMap<String, RevScrHrlyPlyAdStatItem> adStatMap = new HashMap<String, RevScrHrlyPlyAdStatItem>();
		for(Tuple tuple : shpList) {
			RevScrHrlyPlyAdStatItem item = new RevScrHrlyPlyAdStatItem(tuple);
			adStatMap.put("A" + item.getAdId(), item);
		}
		
		// 동일 광고 및 광고주 송출 금지 시간 자료 획득
		//
		// SELECT CCH.AD_SEL_CACHE_ID, CCH.SEL_DATE, CCH.AD_CREATIVE_ID, AC.AD_ID, C.ADVERTISER_ID
		//
		List<Tuple> cacheList = sRevService.getAdSelCacheTupleListByScreenId(screen.getId());
		
		for(AdcAdCreative adC : orderedList) {
			
			// 게시 유형부터 먼저 검사
			if (!adC.getAd().getViewTypeCode().equals(viewType)) {
				continue;
			}
			
			// 강제 노출 모드가 아니라면...
			if (!impWaveMode) {
				//logger.info("--- tmp loop: " + adC.getAd().getName());
    			// 오늘 목표값(예산 or 노출) 및 모든 화면 하루 노출한도 총합 확인
    			boolean todayTgtCheckRequired = false;
    			if (adC.getAd().getTgtToday() > 0 && adC.getAd().getStatus().equals("R") &&
    					(adC.getAd().getPurchType().equals("G") || adC.getAd().getPurchType().equals("N"))) {
    				todayTgtCheckRequired = adC.getAd().getGoalType().equals("A") || adC.getAd().getGoalType().equals("I");
    			}
    			if (adC.getAd().getDailyCap() > 0 || todayTgtCheckRequired) {
	    			Tuple adAccTuple = sRevService.getHourlyPlayAccStatByAdIdPlayDate(adC.getAd().getId(), Util.removeTimeOfDate(now));
	    			if (adAccTuple != null) {
	    				// 금일 누적 횟수: adActualVal, 금일 누적 금액: adActualAmount
	    				BigDecimal adActualVal = (BigDecimal) adAccTuple.get(0);
	    				BigDecimal adActualAmount = (BigDecimal) adAccTuple.get(1);
	    				
	    				if (adActualVal != null) {
		    				// 모든 화면 하루 노출한도 총합 확인
	    					if (adC.getAd().getDailyCap() > 0 && adC.getAd().getDailyCap() <= adActualVal.intValue()) {
	    						continue;
	    					}
		    				// 오늘 노출 목표 확인
	    					if (adC.getAd().getGoalType().equals("I") && adC.getAd().getTgtToday() <= adActualVal.intValue()) {
	    						continue;
	    					}
	    				}
	    				
	    				// 오늘 예산 목표 확인
	    				if (adC.getAd().getGoalType().equals("A") && adActualAmount != null && adC.getAd().getTgtToday() <= adActualAmount.intValue()) {
	    					continue;
	    				}
	    			}
    			}
    			
    			// 캠페인에 설정된 오늘 목표값(예산 or 노출) 확인
    			if (adC.getAd().getCampaign().isSelfManaged() && adC.getAd().getCampaign().getTgtToday() > 0 &&
    					adC.getAd().getCampaign().getStatus().equals("R") &&
    					(adC.getAd().getCampaign().getGoalType().equals("A") || adC.getAd().getCampaign().getGoalType().equals("I"))) {
    				Tuple campAccTuple = sRevService.getHourlyPlayAccStatByCampaignIdPlayDate(adC.getAd().getCampaign().getId(), Util.removeTimeOfDate(now));
    				if (campAccTuple != null) {
	    				// 금일 누적 횟수: adActualVal, 금일 누적 금액: adActualAmount
	    				BigDecimal campActualVal = (BigDecimal) campAccTuple.get(0);
	    				BigDecimal campActualAmount = (BigDecimal) campAccTuple.get(1);
	    				
	    				if (adC.getAd().getCampaign().getGoalType().equals("I") && campActualVal != null && 
	    						adC.getAd().getCampaign().getTgtToday() <= campActualVal.intValue()) {
	    					continue;
	    				}
	    				if (adC.getAd().getCampaign().getGoalType().equals("A") && campActualAmount != null &&
	    						adC.getAd().getCampaign().getTgtToday() <= campActualAmount.intValue()) {
	    					continue;
	    				}
    				}
    			}

    			// 광고의 화면당 하루 노출한도 확인
    			int dailyScrCap = adC.getAd().getDailyScrCap();
    			if (dailyScrCap == 0 && mediumItem.getDailyScrCap() > 0) {
    				dailyScrCap = mediumItem.getDailyScrCap();
    			}
    			if (dailyScrCap > 0) {
    				RevScrHrlyPlyAdStatItem adStatItem = adStatMap.get("A" + adC.getAd().getId());
	    			if (adStatItem != null) {
	    				// 화면당 하루 노출량 초과 확인
    					if (adStatItem.getSuccTot() >= dailyScrCap) {
    						continue;
    					}
    					// 시간당 노출량 초과 학인
    					if (adStatItem.getCurrHourGoal() != null && adStatItem.getCnt() >= adStatItem.getCurrHourGoal().intValue()) {
    						continue;
    					}
    					// 시간(60분) 분할 대비 노출량 확인
    					//   - 진행 성공량: adStatItem.getCnt()
    					//   - 이번 시도 차수: adStatItem.getCnt() + 1
    					//   - 목표량: adStatItem.getCurrHourGoal().intValue()
    					//   - 현재 분: getMinutes(now)
    					//
    					// 식: (60분 % 목표량) x (이번 시도 차수 - 1) <= curr Mins 일 경우 진행
    					//
    					if (adStatItem.getCurrHourGoal() == null) {
    						// 시간당 목표값이 null일 경우 다음으로
    						continue;
    					} else {
    				    	
    						GregorianCalendar calendar = new GregorianCalendar();
    						calendar.setTime(now);
    						
	    					if ((60f / (float)adStatItem.getCurrHourGoal().intValue()) * (float)adStatItem.getCnt() > calendar.get(Calendar.MINUTE)) {
	    						continue;
	    					}
    					}

	    			} else {
	    				// 오늘의 송출 자료가 없다면, 최소한으로 시간당 노출량 초과 확인만큼은 진행
	    				String mapKey = "AdSel_A" + adC.getAd().getId() + "S" + screen.getId();
	    				if (Util.isValid(SolUtil.getAutoExpVarValue(mapKey))) {
	    					// 추가적인 광고 선택이 해당 시간 내 되지 말아야 한다
	    					continue;
	    				} else {
	    					//
	    					// 설정 부분은 이 메소드 호출에서 확정된 후 진행함
	    					//
	    					/*
	    					// 루프의 현재 광고의 조건이 만족된 상태
	    					// 다음의 시간까지는 선택이 불가능하도록 함
	    					int impPlanPerHour = Util.parseInt(SolUtil.getOptValue(screen.getMedium().getId(), "impress.per.hour"), 6);
	    					if (impPlanPerHour < 1) {
	    						impPlanPerHour = 6;
	    					}
	    					
	    					// 1hr = 60 * 60 = 3600 sec
	    					// 매체에 정해진 시간당 송출 횟수 * 2.5배가 가능한 수치가 되도록
	    					// 의도적인 floor 처리
	    					int expireSecs = (int)(60f * 60f / (float)impPlanPerHour / 2.5f);
	    					SolUtil.putAutoExpVarValue(mapKey, "Y", Util.addSeconds(new Date(), expireSecs));
	    					*/
	    				}
	    			}
    			}
			} else {
				impWaveMode = false;
			}
			
			
			// 동일 광고 송출 금지 시간 확인
			int adFreqCap = adC.getAd().getFreqCap();
			if (adFreqCap == 0 && mediumItem.getAdFreqCap() > 0) {
				adFreqCap = mediumItem.getAdFreqCap();
			}
			if (adFreqCap > 1) {
				Date date = getLastCacheDate(cacheList, adC.getAd().getId(), -1, "");
				if (date != null && Util.addSeconds(date, adFreqCap).after(now)) {
					continue;
				}
			}
			
			// 동일 광고주 송출 금지 시간 확인
			int advFreqCap = adC.getAd().getCampaign().getFreqCap();
			if (advFreqCap == 0 && mediumItem.getAdvFreqCap() > 0) {
				advFreqCap = mediumItem.getAdvFreqCap();
			}
			if (advFreqCap > 0) {
				Date date = getLastCacheDate(cacheList, -1, adC.getCreative().getAdvertiser().getId(), "");
				if (date != null && Util.addSeconds(date, advFreqCap).after(now)) {
					continue;
				}
			}
			
			// 동일 범주 송출 금지 시간 확인(범주 적용 광고가 적다고 가능할 때, 이 조건을 먼저 확인)
			if (Util.isValid(adC.getCreative().getCategory())) {
				int catFreqCap = mediumItem.getCatFreqCap();
				if (catFreqCap > 0) {
    				Date date = getLastCacheDate(cacheList, -1, -1, adC.getCreative().getCategory());
    				if (date != null && Util.addSeconds(date, catFreqCap).after(now)) {
    					continue;
    				}
				}
			}
			//-
			
			creatFile = null;
			
			if (Util.isNotValid(viewType)) {
    			if (mapCreatFileList != null) {
        			for(AdcCreatFile acf : mapCreatFileList) {
        				if (acf.getCreative().getId() == adC.getCreative().getId()) {
        					creatFile = acf;
        					break;
        				}
        			}
    			}
        		
        		if (creatFile == null) {
	    			creatFile = sAdcService.getCreatFileByCreativeIdResolution(adC.getCreative().getId(), screen.getResolution());
        		}
			} else {
				
				ArrayList<AdcCreatFile> tmpCreatFileList = null;
				key = GlobalInfo.FileCandiCreatFileVerKey.get("VTS" + screen.getMedium().getId() + "R" + screen.getResolution());
	    		if (Util.isValid(key)) {
	    			tmpCreatFileList = GlobalInfo.FileCandiCreatFileMap.get(key);
	    		}
    			if (tmpCreatFileList != null) {
        			for(AdcCreatFile acf : tmpCreatFileList) {
        				if (acf.getCreative().getId() == adC.getCreative().getId()) {
        					creatFile = acf;
        					break;
        				}
        			}
    			}
    			
    			if (creatFile == null) {
    				List<AdcCreatFile> tmpList = sAdcService.getCreatFileListByCreativeId(adC.getCreative().getId());
    				if (tmpList.size() > 0) {
    					creatFile = tmpList.get(0);
    				}
    			}
			}

			
			if (creatFile != null) {
				
				
				// 화면의 미디어 유형 수용 확인
				if (creatFile.getMediaType().equals("V") && !screen.isVideoAllowed()) {
					continue;
				} else if (creatFile.getMediaType().equals("I") && !screen.isImageAllowed()) {
					continue;
				}

				
				// 광고 소재 인벤 타겟팅 확인
				//
				//   key가 없다는 것은 해당 소재 타겟팅이 없음을 의미
				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
				//
				key = GlobalInfo.TgtScreenIdVerKey.get("C" + creatFile.getCreative().getId());
				if (Util.isValid(key)) {
					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
					if (!idList.contains(screen.getId())) {
						continue;
					}
				} else {
					// 소재에 대한 인벤 타겟팅이 없으므로 "통과!!"
				}
				
				
				// 광고 소재 시간 타겟팅 확인
				if (Util.isValid(creatFile.getCreative().getExpHour()) && creatFile.getCreative().getExpHour().length() == 168) {
					if (!SolUtil.isCurrentOpHours(creatFile.getCreative().getExpHour())) {
						continue;
					}
				} else {
					// 소재에 대한 시간 타겟팅이 없으므로 "통과!!"
				}

				
				// 광고 인벤 타겟팅 확인
				//
				//   key가 없다는 것은 해당 광고 타겟팅이 없음을 의미
				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
				//
				key = GlobalInfo.TgtScreenIdVerKey.get("A" + adC.getAd().getId());
				if (Util.isValid(key)) {
					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
					if (!idList.contains(screen.getId())) {
						continue;
					}
				} else {
					// 광고에 대한 인벤 타겟팅이 없으므로 "통과!!"
				}
				
				
				// 광고 모바일 타겟팅 확인
				//
				//   lat & lng 전달될 경우:
				//      1)모바일 타겟팅 설정==y, 대상=y: 아래 쪽 코드로 진행
				//      2)모바일 타겟팅 설정==y, 대상=n: continue
				//      3)모바일 타겟팅 설정==n: 아래 쪽 코드로 진행
				//
				//   lat & lng 전달 안됨:
				//      4)모바일 타겟팅 설정==y: continue
				//      5)모바일 타겟팅 설정==n: 아래 쪽 코드로 진행
				//   
				key = GlobalInfo.MobTgtItemVerKey.get("A" + adC.getAd().getId());
				if (Util.isValid(key)) {
					List<String> items = Util.tokenizeValidStr(GlobalInfo.MobTgtItemMap.get(key));
					if (items.size() > 0) {
						if (lat > 0d && lng > 0d) {
    						// 해당 광고에 대한 모바일 타겟팅이 적용되어 있는 상태
    						//
    						//    - 아직은 대상 여부 모름
    						//
    						//    - 타겟팅 대상이면 아래 코드로,
    						//    - 타겟팅 대상이 아니면 continue 처리
							//
							// 1)번, 2)번 흐름
    						//logger.info(">>> 모바일 타겟팅: " + adC.getAd().getName() + ", tgt=" + items.size());
    						
    						// 지오코딩은 동일한 lat, lng에는 1회만 수행하면 되나, RG 유형에만 필요한 것이기 때문에
    						// 루핑을 돌면서 값이 없으면 채우고, 있으면 스킵하게
    						String gcName = "";
    						Boolean success = null;
    						for(String tgtItem : items) {
    							//
    							// size=3 - O,RG,서울특별시 강남구 삼성동
    							// size=5 - A,CR,37.4979538,127.0276406,500
    							//
    							List<String> itemList = Util.tokenizeValidStr(tgtItem, ",");
    							String filterType = "";
    							boolean currSuccess = false;
    							if (itemList.size() == 3 && itemList.get(1).equals("RG")) {
    								// 모바일 지역 유형
    								if (Util.isNotValid(gcName)) {
    									gcName = SolUtil.reverseGCRegion2(lat, lng);
    								}
    								
    								filterType = itemList.get(0);
    								currSuccess = Util.isValid(gcName) && gcName.startsWith(itemList.get(2));
    								
		    						//logger.info(">>> 모바일 타겟팅 RG 항목: " + tgtItem + ">>>" + gcName + ", 결과: " + currSuccess);
    							} else if (itemList.size() == 5 && itemList.get(1).equals("CR")) {
    								// 원 반경 지역 유형
    								double lat2 = Util.parseDouble(itemList.get(2));
    								double lng2 = Util.parseDouble(itemList.get(3));
    								int radius = Util.parseInt(itemList.get(4));
    								
    								filterType = itemList.get(0);
    								currSuccess = SolUtil.isAcceptableLocation(lat2, lng2) && radius > 0 &&
    										Util.distance(lat, lng, lat2, lng2) * 1000 <= radius;
		    						//logger.info(">>> 모바일 타겟팅 CR 항목: " + tgtItem + ", 결과: " + currSuccess);
    							} else {
    								currSuccess = false;
    							}
    							
    							if (success == null) {
    								success = currSuccess;
    							} else if (Util.isValid(filterType)) {
    								if (filterType.equals("O")) {
    									success = success || currSuccess;
    								} else {
    									success = success && currSuccess;
    								}
    							}
    						}
    						if (success == null || success.booleanValue() == false) {
    							continue;
    						}
						} else {
							// 4)번 흐름
							continue;
						}
					} else {
						// key는 등록되어 있는데, 그 대상이 없다는 것은 비정상 상황
						continue;
					}
				} else {
					// 광고에 대한 모바일 타겟팅이 없으므로 "통과!!"
				}
				//logger.info(">>> 모바일 타겟팅 pass: " + adC.getAd().getName());
				
				
				// 광고 시간 타겟팅 확인
				if (Util.isValid(adC.getAd().getExpHour()) && adC.getAd().getExpHour().length() == 168) {
					if (!SolUtil.isCurrentOpHours(adC.getAd().getExpHour())) {
						continue;
					}
				} else {
					// 광고에 대한 시간 타겟팅이 없으므로 "통과!!"
				}
				
				
				// 광고의 노출 시간
				//   1) 광고 설정값(5초 이상인 경우): 이미지 유형 포함
				//   2) 재생 시간 미설정(이미지 유형)이라면, 화면의 기본 재생 시간, 매체의 기본 재생 시간 순으로 설정
				//   3) 광고 소재의 재생 시간
				int adDurMillis = 0;
				if (adC.getAd().getDuration() >= 5) {
					adDurMillis = adC.getAd().getDuration() * 1000;
				} else if (creatFile.getMediaType().equals("I")) {
					adDurMillis = (screen.isDurationOverridden() ?
							screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs()) * 1000;
				} else {
					adDurMillis = creatFile.getSrcDurSecs() > 5 ? (int) Math.round(creatFile.getSrcDurSecs() * 1000) : 5000;
				}
				
				if (adDurMillis < 5000) {
					continue;
				} else {
					int adDurSecs = (int)Math.round(adDurMillis / 1000);
					if (screen.isDurationOverridden()) {
						if (screen.getRangeDurAllowed() == true) {
							if (screen.getMinDurSecs().intValue() <= adDurSecs && adDurSecs <= screen.getMaxDurSecs().intValue()) {
								// go ahead
							} else {
								continue;
							}
						} else if (screen.getDefaultDurSecs().intValue() != adDurSecs) {
							continue;
						}
					} else {
						if (screen.getMedium().isRangeDurAllowed()) {
							if (screen.getMedium().getMinDurSecs() <= adDurSecs && adDurSecs <= screen.getMedium().getMaxDurSecs()) {
								// go ahead
							} else {
								continue;
							}
						} else if (screen.getMedium().getDefaultDurSecs() != adDurSecs) {
							continue;
						}
					}
				}
				
				AdcJsonFileObject jsonFileObject = new AdcJsonFileObject(creatFile);
				jsonFileObject.setDurMillis(adDurMillis);

				
				return new AdcAdCreatFileObject(adC, jsonFileObject);
			}
		}
		
		return null;
	}
	
	public static String getAuditTrailValueCodeText(String objType, String itemName, String value) {
		
		if (Util.isNotValid(objType) || Util.isNotValid(itemName) || Util.isNotValid(value)) {
			return "";
		}
		
		if (objType.equals("A")) {
			if (itemName.equals("PurchType")) {
				if (value.equals("G")) { return "[목표 보장]"; }
				else if (value.equals("N")) { return "[목표 비보장]"; }
				else if (value.equals("H")) { return "[하우스 광고]"; }
			} else if (itemName.equals("GoalType")) {
				if (value.equals("A")) { return "[광고 예산]"; }
				else if (value.equals("I")) { return "[노출량]"; }
				else if (value.equals("U")) { return "[무제한 노출]"; }
			} else if (itemName.equals("ImpDailyType")) {
				if (value.equals("E")) { return "[모든 날짜 균등]"; }
				else if (value.equals("W")) { return "[통계 기반 요일별 차등]"; }
			} else if (itemName.equals("ImpHourlyType")) {
				if (value.equals("E")) { return "[모든 시간 균등]"; }
				else if (value.equals("D")) { return "[일과 시간 집중]"; }
			} else if (itemName.equals("ImpAddRatio")) {
				if (value.equals("1000")) { return "[+1000 %]"; }
				else if (value.equals("900")) { return "[+900 %]"; }
				else if (value.equals("800")) { return "[+800 %]"; }
				else if (value.equals("700")) { return "[+700 %]"; }
				else if (value.equals("600")) { return "[+600 %]"; }
				else if (value.equals("500")) { return "[+500 %]"; }
				else if (value.equals("400")) { return "[+400 %]"; }
				else if (value.equals("300")) { return "[+300 %]"; }
				else if (value.equals("200")) { return "[+200 %]"; }
				else if (value.equals("150")) { return "[+150 %]"; }
				else if (value.equals("100")) { return "[+100 %]"; }
				else if (value.equals("75")) { return "[+75 %]"; }
				else if (value.equals("50")) { return "[+50 %]"; }
				else if (value.equals("20")) { return "[+20 %]"; }
				else if (value.equals("0")) { return "[+0 %]"; }
				else if (value.equals("-15")) { return "[-15 %]"; }
				else if (value.equals("-30")) { return "[-30 %]"; }
				else if (value.equals("-50")) { return "[-50 %]"; }
				else if (value.equals("-70")) { return "[-70 %]"; }
				else if (value.equals("-90")) { return "[-90 %]"; }
			} else if (itemName.equals("CPM")) {
				if (value.equals("0")) { return "[화면 설정값 기준]"; }
				else { return value; }
			} else if (itemName.equals("DailyCap")) {
				if (value.equals("0")) { return "[노출한도 설정 안함]"; }
				else { return value; }
			} else if (itemName.equals("FreqCap")) {
				if (value.equals("0")) { return "[매체 설정값 적용]"; }
				if (value.equals("1")) { return "[금지 시간 설정 안함]"; }
				else { return value; }
			} else if (itemName.equals("DailyScrCap")) {
				if (value.equals("0")) { return "[매체 설정값 적용]"; }
				else { return value; }
			} else if (itemName.equals("Duration")) {
				if (value.equals("0")) { return "[화면 설정값 기준]"; }
				else { return value; }
			} else if (itemName.equals("TargetOper")) {
				if (value.equals("A")) { return "[And]"; }
				else if (value.equals("O")) { return "[Or]"; }
			} else if (itemName.equals("Status")) {
				if (value.equals("D")) { return "[준비]"; }
				else if (value.equals("P")) { return "[승인대기]"; }
				else if (value.equals("A")) { return "[예약]"; }
				else if (value.equals("R")) { return "[진행]"; }
				else if (value.equals("C")) { return "[완료]"; }
				else if (value.equals("J")) { return "[거절]"; }
				else if (value.equals("V")) { return "[보관]"; }
				else if (value.equals("T")) { return "[삭제]"; }
			}
		} else if (objType.equals("C")) {
			if (itemName.equals("Category")) {
				if (value.equals("A")) { return "[가전]"; }
				else if (value.equals("B")) { return "[게임]"; }
				else if (value.equals("C")) { return "[관공서/단체]"; }
				else if (value.equals("D")) { return "[관광/레저]"; }
				else if (value.equals("E")) { return "[교육/출판]"; }
				else if (value.equals("F")) { return "[금융]"; }
				else if (value.equals("G")) { return "[문화/엔터테인먼트]"; }
				else if (value.equals("H")) { return "[미디어/서비스]"; }
				else if (value.equals("I")) { return "[생활용품]"; }
				else if (value.equals("J")) { return "[유통]"; }
				else if (value.equals("K")) { return "[제약/의료]"; }
				else if (value.equals("L")) { return "[주류]"; }
				else if (value.equals("M")) { return "[주택/가구]"; }
				else if (value.equals("N")) { return "[패션]"; }
				else if (value.equals("O")) { return "[화장품]"; }
			} else if (itemName.equals("CreatType")) {
				if (value.equals("C")) { return "[일반 광고]"; }
				else if (value.equals("F")) { return "[대체 광고]"; }
			} else if (itemName.equals("durPolicy")) {
				if (value.equals("Y")) { return "[Y]"; }
				else if (value.equals("N")) { return "[N]"; }
			} else if (itemName.equals("Status")) {
				if (value.equals("D")) { return "[준비]"; }
				else if (value.equals("P")) { return "[승인대기]"; }
				else if (value.equals("A")) { return "[승인]"; }
				else if (value.equals("J")) { return "[거절]"; }
				else if (value.equals("V")) { return "[보관]"; }
				else if (value.equals("T")) { return "[삭제]"; }
			}
		}
		
		return "";
	}
	
	
    /**
	 * 개체 유형과 번호로 가장 순위가 높은 채널 번호를 획득
	 */
    public static int getFirstPriorityChannelByTypeObjId(String type, int objId) {
    	
    	return getFirstPriorityChannelByTypeObjId(type, objId, "");
    }
	
	
    /**
	 * 개체 유형과 번호로 가장 순위가 높은 채널 번호를 획득
	 */
    public static int getFirstPriorityChannelByTypeObjId(String type, int objId, String viewTypeCode) {
    	
    	if (Util.isNotValid(type)) {
    		return -1;
    	}
    	
		List<Tuple> tupleList = sOrgService.getChannelTupleListByTypeObjId(type, objId);
		for(Tuple tuple : tupleList) {
			// SELECT c.channel_id, c.name, c.active_status, c.view_type_code
			int channelId = (Integer) tuple.get(0);
			//String name = (String) tuple.get(1);
			boolean activeStatus = (Boolean) tuple.get(2);
			String chanViewTypeCode = (String) tuple.get(3);
			
			if (activeStatus) {
				if (type.equals("P")) {
					return channelId;
				} else if (type.equals("S")) {
					if (Util.isNotValid(viewTypeCode) && Util.isNotValid(chanViewTypeCode)) {
						// 둘다 지정안됨
						return channelId;
					} else if (Util.isValid(viewTypeCode) && Util.isValid(chanViewTypeCode) && 
							viewTypeCode.equals(chanViewTypeCode)) {
						return channelId;
					}
				}
			}
		}
		
		return -1;
    }
	
	
    /**
	 * ID 문자열에 따라 같음 다름을 판단하여 문자열로 표시
	 */
    public static String getAdPackType(String ids, boolean hasSpace) {
    	
    	if (Util.isNotValid(ids)) {
    		return "";
    	}
    	
		List<String> adPackIds = Util.tokenizeValidStr(ids);
		if (adPackIds.size() == 0) {
			return "";
		}

		String ret = "";
		
		try {
			
			String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			HashMap<String, String> map = new HashMap<String, String>();
			int idx = 0;
			
			for(String id : adPackIds) {
				String val = map.get(id);
				if (Util.isNotValid(val)) {
					if (idx <= 25) {
						val = str.substring(idx, idx + 1);
						map.put(id,  val);
						idx++;
					} else {
						val = "9";
					}
				}
				
				ret += val;
			}
			
			if (Util.isNotValid(ret)) {
				return ret;
			}
			
			if (hasSpace) {
				return ret.replaceAll(".(?!$)", "$0 ");
			}
			
			return ret;
			
		} catch (Exception e) {
			logger.error("getAdPackType", e);
		}

		return ret;
    }

    
    private static boolean sendAlimTalk(String serverUrl, String jsonStr) {
    	
    	if (Util.isNotValid(jsonStr) || Util.isNotValid(serverUrl)) {
    		return false;
    	}
    	
    	int retCode = Util.sendStreamToServer(serverUrl, jsonStr);
    	
    	return retCode == 200;
    }
	
	
    /**
	 * 알림톡 이벤트: ActScr 알림톡 발송
	 */
    public static boolean sendAlimTalkForActScr(String mediumID, String talkID, String subscribers, int failMins, int failCnt, String failStr) {
    	
    	if (failCnt < 1 || Util.isNotValid(mediumID) || Util.isNotValid(talkID) || Util.isNotValid(subscribers)) {
    		return false;
    	}
    	
    	String server = Util.getFileProperty("alim.baseUrl");
    	if (!server.toLowerCase().startsWith("http")) {
    		return false;
    	}
    	
    	String failList = failStr;
    	if (failCnt > 10) {
    		failList += " 등";
    	}
    	
		JSONObject reqParams = new JSONObject();
		reqParams.put("mediumID", mediumID);
		reqParams.put("Phone", subscribers);

		reqParams.put("alimID", talkID);
		reqParams.put("failMins", String.valueOf(failMins));
		reqParams.put("failCnt", String.valueOf(failCnt));
		reqParams.put("failList", failList);
		
		return sendAlimTalk(server + "/alimtalk/send/adnet-active-screen", reqParams.toString());
    	
    }
	
	
    /**
	 * 알림톡 이벤트: ActScr 종료 알림톡 발송
	 */
    public static boolean sendAlimTalkForActScrEnd(String mediumID, String talkID, String subscribers) {
    	
    	if (Util.isNotValid(mediumID) || Util.isNotValid(talkID) || Util.isNotValid(subscribers)) {
    		return false;
    	}
    	
    	String server = Util.getFileProperty("alim.baseUrl");
    	if (!server.toLowerCase().startsWith("http")) {
    		return false;
    	}
    	
		JSONObject reqParams = new JSONObject();
		reqParams.put("mediumID", mediumID);
		reqParams.put("Phone", subscribers);

		reqParams.put("alimID", talkID);
		
		return sendAlimTalk(server + "/alimtalk/send/adnet-active-screen-end", reqParams.toString());
    	
    }
    
}
