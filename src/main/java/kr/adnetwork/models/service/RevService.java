package kr.adnetwork.models.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Tuple;

import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.rev.RevAdSelCache;
import kr.adnetwork.models.rev.RevAdSelect;
import kr.adnetwork.models.rev.RevChanAd;
import kr.adnetwork.models.rev.RevChanAdRpt;
import kr.adnetwork.models.rev.RevCreatDecn;
import kr.adnetwork.models.rev.RevDailyAchv;
import kr.adnetwork.models.rev.RevEventReport;
import kr.adnetwork.models.rev.RevFbSelCache;
import kr.adnetwork.models.rev.RevHourlyPlay;
import kr.adnetwork.models.rev.RevImpWave;
import kr.adnetwork.models.rev.RevInvenRequest;
import kr.adnetwork.models.rev.RevObjTouch;
import kr.adnetwork.models.rev.RevPlayHist;
import kr.adnetwork.models.rev.RevScrHourlyPlay;
import kr.adnetwork.models.rev.RevScrHrlyFailTot;
import kr.adnetwork.models.rev.RevScrHrlyFbTot;
import kr.adnetwork.models.rev.RevScrHrlyNoAdTot;
import kr.adnetwork.models.rev.RevScrHrlyPlyTot;
import kr.adnetwork.models.rev.RevScrStatusLine;
import kr.adnetwork.models.rev.RevSitHrlyPlyTot;
import kr.adnetwork.models.rev.RevSyncPackImp;

@Transactional
public interface RevService {
	
	// Common
	public void flush();

	
	//
	// for RevAdSelect
	//
	// Common
	public RevAdSelect getAdSelect(int id);
	public void saveOrUpdate(RevAdSelect adSelect);
	public void deleteAdSelect(RevAdSelect adSelect);
	public void deleteAdSelects(List<RevAdSelect> adSelects);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdSelectList(DataSourceRequest request);

	// for DAO specific
	public RevAdSelect getAdSelect(UUID uuid);
	public List<RevAdSelect> getLastAdSelectListByScreenId(int screenId, int maxRecords);
	public List<RevAdSelect> getAdSelectListByScreenId(int screenId);
	public List<RevAdSelect> getReportedAdSelectListOrderBySelDateBeforeReportDate(Date date);
	public List<RevAdSelect> getAdSelectListBeforeSelectDateOrderBySelDate(Date selectDate);
	public void deleteAdSelectBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getAdSelectHourStatTupleList1();
	public List<Tuple> getAdSelectHourStatTupleList2();
	public List<Tuple> getAdSelectMediumStatTupleList();
	public List<Tuple> getAdSelectMinStatTupleList1();
	public List<Tuple> getAdSelectMinStatTupleList2();
	public List<RevAdSelect> getNotReportedOrFailedAdSelectListByScreenId(int screenId);

	
	//
	// for RevPlayHist
	//
	// Common
	public RevPlayHist getPlayHist(int id);
	public void saveOrUpdate(RevPlayHist playHist);
	public void deletePlayHist(RevPlayHist playHist);
	public void deletePlayHists(List<RevPlayHist> playHists);

	// for Kendo Grid Remote Read

