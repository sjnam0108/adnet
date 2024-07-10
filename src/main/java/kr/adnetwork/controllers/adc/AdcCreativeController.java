package kr.adnetwork.controllers.adc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Tuple;
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

import com.google.common.io.Files;

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.info.StringInfo;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.CustomComparator;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.LoginUser;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.adc.AdcCreatFile;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.fnd.FndCtntFolder;
import kr.adnetwork.models.fnd.FndViewType;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgAdvertiser;
import kr.adnetwork.models.rev.RevCreatDecn;
import kr.adnetwork.models.rev.RevObjTouch;
import kr.adnetwork.models.service.AdcService;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.models.service.KnlService;
import kr.adnetwork.models.service.OrgService;
import kr.adnetwork.models.service.RevService;
import kr.adnetwork.models.service.SysService;
import kr.adnetwork.models.sys.SysAuditTrail;
import kr.adnetwork.models.sys.SysAuditTrailValue;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.DropDownListItem;
import kr.adnetwork.viewmodels.knl.KnlMediumItem;
import kr.adnetwork.viewmodels.sys.SysAuditTrailValueItem;

/**
 * 광고 소재 컨트롤러
 */
@Controller("adc-creative-controller")
@RequestMapping(value="/adc/creative")
public class AdcCreativeController {

	private static final Logger logger = LoggerFactory.getLogger(AdcCreativeController.class);


    @Autowired 
    private AdcService adcService;

    @Autowired 
    private OrgService orgService;

    @Autowired 
    private KnlService knlService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private FndService fndService;

    @Autowired 
    private SysService sysService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 광고 소재 페이지
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
    	model.addAttribute("pageTitle", "광고 소재");
    	
    	
    	ArrayList<KnlMediumItem> otherMedia = new ArrayList<KnlMediumItem>();
    	if (session != null) {
    		LoginUser loginUser = (LoginUser)session.getAttribute("loginUser");
    		int mediumId = Util.getSessionMediumId(session);
    		if (loginUser != null) {
    			List<KnlMediumItem> media = SolUtil.getAvailMediumListByUserId(loginUser.getId());
    			for(KnlMediumItem item : media) {
    				if (item.getId() == mediumId) {
    					continue;
    				}
    				otherMedia.add(item);
    			}
    		}
    	}
		model.addAttribute("otherMedia", otherMedia);
    	
		model.addAttribute("ViewTypes", getViewTypeDropDownList(Util.getSessionMediumId(session)));
    	
		

    	
        return "adc/creative";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = adcService.getCreativeList(request);
    		
