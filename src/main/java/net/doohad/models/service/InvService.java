package net.doohad.models.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvRTScreen;
import net.doohad.models.inv.InvRTSyncPack;
import net.doohad.models.inv.InvScrLoc;
import net.doohad.models.inv.InvScrPack;
import net.doohad.models.inv.InvScrPackItem;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.inv.InvSite;
import net.doohad.models.inv.InvSyncPack;
import net.doohad.models.inv.InvSyncPackItem;
import net.doohad.models.knl.KnlMedium;
import net.doohad.viewmodels.inv.InvSiteMapLocItem;

@Transactional
public interface InvService {
	
	// Common
	public void flush();

	
	//
	// for InvSite
	//
	// Common
	public InvSite getSite(int id);
	public void saveOrUpdate(InvSite site);
	public void deleteSite(InvSite site);
	public void deleteSites(List<InvSite> sites);

	// for Kendo Grid Remote Read
	public DataSourceResult getSiteList(DataSourceRequest request);

	// for DAO specific
	public InvSite getSite(KnlMedium medium, String shortName);
	public List<InvSite> getSiteListByMediumIdNameLike(int mediumId, String name);
	public List<InvSite> getSiteListByMediumIdShortNameLike(int mediumId, String shortName);
	public List<InvSite> getSiteList();
	public List<Tuple> getSiteCountGroupByMediumSiteCondId(int mediumId);
	public List<InvSite> getMonitSiteList();
	public List<InvSite> getMonitSiteListByMediumId(int mediumId);
	public List<InvSite> getMonitSiteListByMediumNameLike(int mediumId, String name);
	public List<Tuple> getSiteLocListBySiteIdIn(List<Integer> list);
	public List<Tuple> getSiteLocListByVenueType(String venueType);
	
	public int getMonitSiteCountByMediumRegionCodeIn(int mediumId, List<String> list);
	public int getMonitSiteCountByMediumStateCodeIn(int mediumId, List<String> list);
	public int getMonitSiteCountByMediumScreenIdIn(int mediumId, List<Integer> list);
	public int getMonitSiteCountByMediumSiteIdIn(int mediumId, List<Integer> list);
	public int getMonitSiteCountByMediumSiteCondCodeIn(int mediumId, List<String> list);
	public int getMonitSiteCountByMediumScrPackIdIn(int mediumId, List<Integer> list);
	
	public List<Integer> getMonitSiteIdsByMediumRegionCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitSiteIdsByMediumStateCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitSiteIdsByMediumScreenIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitSiteIdsByMediumSiteIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitSiteIdsByMediumSiteCondCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitSiteIdsByMediumScrPackIdIn(int mediumId, List<Integer> list);
	
	public int getAvailSiteCountByMediumId(int mediumId);
	public int getActiveSiteCountByMediumId(int mediumId);
	public List<Tuple> getActiveSiteLocListByMediumId(int mediumId);
	
	
	//
	// for InvScreen
	//
	// Common
	public InvScreen getScreen(int id);
	public void saveOrUpdate(InvScreen screen);
	public void deleteScreen(InvScreen screen);
	public void deleteScreens(List<InvScreen> screens);

	// for Kendo Grid Remote Read
	public DataSourceResult getScreenList(DataSourceRequest request);
	public DataSourceResult getScreenList(DataSourceRequest request, int siteId);
	public DataSourceResult getMonitScreenList(DataSourceRequest request);
	public DataSourceResult getMonitScreenListByScreenIdIn(DataSourceRequest request, List<Integer> list);

	// for DAO specific
	public InvScreen getScreen(KnlMedium medium, String shortName);
	public InvScreen getScreenByName(KnlMedium medium, String name);
	public InvScreen getScreenByMediumIdShortName(int mediumId, String shortName);
	public List<InvScreen> getScreenListBySiteId(int siteId);
	public List<Tuple> getScreenCountGroupByMediumSiteCondId(int mediumId);
	public List<InvScreen> getMonitScreenListByMediumId(int mediumId);
	public List<InvScreen> getMonitScreenList();
	public List<Tuple> getScreenCountGroupByMediumResolution(int mediumId);
	public List<InvScreen> getMonitScreenListByMediumNameLike(int mediumId, String name);
	public InvScreen getMonitScreen(int id);
	public List<Integer> getMonitScreenIdsByMediumId(int mediumId);
	public List<Tuple> getScreenIdResoListByScreenIdIn(List<Integer> list);
	public List<InvScreen> getScreenList();
	public void updateScreenLastAdReportDate(int id, Date lastAdReportDate);
	public void updateScreenLastAdRequestDate(int id, Date lastAdRequestDate);
	public List<InvScreen> getScreenListByMediumIdNameLike(int mediumId, String name);
	
