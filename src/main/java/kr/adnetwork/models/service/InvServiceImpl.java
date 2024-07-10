package kr.adnetwork.models.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcAdTarget;
import kr.adnetwork.models.adc.AdcCreatTarget;
import kr.adnetwork.models.inv.InvRTScreen;
import kr.adnetwork.models.inv.InvRTSyncPack;
import kr.adnetwork.models.inv.InvScrLoc;
import kr.adnetwork.models.inv.InvScrPack;
import kr.adnetwork.models.inv.InvScrPackItem;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.inv.InvSyncPackItem;
import kr.adnetwork.models.inv.dao.InvRTScreenDao;
import kr.adnetwork.models.inv.dao.InvRTScreenViewDao;
import kr.adnetwork.models.inv.dao.InvRTSyncPackDao;
import kr.adnetwork.models.inv.dao.InvScrLocDao;
import kr.adnetwork.models.inv.dao.InvScrPackDao;
import kr.adnetwork.models.inv.dao.InvScrPackItemDao;
import kr.adnetwork.models.inv.dao.InvScreenDao;
import kr.adnetwork.models.inv.dao.InvSiteDao;
import kr.adnetwork.models.inv.dao.InvSyncPackDao;
import kr.adnetwork.models.inv.dao.InvSyncPackItemDao;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.rev.RevObjTouch;
import kr.adnetwork.utils.Util;
import kr.adnetwork.viewmodels.inv.InvSiteMapLocItem;

@Transactional
@Service("invService")
public class InvServiceImpl implements InvService {

    //
    // General
    //
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public void flush() {
		
		sessionFactory.getCurrentSession().flush();
	}

	
    
    //
    // DAO
    //
    @Autowired
    private InvSiteDao siteDao;

    @Autowired
    private InvScreenDao screenDao;

    @Autowired
    private InvScrPackDao scrPackDao;

    @Autowired
    private InvScrPackItemDao scrPackItemDao;

    @Autowired
    private InvSyncPackDao syncPackDao;

    @Autowired
    private InvSyncPackItemDao syncPackItemDao;

    @Autowired
    private InvRTScreenDao rtScreenDao;

    @Autowired
    private InvRTScreenViewDao rtScreenViewDao;

    @Autowired
    private InvScrLocDao scrLocDao;

    @Autowired
    private InvRTSyncPackDao rtSyncPackDao;

    @Autowired
    private AdcService adcService;

    @Autowired
    private RevService revService;

    
    
	//
	// for InvSiteDao
	//
	@Override
	public InvSite getSite(int id) {
		return siteDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvSite site) {
		siteDao.saveOrUpdate(site);
	}

	@Override
	public void deleteSite(InvSite site) {
		siteDao.delete(site);
	}

	@Override
	public void deleteSites(List<InvSite> sites) {
		siteDao.delete(sites);
	}

	@Override
	public DataSourceResult getSiteList(DataSourceRequest request) {
		return siteDao.getList(request);
	}

	@Override
	public InvSite getSite(KnlMedium medium, String shortName) {
		return siteDao.get(medium, shortName);
	}

	@Override
	public List<InvSite> getSiteListByMediumIdNameLike(int mediumId, String name) {
		return siteDao.getListByMediumIdNameLike(mediumId, name);
	}

	@Override
	public List<InvSite> getSiteListByMediumIdShortNameLike(int mediumId, String shortName) {
		return siteDao.getListByMediumIdShortNameLike(mediumId, shortName);
	}

	@Override
	public List<InvSite> getSiteList() {
		return siteDao.getList();
	}

	@Override
	public List<Tuple> getSiteCountGroupByMediumSiteCondId(int mediumId) {
		return siteDao.getCountGroupByMediumSiteCondId(mediumId);
	}

	@Override
	public List<InvSite> getMonitSiteList() {
		return siteDao.getMonitList();
	}

	@Override
	public List<InvSite> getMonitSiteListByMediumId(int mediumId) {
		return siteDao.getMonitListByMediumId(mediumId);
	}

