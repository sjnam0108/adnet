package net.doohad.controllers.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Tuple;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import net.doohad.info.GlobalInfo;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.inv.InvRTSyncPack;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSyncPack;
import net.doohad.models.rev.RevAdSelCache;
import net.doohad.models.rev.RevAdSelect;
import net.doohad.models.rev.RevFbSelCache;
import net.doohad.models.rev.RevPlayHist;
import net.doohad.models.service.InvService;
import net.doohad.models.service.RevService;
import net.doohad.models.service.SysService;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;
import net.doohad.viewmodels.adc.AdcAdCreatFileObject;
import net.doohad.viewmodels.adc.AdcJsonFileObject;
import net.doohad.viewmodels.knl.KnlMediumCompactItem;
import net.doohad.viewmodels.rev.RevObjEventTimeItem;
import net.doohad.viewmodels.rev.RevScrWorkTimeItem;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 현재 광고 API 컨트롤러
 */
@Controller("api-ad-controller")
@RequestMapping(value="")
public class AdController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdController.class);


	//
	// 현재 광고 API: 읽기 / 쓰기 가능(test 파라미터 처리 완료)
	//
	
    @Autowired 
    private InvService invService;

    @Autowired 
    private RevService revService;

    @Autowired 
    private SysService sysService;
	
    
    /**
	 * 현재 광고 API
	 */
    @RequestMapping(value = {"/v1/ad/{displayID}", "/v1/ad/{displayID}/{viewType}"}, method = RequestMethod.GET)
    public void processApiAd(HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Map<String, String> pathMap, @RequestParam Map<String,String> paramMap) {
    	
    	String displayID = Util.parseString(pathMap.get("displayID"));
    	String viewType = Util.parseString(pathMap.get("viewType"));
    
    	String apiKey = Util.parseString(paramMap.get("apikey"));
    	String test = Util.parseString(paramMap.get("test"));
    	boolean testMode = Util.isValid(test) && test.toLowerCase().equals("y");
    	
    	int cnt = Util.parseInt(paramMap.get("cnt"), 1);
    	if (cnt < 1 || cnt > 15) {
    		cnt = 1;
    	}
    	
    	// 위치 정도 전달 여부 확인
    	double lat = Util.parseDouble(Util.parseString(paramMap.get("lat")));
    	double lng = Util.parseDouble(Util.parseString(paramMap.get("lng")));
    	
    	if (!SolUtil.isAcceptableLocation(lat, lng)) {
    		lat = 0d;
    		lng = 0d;
    	}
    	
    	
    	int statusCode = 0;
    	String message = "Ok";
    	String localMessage = "Ok";
    	
    	JSONObject obj = new JSONObject();
    	InvScreen screen = null;
    	KnlMediumCompactItem mediumItem = null;
    	
    	if (Util.isNotValid(apiKey) || Util.isNotValid(displayID)) {
    		
    		statusCode = -3;
    		message = "WrongParams";
    		localMessage = "필수 인자의 값이 전달되지 않았습니다.";
    	} else {
    		
        	mediumItem = GlobalInfo.ApiKeyMediaMap.get(apiKey);
        	if (mediumItem == null) {
        		statusCode = -1;
        		message = "WrongApiKey";
        		localMessage = "등록되지 않은 API key가 전달되었습니다.";
        	} else {
        		screen = invService.getScreenByMediumIdShortName(mediumItem.getId(), displayID);
        		if (screen == null) {
            		statusCode = -2;
            		message = "WrongDisplayID";
            		localMessage = "등록되지 않은 디스플레이 ID가 전달되었습니다.";
        		}
        	}
    	}

    	
    	Date now = new Date();
    	
    	//
    	// 대상의 매체 화면이 정상적으로 광고 방송을 하려면,
    	//
    	//
    	//   매체 화면 기본 검증 사항:
    	//
    	//   1. 유효기간내
    	//   2. activeStatus == true
    	//   3. adServerAvailable == true
    	//   4. 매체 화면의 운영 시간
    	//
    	//
    	//   쿼리에서의 제한 항목:
    	//
    	//   11. 광고의 상태 코드는 A/R/C 중 하나
    	//       - 예약이나 완료건에 대해서도 통과. 이유는 상태 A/R/C는 현재 날짜에 따른 가변값으로 
    	//         오늘 날짜에 대한 검증이 진행전이라면 선택 오류가 발생할 수도 있기 때문
    	//       - 예약이나 완료건에 대한 필터링은 아래 12에서 별도로 진행됨
    	//   12. 광고의 시작일과 종료일 내
    	//   13. 광고의 pause == false
    	//   14. 광고의 deleted == false
    	//   15. 광고 소재의 상태 코드는 A
    	//   16. 광고 소재의 paused == false
    	//   17. 광고 소재의 deleted == false
    	//
    	//
    	//   광고 소재 파일의 검증 항목:
    	//
    	//   21. 파일 해상도: 화면해상도가 광고 소재 파일의 해상도와 일치하거나 적합
    	//   22. 파일 미디어 유형: 광고 소재 파일의 미디어 유형(동영상, 이미지 등)이 가능하도록 매체 화면 설정 필요
    	//   23. 파일 재생시간: 파일 재생시간이 광고/매체/화면의 설정에 부합
    	//
    	//
    	//   광고 소재의 검증 항목:
    	//
    	//   31. 인벤 타겟팅이 없거나, 있다면 포함되어야 함
    	//   32. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함
    	//
    	//
    	//   광고의 검증 항목:
    	//
    	//   41. 인벤 타겟팅이 없거나, 있다면 포함되어야 함
    	//   42. 시간 타겟팅이 없거나, 있다면 대상 시간이어야 함
    	//
    	//
    	//   광고 송출 이력의 검증 항목:
    	//
    	//   51. 동일 광고 송출 금지 시간이 없거나, 그 시간 초과되어야 함
    	//   52. 동일 광고주 송출 금지 시간이 없거나, 그 시간 초과되어야 함
    	//
    	if (statusCode >= 0 && screen != null) {
    		// 아직까지는 큰 문제 없음
    		
    		if (!SolUtil.isEffectiveDate(screen.getEffectiveStartDate(), screen.getEffectiveEndDate())) {
    			statusCode = -4;
    			message = "EffectiveDateExpired";
    			localMessage = "유효 기간의 범위에 포함되지 않습니다.";
    		} else if (screen.isActiveStatus() != true) {
    			statusCode = -5;
    			message = "NotActive";
    			localMessage = "정상 서비스 중이 아닙니다.";
    		} else if (screen.isAdServerAvailable() != true) {
    			statusCode = -6;
    			message = "NotAdServerAvailable";
    			localMessage = "광고 서비스로 이용할 수 없습니다.";
    		}
    		
    		// 화면만 확정되면 바로 요청일시 저장
    		if (!testMode) {
    			
    			String groupID = Util.parseString(paramMap.get("gid"));
    			if (Util.isValid(groupID)) {
    				InvSyncPack syncPack = invService.getActiveSyncPackByShortName(groupID);
    				if (syncPack != null) {
            			
    					// RTSyncPack으로 대체
            			// 개체 이벤트 처리: 동기화 화면 묶음(P)의 현재 광고 요청(ad)
            			// GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(syncPack.getId(), now, 32));
    		    		InvRTSyncPack rtSyncPack = invService.getRTSyncPackBySyncPackId(syncPack.getId());
    		    		if (rtSyncPack == null) {
    		    			invService.saveOrUpdate(new InvRTSyncPack(syncPack.getId()));
    		    			
    		    			rtSyncPack = invService.getRTSyncPackBySyncPackId(syncPack.getId());
    		    		}
    		    		if (rtSyncPack != null) {
    		    			rtSyncPack.setLastAdReqDate(now);
    		    			rtSyncPack.setWhoLastUpdateDate(now);
    		    			
    		    			invService.saveOrUpdate(rtSyncPack);
    		    		}

    				}
    			} else {
        			
        			// 개체 이벤트 처리: 화면(S)의 현재광고 요청(ad, playlist)
        			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(screen.getId(), now, 12));
            		
            		// 상태 라인 처리 위해 공용리스트에 추가
        			//
        			//   게시 유형이 존재하는 광고 요청 API는 상태 라인에 추가하지 않음
        			//   - 게시 유형이 존재하지 않는 main 요청이 있을 수 있음
        			//   - 묶음 광고 게시 유형의 경우 대표 화면 ID로 요청되는데, 해당 화면은 오프일 수 있음
        			//
        			if (Util.isNotValid(viewType)) {
                		GlobalInfo.ScrWorkTimeItemList.add(new RevScrWorkTimeItem(screen.getId(), now));
        			}
            		
            		if (!screen.getSite().isServed()) {
                		screen.getSite().setServed(true);
                		invService.saveOrUpdate(screen.getSite());
            		}
    			}
    		}
    	}
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);

		
    	if (statusCode == 0 && screen != null) {
			JSONArray ads = new JSONArray();

			AdcAdCreatFileObject adCreatFileObject = null;
			String creatKey = "I" + String.valueOf(screen.getId());

			// 이전 광고 소재와 동일한 값이 선택되지 않게 하기 위해 5회 선택하여, 
			// 그래도 동일한 게 선택되면 어쩔 수 없고
			for (int i = 0; i < 5; i ++) {
				adCreatFileObject = SolUtil.selectAdFromCandiList(screen, viewType, lat, lng, null);
				if (adCreatFileObject != null) {
					
					Integer lastId = GlobalInfo.LastSelCreatMap.get(creatKey);
					if (lastId == null || lastId.intValue() != adCreatFileObject.getAdCreat().getCreative().getId()) {
						GlobalInfo.LastSelCreatMap.put(creatKey, adCreatFileObject.getAdCreat().getCreative().getId());
						break;
					}
				}
			}
			if (adCreatFileObject != null) {
				
				String mapKey = "AdSel_A" + adCreatFileObject.getAdCreat().getAd().getId() + "S" + screen.getId();
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


				JSONObject jObj = getAdObject(adCreatFileObject.getAdCreat(),
						adCreatFileObject.getJsonFileObject(), screen, viewType, testMode);
				if (jObj != null) {
					ads.add(jObj);
				}
			}
			
			
	    	if (cnt > 1) {
    			
    			setCodeAndMessage(obj, 2, "MultiAdDeprecated", "복수 개의 현재 광고 기능은 더 이상 지원되지 않습니다.");
    			
	    	} else if (GlobalInfo.AdCandiAdCreatVerKey.size() == 0) {
    			
    			setCodeAndMessage(obj, 1, "CandiListNotFound", "후보 리스트가 확인되지 않습니다.");
    			
    		}
	    	
	    	
			if (statusCode == 0 && ads.size() == 0) {

				// 이후에 사용할 목적으로 보관
				ArrayList<AdcCreatFile> mapCreatFileList = null; 
				String key1 = GlobalInfo.FileCandiCreatFileVerKey.get("S" + screen.getId());
	    		if (Util.isValid(key1)) {
	    			mapCreatFileList = GlobalInfo.FileCandiCreatFileMap.get(key1);
	    		}
				
				// 대체 광고 존재 확인
				if (mapCreatFileList != null && mapCreatFileList.size() > 0) {

					ArrayList<AdcJsonFileObject> fileList = new ArrayList<AdcJsonFileObject>();
					int fbCnt = 0;
					
					for(AdcCreatFile cf : mapCreatFileList) {
						if (!cf.getCreative().getType().equals("F")) {
							continue;
						}

						//
						// 여기까지 대체 광고이고, 해상도 조건 ok
						
	    				
	    				// 화면의 미디어 유형 수용 확인
	    				if (cf.getMediaType().equals("V") && !screen.isVideoAllowed()) {
	    					continue;
	    				} else if (cf.getMediaType().equals("I") && !screen.isImageAllowed()) {
	    					continue;
	    				}

	    				
	    				// 광고 소재 인벤 타겟팅 확인
	    				//
	    				//   key가 없다는 것은 해당 소재 타겟팅이 없음을 의미
	    				//   list.size() > 0: 타겟팅에 포함되는 화면 수(꼭 현재 화면이 포함된다는 보장 없음)
	    				//   list.size() == 0: 타겟팅은 되었으나, 그 대상 화면 수가 0
	    				//
	    				String key = GlobalInfo.TgtScreenIdVerKey.get("C" + cf.getCreative().getId());
	    				if (Util.isValid(key)) {
	    					List<Integer> idList = GlobalInfo.TgtScreenIdMap.get(key);
	    					if (!idList.contains(screen.getId())) {
	    						continue;
	    					}
	    				} else {
	    					// 소재에 대한 인벤 타겟팅이 없으므로 "통과!!"
	    				}
	    				
	    				
	    				// 광고 소재 시간 타겟팅 확인
	    				if (Util.isValid(cf.getCreative().getExpHour()) && cf.getCreative().getExpHour().length() == 168) {
	    					if (!SolUtil.isCurrentOpHours(cf.getCreative().getExpHour())) {
	    						continue;
	    					}
	    				} else {
	    					// 소재에 대한 시간 타겟팅이 없으므로 "통과!!"
	    				}
	    				
	    				
	    				AdcJsonFileObject jsonFileObject = new AdcJsonFileObject(cf);
	    				jsonFileObject.setCreative(cf.getCreative());
						
						
						// 광고의 노출 시간
						//   1) 광고 설정값(5초 이상인 경우): 이미지 유형 포함 -> 광고 소재에서는 해당되지 않음
						//   2) 재생 시간 미설정(이미지 유형)이라면, 화면의 기본 재생 시간, 매체의 기본 재생 시간 순으로 설정
						//   3) 광고 소재의 재생 시간
						int adDurMillis = 0;
						if (cf.getMediaType().equals("I")) {
							adDurMillis = (screen.isDurationOverridden() ?
									screen.getDefaultDurSecs().intValue() : screen.getMedium().getDefaultDurSecs()) * 1000;
						} else {
							adDurMillis = cf.getSrcDurSecs() > 5 ? (int) Math.round(cf.getSrcDurSecs() * 1000) : 5000;
						}
						
						if (adDurMillis < 5000) {
							continue;
						} else if (cf.getCreative().isDurPolicyOverriden()) {
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
						
						jsonFileObject.setDurMillis(adDurMillis);
						
						if (cf.getCreative().getFbWeight() > 0) {
							fbCnt ++;
							for(int i = 0; i < cf.getCreative().getFbWeight(); i ++) {
								fileList.add(jsonFileObject);
							}
						}
					}
					
					int prevCreativeId = -1;
					if (fbCnt > 1) {
	        			// SELECT FB_SEL_CACHE_ID, SEL_DATE, CREATIVE_ID
	            		Tuple fbSelCacheTuple = revService.getLastFbSelCacheTupleByScreenId(screen.getId());
	            		if (fbSelCacheTuple != null) {
	            			RevFbSelCache fbSelCache = revService.getFbSelCache((int)fbSelCacheTuple.get(0));
	            			if (fbSelCache != null) {
	            				prevCreativeId = fbSelCache.getCreative().getId();
	            			}
	            		}
					}
					
					if (fileList.size() > 0) {
						Collections.shuffle(fileList);
						AdcJsonFileObject nextObj = null;
						
						if (prevCreativeId != -1) {
							for(int i = 0; i < fileList.size(); i ++) {
								AdcJsonFileObject jsonObj = fileList.get(i);
								if (jsonObj.getCreative().getId() != prevCreativeId) {
									nextObj = jsonObj;
									break;
								}
							}
						}
						if (nextObj == null) {
							nextObj = fileList.get(0);
						}
		    			
		    			// 개체 이벤트 처리: 광고 소재(C)의 송출 완료
		    			GlobalInfo.ObjEventTimeItemList.add(new RevObjEventTimeItem(nextObj.getAdId(), now, 21));
		    			
						
						ads.add(getFallbackAdObject(nextObj, screen, testMode));
					}
				}
				
				
				// 운영 시간일 경우에만 시간당 화면 통계 이벤트 등록
				// ads가 1이상일 경우는 대체 광고가 등록된 상태, 0이면 광고가 없음
	    		boolean isCurrentOpHours = SolUtil.isCurrentOpHours((Util.isValid(screen.getBizHour()) && screen.getBizHour().length() == 168)
						? screen.getBizHour() : screen.getMedium().getBizHour());
				if (isCurrentOpHours) {
					sysService.insertTmpHrlyEvent(screen.getId(), now, (ads.size() > 0) ? 3 : 2);
				}
				
				
				if (ads.size() == 0) {
					
					setCodeAndMessage(obj, 3, "NoSchedAd", "일정이 잡혀 있는 광고를 확인할 수 없습니다.");
				}
			}
    		
			obj.put("ads", ads);
    	}
		
		Util.toJson(response, obj);
    }
    
    
    private void setCodeAndMessage(JSONObject obj, int statusCode, String message, String localMessage) {
    	
		obj.put("code", statusCode);
		obj.put("message", message);
		obj.put("local_message", localMessage);
    }
    
    
    private JSONObject getAdObject(AdcAdCreative adCreative, AdcJsonFileObject jsonFileObject, InvScreen screen, String viewType, boolean testMode) {
    	
    	try {
    		
        	JSONObject obj = new JSONObject();
        	
        	obj.put("ad_uuid", jsonFileObject.getAdUuid());
        	obj.put("local_filename", jsonFileObject.getUuidDurFilename());
        	obj.put("ad_pack_ids", adCreative.getAd().getAdPackIds());
        	
        	RevAdSelect adSelect = new RevAdSelect(screen, adCreative);
        	if (!testMode) {
        		revService.saveOrUpdate(adSelect);
            	
            	RevAdSelCache adSelCache = revService.getLastAdSelCacheByScreenIdAdCreativeId(
            			screen.getId(), adCreative.getId());
            	if (adSelCache == null) {
            		revService.saveOrUpdate(new RevAdSelCache(adSelect, jsonFileObject.getDurSecs()));
            	} else {
            		// 광고 선택 캐쉬에서의 광고 선택 일시는 광고의 재생 시간을 고려치 않은 순수 선택 일시로 다시 회귀
            		// 동일 광고주에 의해 다른 광고만으로 구성된 경우 선택될 수 없기 때문
            		adSelCache.setSelectDate(adSelect.getSelectDate());
            		//adSelCache.setSelectDate(Util.addSeconds(adSelect.getSelectDate(), jsonFileObject.getDurSecs()));
            		revService.saveOrUpdate(adSelCache);
            	}

            	logger.info("[API] ad: " + screen.getName() + " - " + adSelect.getUuid().toString());
        	}

        	
        	String trialUuid = adSelect.getUuid().toString();
        	
        	if (SolUtil.isViewTypeAdPackUsed(viewType)) {
            	obj.put("success_url", GlobalInfo.ReportServer + "/v1/report/{0}/success/" + trialUuid + "?start={1}&end={2}&duration={3}");
            	obj.put("error_url", GlobalInfo.ReportServer + "/v1/report/{0}/error/" + trialUuid + "?start={1}&end={2}&duration={3}");
        	} else {
            	obj.put("success_url", GlobalInfo.ReportServer + "/v1/report/success/" + trialUuid + "?start={1}&end={2}&duration={3}");
            	obj.put("error_url", GlobalInfo.ReportServer + "/v1/report/error/" + trialUuid + "?start={1}&end={2}&duration={3}");
        	}
        	
        	String delayUrl = GlobalInfo.ReportServer + "/v1/report/direct/{0}/{8}?apikey={7}&start={1}&end={2}";
        	delayUrl = delayUrl.replace("{8}", String.valueOf(adCreative.getId()))
        			.replace("{7}", screen.getMedium().getApiKey());
        	
        	obj.put("delay_url", delayUrl);
        	
        	return obj;
    		
    	} catch (Exception e) {
    		logger.error("Ad API - getAdObject", e);
    	}
    	
    	return null;
    }

    
    private JSONObject getFallbackAdObject(AdcJsonFileObject jsonFileObject, InvScreen screen, boolean testMode) {
    	
    	try {
    		
        	JSONObject obj = new JSONObject();
        	
        	obj.put("ad_uuid", jsonFileObject.getAdUuid());
        	obj.put("local_filename", jsonFileObject.getUuidDurFilename());
        	obj.put("ad_pack_ids", "");
        	obj.put("success_url", "");
        	obj.put("error_url", "");
        	obj.put("delay_url", "");
        	
        	if (!testMode) {
        		
        		if (jsonFileObject.getCreative() != null) {
        			// SELECT FB_SEL_CACHE_ID, SEL_DATE, CREATIVE_ID
            		Tuple fbSelCacheTuple = revService.getLastFbSelCacheTupleByScreenId(screen.getId());
            		if (fbSelCacheTuple != null) {
            			RevFbSelCache fbSelCache = revService.getFbSelCache((int)fbSelCacheTuple.get(0));
            			if (fbSelCache == null) {
                			revService.saveOrUpdate(new RevFbSelCache(screen, jsonFileObject.getCreative(), new Date()));
            			} else {
            				fbSelCache.setSelectDate(new Date());
            				fbSelCache.setCreative(jsonFileObject.getCreative());
            				revService.saveOrUpdate(fbSelCache);
            			}
            		} else {
            			revService.saveOrUpdate(new RevFbSelCache(screen, jsonFileObject.getCreative(), new Date()));
            		}
        		}
        		
        		revService.saveOrUpdate(new RevPlayHist(new Date(), screen.getMedium().getId(), screen.getMedium().getShortName(),
        				screen.getId(), screen.getName(), jsonFileObject.getAdId(), jsonFileObject.getAdName()));

            	logger.info("[API] ad: " + screen.getName() + " - " + jsonFileObject.getAdName());
        	}
        	
        	return obj;
    		
    	} catch (Exception e) {
    		logger.error("Ad API - getFallbackAdObject", e);
    	}
    	
    	return null;
    }
}
