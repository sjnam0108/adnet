package kr.adnetwork.controllers.adn;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.UploadTransitionModel;
import kr.adnetwork.models.adc.AdcAdTarget;
import kr.adnetwork.models.adc.AdcCreatTarget;
import kr.adnetwork.models.adc.AdcMobTarget;
import kr.adnetwork.models.fnd.FndMobRegion;
import kr.adnetwork.models.inv.InvScrLoc;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgRadRegion;
import kr.adnetwork.models.rev.RevAdSelect;
import kr.adnetwork.models.rev.RevPlayHist;
import kr.adnetwork.models.rev.RevScrStatusLine;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.inv.InvScreenInfoData;
import kr.adnetwork.viewmodels.inv.InvScreenLocData;
import kr.adnetwork.viewmodels.inv.InvScreenLocItem;
import kr.adnetwork.viewmodels.inv.InvSimpleAdSelect;
import kr.adnetwork.viewmodels.inv.InvSiteMapLocItem;

/**
 * ADN 공통 컨트롤러
 */
@Controller("adn-common-controller")
@RequestMapping(value="/adn/common")
public class CommonController {
	
	private static final Logger logger = LoggerFactory.getLogger(CommonController.class);


    @Autowired 
    private KnlService knlService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private AdcService adcService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private OrgService orgService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;

    
    /**
     * 파일 다운로드 액션
     */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
    public ModelAndView download(HttpServletRequest request,
    		HttpServletResponse response) {

    	String type = Util.parseString(request.getParameter("type"));
    	String file = Util.parseString(request.getParameter("file"));
    	//String code = Util.parseString(request.getParameter("code"));
    	
    	String prefix = Util.parseString(request.getParameter("prefix"));

    	File target = null;
    	
    	try {
    		if (Util.isValid(file) && Util.isValid(type)) {
    			if (type.equals("XlsTemplate") || type.equals("Log")) {
    				target = new File(SolUtil.getPhysicalRoot(type) + "/" + file);
    			}
    		}
        	
        	if (target == null) {
        		throw new ServerOperationForbiddenException("OperationError");
        	}

            response.setContentType("application/octet-stream;charset=UTF-8");
            
            if (target.length() < Integer.MAX_VALUE) {
                response.setContentLength((int)target.length());
            }

            String userAgent = request.getHeader("User-Agent");
            
            String targetName = (Util.isValid(prefix) ? prefix + "_" : "") + target.getName();
        	if (userAgent.indexOf("MSIE 5.5") > -1) { // MS IE 5.5 이하
        	    response.setHeader("Content-Disposition", "filename=\""
        		    + URLEncoder.encode(targetName, "UTF-8") + "\";");
        	} else if (userAgent.indexOf("MSIE") > -1) { // MS IE (보통은 6.x 이상 가정)
        	    response.setHeader("Content-Disposition", "attachment; filename=\""
        		    + URLEncoder.encode(targetName, "UTF-8") + "\";");
        	} else {
        	    response.setHeader("Content-Disposition", "attachment; filename=\""
        		    + new String(targetName.getBytes("UTF-8"), "latin1") + "\";");
        	}
        	
            FileCopyUtils.copy(new FileInputStream(target), response.getOutputStream());
    	} catch (Exception e) {
			logger.error("download", e);
			throw new ServerOperationForbiddenException("OperationError");
    	}
 
        return null;
    }
	
	
	/**
	 * 모듈 전용 파일 업로드 페이지
	 */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String upload(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {

    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	String type = Util.parseString(request.getParameter("type"));
    	String code = Util.parseString(request.getParameter("code"));
    	
    	UploadTransitionModel uploadModel = new UploadTransitionModel();
    	
    	try {
        	if (medium != null && Util.isValid(type)) {
        		uploadModel.setMediumId(medium.getId());
        		uploadModel.setType(type);
        		uploadModel.setCode(code);
        		uploadModel.setSaveUrl("/adn/common/uploadsave");
        		
        		if (type.equals("MEDIA")) {
        			uploadModel.setAllowedExtensions("[\".jpg\", \".png\", \".mp4\"]");
        		}
        	}
    	} catch (Exception e) {
    		logger.error("upload", e);
    	}

    	model.addAttribute("uploadModel", uploadModel);
    	
        return "adn/modal/upload";
    }
	
    
	/**
	 * 화면 정보 모달
	 */
    @RequestMapping(value = "/screenInfo", method = {RequestMethod.GET, RequestMethod.POST })
    public String screenInfo(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	
    	int screenId = Util.parseInt(request.getParameter("id"));
    	Date date = Util.setMaxTimeOfDate(Util.parseDate(request.getParameter("date")));
    	String dateStr = date == null ? Util.toDateString(new Date()) : Util.toDateString(date);
    	
    	//KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session)); 
    	

    	ArrayList<String> svcDateList = new ArrayList<String>();
    	List<RevScrStatusLine> statusList = revService.getScrStatusLineListByScreenId(screenId);
    	for(RevScrStatusLine statusLine : statusList) {
    		String dateNumber = String.format("%1$tQ", statusLine.getPlayDate());
			
			if (!svcDateList.contains(dateNumber)) {
				svcDateList.add(dateNumber);
			}
    	}
    	
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	model.addAttribute("dates", svcDateList);
    	
    	model.addAttribute("value_id", String.valueOf(screenId));
    	model.addAttribute("value_date", dateStr);
    	
    	// 화면 정보창에서 현재 화면의 위치를 나타내는 marker-[venue]-ico.png 파일은 변경 예정
    	// 아래에서는 임시로 default 적용
    	model.addAttribute("icoMarkerUrl", SolUtil.getIconMarkerUrl("default"));

    	model.addAttribute("markerList", getNearBySiteList(screenId, 10));
    	
        return "inv/modal/screenInfo";
    }
    
	/**
	 * 화면 정보 모달 - 모든 요약 정보 읽기 액션
	 */
    @RequestMapping(value = "/readScreenInfo", method = RequestMethod.POST)
    public @ResponseBody InvScreenInfoData readScreenInfoData(@RequestBody Map<String, Object> model,
    		Locale locale, HttpSession session) {

    	int screenId = (int)model.get("id");
    	Date playDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("date")));
    	if (playDate == null) {
    		playDate = Util.removeTimeOfDate(new Date());
    	}
    	