    		// 하나라도 타겟팅이 존재하는 것만 기록
    		ArrayList<Integer> targetIds = new ArrayList<Integer>();
    		List<Tuple> countList = adcService.getCreatTargetCountGroupByMediumCreativeId(Util.getSessionMediumId(session));
    		for(Tuple tuple : countList) {
    			targetIds.add((Integer) tuple.get(0));
    		}
    		
    		
    		for(Object obj : result.getData()) {
    			AdcCreative creative = (AdcCreative)obj;
    			List<AdcCreatFile> fileList = adcService.getCreatFileListByCreativeId(creative.getId());
    			
    			String resolutions = "";
    			
    			// 이 값이 유효하다는 것: 게시 유형이 지정되어 있고, 유효한 게시 크기(해상도) 존재
    			String fixedReso = "";
    			if (Util.isValid(creative.getViewTypeCode())) {
    				fixedReso = fndService.getViewTypeResoByCode(creative.getViewTypeCode());
    			}

    			for(AdcCreatFile creatFile : fileList) {
        			
    				// 20% 범위로 적합도 판정
    				int fitness = Util.isValid(fixedReso) ?
    						SolUtil.measureResolutionWith(creatFile.getResolution(), fixedReso, 20) :
    						adcService.measureResolutionWithMedium(
    	            				creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    				
    				if (Util.isValid(resolutions)) {
    					resolutions += "|" + String.valueOf(fitness) + ":" + creatFile.getResolution();
    				} else {
    					resolutions = String.valueOf(fitness) + ":" + creatFile.getResolution();
    				}
    			}
    			
    			creative.setFileResolutions(resolutions);
    			
    			if (targetIds.contains(creative.getId())) {
    				creative.setInvenTargeted(true);
    			}
    			
    			RevObjTouch objTouch = revService.getObjTouch("C", creative.getId());
    			if (objTouch != null) {
    				creative.setLastPlayDate(objTouch.getDate1());
    			}
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 광고주 목록 획득
	 */
    public List<DropDownListItem> getAdvertiserListByMediumId(int mediumId) {
    	
		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();

		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(mediumId);
		
		retList.add(new DropDownListItem("", String.valueOf(-1)));
		
		for(OrgAdvertiser advertiser : list) {
			retList.add(new DropDownListItem(advertiser.getName(), 
					String.valueOf(advertiser.getId())));
		}
		
		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
    	
    	return retList;
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	int advId = (int)model.get("advertiser");
    	
    	String name = (String)model.get("name");
    	String type = (String)model.get("type");
    	String category = (String)model.get("category");
    	String viewType = Util.parseString((String)model.get("viewType"));

    	String advName = (String)model.get("advName");
    	String advDomainName = (String)model.get("advDomainName");

    	int fbWeight = (int)model.get("fbWeight");
    	boolean durPolicyOverriden = (Boolean)model.get("durPolicyOverriden");
    	
    	OrgAdvertiser advertiser = null;
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || Util.isNotValid(type) || fbWeight < 1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	if (advId == -1) {
    		// 광고주 신규 등록 모드
    		if (Util.isNotValid(advDomainName) || Util.isNotValid(advName)) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    		
    		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(Util.getSessionMediumId(session));
    		for (OrgAdvertiser a : list) {
    			if (a.getName().equals(advName) || a.getDomainName().equals(advDomainName)) {
            		throw new ServerOperationForbiddenException(
            				"기존에 동일한 이름 혹은 도메인명으로 등록된 광고주가 있습니다.");
    			}
    		}
    		
    		KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    		advertiser = new OrgAdvertiser(medium, advName, advDomainName, session);
    		orgService.saveOrUpdate(advertiser);
    	} else {
    		// 기존 광고주 선택 모드
    		advertiser = orgService.getAdvertiser(advId);
    		if (advertiser == null) {
        		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		}
    	}
    	

    	AdcCreative target = new AdcCreative(advertiser, name, type, Util.isValid(category) ? category : "",
    			viewType, durPolicyOverriden, fbWeight, session);
    	
        saveOrUpdate(target, locale, session);
        
        
        // 감사 추적: Case NC1
        sysService.saveOrUpdate(new SysAuditTrail(target, "N", "F", session));
        

        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String name = (String)model.get("name");
    	String type = (String)model.get("type");
    	String category = (String)model.get("category");
    	String viewType = Util.parseString((String)model.get("viewType"));
    	
    	int fbWeight = (int)model.get("fbWeight");
    	boolean durPolicyOverriden = (Boolean)model.get("durPolicyOverriden");
    	
    	// 광고주는 수정 불가
    	//OrgAdvertiser advertiser = orgService.getAdvertiser((int)model.get("advertiser"));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(name) || Util.isNotValid(type) || fbWeight < 1) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	AdcCreative target = adcService.getCreative((int)model.get("id"));
    	if (target != null) {
    		
    		
    		category = Util.isValid(category) ? category : "";
    		
    		//
    		// 감사 추적: Case EC1
    		//
    		//   대상 항목(전 항목 NotNull 속성이기 때문에, null 비교는 하지 않음)
    		// - 01: 광고 소재명
    		// - 02: 범주
    		// - 03: 게시 유형
    		// - 04: 소재 유형
    		// - 05: 매체화면 재생시간 무시
    		// - 06: 대체 광고간 가중치
    		//
    		String oName = target.getName();					// 01: 광고 소재명
    		String oCategory = target.getCategory();			// 02: 범주
    		String oViewTypeCode = target.getViewTypeCode();	// 03: 게시 유형
    		String oType = target.getType();					// 04: 소재 유형
    		boolean oPolicy = target.isDurPolicyOverriden();	// 05: 매체화면 재생시간 무시
    		int oWeight = target.getFbWeight();					// 06: 대체 광고간 가중치
    		
    		ArrayList<SysAuditTrailValueItem> editItems = new ArrayList<SysAuditTrailValueItem>();
    		
    		if (!oName.equals(name)) {
    			editItems.add(new SysAuditTrailValueItem("광고 소재명", oName, name));
    		}
    		if (!oCategory.equals(category)) {
    			editItems.add(new SysAuditTrailValueItem("범주",
    					Util.isValid(oCategory) ? oCategory : "[-]", Util.isValid(category) ? category : "[-]"));
    		}
    		if (!oViewTypeCode.equals(viewType)) {
    			editItems.add(new SysAuditTrailValueItem("게시 유형",
    					Util.isValid(oViewTypeCode) ? oViewTypeCode : "[-]", Util.isValid(viewType) ? viewType : "[-]"));
    		}
    		if (!oType.equals(type)) {
    			editItems.add(new SysAuditTrailValueItem("소재 유형", oType, type));
    		}
    		if (oPolicy != durPolicyOverriden) {
    			editItems.add(new SysAuditTrailValueItem("매체화면 재생시간 무시", oPolicy ? "Y" : "N", durPolicyOverriden ? "Y" : "N"));
    		}
    		if (oWeight != fbWeight) {
    			editItems.add(new SysAuditTrailValueItem("대체 광고간 가중치", String.valueOf(oWeight), String.valueOf(fbWeight)));
    		}
    		
    		
            target.setName(name);
            target.setType(type);
            target.setFbWeight(fbWeight);
            target.setDurPolicyOverriden(durPolicyOverriden);
            target.setCategory(category);
            target.setViewTypeCode(viewType);
            
            saveOrUpdate(target, locale, session);

            
            if (editItems.size() > 0) {
            	
                // 감사 추적: Case EC1
            	SysAuditTrail auditTrail = new SysAuditTrail(target, "E", "P", "F", session);
                sysService.saveOrUpdate(auditTrail);
            	
                for(SysAuditTrailValueItem item : editItems) {
                	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                }
            }
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(AdcCreative target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            adcService.saveOrUpdate(target);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model, HttpSession session) {
    	
    	KnlMedium medium = knlService.getMedium(Util.getSessionMediumId(session));
    	if (medium == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}
    	
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	try {
        	for (Object id : objs) {
        		AdcCreative creative = adcService.getCreative((Integer)id);
        		if (creative != null) {
        			
        			// 물리적인 삭제가 아니기 때문에 파일은 그대로 유지
        			/*
        			// 소재 파일과 관련된 물리적 파일을 먼저 삭제
        			List<AdcCreatFile> creatFiles = adcService.getCreatFileListByCreativeId(creative.getId());
        			for(AdcCreatFile creatFile : creatFiles) {
            			
            			String mediaFolder = creatFile.getCtntFolder().getLocalPath() + "/" + creatFile.getCtntFolder().getName() + "/" + creatFile.getUuid().toString();
            			File thumbFile = new File(SolUtil.getPhysicalRoot("Thumb") + "/" + creatFile.getCtntFolder().getName() + "/" + 
            						creatFile.getUuid().toString() + ".png");
            			
            			FileUtils.deleteDirectory(new File(mediaFolder));
            			
        	    		if (thumbFile.exists() && thumbFile.isFile()) {
        	    			thumbFile.delete();
        	    		}
        			}
        			
        			creatives.add(creative);
        			*/

        			// 소프트 삭제 진행
        			adcService.deleteSoftCreative(creative, session);
        			
        			// 광고 소재 파일도 함께 소프트 삭제 진행
        			List<AdcCreatFile> creatFiles = adcService.getCreatFileListByCreativeId(creative.getId());
        			for(AdcCreatFile creatFile : creatFiles) {
        				adcService.deleteSoftCreatFile(creatFile, session);
        			}
        		}
        	}
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 유형 정보
	 */
    @RequestMapping(value = "/readTypes", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readTypes(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-audio-description fa-fw", "일반 광고", "C"));
		list.add(new DropDownListItem("fa-regular fa-house fa-fw text-orange", "대체 광고", "F"));
		
		return list;
    }

    
	/**
	 * 읽기 액션 - 상태 정보
	 */
    @RequestMapping(value = "/readStatuses", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readStatuses(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-asterisk fa-fw", "준비", "D"));
		list.add(new DropDownListItem("fa-regular fa-square-question fa-fw", "승인대기", "P"));
		list.add(new DropDownListItem("fa-regular fa-square-check text-blue fa-fw", "승인", "A"));
		list.add(new DropDownListItem("fa-regular fa-do-not-enter fa-fw", "거절", "J"));
		list.add(new DropDownListItem("fa-regular fa-box-archive fa-fw", "보관", "V"));
		
		return list;
    }
    
    
	/**
	 * 읽기 액션 - 범주
	 */
    @RequestMapping(value = "/readCategories", method = RequestMethod.POST)
    public @ResponseBody List<DropDownListItem> readCategories(HttpSession session) {
    	
		ArrayList<DropDownListItem> list = new ArrayList<DropDownListItem>();
		
		list.add(new DropDownListItem("fa-regular fa-tv-retro fa-fw", "가전", "A"));
		list.add(new DropDownListItem("fa-regular fa-gamepad fa-fw", "게임", "B"));
		list.add(new DropDownListItem("fa-regular fa-building-flag fa-fw", "관공서/단체", "C"));
		list.add(new DropDownListItem("fa-regular fa-golf-club fa-fw", "관광/레저", "D"));
		list.add(new DropDownListItem("fa-regular fa-books fa-fw", "교육/출판", "E"));
		list.add(new DropDownListItem("fa-regular fa-coins fa-fw", "금융", "F"));
		list.add(new DropDownListItem("fa-regular fa-music fa-fw", "문화/엔터테인먼트", "G"));
		list.add(new DropDownListItem("fa-regular fa-hand-holding-seedling fa-fw", "미디어/서비스", "H"));
		list.add(new DropDownListItem("fa-regular fa-toothbrush fa-fw", "생활용품", "I"));
		list.add(new DropDownListItem("fa-regular fa-truck-fast fa-fw", "유통", "J"));
		list.add(new DropDownListItem("fa-regular fa-syringe fa-fw", "제약/의료", "K"));
		list.add(new DropDownListItem("fa-regular fa-martini-glass fa-fw", "주류", "L"));
		list.add(new DropDownListItem("fa-regular fa-house fa-fw", "주택/가구", "M"));
		list.add(new DropDownListItem("fa-regular fa-shirt fa-fw", "패션", "N"));
		list.add(new DropDownListItem("fa-regular fa-spray-can fa-fw", "화장품", "O"));
		
		return list;
    }

    
    /**
	 * 승인요청 액션
	 */
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public @ResponseBody String submit(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative != null) {
    			// 승인요청 처리는 준비(D)만 가능
    			if (creative.getStatus().equals("D")) {
    				
    				String oStatus = creative.getStatus();
    				
    				creative.setStatus("P");
    				creative.setSubmitDate(new Date());
    				
    				creative.touchWho(session);
    				
                	adcService.saveOrUpdate(creative);

                	
            		//
            		// 감사 추적: Case EC6
            		//
                	if (!oStatus.equals("P")) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(creative, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 소재 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "P");
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("C", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("C", "Status", "P"));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 승인요청 회수 액션
	 */
    @RequestMapping(value = "/recall", method = RequestMethod.POST)
    public @ResponseBody String recall(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative != null) {
    			// 승인요청 회수 처리는 승인요청(P)만 가능
    			if (creative.getStatus().equals("P")) {
    				
    				String oStatus = creative.getStatus();
    				
    				creative.setStatus("D");
    				creative.setSubmitDate(null);
    				
    				creative.touchWho(session);
    				
                	adcService.saveOrUpdate(creative);

                	
            		//
            		// 감사 추적: Case EC6
            		//
                	if (!oStatus.equals("D")) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(creative, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 소재 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "D");
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("C", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("C", "Status", "D"));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 승인 액션 - 광고 소재 페이지에서의 이 기능은 더 이상 사용하지 않음. 대신 "승인 요청" 기능을 이용.
	 */
    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    public @ResponseBody String approve(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative != null) {
    			// 승인 처리는 준비(D), 승인대기(P), 거절(J), 보관(V)만 가능
    			// 승인 처리는 승인대기(P), 거절(J)에서만 가능한 것으로 변경(23/2/25)
    			if (creative.getStatus().equals("P") || creative.getStatus().equals("J")) {
    				
    				creative.setStatus("A");
    				
    				creative.touchWho(session);
    				
                	adcService.saveOrUpdate(creative);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 거절 액션
	 */
    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    public @ResponseBody String reject(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	String reason = (String)model.get("reason");
    	
    	// 파라미터 검증
    	if (objs.size() == 0 || Util.isNotValid(reason)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    	}

    	// 광고 소재 페이지에서의 거절이기 때문에 현재 상태는 승인(A)만 가능
    	ArrayList<AdcCreative> creativeList = new ArrayList<AdcCreative>();
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative == null) {
    			throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
    		} else if (!creative.getStatus().equals("A")) {
				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    		}
    		
    		creativeList.add(creative);
    	}
    	
    	
    	for(AdcCreative creative : creativeList) {
			
			String oStatus = creative.getStatus();
    		
    		// 광고 소재 승인/거절 판단 추가
    		RevCreatDecn creatDecn = new RevCreatDecn(creative, session);
    		creatDecn.setStatus("J");
    		creatDecn.setActDate(new Date());
    		creatDecn.setActedBy(Util.loginUserId(session));
    		creatDecn.setReason(reason);
    		
    		revService.saveOrUpdate(creatDecn);
    		
    		// 광고 소재 상태 변경
			creative.setStatus("J");
			
			creative.touchWho(session);
			
        	adcService.saveOrUpdate(creative);

        	
    		//
    		// 감사 추적: Case EC6
    		//
        	if (!oStatus.equals("J")) {
        		
            	SysAuditTrail auditTrail = new SysAuditTrail(creative, "E", "Sts", "F", session);
            	auditTrail.setTgtName("광고 소재 상태");
            	auditTrail.setTgtValue("");
                sysService.saveOrUpdate(auditTrail);

                
        		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "J");
        		item.setItemName("Status");
        		item.setOldText(SolUtil.getAuditTrailValueCodeText("C", "Status", oStatus));
        		item.setNewText(SolUtil.getAuditTrailValueCodeText("C", "Status", "J"));
        		
        		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
        	}
        	
    	}

        return "Ok";
    }

    
    /**
	 * 보관 액션
	 */
    @RequestMapping(value = "/archive", method = RequestMethod.POST)
    public @ResponseBody String archive(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative != null) {
    			// 보관 처리는 준비(D), 승인대기(P), 승인(A), 거절(J)만 가능
    			if (creative.getStatus().equals("D") || creative.getStatus().equals("P") ||
    					creative.getStatus().equals("A") || creative.getStatus().equals("J")) {
    				
    				String oStatus = creative.getStatus();
    				
    				creative.setStatus("V");
    				
    				creative.touchWho(session);
    				
                	adcService.saveOrUpdate(creative);

                	
            		//
            		// 감사 추적: Case EC6
            		//
                	if (!oStatus.equals("V")) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(creative, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 소재 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "V");
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("C", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("C", "Status", "V"));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 보관 해제 액션
	 */
    @RequestMapping(value = "/unarchive", method = RequestMethod.POST)
    public @ResponseBody String unarchive(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative != null) {
    			// 보관 해제 처리는 보관(V)만 가능
    			if (creative.getStatus().equals("V")) {
    				
    				String oStatus = creative.getStatus();
    				
    				
    				creative.setStatus("D");
    				
    				creative.touchWho(session);
    				
                	adcService.saveOrUpdate(creative);

                	
            		//
            		// 감사 추적: Case EC6
            		//
                	if (!oStatus.equals("D")) {
                		
                    	SysAuditTrail auditTrail = new SysAuditTrail(creative, "E", "Sts", "F", session);
                    	auditTrail.setTgtName("광고 소재 상태");
                    	auditTrail.setTgtValue("");
                        sysService.saveOrUpdate(auditTrail);

                        
                		SysAuditTrailValueItem item = new SysAuditTrailValueItem("현재 상태", oStatus, "D");
                		item.setItemName("Status");
                		item.setOldText(SolUtil.getAuditTrailValueCodeText("C", "Status", oStatus));
                		item.setNewText(SolUtil.getAuditTrailValueCodeText("C", "Status", "D"));
                		
                		sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
                	}
                	
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 잠시 멈춤 액션
	 */
    @RequestMapping(value = "/pause", method = RequestMethod.POST)
    public @ResponseBody String pause(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative != null) {
    			// 잠시 멈춤 처리는 잠시 멈춤이 아닌 항목만 가능
    			if (!creative.isPaused()) {
    				
    				creative.setPaused(true);
    				
    				creative.touchWho(session);
    				
                	adcService.saveOrUpdate(creative);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 재개 액션
	 */
    @RequestMapping(value = "/resume", method = RequestMethod.POST)
    public @ResponseBody String resume(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((int)id);
    		if (creative != null) {
    			// 재개 처리는 잠시 멈춤인 항목만 가능
    			if (creative.isPaused()) {
    				
    				creative.setPaused(false);
    				
    				creative.touchWho(session);
    				
                	adcService.saveOrUpdate(creative);
    			} else {
    				throw new ServerOperationForbiddenException(StringInfo.UPD_ERROR_NOT_PROPER_STATUS);
    			}
    		}
    	}

        return "Ok";
    }

    
    /**
	 * 복사 액션
	 */
    @RequestMapping(value = "/copy", method = RequestMethod.POST)
    public @ResponseBody String copy(@RequestBody Map<String, Object> model, HttpSession session) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	KnlMedium medium = knlService.getMedium((String)model.get("mediumID"));
    	
    	// 파라미터 검증
    	if (medium == null || objs.size() == 0) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	//
    	// 광고 소재 및 소재 파일을 다른 매체로 복사
    	//
    	// 광고 소재:
    	//   - name, type, durPolicyOverriden, fbWeight 4개 필드외 값은 디폴트값
    	//   - name: 동일한 이름의 소재 존재 시 오류(E1): 동일한 이름({광고 소재명})의 광고 소재가 발견되어 복사를 진행할 수 없습니다.
    	//
    	// 광고 소재의 광고주:
    	//   - name, domainName 모두 존재하지 않을 경우: 신규 생성
    	//   - name 일치하고, domainName 일치 않는 자료의 경우: name 따로 이 광고주 이용
    	//   - name 일치 않고, domainName 일치하는 자료의 경우: 오류(E2): 복사 대상 소재의 광고주 정보(도메인명: {광고주 도메인명})가 일치하지 않습니다.
    	//   - 동일 자료로 name, domainName 일치하는 자료의 경우: 이 광고주 이용
    	//
    	
    	// 위의 두 오류(E1, E2)에 대한 검증 진행
    	List<OrgAdvertiser> advertiserList = orgService.getAdvertiserListByMediumId(medium.getId());
    	for (Object id : objs) {
    		AdcCreative creative = adcService.getCreative((Integer)id);
    		if (creative != null) {
    			List<AdcCreative> sameCreativeList = adcService.getCreativeListByMediumIdName(
    					medium.getId(), creative.getName());
    			if (sameCreativeList.size() > 0) {
    				throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_CREATIVE_NAME.replace("{0}", creative.getName()));
    			}
    			
    			for(OrgAdvertiser advertiser : advertiserList) {
    				if (!advertiser.getName().equals(creative.getAdvertiser().getName()) && 
    						advertiser.getDomainName().equals(creative.getAdvertiser().getDomainName())) {
        				throw new ServerOperationForbiddenException(StringInfo.VAL_DIFF_ADVERTISER.replace("{0}", creative.getAdvertiser().getDomainName()));
    				}
    			}
    		}
    	}
    	
    	try {
        	for (Object id : objs) {
        		AdcCreative creative = adcService.getCreative((Integer)id);
        		if (creative != null) {
        			
        			// 광고 소재
        			OrgAdvertiser newAdvertiser = null;
        			for(OrgAdvertiser advertiser : advertiserList) {
        				if (advertiser.getName().equals(creative.getAdvertiser().getName())) {
        					newAdvertiser = advertiser;
        					break;
        				}
        			}
        			if (newAdvertiser == null) {
        				newAdvertiser = new OrgAdvertiser(medium, creative.getAdvertiser().getName(),
        						creative.getAdvertiser().getDomainName(), session);
        				orgService.saveOrUpdate(newAdvertiser);
        			}
        			
        			AdcCreative target = new AdcCreative(newAdvertiser, creative.getName(), creative.getType(),
        					creative.getCategory(), creative.getViewTypeCode(),
        					creative.isDurPolicyOverriden(), creative.getFbWeight(), session);
        			adcService.saveOrUpdate(target);
        			
        			
        			// 감사 추적: Case NC2
        			sysService.saveOrUpdate(new SysAuditTrail(target, "N", "F", session));
        			
        			
        			
        			// 광고 소재 파일
        			FndCtntFolder ctntFolder = fndService.getDefCtntFolder();
        			List<AdcCreatFile> creatFiles = adcService.getCreatFileListByCreativeId(creative.getId());
        			for(AdcCreatFile creatFile : creatFiles) {
			        	UUID uuid = UUID.randomUUID();
			        	
			        	AdcCreatFile cFile = new AdcCreatFile(target, ctntFolder, creatFile.getSrcFilename(),
			        			creatFile.getFileLength(), creatFile.getMediaType(), creatFile.getResolution(), 
			        			creatFile.getMimeType(), uuid, UUID.randomUUID(),
			        			creatFile.getSrcDurSecs(), session);
			        	adcService.saveOrUpdate(cFile);
			        	
			        	
			        	String thumbF = uuid.toString() + ".png";
			        	String mediaF = cFile.getMediaType() + String.format("%05d", cFile.getId()) + "." + 
			        			Util.getFileExt(cFile.getSrcFilename()).toLowerCase();
			        	
			        	cFile.setThumbFilename(thumbF);
			        	cFile.setFilename(mediaF);
			        	
						String finalMediaFilename = ctntFolder.getLocalPath() + "/" + ctntFolder.getName() + "/" + uuid.toString() + "/" + mediaF;
						String finalThumbFilename = SolUtil.getPhysicalRoot("Thumb") + "/" + ctntFolder.getName() + "/" + thumbF;
						
						Util.checkParentDirectory(finalMediaFilename);
						Util.checkParentDirectory(finalThumbFilename);
						
						
						String finalSrcMediaFilename = creatFile.getCtntFolder().getLocalPath() + "/" + creatFile.getCtntFolder().getName() + "/" +
								creatFile.getUuid().toString() + "/" + creatFile.getFilename();
						String finalSrcThumbFilename = SolUtil.getPhysicalRoot("Thumb") + "/" + creatFile.getCtntFolder().getName() + "/" + 
								creatFile.getThumbFilename();
						
						// 컨텐츠 파일의 복사
						Files.copy(new File(finalSrcThumbFilename), new File(finalThumbFilename));
						Files.copy(new File(finalSrcMediaFilename), new File(finalMediaFilename));
						
						// 해쉬값 설정
						cFile.setHash(Util.getFileHashSha256(finalMediaFilename));
						
						adcService.saveOrUpdate(cFile);
						
						
						//
						// 감사 추적: Case SC1
						//
						// - 01: 해상도
						// - 02: 파일형식
						// - 03: 파일크기
						// - 04: 재생시간
				        
						ArrayList<SysAuditTrailValueItem> editItems = new ArrayList<SysAuditTrailValueItem>();
						
						editItems.add(new SysAuditTrailValueItem("해상도", "[-]", creatFile.getResolution().replace("x", " x ")));
						editItems.add(new SysAuditTrailValueItem("파일형식", "[-]", creatFile.getMimeType()));
						editItems.add(new SysAuditTrailValueItem("파일크기", "[-]", Util.getSmartFileLength(creatFile.getFileLength())));
						
						if (creatFile.getMediaType().equals("V")) {
    						editItems.add(new SysAuditTrailValueItem("재생시간", "[-]", creatFile.getDispSrcDurSecs()));
						}
				    	

				    	SysAuditTrail auditTrail = new SysAuditTrail(target, "S", "File", "F", session);
				    	auditTrail.setTgtName(creatFile.getSrcFilename());
				    	auditTrail.setTgtValue(String.valueOf(target.getId()));
				    	
				        sysService.saveOrUpdate(auditTrail);
				    	
				        for(SysAuditTrailValueItem item : editItems) {
				        	sysService.saveOrUpdate(new SysAuditTrailValue(auditTrail, item));
				        }
        			}
        		}
        	}
    	} catch (Exception e) {
    		logger.error("copy", e);
    		throw new ServerOperationForbiddenException("OperationError");
    	}

        return "Ok";
    }
    
    
	/**
	 * 읽기 액션 - 광고주 정보
	 */
    @RequestMapping(value = "/readAdvertisers", method = RequestMethod.POST)
    public @ResponseBody String readAdvertisers(HttpSession session) {
    	
    	
		ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();

		List<OrgAdvertiser> list = orgService.getAdvertiserListByMediumId(Util.getSessionMediumId(session));
		
		for(OrgAdvertiser advertiser : list) {
			retList.add(new DropDownListItem(advertiser.getName(), 
					String.valueOf(advertiser.getId())));
		}
		
		Collections.sort(retList, CustomComparator.DropDownListItemTextComparator);
    	
    	return Util.getObjectToJson(retList, false);
    }
    
    
	/**
	 * 현재 매체에서 이용가능한 게시 유형 획득
	 */
    private List<DropDownListItem> getViewTypeDropDownList(int mediumId) {
    	
    	ArrayList<DropDownListItem> retList = new ArrayList<DropDownListItem>();
    	
    	// 빈 행 추가
    	retList.add(new DropDownListItem("", ""));
    	
    	
    	List<FndViewType> viewTypeList = fndService.getViewTypeList();
    	
    	List<String> viewTypes = SolUtil.getViewTypeListByMediumId(mediumId);
    	for(String s : viewTypes) {
    		String text = s;
    		for(FndViewType viewType : viewTypeList) {
    			if (viewType.getCode().equals(s)) {
    				text = String.format("%s - %s", s, viewType.getName());
    			}
    		}
    		retList.add(new DropDownListItem(text, s));
    	}
    	
    	return retList;
    }
    
    
	/**
	 * 읽기 액션 - 소재 파일
	 */
    @RequestMapping(value = "/readCreatFiles", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult readCreatFiles(@RequestBody DataSourceRequest request, HttpSession session) {
    	try {
    		DataSourceResult result = adcService.getCreatFileList(request);
    		
    		for(Object obj : result.getData()) {
    			AdcCreatFile creatFile = (AdcCreatFile)obj;
    			
    			// 이 값이 유효하다는 것: 게시 유형이 지정되어 있고, 유효한 게시 크기(해상도) 존재
    			String fixedReso = "";
    			if (Util.isValid(creatFile.getCreative().getViewTypeCode())) {
    				fixedReso = fndService.getViewTypeResoByCode(creatFile.getCreative().getViewTypeCode());
    			}
    			
    			// 20% 범위로 적합도 판정
				int fitness = Util.isValid(fixedReso) ?
						SolUtil.measureResolutionWith(creatFile.getResolution(), fixedReso, 20) :
						adcService.measureResolutionWithMedium(
	            				creatFile.getResolution(), creatFile.getMedium().getId(), 20);
    			
    			creatFile.setFitnessOfResRatio(fitness);
    		}
    		
    		return result;
    	} catch (Exception e) {
    		logger.error("readCreatFiles", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
}