	public int getMonitScreenCountByMediumRegionCodeIn(int mediumId, List<String> list);
	public int getMonitScreenCountByMediumStateCodeIn(int mediumId, List<String> list);
	public int getMonitScreenCountByMediumScreenIdIn(int mediumId, List<Integer> list);
	public int getMonitScreenCountByMediumSiteIdIn(int mediumId, List<Integer> list);
	public int getMonitScreenCountByMediumSiteCondCodeIn(int mediumId, List<String> list);
	public int getMonitScreenCountByMediumScrPackIdIn(int mediumId, List<Integer> list);
	
	public List<Integer> getMonitScreenIdsByMediumRegionCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitScreenIdsByMediumStateCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitScreenIdsByMediumScreenIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitScreenIdsByMediumSiteIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitScreenIdsByMediumSiteCondCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitScreenIdsByMediumScrPackIdIn(int mediumId, List<Integer> list);

	public int getAvailScreenCountByMediumId(int mediumId);
	public int getActiveScreenCountByMediumId(int mediumId);

	
	//
	// for InvScrPack
	//
	// Common
	public InvScrPack getScrPack(int id);
	public void saveOrUpdate(InvScrPack scrPack);
	public void deleteScrPack(InvScrPack scrPack);
	public void deleteScrPacks(List<InvScrPack> scrPacks);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrPackList(DataSourceRequest request);

	// for DAO specific
	public InvScrPack getScrPack(KnlMedium medium, String name);
	public List<InvScrPack> getScrPackListByMediumId(int mediumId);
	public List<InvScrPack> getScrPackListByMediumIdActiveStatus(int mediumId, boolean activeStatus);
	public List<InvScrPack> getScrPackListByMediumIdNameLike(int mediumId, String name);
	
	
	//
	// for InvScrPackItem
	//
	// Common
	public InvScrPackItem getScrPackItem(int id);
	public void saveOrUpdate(InvScrPackItem item);
	public void deleteScrPackItem(InvScrPackItem item);
	public void deleteScrPackItems(List<InvScrPackItem> items);

	// for Kendo Grid Remote Read
	public DataSourceResult getScrPackItemList(DataSourceRequest request, int scrPackId);

	// for DAO specific
	public InvScrPackItem getScrPackItem(InvScrPack scrPack, int screenId);
	public int getScrPackItemCountByScrPackId(int scrPackId);
	public List<Tuple> getScrPackItemScreenIdListByScrPackIdIn(List<Integer> ids);
	public List<InvScrPackItem> getScrPackItemListByScreenId(int screenId);
	
	
	//
	// for InvSyncPack
	//
	// Common
	public InvSyncPack getSyncPack(int id);
	public void saveOrUpdate(InvSyncPack syncPack);
	public void deleteSyncPack(InvSyncPack syncPack);
	public void deleteSyncPacks(List<InvSyncPack> syncPacks);

	// for Kendo Grid Remote Read
	public DataSourceResult getSyncPackList(DataSourceRequest request);
	public DataSourceResult getSyncPackListBySyncPackIdIn(DataSourceRequest request, List<Integer> list);

	// for DAO specific
	public InvSyncPack getSyncPack(KnlMedium medium, String name);
	public List<InvSyncPack> getSyncPackListByMediumId(int mediumId);
	public List<InvSyncPack> getSyncPackListByMediumIdNameLike(int mediumId, String name);
	public List<InvSyncPack> getEffSyncPackListByAdSelSecs(int adSelSecs);
	public int getActiveSyncPackCountByMediumId(int mediumId);
	public List<InvSyncPack> getActiveSyncPackList();
	public InvSyncPack getActiveSyncPackByShortName(String shortName);
	public InvSyncPack getSyncPackByShortName(String shortName);
	
	
	//
	// for InvSyncPackItem
	//
	// Common
	public InvSyncPackItem getSyncPackItem(int id);
	public void saveOrUpdate(InvSyncPackItem item);
	public void deleteSyncPackItem(InvSyncPackItem item);
	public void deleteSyncPackItems(List<InvSyncPackItem> items);

