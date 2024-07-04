package net.doohad.controllers.api;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.doohad.exceptions.ServerOperationForbiddenException;
import net.doohad.info.GlobalInfo;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.service.InvService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.knl.KnlMediumCompactItem;
import net.sf.json.JSONObject;

/**
 * 업로드 API 컨트롤러
 */
@Controller("api-upload-controller")
@RequestMapping(value="")
public class UploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
    
	
    @Autowired 
    private InvService invService;
    
    
    /**
     * 업로드된 파일 검증 API
     */
    @RequestMapping(value = "/v1/upcheck/{displayID}", method = {RequestMethod.GET, RequestMethod.POST })
    public void check(@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap, HttpServletResponse response) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    	
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String type = Util.parseString(paramMap.get("type"));
    	String fileArr = Util.parseString(paramMap.get("files"));
    	
    	
    	//
    	// type:
    	//        log - 플레이어의 로그 파일
    	//
    	
    	InvScreen screen = null;
    	KnlMediumCompactItem mediumItem = null;

    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID) || Util.isNotValid(type) || Util.isNotValid(fileArr)) {
    		throw new ServerOperationForbiddenException("WrongParams");
    	} else {
    		
        	mediumItem = GlobalInfo.ApiKeyMediaMap.get(apiKey);
        	if (mediumItem == null) {
        		throw new ServerOperationForbiddenException("WrongApiKey");
        	} else {
        		screen = invService.getScreenByMediumIdShortName(mediumItem.getId(), displayID);
        		if (screen == null) {
        			throw new ServerOperationForbiddenException("WrongDisplayID");
        		}
        	}
    	}
    	
    	
    	List<String> files = Util.tokenizeValidStr(fileArr);
    	ArrayList<String> dates = new ArrayList<String>();
    	for (String file : files) {
    		if (Util.isValid(file) && file.length() == 8) {
    			dates.add(Util.delimitDateStr(file).replaceAll("-", "."));
    		}
    	}


    	// 여기 이후는 모두 screen != null
		logger.info("[API] upcheck: " + screen.getName() + " - " + type + " - files=" + fileArr);
		
		JSONObject obj = new JSONObject();
		
    	try {
    		
    		if (type.equals("log")) {
    			
    			//
    			// 로그 파일의 예: 7_17879_log_2024.06.30.zip
    			//
        		String typeRootDir = SolUtil.getPhysicalRoot("Log");
        		
    	        for (String date : dates) {
    	        	String filename = "log_" + date + ".zip";
    	        	String pathFilename = typeRootDir + "/" + screen.getMedium().getId() + "_" + screen.getId() + "_" + filename;
    	        	
    	        	String hash = "";
    	        	File logFile = new File(pathFilename);
    	        	if (logFile.exists()) {
    	        		hash = Util.getFileHashSha256(pathFilename);
    	        	}
    	        	
    	        	obj.put(filename, hash);
    	        }
    	        
    		}
    		
    	} catch (Exception e) {
    		logger.error("upload", e);
    	}
    	
    	
    	if (!type.equals("log")) {
    		throw new ServerOperationForbiddenException("WrongParams");
    	}
        
    	Util.toJson(response, obj);
    }

    /**
     * 파일 업로드 API
     */
    @RequestMapping(value = "/v1/upload/{displayID}", method = RequestMethod.POST)
    public @ResponseBody String upload(@RequestParam List<MultipartFile> files,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    	
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String type = Util.parseString(paramMap.get("type"));
    	
    	
    	//
    	// type:
    	//        log - 플레이어의 로그 파일
    	//
    	
    	InvScreen screen = null;
    	KnlMediumCompactItem mediumItem = null;

    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID) || Util.isNotValid(type)) {
    		throw new ServerOperationForbiddenException("WrongParams");
    	} else {
    		
        	mediumItem = GlobalInfo.ApiKeyMediaMap.get(apiKey);
        	if (mediumItem == null) {
        		throw new ServerOperationForbiddenException("WrongApiKey");
        	} else {
        		screen = invService.getScreenByMediumIdShortName(mediumItem.getId(), displayID);
        		if (screen == null) {
        			throw new ServerOperationForbiddenException("WrongDisplayID");
        		}
        	}
    	}


    	// 여기 이후는 모두 screen != null
		logger.info("[API] upload: " + screen.getName() + " - " + type + " - fileSize=" + files.size());
		
		
    	try {
    		
    		if (type.equals("log")) {
    			
        		String typeRootDir = SolUtil.getPhysicalRoot("Log");
        		
    	        for (MultipartFile file : files) {
    	        	if (!file.isEmpty()) {
    	        		String fullName = screen.getMedium().getId() + "_" + screen.getId() + "_" + file.getOriginalFilename();
    	        		File uploadedFile = new File(typeRootDir + "/" + fullName);
    	        		
    	        		FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(uploadedFile));
    	        	}
    	        }
    	        
    		}
    		
    	} catch (Exception e) {
    		logger.error("upload", e);
    	}
    	
    	
    	if (!type.equals("log")) {
    		throw new ServerOperationForbiddenException("WrongParams");
    	}
        
        return "Ok";
    }

}
