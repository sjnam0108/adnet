package kr.adnetwork.controllers.fnd;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import kr.adnetwork.exceptions.ServerOperationForbiddenException;
import kr.adnetwork.info.StringInfo;
import kr.adnetwork.models.AdnMessageManager;
import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.Message;
import kr.adnetwork.models.MessageManager;
import kr.adnetwork.models.ModelManager;
import kr.adnetwork.models.UploadTransitionModel;
import kr.adnetwork.models.fnd.FndCtntFolder;
import kr.adnetwork.models.fnd.FndSetupFile;
import kr.adnetwork.models.service.FndService;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.fnd.SetupFileItem;

/**
 * 앱 설치 파일 컨트롤러
 */
@Controller("fnd-setup-file-controller")
@RequestMapping(value="/fnd/setupfile")
public class FndSetupFileController {

	private static final Logger logger = LoggerFactory.getLogger(FndSetupFileController.class);


    @Autowired 
    private FndService fndService;

    
	@Autowired
	private MessageManager msgMgr;

	@Autowired
	private AdnMessageManager solMsgMgr;
    
	@Autowired
	private ModelManager modelMgr;
	
	
	/**
	 * 앱 설치 파일 페이지
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
    	model.addAttribute("pageTitle", "앱 설치 파일");
    	
    	
    	UploadTransitionModel uploadModel = new UploadTransitionModel();

		//uploadModel.setType("SETUP");
		uploadModel.setSaveUrl("/fnd/setupfile/uploadsave");
		uploadModel.setAllowedExtensions("[\".exe\", \".apk\"]");

    	model.addAttribute("uploadModel", uploadModel);

    	
        return "fnd/setupfile";
    }
    
    
	/**
	 * 읽기 액션
	 */
    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public @ResponseBody DataSourceResult read(@RequestBody DataSourceRequest request) {
    	try {
    		DataSourceResult result = fndService.getSetupFileList(request);
    		
    		for(Object obj : result.getData()) {
    			FndSetupFile setupFile = (FndSetupFile)obj;
    			
    			SetupFileItem nameObj = new SetupFileItem(setupFile.getFilename());
    			
    			setupFile.setMajorCat(nameObj.getMajorCat());
    			setupFile.setProdKeyword(nameObj.getProdKeyword());
    			setupFile.setVersion(nameObj.getVersion());
    			setupFile.setPlatKeyword(nameObj.getPlatKeyword());
    		}

    		
    		return result;
    	} catch (Exception e) {
    		logger.error("read", e);
    		throw new ServerOperationForbiddenException("ReadError");
    	}
    }
    
    
	/**
	 * 추가 액션
	 */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public @ResponseBody String create(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String file = (String)model.get("file");
    	String updateList = Util.parseString((String)model.get("updateList"));
    	
    	// 파라미터 검증
    	if (Util.isNotValid(file)) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	
    	long length = 0;
		String typeRootDir = SolUtil.getPhysicalRoot("UpTemp");
		File upFile = new File(typeRootDir + "/" + file);
		if (upFile.exists() && upFile.isFile()) {
			length = upFile.length();
		}
    	
    	FndCtntFolder ctntFolder = fndService.getDefCtntFolder();
    	
    	SetupFileItem nameObj = new SetupFileItem(file);
    	
    	// 파라미터 검증
    	if (upFile == null || length == 0 || ctntFolder == null || nameObj.getVerNumber() == 0 || 
    			Util.isNotValid(nameObj.getProdKeyword())) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
		
    	
    	UUID uuid = UUID.randomUUID();
    	
    	FndSetupFile target = new FndSetupFile(ctntFolder, file, length, updateList, uuid, 
    			nameObj.getProdKeyword(), nameObj.getVerNumber(), nameObj.getPlatKeyword(), session);
    	
        // DB 작업 수행 결과 검증
        try {
        	String finalPathFile = ctntFolder.getLocalPath() + "/" + ctntFolder.getName() + "/" + uuid.toString() + "/" + file;
        	Util.checkParentDirectory(finalPathFile);
        	
        	// 파일 이동
        	upFile.renameTo(new File(finalPathFile));
        	
        	// 해쉬값 설정
        	target.setHash(Util.getFileHashSha256(finalPathFile));
        	
        	fndService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_FILENAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_FILENAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }


        return "Ok";
    }
    
    
	/**
	 * 변경 액션
	 */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public @ResponseBody String update(@RequestBody Map<String, Object> model, Locale locale, HttpSession session) {
    	
    	String updateList = Util.parseString((String)model.get("updateList"));
    	
    	boolean activeStatus = (Boolean)model.get("activeStatus");
    	
    	FndSetupFile target = fndService.getSetupFile((int)model.get("id"));
    	
    	// 파라미터 검증
    	if (target == null) {
    		throw new ServerOperationForbiddenException(StringInfo.CMN_WRONG_PARAM_ERROR);
        }
    	
    	if (target != null) {

    		target.setUpdateList(updateList);
    		target.setActiveStatus(activeStatus);
            
            target.touchWho(session);
            
            saveOrUpdate(target, locale, session);
    	}
    	
        return "Ok";
    }

    
	/**
	 * 추가 / 변경 시의 자료 저장
	 */
    private void saveOrUpdate(FndSetupFile target, Locale locale, HttpSession session) throws ServerOperationForbiddenException {
    	
    	// 비즈니스 로직 검증
        
        // DB 작업 수행 결과 검증
        try {
            fndService.saveOrUpdate(target);
        } catch (DataIntegrityViolationException dive) {
    		logger.error("saveOrUpdate", dive);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_FILENAME);
        } catch (ConstraintViolationException cve) {
    		logger.error("saveOrUpdate", cve);
        	throw new ServerOperationForbiddenException(StringInfo.UK_ERROR_FILENAME);
        } catch (Exception e) {
    		logger.error("saveOrUpdate", e);
        	throw new ServerOperationForbiddenException("SaveError");
        }
    }

    
    /**
	 * 삭제 액션
	 */
    @RequestMapping(value = "/destroy", method = RequestMethod.POST)
    public @ResponseBody String destroy(@RequestBody Map<String, Object> model) {
    	@SuppressWarnings("unchecked")
		ArrayList<Object> objs = (ArrayList<Object>) model.get("items");
    	
    	List<FndSetupFile> setupFiles = new ArrayList<FndSetupFile>();

    	try {
        	for (Object id : objs) {
        		FndSetupFile setupFile = fndService.getSetupFile((Integer)id);
        		if (setupFile != null) {
        			
        			String folder = setupFile.getCtntFolder().getLocalPath() + "/" + 
        					setupFile.getCtntFolder().getName() + "/" + setupFile.getUuid().toString();
        			FileUtils.deleteDirectory(new File(folder));
        			
            		setupFiles.add(setupFile);
        		}
        	}
        	
        	fndService.deleteSetupFiles(setupFiles);
    	} catch (Exception e) {
    		logger.error("destroy", e);
    		throw new ServerOperationForbiddenException("DeleteError");
    	}

        return "Ok";
    }
    
    
    /**
     * 업로드 저장 액션
     */
    @RequestMapping(value = "/uploadsave", method = RequestMethod.POST)
    public @ResponseBody String save(@RequestParam List<MultipartFile> files, HttpSession session) {
    	
    	logger.info("/uploadsave new file entered. size = " + files.size());
    	
    	try {
			String typeRootDir = SolUtil.getPhysicalRoot("UpTemp");
			
	        for (MultipartFile file : files) {
	        	if (!file.isEmpty()) {
	        		File uploadedFile = new File(typeRootDir + "/" + file.getOriginalFilename());
	        		Util.checkParentDirectory(uploadedFile.getAbsolutePath());
	        		
	        		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(uploadedFile));
	            	logger.info("/uploadsave file copy to " + uploadedFile.getAbsolutePath());
	        	}
	        }
    	} catch (Exception e) {
    		logger.error("uploadSave", e);
    	}
        
        // Return an empty string to signify success
        return "";
    }
    
    
    /**
     * 업로드 파일 규칙 준수 확인 읽기 액션
     */
    @RequestMapping(value = "/readfileoverview", method = RequestMethod.POST)
    public @ResponseBody SetupFileItem readFileOverview(@RequestBody Map<String, Object> model, 
    		Locale locale, HttpSession session) {

    	FndSetupFile setupFile = fndService.getSetupFile((String)model.get("file"));
    	SetupFileItem retObj = new SetupFileItem((String)model.get("file"));
    	
    	if (setupFile != null) {
    		retObj.setErrorMsg("이미 등록된 파일입니다.");
    	} else if (Util.isValid(retObj.getFilename())) {
    		
    		String typeRootDir = SolUtil.getPhysicalRoot("UpTemp");
    		File file = new File(typeRootDir + "/" + retObj.getFilename());
    		if (file.exists() && file.isFile()) {
    			retObj.setLengthStr(Util.getSmartFileLength(file.length()) + " (" + 
    					String.format("%,d", file.length()) + " bytes)");
    			
    			if (!retObj.isRightFormatted()) {
    				retObj.setErrorMsg("규칙에 맞지 않는 파일입니다.");
    			}
    		} else {
        		retObj.setErrorMsg("잘못된 경로 / 파일입니다.");
    		}
    		
    	} else {
    		retObj.setErrorMsg("파일명이 없거나, 유효하지 않습니다.");
    	}
    	
    	return retObj;
    	
    }

}