    	InvScreenInfoData ret = new InvScreenInfoData(
    			invService.getScreen(screenId), 
    			revService.getScrStatusLine(screenId, playDate),
    			playDate,
    			revService.getObjTouch("S", screenId),
    			invService.getRTScreenByScreenId(screenId));
    	
    	return ret;
    }
    
	/**
	 * 화면 정보 모달 - 화면 재생 기록 읽기 액션
	 */
    @RequestMapping(value = "/readScreenPlayHist", method = RequestMethod.POST)
    public @ResponseBody List<InvSimpleAdSelect> readScreenPlayHist(@RequestBody Map<String, Object> model,
    		Locale locale, HttpSession session) {

    	int screenId = (int)model.get("id");
    	
    	ArrayList<InvSimpleAdSelect> apiAdSelList = new ArrayList<InvSimpleAdSelect>();
    	
    	List<RevAdSelect> adSelList = revService.getLastAdSelectListByScreenId(screenId, 10);
    	for(RevAdSelect adSelect : adSelList) {
    		apiAdSelList.add(new InvSimpleAdSelect(adSelect));
    	}

    	List<RevPlayHist> playHistList = revService.getLastPlayHistListByScreenId(screenId, 10);
    	for(RevPlayHist playHist : playHistList) {
    		apiAdSelList.add(new InvSimpleAdSelect(playHist));
    	}

    	Collections.sort(apiAdSelList, new Comparator<InvSimpleAdSelect>() {
	    	public int compare(InvSimpleAdSelect item1, InvSimpleAdSelect item2) {
	    		return item2.getDate().compareTo(item1.getDate());
	    	}
		});
    	
    	return apiAdSelList.stream().limit(10).collect(Collectors.toList());
    }
    
	/**
	 * 화면 정보 모달 - 근처 사이트 마커 정보 획득
	 */
    private List<InvSiteMapLocItem> getNearBySiteList(int screenId, int cnt) {
    	
    	InvScreen screen = invService.getScreen(screenId);
    	if (screen == null) {
    		return new ArrayList<InvSiteMapLocItem>();
    	}
    	
    	return invService.getCloseSitesBy(screen.getSite(), cnt, true); 
    }
	
    
	/**
	 * 지리 위치 모달
	 */
    @RequestMapping(value = "/geoLoc", method = {RequestMethod.GET, RequestMethod.POST })
    public String geoLoc(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	int mediumId = Util.getSessionMediumId(session);
    	
    	String typeLvl = "";
    	String display = "";
    	String scrCnt = "";
    	String siteCnt = "";
    	
    	// 모바일 타겟팅의 경우의 지도의 중심 및 줌 레벨을 확정할 수 있다.
    	String mobLat = "";
    	String mobLng = "";
    	String mobZoom = "";
    	
    	// 모바일 타겟팅에서 유형별 중요 정보를 담기 위함
    	//   - CR의 경우 반경
    	//   - RG2의 경우 json 파일 내용
    	String mobCode = "";
    	
    	List<Integer> ids = new ArrayList<Integer>();
    	List<Integer> tmpIds = new ArrayList<Integer>();
    	
    	ArrayList<InvSiteMapLocItem> mapItemList = new ArrayList<InvSiteMapLocItem>();
    	
    	//
    	// 유형: type
    	//
    	//     CREATINVEN	광고 소재의 인벤 타겟팅
    	//     ADINVEN		광고의 인벤 타겟팅
    	//     ADMOB		광고의 모바일 타겟팅
    	//
    	String type = Util.parseString(request.getParameter("type"));
    	if (Util.isValid(type)) {
    		
    		String tgtValue = "";
    		
    		if (type.equals("CREATINVEN")) {
    			AdcCreatTarget tgt = adcService.getCreatTarget(Util.parseInt(request.getParameter("id")));
    			if (tgt != null) {
    				typeLvl = tgt.getInvenType();
    				display = tgt.getTgtDisplay();
    				tgtValue = tgt.getTgtValue();
    			}
    		} else if (type.equals("ADINVEN")) {
    			AdcAdTarget tgt = adcService.getAdTarget(Util.parseInt(request.getParameter("id")));
    			if (tgt != null) {
    				typeLvl = tgt.getInvenType();
    				display = tgt.getTgtDisplay();
    				tgtValue = tgt.getTgtValue();
    			}
    		} else if (type.equals("ADMOB")) {
    			AdcMobTarget tgt = adcService.getMobTarget(Util.parseInt(request.getParameter("id")));
    			if (tgt != null) {
    				if (tgt.getMobType().equals("RG")) {
    					FndMobRegion mobRegion = fndService.getMobRegion(tgt.getTgtId());
    					if (mobRegion != null) {
    						typeLvl = "RG2";
    						display = mobRegion.getName();
    						
    						List<String> codeStrs = Util.tokenizeValidStr(mobRegion.getCode(), ",");
    						if (codeStrs.size() == 4) {
    							mobLat = codeStrs.get(0);
    							mobLng = codeStrs.get(1);
    							mobZoom = codeStrs.get(2);
    							
    							mobCode = codeStrs.get(3);
    						}
    					}
    				} else if (tgt.getMobType().equals("CR")) {
    					OrgRadRegion radRegion = orgService.getRadRegion(tgt.getTgtId());
    					if (radRegion != null) {
    						typeLvl = "CR";
    						display = radRegion.getName();
    						mobLat = String.valueOf(radRegion.getLat());
    						mobLng = String.valueOf(radRegion.getLng());
    						mobCode = String.valueOf(radRegion.getRadius());
    						
    						if (radRegion.getRadius() <= 100) {
    							mobZoom = String.valueOf(18);
    						} else if (radRegion.getRadius() <= 200) {
    							mobZoom = String.valueOf(17);
    						} else if (radRegion.getRadius() <= 300) {
    							mobZoom = String.valueOf(16);
    						} else if (radRegion.getRadius() <= 500) {
    							mobZoom = String.valueOf(15);
    						} else if (radRegion.getRadius() <= 2000) {
    							mobZoom = String.valueOf(14);
    						} else if (radRegion.getRadius() <= 3000) {
    							mobZoom = String.valueOf(13);
    						} else {
    							mobZoom = String.valueOf(12);
    						}
    					}
    				}
    			}
    		}
    		
    		if (type.equals("CREATINVEN") || type.equals("ADINVEN")) {
        		if (Util.isValid(tgtValue) && Util.isValid(typeLvl)) {
        			if (typeLvl.equals("CT")) {
        				
        		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumStateCodeIn(
        		    			mediumId, Util.getStringList(tgtValue)));
        		    	
        		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumStateCodeIn(
        		    			mediumId, Util.getStringList(tgtValue)));
        		    	
        		    	ids = invService.getMonitSiteIdsByMediumStateCodeIn(
        		    			mediumId, Util.getStringList(tgtValue));
        		    	
        			} else if (typeLvl.equals("RG")) {
        				
        		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumRegionCodeIn(
        		    			mediumId, Util.getStringList(tgtValue)));
        		    	
        		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumRegionCodeIn(
        		    			mediumId, Util.getStringList(tgtValue)));
        		    	
        		    	ids = invService.getMonitSiteIdsByMediumRegionCodeIn(
        		    			mediumId, Util.getStringList(tgtValue));
        		    	
        			} else if (typeLvl.equals("SC")) {
        				
        		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumScreenIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue)));
        		    	
        		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumScreenIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue)));
        		    	
        		    	ids = invService.getMonitSiteIdsByMediumScreenIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue));
        		    	
        			} else if (typeLvl.equals("ST")) {
        				
        		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumSiteIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue)));
        		    	
        		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumSiteIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue)));
        		    	
        		    	ids = invService.getMonitSiteIdsByMediumSiteIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue));
        		    	
        			} else if (typeLvl.equals("CD")) {
        				
        		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumSiteCondCodeIn(
        		    			mediumId, Util.getStringList(tgtValue)));
        		    	
        		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumSiteCondCodeIn(
        		    			mediumId, Util.getStringList(tgtValue)));
        		    	
        		    	ids = invService.getMonitSiteIdsByMediumSiteCondCodeIn(mediumId, Util.getStringList(tgtValue));
        		    	
        			} else if (typeLvl.equals("SP")) {
        				
        		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumScrPackIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue)));
        		    	
        		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumScrPackIdIn(
        		    			mediumId, Util.getIntegerList(tgtValue)));
        		    	
        		    	ids = invService.getMonitSiteIdsByMediumScrPackIdIn(mediumId, Util.getIntegerList(tgtValue));
        		    	
        			} else if (typeLvl.equals("PL") || typeLvl.equals("CR") || typeLvl.equals("AD")) {
        				
        		    	scrCnt = NumberFormat.getInstance().format(invService.getMonitScreenCountByMediumScreenIdIn(
        		    			mediumId, tmpIds));
        		    	
        		    	siteCnt = NumberFormat.getInstance().format(invService.getMonitSiteCountByMediumScreenIdIn(
        		    			mediumId, tmpIds));
        		    	
        		    	ids = invService.getMonitSiteIdsByMediumScreenIdIn(mediumId, tmpIds);
        			}
        	    	
        	    	List<Tuple> locList = invService.getSiteLocListBySiteIdIn(ids);
        	    	for(Tuple tuple : locList) {
        	    		double lat = (double)Util.parseFloat((String) tuple.get(1));
        	    		double lng = (double)Util.parseFloat((String) tuple.get(2));
        	    		
        	    		mapItemList.add(new InvSiteMapLocItem((String) tuple.get(0), 
        	    				(String) tuple.get(3), lat, lng));
        	    	}
        		}
    		} else if (type.equals("ADMOB")) {
    			scrCnt = "N/A";
    			siteCnt = "N/A";
    		}
    	}
    	
    	model.addAttribute("type", typeLvl);
    	model.addAttribute("display", display);
    	model.addAttribute("scrCnt", scrCnt);
    	model.addAttribute("siteCnt", siteCnt);
    	
    	model.addAttribute("markerList", mapItemList);
    	
    	
    	model.addAttribute("mobLat", mobLat);
    	model.addAttribute("mobLng", mobLng);
    	model.addAttribute("mobZoom", mobZoom);
    	model.addAttribute("mobCode", mobCode);
    	
    	
        return "inv/modal/geoloc";
    }
	
    
	/**
	 * 이동형 화면 위치 모달
	 */
    @RequestMapping(value = "/scrLoc", method = {RequestMethod.GET, RequestMethod.POST })
    public String scrLoc(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	
    	int screenId = Util.parseInt(request.getParameter("id"));
    	Date date = Util.setMaxTimeOfDate(Util.parseDate(request.getParameter("date")));
    	String dateStr = "";
    	//String dateStr = date == null ? Util.toDateString(new Date()) : Util.toDateString(date);
    	

    	ArrayList<String> svcDateList = new ArrayList<String>();
    	List<Date> dateList = invService.getScrLocDateListByScreenId(screenId);
    	for(Date d : dateList) {
    		svcDateList.add((String.format("%1$tQ", d)));
    	}

    	
    	Collections.sort(dateList, Collections.reverseOrder());

    	if (date == null) {
    		if (dateList.size() > 0) {
    			dateStr = Util.toDateString(dateList.get(0));
    		} else {
        		dateStr = Util.toDateString(new Date());
    		}
    	} else {
    		dateStr = Util.toDateString(date);
    	}
    	
    	
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	model.addAttribute("dates", svcDateList);
    	
    	model.addAttribute("value_id", String.valueOf(screenId));
    	model.addAttribute("value_date", dateStr);
    	
    	
        return "inv/modal/scrLoc";
    }
    
    
	/**
	 * 이동형 화면 위치 모달 - 읽기 액션
	 */
    @RequestMapping(value = "/readScrLoc", method = RequestMethod.POST)
    public @ResponseBody InvScreenLocData readScreenLocData(@RequestBody Map<String, Object> model,
    		Locale locale, HttpSession session) {

    	int screenId = (int)model.get("id");
    	Date playDate = Util.removeTimeOfDate(Util.parseZuluTime((String)model.get("date")));
    	if (playDate == null) {
    		playDate = Util.removeTimeOfDate(new Date());
    	}
    	
    	
    	ArrayList<InvScreenLocItem> items = new ArrayList<InvScreenLocItem>();
    	
    	InvScreenLocData ret = new InvScreenLocData(playDate);
    	
    	List<InvScrLoc> list = invService.getScrLocListByScreenIdDate(screenId, playDate);
    	for(InvScrLoc scrLoc : list) {
    		items.add(new InvScreenLocItem(scrLoc));
    	}
    	ret.setLocItems(items);
    	
    	return ret;
    }
}
