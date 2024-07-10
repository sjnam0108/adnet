package kr.adnetwork.controllers.adc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.InvService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.adc.AdcCreatFileThumbItem;

/**
 * 광고 갤러리 컨트롤러
 */
@Controller("adc-gallery-controller")
@RequestMapping(value="/adc/gallery")
public class AdcGalleryController {

	private static final Logger logger = LoggerFactory.getLogger(AdcGalleryController.class);


    @Autowired 
    private AdcService adcService;

    @Autowired 
    private InvService invService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private KnlService knlService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 광고 갤러리 페이지
	 */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String index(Model model, Locale locale, HttpSession session,
    		HttpServletRequest request) {
    	modelMgr.addMainMenuModel(model, locale, session, request);
    	solMsgMgr.addCommonMessages(model, locale, session, request);

    	msgMgr.addViewMessages(model, locale,
    			new Message[] {

    			});

    	// 페이지 제목
    	model.addAttribute("pageTitle", "광고 갤러리");

    	//model.addAttribute("PlaylistTypes", getPlaylistTypeDropDownList(Util.getSessionMediumId(session)));

    	
        return "adc/gallery";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody List<AdcCreatFileThumbItem> read(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	try {
    		ArrayList<AdcCreatFileThumbItem> items = new ArrayList<AdcCreatFileThumbItem>();
    		
    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    		if (medium != null) {
    			
        		List<Integer> activeIds = getActiveCreatFileIds(medium.getShortName(), medium.getId());

        		List<AdcCreatFile> list = adcService.getCreatFileListByMediumId(medium.getId());
        		Date now = new Date();
        		for (AdcCreatFile creatFile : list) {
        			AdcCreatFileThumbItem item = new AdcCreatFileThumbItem(creatFile);
        			if (activeIds.contains(item.getId())) {
        				item.setType(item.getType() + "P");
        			}
        			if (Util.addDays(item.getDate(), 3).after(now)) {
        				item.setType(item.getType() + "R");
        			}
        			items.add(item);
        		}

        		
        	    Collections.sort(items, new Comparator<AdcCreatFileThumbItem>() {
        	        public int compare(AdcCreatFileThumbItem item1, AdcCreatFileThumbItem item2) {
        	            return item2.getDate().compareTo(item1.getDate());
        	        }
        	    });
        	    
    		}
    		
    		return items;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }

    
    private List<Integer> getActiveCreatFileIds(String mediumShortName, int mediumId) {
    	
    	ArrayList<Integer> list = new ArrayList<Integer>();
		List<Integer> resultScrIds = null;
		Date today = Util.removeTimeOfDate(new Date());
		
		// 대체 광고를 미리 조회
		List<AdcCreative> fallbackList = adcService.getValidCreativeFallbackListByMediumId(mediumId);
		
		resultScrIds = invService.getMonitScreenIdsByMediumId(mediumId);
		if (resultScrIds.size() > 0) {
			
			// 게시 유형이 명시되지 않은 일반 형식 대상
			List<AdcAdCreative> candiList = adcService.getCandiAdCreativeListByMediumIdDate
					(mediumId, today, Util.addDays(today, 1));
			HashMap<String, List<Integer>> map = invService.getResoScreenIdMapByScreenIdIn(resultScrIds);
			
			Set<String> keys = map.keySet();
			for(String reso : keys) {
				ArrayList<String> creatFileIds = new ArrayList<String>();
				
				for(AdcAdCreative ac : candiList) {
					if (Util.isValid(ac.getAd().getViewTypeCode())) {
						continue;
					}
					AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(ac.getCreative().getId(), reso);
					
					// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
					if (creatFile != null) {
						// 광고에 설정된 재생 시간 고정값 수용
						if (ac.getAd().getDuration() >= 5) {
							creatFile.setFormalDurSecs(ac.getAd().getDuration());
						}
						
						String idKey = "C" + String.valueOf(ac.getCreative().getId()) + "R" + reso + "D" + ac.getAd().getDuration();
						if (!creatFileIds.contains(idKey)) {
							creatFileIds.add(idKey);
							if (!list.contains(creatFile.getId())) {
								list.add(creatFile.getId());
							}
						}
					}
				}
				
				for(AdcCreative c : fallbackList) {
					if (Util.isValid(c.getViewTypeCode())) {
						continue;
					}
					AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(c.getId(), reso);
					
					// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
					if (creatFile != null) {
						// 광고에 설정될 수 없기 때문에 추가 설정이 필요없고,
						// 해상도 뒤 Duration의 0은 화면 설정 값 기반을 의미
						String idKey = "C" + String.valueOf(c.getId()) + "R" + reso + "D0";
						if (!creatFileIds.contains(idKey)) {
							creatFileIds.add(idKey);
							if (!list.contains(creatFile.getId())) {
								list.add(creatFile.getId());
							}
						}
					}
				}
			}
			
			
			// 게시유형이 명시된 항목 대상
			List<FndViewType> viewTypeList = fndService.getViewTypeList();
			ArrayList<String> viewTypeResos = new ArrayList<String>();
			for(FndViewType viewType : viewTypeList) {
				List<String> media = Util.tokenizeValidStr(viewType.getDestMedia());
				String r = "";
				for(String m : media) {
					if (mediumShortName.equals(m)) {
						r = viewType.getResolution();
						break;
					}
				}
				if (Util.isValid(r) && !viewTypeResos.contains(r)) {
					// 이 해상도는 이 매체에 사용이 허용됨
					viewTypeResos.add(r);
				}
			}
			
			for(String reso : viewTypeResos) {
				
				ArrayList<String> creatFileIds = new ArrayList<String>();
				
				for(AdcAdCreative ac : candiList) {
					if (Util.isNotValid(ac.getAd().getViewTypeCode())) {
						continue;
					}
					AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(ac.getCreative().getId(), reso);
					
					// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
					if (creatFile != null) {
						// 광고에 설정된 재생 시간 고정값 수용
						if (ac.getAd().getDuration() >= 5) {
							creatFile.setFormalDurSecs(ac.getAd().getDuration());
						}
						
						String idKey = "C" + String.valueOf(ac.getCreative().getId()) + "R" + reso + "D" + ac.getAd().getDuration();
						if (!creatFileIds.contains(idKey)) {
							creatFileIds.add(idKey);
							if (!list.contains(creatFile.getId())) {
								list.add(creatFile.getId());
							}
						}
					}
				}
				
				for(AdcCreative c : fallbackList) {
					if (Util.isNotValid(c.getViewTypeCode())) {
						continue;
					}
					AdcCreatFile creatFile = adcService.getCreatFileByCreativeIdResolution(c.getId(), reso);
					
					// 해당 광고 소재에 이 해상도의 파일이 존재하지 않을 수도 있다!!
					if (creatFile != null) {
						// 광고에 설정될 수 없기 때문에 추가 설정이 필요없고,
						// 해상도 뒤 Duration의 0은 화면 설정 값 기반을 의미
						String idKey = "C" + String.valueOf(c.getId()) + "R" + reso + "D0";
						if (!creatFileIds.contains(idKey)) {
							creatFileIds.add(idKey);
							if (!list.contains(creatFile.getId())) {
								list.add(creatFile.getId());
							}
						}
					}
				}
			}
			
		}
		
		return list;
    }
}