	@Override
	public List<InvSite> getMonitSiteListByMediumNameLike(int mediumId, String name) {
		return siteDao.getMonitListByMediumNameLike(mediumId, name);
	}

	@Override
	public List<Tuple> getSiteLocListBySiteIdIn(List<Integer> list) {
		return siteDao.getLocListBySiteIdIn(list);
	}

	@Override
	public List<Tuple> getSiteLocListByVenueType(String venueType) {
		return siteDao.getLocListByVenueType(venueType);
	}

	@Override
	public int getMonitSiteCountByMediumRegionCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitCountByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumStateCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitCountByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitCountByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitCountByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitCountByMediumSiteCondCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumRegionCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitIdsByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumStateCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitIdsByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitIdsByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return siteDao.getMonitIdsByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return siteDao.getMonitIdsByMediumSiteCondCodeIn(mediumId, list);
	}

	@Override
	public int getMonitSiteCountByMediumScrPackIdIn(int mediumId, List<Integer> list) {
		
		List<Tuple> tupleList = getScrPackItemScreenIdListByScrPackIdIn(list);
		ArrayList<Integer> screenIds = new ArrayList<Integer>();
		
		for(Tuple tuple : tupleList) {
			screenIds.add((Integer)tuple.get(0));
		}
		
		return siteDao.getMonitCountByMediumScreenIdIn(mediumId, screenIds);
	}

	@Override
	public List<Integer> getMonitSiteIdsByMediumScrPackIdIn(int mediumId, List<Integer> list) {
		
		List<Tuple> tupleList = getScrPackItemScreenIdListByScrPackIdIn(list);
		ArrayList<Integer> screenIds = new ArrayList<Integer>();
		
		for(Tuple tuple : tupleList) {
			screenIds.add((Integer)tuple.get(0));
		}
		
		return siteDao.getMonitIdsByMediumScreenIdIn(mediumId, screenIds);
	}

	@Override
	public int getAvailSiteCountByMediumId(int mediumId) {
		return siteDao.getAvailCountByMediumId(mediumId);
	}

	@Override
	public int getActiveSiteCountByMediumId(int mediumId) {
		return siteDao.getActiveCountByMediumId(mediumId);
	}

	@Override
	public List<Tuple> getActiveSiteLocListByMediumId(int mediumId) {
		return siteDao.getActiveLocListByMediumId(mediumId);
	}

	
    
	//
	// for InvScreenDao
	//
	@Override
	public InvScreen getScreen(int id) {
		return screenDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvScreen screen) {
		screenDao.saveOrUpdate(screen);
	}

	@Override
	public void deleteScreen(InvScreen screen) {
		screenDao.delete(screen);
	}

	@Override
	public void deleteScreens(List<InvScreen> screens) {
		screenDao.delete(screens);
	}

	@Override
	public DataSourceResult getScreenList(DataSourceRequest request) {
		return screenDao.getList(request);
	}

	@Override
	public DataSourceResult getScreenList(DataSourceRequest request, int siteId) {
		return screenDao.getList(request, siteId);
	}

	@Override
	public DataSourceResult getMonitScreenList(DataSourceRequest request) {
		return screenDao.getMonitList(request);
	}

	@Override
	public DataSourceResult getMonitScreenListByScreenIdIn(DataSourceRequest request, List<Integer> list) {
		return screenDao.getMonitListByScreenIdIn(request, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumId(int mediumId) {
		return screenDao.getMonitIdsByMediumId(mediumId);
	}

	@Override
	public List<Tuple> getScreenIdResoListByScreenIdIn(List<Integer> list) {
		return screenDao.getIdResoListByScreenIdIn(list);
	}

	@Override
	public List<InvScreen> getScreenList() {
		return screenDao.getList();
	}

	@Override
	public void updateScreenLastAdReportDate(int id, Date lastAdReportDate) {
		screenDao.updateLastAdReportDate(id, lastAdReportDate);
	}

	@Override
	public void updateScreenLastAdRequestDate(int id, Date lastAdRequestDate) {
		screenDao.updateLastAdRequestDate(id, lastAdRequestDate);
	}

	@Override
	public List<InvScreen> getScreenListByMediumIdNameLike(int mediumId, String name) {
		return screenDao.getListByMediumIdNameLike(mediumId, name);
	}

	@Override
	public InvScreen getScreen(KnlMedium medium, String shortName) {
		return screenDao.get(medium, shortName);
	}

	@Override
	public InvScreen getScreenByName(KnlMedium medium, String name) {
		return screenDao.getByName(medium, name);
	}

	@Override
	public InvScreen getScreenByMediumIdShortName(int mediumId, String shortName) {
		return screenDao.getByMediumIdShortName(mediumId, shortName);
	}

	@Override
	public List<Tuple> getScreenCountGroupByMediumSiteCondId(int mediumId) {
		return screenDao.getCountGroupByMediumSiteCondId(mediumId);
	}

	@Override
	public List<InvScreen> getScreenListBySiteId(int siteId) {
		return screenDao.getListBySiteId(siteId);
	}

	@Override
	public List<InvScreen> getMonitScreenListByMediumId(int mediumId) {
		return screenDao.getMonitListByMediumId(mediumId);
	}

	@Override
	public List<InvScreen> getMonitScreenList() {
		return screenDao.getMonitList();
	}

	@Override
	public List<Tuple> getScreenCountGroupByMediumResolution(int mediumId) {
		return screenDao.getCountGroupByMediumResolution(mediumId);
	}

	@Override
	public List<InvScreen> getMonitScreenListByMediumNameLike(int mediumId, String name) {
		return screenDao.getMonitListByMediumNameLike(mediumId, name);
	}

	@Override
	public InvScreen getMonitScreen(int id) {
		return screenDao.getMonit(id);
	}

	@Override
	public int getMonitScreenCountByMediumRegionCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitCountByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumStateCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitCountByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitCountByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitCountByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitCountByMediumSiteCondCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumRegionCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitIdsByMediumRegionCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumStateCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitIdsByMediumStateCodeIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumScreenIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitIdsByMediumScreenIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumSiteIdIn(int mediumId, List<Integer> list) {
		return screenDao.getMonitIdsByMediumSiteIdIn(mediumId, list);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumSiteCondCodeIn(int mediumId, List<String> list) {
		return screenDao.getMonitIdsByMediumSiteCondCodeIn(mediumId, list);
	}

	@Override
	public int getMonitScreenCountByMediumScrPackIdIn(int mediumId, List<Integer> list) {
		
		List<Tuple> tupleList = getScrPackItemScreenIdListByScrPackIdIn(list);
		ArrayList<Integer> screenIds = new ArrayList<Integer>();
		
		for(Tuple tuple : tupleList) {
			screenIds.add((Integer)tuple.get(0));
		}
		
		return screenDao.getMonitCountByMediumScreenIdIn(mediumId, screenIds);
	}

	@Override
	public List<Integer> getMonitScreenIdsByMediumScrPackIdIn(int mediumId, List<Integer> list) {
		
		List<Tuple> tupleList = getScrPackItemScreenIdListByScrPackIdIn(list);
		ArrayList<Integer> screenIds = new ArrayList<Integer>();
		
		for(Tuple tuple : tupleList) {
			screenIds.add((Integer)tuple.get(0));
		}
		
		return screenDao.getMonitIdsByMediumScreenIdIn(mediumId, screenIds);
	}

	@Override
	public int getActiveScreenCountByMediumId(int mediumId) {
		return screenDao.getActiveCountByMediumId(mediumId);
	}

	@Override
	public int getAvailScreenCountByMediumId(int mediumId) {
		return screenDao.getAvailCountByMediumId(mediumId);
	}

    
    
	//
	// for InvScrPackDao
	//
	@Override
	public InvScrPack getScrPack(int id) {
		return scrPackDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvScrPack scrPack) {
		scrPackDao.saveOrUpdate(scrPack);
	}

	@Override
	public void deleteScrPack(InvScrPack scrPack) {
		scrPackDao.delete(scrPack);
	}

	@Override
	public void deleteScrPacks(List<InvScrPack> scrPacks) {
		scrPackDao.delete(scrPacks);
	}

	@Override
	public DataSourceResult getScrPackList(DataSourceRequest request) {
		return scrPackDao.getList(request);
	}

	@Override
	public InvScrPack getScrPack(KnlMedium medium, String name) {
		return scrPackDao.get(medium, name);
	}

	@Override
	public List<InvScrPack> getScrPackListByMediumId(int mediumId) {
		return scrPackDao.getListByMediumId(mediumId);
	}

	@Override
	public List<InvScrPack> getScrPackListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {
		return scrPackDao.getListByMediumIdActiveStatus(mediumId, activeStatus);
	}

	@Override
	public List<InvScrPack> getScrPackListByMediumIdNameLike(int mediumId, String name) {
		return scrPackDao.getListByMediumIdNameLike(mediumId, name);
	}

    
    
	//
	// for InvScrPackItemDao
	//
	@Override
	public InvScrPackItem getScrPackItem(int id) {
		return scrPackItemDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvScrPackItem item) {
		scrPackItemDao.saveOrUpdate(item);
	}

	@Override
	public void deleteScrPackItem(InvScrPackItem item) {
		scrPackItemDao.delete(item);
	}

	@Override
	public void deleteScrPackItems(List<InvScrPackItem> items) {
		scrPackItemDao.delete(items);
	}

	@Override
	public DataSourceResult getScrPackItemList(DataSourceRequest request, int scrPackId) {
		return scrPackItemDao.getList(request, scrPackId);
	}

	@Override
	public InvScrPackItem getScrPackItem(InvScrPack scrPack, int screenId) {
		return scrPackItemDao.get(scrPack, screenId);
	}

	@Override
	public int getScrPackItemCountByScrPackId(int scrPackId) {
		return scrPackItemDao.getCountByScrPackId(scrPackId);
	}

	@Override
	public List<Tuple> getScrPackItemScreenIdListByScrPackIdIn(List<Integer> ids) {
		return scrPackItemDao.getScreenIdListByScrPackIdIn(ids);
	}

	@Override
	public List<InvScrPackItem> getScrPackItemListByScreenId(int screenId) {
		return scrPackItemDao.getListByScreenId(screenId);
	}

    
    
	//
	// for InvSyncPackDao
	//
	@Override
	public InvSyncPack getSyncPack(int id) {
		return syncPackDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvSyncPack syncPack) {
		syncPackDao.saveOrUpdate(syncPack);
	}

	@Override
	public void deleteSyncPack(InvSyncPack syncPack) {
		syncPackDao.delete(syncPack);
	}

	@Override
	public void deleteSyncPacks(List<InvSyncPack> syncPacks) {
		syncPackDao.delete(syncPacks);
	}

	@Override
	public DataSourceResult getSyncPackList(DataSourceRequest request) {
		return syncPackDao.getList(request);
	}

	@Override
	public DataSourceResult getSyncPackListBySyncPackIdIn(DataSourceRequest request, List<Integer> list) {
		return syncPackDao.getListBySyncPackIdIn(request, list);
	}

	@Override
	public InvSyncPack getSyncPack(KnlMedium medium, String name) {
		return syncPackDao.get(medium, name);
	}

	@Override
	public List<InvSyncPack> getSyncPackListByMediumId(int mediumId) {
		return syncPackDao.getListByMediumId(mediumId);
	}

	@Override
	public List<InvSyncPack> getSyncPackListByMediumIdNameLike(int mediumId, String name) {
		return syncPackDao.getListByMediumIdNameLike(mediumId, name);
	}

	@Override
	public List<InvSyncPack> getEffSyncPackListByAdSelSecs(int adSelSecs) {
		return syncPackDao.getEffListByAdSelSecs(adSelSecs);
	}

	@Override
	public int getActiveSyncPackCountByMediumId(int mediumId) {
		return syncPackDao.getActiveCountByMediumId(mediumId);
	}

	@Override
	public List<InvSyncPack> getActiveSyncPackList() {
		return syncPackDao.getActiveList();
	}

	@Override
	public InvSyncPack getActiveSyncPackByShortName(String shortName) {
		return syncPackDao.getActiveByShortName(shortName);
	}

	@Override
	public InvSyncPack getSyncPackByShortName(String shortName) {
		return syncPackDao.getByShortName(shortName);
	}

    
    
	//
	// for InvSyncPackItemDao
	//
	@Override
	public InvSyncPackItem getSyncPackItem(int id) {
		return syncPackItemDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvSyncPackItem item) {
		syncPackItemDao.saveOrUpdate(item);
	}

	@Override
	public void deleteSyncPackItem(InvSyncPackItem item) {
		syncPackItemDao.delete(item);
	}

	@Override
	public void deleteSyncPackItems(List<InvSyncPackItem> items) {
		syncPackItemDao.delete(items);
	}

	@Override
	public DataSourceResult getSyncPackItemList(DataSourceRequest request, int syncPackId) {
		return syncPackItemDao.getList(request, syncPackId);
	}

	@Override
	public InvSyncPackItem getSyncPackItemByScreenId(int screenId) {
		return syncPackItemDao.getByScreenId(screenId);
	}

	@Override
	public int getSyncPackItemCountBySyncPackId(int syncPackId) {
		return syncPackItemDao.getCountBySyncPackId(syncPackId);
	}

	@Override
	public List<InvSyncPackItem> getSyncPackItemListBySyncPackId(int syncPackId) {
		return syncPackItemDao.getListBySyncPackId(syncPackId);
	}

	@Override
	public void saveAndReorderSyncPackItem(InvSyncPackItem item) {
		syncPackItemDao.saveAndReorder(item);
	}

	@Override
	public void reorderSyncPackItem(int syncPackId) {
		syncPackItemDao.reorder(syncPackId);
	}

	@Override
	public List<InvSyncPackItem> getActiveParentSyncPackItemListByMediumId(int mediumId) {
		return syncPackItemDao.getActiveParentListByMediumId(mediumId);
	}

	@Override
	public int getSyncPackItemLaneIdByScreenId(int screenId) {
		return syncPackItemDao.getLaneIdByScreenId(screenId);
	}

	@Override
	public List<InvSyncPackItem> getSyncPackItemList() {
		return syncPackItemDao.getList();
	}

	@Override
	public InvSyncPackItem getSyncPackItemFirstLaneBySyncPackId(int syncPackId) {
		return syncPackItemDao.getFirstLaneBySyncPackId(syncPackId);
	}

    
    
	//
	// for InvRTScreenDao
	//
	@Override
	public InvRTScreen getRTScreen(int id) {
		return rtScreenDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvRTScreen rtScreen) {
		rtScreenDao.saveOrUpdate(rtScreen);
	}

	@Override
	public void deleteRTScreen(InvRTScreen rtScreen) {
		rtScreenDao.delete(rtScreen);
	}

	@Override
	public void deleteRTScreens(List<InvRTScreen> rtScreens) {
		rtScreenDao.delete(rtScreens);
	}

	@Override
	public InvRTScreen getRTScreenByScreenId(int screenId) {
		return rtScreenDao.getByScreenId(screenId);
	}

	@Override
	public List<Tuple> getRTScreenCmdTupleListByMediumId(int mediumId) {
		return rtScreenDao.getCmdTupleListByMediumId(mediumId);
	}

    
    
	//
	// for InvRTScreenViewDao
	//
	@Override
	public DataSourceResult getRTScreenViewList(DataSourceRequest request) {
		return rtScreenViewDao.getList(request);
	}

	@Override
	public DataSourceResult getRTScreenViewListByScreenIdIn(DataSourceRequest request, List<Integer> list) {
		return rtScreenViewDao.getListByScreenIdIn(request, list);
	}

    
    
	//
	// for InvScrLocDao
	//
	@Override
	public InvScrLoc getScrLoc(int id) {
		return scrLocDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvScrLoc scrLoc) {
		scrLocDao.saveOrUpdate(scrLoc);
	}

	@Override
	public void deleteScrLoc(InvScrLoc scrLoc) {
		scrLocDao.delete(scrLoc);
	}

	@Override
	public void deleteScrLocs(List<InvScrLoc> scrLocs) {
		scrLocDao.delete(scrLocs);
	}

	@Override
	public InvScrLoc getLastScrLocByScreenId(int screenId) {
		return scrLocDao.getLastByScreenId(screenId);
	}

	@Override
	public List<InvScrLoc> getScrLocListByScreenIdDate(int screenId, Date date) {
		return scrLocDao.getListByScreenIdDate(screenId, date);
	}

	@Override
	public List<Date> getScrLocDateListByScreenId(int screenId) {
		return scrLocDao.getDateListByScreenId(screenId);
	}

    
    
	//
	// for InvRTSyncPackDao
	//
	@Override
	public InvRTSyncPack getRTSyncPack(int id) {
		return rtSyncPackDao.get(id);
	}

	@Override
	public void saveOrUpdate(InvRTSyncPack rtSyncPack) {
		rtSyncPackDao.saveOrUpdate(rtSyncPack);
	}

	@Override
	public void deleteRTSyncPack(InvRTSyncPack rtSyncPack) {
		rtSyncPackDao.delete(rtSyncPack);
	}

	@Override
	public void deleteRTSyncPacks(List<InvRTSyncPack> rtSyncPacks) {
		rtSyncPackDao.delete(rtSyncPacks);
	}

	@Override
	public InvRTSyncPack getRTSyncPackBySyncPackId(int syncPackId) {
		return rtSyncPackDao.getBySyncPackId(syncPackId);
	}

    
    
	//
	// for Common
	//
	@Override
	public boolean updateSiteActiveStatusCountBasedScreens(int siteId) {
		
		InvSite site = getSite(siteId);
		if (site == null) {
			return false;
		}
		
		boolean value = false;
		List<InvScreen> screenList = getScreenListBySiteId(siteId);
		if (screenList.size() > 0) {
			for(InvScreen screen : screenList) {
				if (screen.isActiveStatus()) {
					value = true;
					break;
				}
			}
		}

		site.setActiveStatus(value);
		site.setScreenCount(screenList.size());
		
		siteDao.saveOrUpdate(site);
		
		return true;
	}

	@Override
	public void deleteSoftScreen(InvScreen screen, HttpSession session) {
		
		if (screen != null) {
			screen.setDeleted(true);
			screen.setShortName(screen.getShortName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			screen.setName(screen.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			screen.touchWho(session);
			
			screenDao.saveOrUpdate(screen);
		}
	}

	@Override
	public void deleteSoftSite(InvSite site, HttpSession session) {
		
		if (site != null) {
			
			site.setDeleted(true);
			site.setShortName(site.getShortName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			site.setName(site.getName() + Util.toSimpleString(new Date(), "_yyyyMMdd_HHmm"));
			site.touchWho(session);
			
			siteDao.saveOrUpdate(site);

			
			List<InvScreen> screens = new ArrayList<InvScreen>();
			List<InvScreen> screenList = getScreenListBySiteId(site.getId());
			
			for(InvScreen screen : screenList) {
				RevObjTouch objTouch = revService.getObjTouch("S", screen.getId());
				if (objTouch == null) {
					screens.add(screen);
				} else {
					deleteSoftScreen(screen, session);
				}
			}
			
			deleteScreens(screens);
		}
	}

	@Override
	public List<String> getAvailScreenResolutionListByMediumId(int mediumId) {
		
		ArrayList<String> retList = new ArrayList<String>();
		List<Tuple> countList = getScreenCountGroupByMediumResolution(mediumId);
		for(Tuple tuple : countList) {
			retList.add((String) tuple.get(0));
		}

		return retList;
	}

	@Override
	public int getTargetScreenCountByCreativeId(int creativeId) {
		
		return getTargetScreenIdsByCreativeId(creativeId).size();
	}

	@Override
	public List<Integer> getTargetScreenIdsByCreativeId(int creativeId) {
		
		List<AdcCreatTarget> targets = adcService.getCreatTargetListByCreativeId(creativeId);
		
		List<Integer> currIds = new ArrayList<Integer>();
		List<Integer> resultIds = null;
		
		for(AdcCreatTarget creatTarget : targets) {
			if (creatTarget.getInvenType().equals("RG")) {
				currIds = getMonitScreenIdsByMediumRegionCodeIn(creatTarget.getMedium().getId(), 
		    			Util.getStringList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("CT")) {
				currIds = getMonitScreenIdsByMediumStateCodeIn(creatTarget.getMedium().getId(), 
						Util.getStringList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("ST")) {
				currIds = getMonitScreenIdsByMediumSiteIdIn(creatTarget.getMedium().getId(), 
						Util.getIntegerList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("SC")) {
				currIds = getMonitScreenIdsByMediumScreenIdIn(creatTarget.getMedium().getId(), 
						Util.getIntegerList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("CD")) {
				currIds = getMonitScreenIdsByMediumSiteCondCodeIn(creatTarget.getMedium().getId(), 
						Util.getStringList(creatTarget.getTgtValue()));
			} else if (creatTarget.getInvenType().equals("SP")) {
				currIds = getMonitScreenIdsByMediumScrPackIdIn(creatTarget.getMedium().getId(), 
						Util.getIntegerList(creatTarget.getTgtValue()));
			}
			if (creatTarget.getTgtScrCount() != currIds.size()) {
				creatTarget.setTgtScrCount(currIds.size());
				adcService.saveOrUpdate(creatTarget);
			}
			
			if (resultIds == null) {
				resultIds = currIds;
			} else {
				if (creatTarget.getFilterType().equals("A")) {
					resultIds = Util.intersection(resultIds, currIds);
				} else {
					resultIds = Util.union(resultIds, currIds);
				}
			}
		}
		
		return (resultIds == null ? new ArrayList<Integer>() : resultIds);
	}

	@Override
	public HashMap<String, List<Integer>> getResoScreenIdMapByScreenIdIn(List<Integer> list) {
		
		HashMap<String, List<Integer>> map = new HashMap<String, List<Integer>>();
		
		List<Tuple> tuples = getScreenIdResoListByScreenIdIn(list);
		for(Tuple tuple : tuples) {
			Integer id = (Integer) tuple.get(0);
			String reso = (String) tuple.get(1);
			
			if (map.containsKey(reso)) {
				List<Integer> ids = map.get(reso);
				ids.add(id);
			} else {
				ArrayList<Integer> ids = new ArrayList<Integer>();
				ids.add(id);
				map.put(reso, ids);
			}
		}
		
		return map;
	}

	@Override
	public int getTargetScreenCountByAdId(int adId) {
		
		return getTargetScreenIdsByAdId(adId).size();
	}

	@Override
	public List<Integer> getTargetScreenIdsByAdId(int adId) {
		
		List<AdcAdTarget> targets = adcService.getAdTargetListByAdId(adId);
		
		List<Integer> currIds = new ArrayList<Integer>();
		List<Integer> resultIds = null;
		
		for(AdcAdTarget adTarget : targets) {
			if (adTarget.getInvenType().equals("RG")) {
				currIds = getMonitScreenIdsByMediumRegionCodeIn(adTarget.getMedium().getId(), 
		    			Util.getStringList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("CT")) {
				currIds = getMonitScreenIdsByMediumStateCodeIn(adTarget.getMedium().getId(), 
						Util.getStringList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("ST")) {
				currIds = getMonitScreenIdsByMediumSiteIdIn(adTarget.getMedium().getId(), 
						Util.getIntegerList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("SC")) {
				currIds = getMonitScreenIdsByMediumScreenIdIn(adTarget.getMedium().getId(), 
						Util.getIntegerList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("CD")) {
				currIds = getMonitScreenIdsByMediumSiteCondCodeIn(adTarget.getMedium().getId(), 
						Util.getStringList(adTarget.getTgtValue()));
			} else if (adTarget.getInvenType().equals("SP")) {
				currIds = getMonitScreenIdsByMediumScrPackIdIn(adTarget.getMedium().getId(), 
						Util.getIntegerList(adTarget.getTgtValue()));
			}
			if (adTarget.getTgtScrCount() != currIds.size()) {
				adTarget.setTgtScrCount(currIds.size());
				adcService.saveOrUpdate(adTarget);
			}
			
			if (resultIds == null) {
				resultIds = currIds;
			} else {
				if (adTarget.getFilterType().equals("A")) {
					resultIds = Util.intersection(resultIds, currIds);
				} else {
					resultIds = Util.union(resultIds, currIds);
				}
			}
		}
		
		return (resultIds == null ? new ArrayList<Integer>() : resultIds);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count) {
		
		return getCloseSitesBy(mySite, count, false);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count, boolean includeMyself) {
		
		if (mySite == null) {
			return null;
		}
		
		InvSiteMapLocItem mapItem = new InvSiteMapLocItem(mySite);
		return getCloseSitesBy(mySite.getMedium().getId(), mapItem.getLat(), mapItem.getLng(), count, includeMyself);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count) {
		
		return getCloseSitesBy(mediumId, lat, lng, count, false);
	}

	@Override
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count, boolean includeMyself) {
		
		ArrayList<InvSiteMapLocItem> list = new ArrayList<InvSiteMapLocItem>();
    	
    	List<InvSite> siteList = getMonitSiteListByMediumId(mediumId);
    	for(InvSite site : siteList) {
    		InvSiteMapLocItem mapItem = new InvSiteMapLocItem(site);
    		mapItem.setDistance(Util.distance(lat, lng, mapItem.getLat(), mapItem.getLng()));
    		list.add(mapItem);
    	}
    	
		Collections.sort(list, new Comparator<InvSiteMapLocItem>() {
	    	public int compare(InvSiteMapLocItem item1, InvSiteMapLocItem item2) {
	    		return Double.compare(item1.getDistance(),  item2.getDistance());
	    	}
	    });
		
		ArrayList<InvSiteMapLocItem> retList = new ArrayList<InvSiteMapLocItem>();
		int idx = 0;
		for(InvSiteMapLocItem item : list) {
			
			if (!includeMyself && item.getDistance() == 0) {
				continue;
			}
			
			if (idx < count) {
				retList.add(item);
			}
			
			idx ++;
		}
		
		return retList;
	}

	@Override
	public List<Tuple> getSyncPackTupleListGroupByShortName() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT a.medium_id, a.short_name, a.cnt, a.sync_pack_id, a.min_lane_id, s.short_name as scr_short_name, s.screen_id
		//		FROM (
		//         SELECT p.medium_id, p.short_name, COUNT(pi.sync_pack_item_id) as cnt, p.sync_pack_id, MIN(pi.lane_id) as min_lane_id
		//         FROM inv_sync_packs p, inv_sync_pack_items pi 
		//         WHERE p.sync_pack_id = pi.sync_pack_id 
		//         GROUP BY p.medium_id, p.short_name ) a, inv_sync_pack_items pi, inv_screens s
		//		WHERE a.sync_pack_id = pi.sync_pack_id AND a.min_lane_id = pi.lane_id AND pi.screen_id = s.screen_id
		//
		String sql = "SELECT a.medium_id, a.short_name, a.cnt, a.sync_pack_id, a.min_lane_id, s.short_name as scr_short_name, s.screen_id " +
					"FROM ( " +
					"SELECT p.medium_id, p.short_name, COUNT(pi.sync_pack_item_id) as cnt, p.sync_pack_id, MIN(pi.lane_id) as min_lane_id " +
					"FROM inv_sync_packs p, inv_sync_pack_items pi  " +
					"WHERE p.sync_pack_id = pi.sync_pack_id " +
					"GROUP BY p.medium_id, p.short_name ) a, inv_sync_pack_items pi, inv_screens s " +
					"WHERE a.sync_pack_id = pi.sync_pack_id AND a.min_lane_id = pi.lane_id AND pi.screen_id = s.screen_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

}