	// for DAO specific
	public int getPlayHistCountByScreenIdStartDate(int screenId, Date startDate);
	public List<RevPlayHist> getFirstPlayHistList(int maxRecords);
	public void deleteBulkPlayHistRowsInIds(List<Integer> ids);
	public List<RevPlayHist> getLastPlayHistListByScreenId(int screenId, int maxRecords);
	public List<RevPlayHist> getPlayHistListByScreenId(int screenId);
	public RevPlayHist getPlayHistByUuid(String uuid);
	
	
	//
	// for RevScrHourlyPlay
	//
	// Common
	public RevScrHourlyPlay getScrHourlyPlay(int id);
	public void saveOrUpdate(RevScrHourlyPlay hourPlay);
	public void deleteScrHourlyPlay(RevScrHourlyPlay hourPlay);
	public void deleteScrHourlyPlays(List<RevScrHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHourlyPlayList(DataSourceRequest request);
	public DataSourceResult getScrHourlyPlayList(DataSourceRequest request, int adId, Date sDate, Date eDate);

	// for DAO specific
	public RevScrHourlyPlay getScrHourlyPlay(InvScreen screen, AdcAdCreative adCreative, Date playDate);
	public RevScrHourlyPlay getScrHourlyPlay(int screenId, int adCreativeId, Date playDate);
	public List<RevScrHourlyPlay> getScrHourlyPlayListBySiteIdAdIdPlayDate(
			int siteId, int adId, Date playDate);
	public List<Tuple> getScrSumScrHourlyPlayListByPlayDate(Date playDate);
	public List<Tuple> getSitSumScrHourlyPlayListByPlayDate(Date playDate);
	public List<Tuple> getScrHourlyPlayPlayDateListByLastUpdateDate(Date date);
	public List<RevScrHourlyPlay> getScrHourlyPlayListByAdIdPlayDate(int adId, Date playDate);
	public List<Tuple> getScrHourlyPlayAdStatListByScreenIdPlayDate(int screenId, Date playDate);
	public List<Tuple> getScrHourlyPlayStatGroupByAdCreatPlayDate(Date playDate);
	public List<Tuple> getScrHourlyPlayScrCntGroupByAdIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrHourlyPlayScrCntGroupByCreatIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrHourlyPlayScrCntGroupByPlayDateAdIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrHourlyPlayScrCntGroupByPlayDateCreatIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrHourlyPlayScrCntGroupByWeekDayAdIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrHourlyPlayScrCntGroupByWeekDayCreatIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<RevScrHourlyPlay> getScrHourlyPlayListByPlayDate(Date playDate);
	public List<Tuple> getScrHourlyPlayUpdateCpmListByPlayDate(Date playDate);
	public void updateScrHourlyPlayCpmValues(int id, int floorCpm, int adCpm);
	public int getScrHourlyPlayScrCntAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public int getScrHourlyPlayScrCntCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getScrHourlyPlayScrCntGroupByCampIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public Tuple getScrHourlyPlayHourStatByMediumIdPlayDate(int mediumId, Date playDate);
	public boolean deleteScrHourlyPlaysByAdIdScreenIdPlayDate(Date sDate, Date eDate, 
			List<Integer> adIds, List<Integer> screenIds);
	public List<Tuple> getScrHourlyPlayPlayDateListByAdIdScreenIdPlayDate(Date sDate, Date eDate, 
			List<Integer> adIds, List<Integer> screenIds);

	
	//
	// for RevAdSelCache
	//
	// Common
	public RevAdSelCache getAdSelCache(int id);
	public void saveOrUpdate(RevAdSelCache adSelCache);
	public void deleteAdSelCache(RevAdSelCache adSelCache);
	public void deleteAdSelCaches(List<RevAdSelCache> adSelCaches);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdSelCacheList(DataSourceRequest request);

	// for DAO specific
	public RevAdSelCache getLastAdSelCacheByScreenIdAdCreativeId(int screenId, int adCreativeId);
	public Tuple getLastAdSelCacheTupleByScreenId(int screenId);
	public Tuple getLastAdSelCacheTupleByScreenIdAdId(int screenId, int adId);
	public Tuple getLastAdSelCacheTupleByScreenIdAdvertiserId(int screenId, int advertiserId);
	public List<Tuple> getAdSelCacheTupleListByScreenId(int screenId);
	
	
	//
	// for RevCreatDecn
	//
	// Common
	public RevCreatDecn getCreatDecn(int id);
	public void saveOrUpdate(RevCreatDecn creatDecn);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreatDecnList(DataSourceRequest request);

	// for DAO specific

	
	//
	// for RevScrHrlyPlyTot
	//
	// Common
	public RevScrHrlyPlyTot getScrHrlyPlyTot(int id);
	public void saveOrUpdate(RevScrHrlyPlyTot hrlyPlyTot);
	public void deleteScrHrlyPlyTot(RevScrHrlyPlyTot hrlyPlyTot);
	public void deleteScrHrlyPlyTots(List<RevScrHrlyPlyTot> hrlyPlyTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyPlyTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyPlyTot getScrHrlyPlyTot(InvScreen screen, Date playDate);
	public List<RevScrHrlyPlyTot> getScrHrlyPlyTotListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public List<RevScrHrlyPlyTot> getScrHrlyPlyTotListByPlayDate(Date playDate);
	public List<Tuple> getScrHrlyPlyTotTupleListByPlayDate(Date playDate);
	public Tuple getScrHrlyPlyTotStatByMediumIdPlayDate(int mediumId, Date playDate);
	public Double getStdScrHrlyPlyTotByMediumIdPlayDate(int mediumId, Date playDate);
	public Tuple getAvgScrHrlyPlyTotByMediumIdBetweenPlayDates(int mediumId, Date date1, Date date2);
	public Tuple getScrHrlyPlyTotHourStatByMediumIdPlayDate(int mediumId, Date playDate);
	public void deleteInactiveScrHrlyPlyTots();


	
	//
	// for RevSitHrlyPlyTot
	//
	// Common
	public RevSitHrlyPlyTot getSitHrlyPlyTot(int id);
	public void saveOrUpdate(RevSitHrlyPlyTot hrlyPlyTot);
	public void deleteSitHrlyPlyTot(RevSitHrlyPlyTot hrlyPlyTot);
	public void deleteSitHrlyPlyTots(List<RevSitHrlyPlyTot> hrlyPlyTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getSitHrlyPlyTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevSitHrlyPlyTot getSitHrlyPlyTot(InvSite site, Date playDate);
	public List<RevSitHrlyPlyTot> getSitHrlyPlyTotListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public List<RevSitHrlyPlyTot> getSitHrlyPlyTotListByPlayDate(Date playDate);
	public List<Tuple> getSitHrlyPlyTotTupleListByPlayDate(Date playDate);
	public Tuple getSitHrlyPlyTotStatByMediumIdPlayDate(int mediumId, Date playDate);
	public void deleteInactiveSitHrlyPlyTotsByPlaDate(Date playDate);

	
	//
	// for RevScrStatusLine
	//
	// Common
	public RevScrStatusLine getScrStatusLine(int id);
	public void saveOrUpdate(RevScrStatusLine statusLine);
	public void deleteScrStatusLine(RevScrStatusLine statusLine);
	public void deleteScrStatusLines(List<RevScrStatusLine> statusLines);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevScrStatusLine getScrStatusLine(int screenId, Date playDate);
	public List<RevScrStatusLine> getScrStatusLineListByScreenId(int screenId);
	public List<RevScrStatusLine> getScrStatusLineListByPlayDate(Date playDate);
	public Tuple getScrStatusLineTuple(int screenId, Date playDate);
	public void insertScrStatusLine(int screenId, Date playDate, String statusLine);
	public void updateScrStatusLine(int id, String statusLine);

	
	//
	// for RevInvenRequest
	//
	// Common
	public RevInvenRequest getInvenRequest(int id);
	public void saveOrUpdate(RevInvenRequest invenRequest);
	public void deleteInvenRequest(RevInvenRequest invenRequest);
	public void deleteInvenRequests(List<RevInvenRequest> invenRequests);

	// for Kendo Grid Remote Read
	public DataSourceResult getInvenRequestList(DataSourceRequest request);

	// for DAO specific

	
	//
	// for RevFbSelCache
	//
	// Common
	public RevFbSelCache getFbSelCache(int id);
	public void saveOrUpdate(RevFbSelCache fbSelCache);
	public void deleteFbSelCache(RevFbSelCache fbSelCache);
	public void deleteFbSelCaches(List<RevFbSelCache> fbSelCaches);

	// for Kendo Grid Remote Read

	// for DAO specific
	public Tuple getLastFbSelCacheTupleByScreenId(int screenId);

	
	//
	// for RevScrHrlyFailTot
	//
	// Common
	public RevScrHrlyFailTot getScrHrlyFailTot(int id);
	public void saveOrUpdate(RevScrHrlyFailTot hrlyFailTot);
	public void deleteScrHrlyFailTot(RevScrHrlyFailTot hrlyFailTot);
	public void deleteScrHrlyFailTots(List<RevScrHrlyFailTot> hrlyFailTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyFailTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFailTot getScrHrlyFailTot(InvScreen screen, Date playDate);
	public RevScrHrlyFailTot getScrHrlyFailTot(int screenId, Date playDate);
	public Tuple getScrHrlyFailTotHourStatByMediumIdPlayDate(int mediumId, Date playDate);

	
	//
	// for RevScrHrlyNoAdTot
	//
	// Common
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(int id);
	public void saveOrUpdate(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void deleteScrHrlyNoAdTot(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void deleteScrHrlyNoAdTots(List<RevScrHrlyNoAdTot> hrlyNoAdTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyNoAdTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(InvScreen screen, Date playDate);
	public RevScrHrlyNoAdTot getScrHrlyNoAdTot(int screenId, Date playDate);
	public Tuple getScrHrlyNoAdTotStatByMediumIdPlayDate(int mediumId, Date playDate);
	public List<RevScrHrlyNoAdTot> getScrHrlyNoAdTotListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public Tuple getScrHrlyNoAdTotHourStatByMediumIdPlayDate(int mediumId, Date playDate);

	
	//
	// for RevScrHrlyFbTot
	//
	// Common
	public RevScrHrlyFbTot getScrHrlyFbTot(int id);
	public void saveOrUpdate(RevScrHrlyFbTot hrlyFbTot);
	public void deleteScrHrlyFbTot(RevScrHrlyFbTot hrlyFbTot);
	public void deleteScrHrlyFbTots(List<RevScrHrlyFbTot> hrlyFbTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrHrlyFbTotList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFbTot getScrHrlyFbTot(InvScreen screen, Date playDate);
	public RevScrHrlyFbTot getScrHrlyFbTot(int screenId, Date playDate);
	public Tuple getScrHrlyFbTotStatByMediumIdPlayDate(int mediumId, Date playDate);
	public List<RevScrHrlyFbTot> getScrHrlyFbTotListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public Tuple getScrHrlyFbTotHourStatByMediumIdPlayDate(int mediumId, Date playDate);

	
	//
	// for RevObjTouch
	//
	// Common
	public RevObjTouch getObjTouch(int id);
	public void saveOrUpdate(RevObjTouch objTouch);
	public void deleteObjTouch(RevObjTouch objTouch);
	public void deleteObjTouches(List<RevObjTouch> objTouches);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevObjTouch getObjTouch(String type, int objId);
	public List<RevObjTouch> getObjTouchList();
	public List<Tuple> getLastObjTouchListIn(List<Integer> ids, int lastN);
	
	
	//
	// for RevHourlyPlay
	//
	// Common
	public RevHourlyPlay getHourlyPlay(int id);
	public void saveOrUpdate(RevHourlyPlay hourPlay);
	public void deleteHourlyPlay(RevHourlyPlay hourPlay);
	public void deleteHourlyPlays(List<RevHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getHourlyPlayList(DataSourceRequest request);

	// for DAO specific
	public RevHourlyPlay getHourlyPlay(AdcAd ad, AdcCreative creative, Date playDate);
	public List<Tuple> getHourlyPlayStatGroupByAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getHourlyPlayStatGroupByPlayDateAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getHourlyPlayStatGroupByWeekDayAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getHourlyPlayActualStatGroupByAdId();
	public List<Tuple> getHourlyPlayStatGroupByCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getHourlyPlayStatGroupByPlayDateCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getHourlyPlayStatGroupByWeekDayCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getHourlyPlayActualStatGroupByCreatId();
	public List<Tuple> getHourlyPlayActualStatGroupByCampaignId();
	public Tuple getHourlyPlayAccStatByAdIdPlayDate(int adId, Date playDate);
	public Tuple getHourlyPlayAccStatByCampaignIdPlayDate(int campId, Date playDate);
	public List<Tuple> getHourlyPlayCampIdListByMediumPlayDate(int mediumId, Date playDate);
	public List<Tuple> getHourlyPlayAdIdListByMediumPlayDate(int mediumId, Date playDate);
	public List<Tuple> getHourlyPlayCreatIdListByMediumPlayDate(int mediumId, Date playDate);
	public List<Tuple> getHourlyPlayStatGroupByCampIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getHourlyPlayActualStatGroupByAdIdByPlayDate(Date playDate);
	public List<Tuple> getHourlyPlayActualStatGroupByCampaignIdByPlayDate(Date playDate);
	public boolean deleteHourlyPlaysByMediumIdPlayDate(int mediumId, List<Date> playDates);
	
	
	//
	// for RevImpWave
	//
	// Common
	public RevImpWave getImpWave(int id);
	public void saveOrUpdate(RevImpWave impWave);
	public void deleteImpWave(RevImpWave impWave);
	public void deleteImpWaves(List<RevImpWave> impWaves);

	// for Kendo Grid Remote Read

	// for DAO specific
	public List<RevImpWave> getEffImpWaveList();
	
	
	//
	// for RevDailyAchv
	//
	// Common
	public RevDailyAchv getDailyAchv(int id);
	public void saveOrUpdate(RevDailyAchv dailyAchv);
	public void deleteDailyAchv(RevDailyAchv dailyAchv);
	public void deleteDailyAchves(List<RevDailyAchv> dailyAchves);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevDailyAchv getDailyAchvByTypeIdPlayDate(String type, int objId, Date playDate);
	public List<RevDailyAchv> getDailyAchvListByTypeId(String type, int objId);
	
	
	//
	// for RevEventReport
	//
	// Common
	public RevEventReport getEventReport(int id);
	public void saveOrUpdate(RevEventReport eventReport);
	public void deleteEventReport(RevEventReport eventReport);
	public void deleteEventReports(List<RevEventReport> eventReports);

	// for Kendo Grid Remote Read
	public DataSourceResult getEventReportList(DataSourceRequest request);
	public DataSourceResult getEventReportList(DataSourceRequest request, int mediumId);

	// for DAO specific
	
	
	//
	// for RevSyncPackImp
	//
	// Common
	public RevSyncPackImp getSyncPackImp(int id);
	public void saveOrUpdate(RevSyncPackImp syncPackImp);
	public void deleteSyncPackImp(RevSyncPackImp syncPackImp);
	public void deleteSyncPackImps(List<RevSyncPackImp> syncPackImps);

	// for Kendo Grid Remote Read

	// for DAO specific
	public List<Tuple> getSyncPackImpLogTupleListByTime(String unit);
	public List<Tuple> getSyncPackImpControlTupleListByTime(String unit);
	public List<Tuple> getLastSyncPackImpTupleListGroupByShortName();
	public RevSyncPackImp getLastSyncPackImpByShortName(String shortName);
	public int getSyncPackImpCount();
	public int deleteSyncPackImpsBefore(Date date);
	
	
	//
	// for RevChanAd
	//
	// Common
	public RevChanAd getChanAd(int id);
	public void saveOrUpdate(RevChanAd chanAd);
	public void deleteChanAd(RevChanAd chanAd);
	public void deleteChanAds(List<RevChanAd> chanAds);

	// for Kendo Grid Remote Read
	public DataSourceResult getChanAdList(DataSourceRequest request, int channelId);

	// for DAO specific
	public List<Tuple> getLastChanAdListGroupByChannelId();
	public List<RevChanAd> getChanAdListByChannelId(int channelId);
	public List<RevChanAd> getCurrChanAdListByChannelId(int channelId);
	public RevChanAd getLastChanAdByChannelIdSeq(int channelId, int seq);
	public int getChanAdCount();
	public int deleteChanAdBefore(Date date);
	
	
	//
	// for RevChanAdRpt
	//
	// Common
	public RevChanAdRpt getChanAdRpt(int id);
	public void saveOrUpdate(RevChanAdRpt chanAdRpt);
	public void deleteChanAdRpt(RevChanAdRpt chanAdRpt);
	public void deleteChanAdRpts(List<RevChanAdRpt> chanAdRpts);

	// for Kendo Grid Remote Read
	public DataSourceResult getChanAdRptList(DataSourceRequest request, String type, int objId);

	// for DAO specific
	public int getChanAdRptCount();
	public int deleteChanAdRptBefore(Date date);
	
	
	//
	// for Common
	//
	public int calcDailyInvenConnectCountByPlayDate(Date playDate);
	public List<Integer> getPctValueListByMeidumIdDayOfWeek(int mediumId, Date date1, Date date2);
	public void deleteScreenHrlyPlayData(Date playDate, int screenId);
	public void deleteSiteHrlyPlayData(Date playDate, int siteId);
	
}