	// for Kendo Grid Remote Read
	public DataSourceResult getSyncPackItemList(DataSourceRequest request, int syncPackId);

	// for DAO specific
	public InvSyncPackItem getSyncPackItemByScreenId(int screenId);
	public int getSyncPackItemCountBySyncPackId(int syncPackId);
	public List<InvSyncPackItem> getSyncPackItemListBySyncPackId(int syncPackId);
	public void saveAndReorderSyncPackItem(InvSyncPackItem item);
	public void reorderSyncPackItem(int syncPackId);
	public List<InvSyncPackItem> getActiveParentSyncPackItemListByMediumId(int mediumId);
	public int getSyncPackItemLaneIdByScreenId(int screenId);
	public List<InvSyncPackItem> getSyncPackItemList();
	public InvSyncPackItem getSyncPackItemFirstLaneBySyncPackId(int syncPackId);
	
	
	//
	// for InvRTScreen
	//
	// Common
	public InvRTScreen getRTScreen(int id);
	public void saveOrUpdate(InvRTScreen rtScreen);
	public void deleteRTScreen(InvRTScreen rtScreen);
	public void deleteRTScreens(List<InvRTScreen> rtScreens);

	// for Kendo Grid Remote Read

	// for DAO specific
	public InvRTScreen getRTScreenByScreenId(int screenId);
	public List<Tuple> getRTScreenCmdTupleListByMediumId(int mediumId);
	
	
	//
	// for InvRTScreenView
	//
	// Common

	// for Kendo Grid Remote Read
	public DataSourceResult getRTScreenViewList(DataSourceRequest request);
	public DataSourceResult getRTScreenViewListByScreenIdIn(DataSourceRequest request, List<Integer> list);
	
	// for DAO specific
	
	
	//
	// for InvScrLoc
	//
	// Common
	public InvScrLoc getScrLoc(int id);
	public void saveOrUpdate(InvScrLoc scrLoc);
	public void deleteScrLoc(InvScrLoc scrLoc);
	public void deleteScrLocs(List<InvScrLoc> scrLocs);

	// for Kendo Grid Remote Read

	// for DAO specific
	public InvScrLoc getLastScrLocByScreenId(int screenId);
	public List<InvScrLoc> getScrLocListByScreenIdDate(int screenId, Date date);
	public List<Date> getScrLocDateListByScreenId(int screenId);
	
	
	//
	// for InvRTSyncPack
	//
	// Common
	public InvRTSyncPack getRTSyncPack(int id);
	public void saveOrUpdate(InvRTSyncPack rtSyncPack);
	public void deleteRTSyncPack(InvRTSyncPack rtSyncPack);
	public void deleteRTSyncPacks(List<InvRTSyncPack> rtSyncPacks);

	// for Kendo Grid Remote Read

	// for DAO specific
	public InvRTSyncPack getRTSyncPackBySyncPackId(int syncPackId);

	
	//
	// for Common
	//
	public boolean updateSiteActiveStatusCountBasedScreens(int siteId);
	public void deleteSoftScreen(InvScreen screen, HttpSession session);
	public void deleteSoftSite(InvSite site, HttpSession session);
	public List<String> getAvailScreenResolutionListByMediumId(int mediumId);
	public int getTargetScreenCountByCreativeId(int creativeId);
	public List<Integer> getTargetScreenIdsByCreativeId(int creativeId);
	public HashMap<String, List<Integer>> getResoScreenIdMapByScreenIdIn(List<Integer> list);
	public int getTargetScreenCountByAdId(int adId);
	public List<Integer> getTargetScreenIdsByAdId(int adId);
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count);
	public List<InvSiteMapLocItem> getCloseSitesBy(InvSite mySite, int count, boolean includeMyself);
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count);
	public List<InvSiteMapLocItem> getCloseSitesBy(int mediumId, double lat, double lng, int count, boolean includeMyself);
	public List<Tuple> getSyncPackTupleListGroupByShortName();

}
