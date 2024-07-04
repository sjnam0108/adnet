package net.doohad.models.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcAdCreative;
import net.doohad.models.adc.AdcAdTarget;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.adc.AdcCreatFile;
import net.doohad.models.adc.AdcCreatTarget;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.adc.AdcMobTarget;
import net.doohad.models.adc.AdcPlaylist;
import net.doohad.models.knl.KnlMedium;

@Transactional
public interface AdcService {
	
	// Common
	public void flush();

	
	//
	// for AdcCreative
	//
	// Common
	public AdcCreative getCreative(int id);
	public void saveOrUpdate(AdcCreative creative);
	public void deleteCreative(AdcCreative creative);
	public void deleteCreatives(List<AdcCreative> creatives);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreativeList(DataSourceRequest request);
	public DataSourceResult getPendApprCreativeList(DataSourceRequest request);
	public DataSourceResult getCreativeList(DataSourceRequest request, int advertiserId);

	// for DAO specific
	public List<AdcCreative> getCreativeListByMediumIdNameLike(int mediumId, String name);
	public List<Tuple> getCreativeCountGroupByMediumAdvertiserId(int mediumId);
	public List<AdcCreative> getCreativeListByAdvertiserId(int advertiserId);
	public List<AdcCreative> getCreativeListByAdvertiserIdViewTypeCode(
			int advertiserId, String viewTypeCode);
	public List<AdcCreative> getValidCreativeList();
	public List<AdcCreative> getValidCreativeFallbackListByMediumId(int mediumId);
	public List<AdcCreative> getCreativeListByMediumIdName(int mediumId, String name);
	public int getCreativeCountByAdvertiserId(int advertiserId);
	public List<Tuple> getCreativeIdListByCampaignId(int campaignId);
	public List<AdcCreative> getCreativeListByMediumId(int mediumId);

	
	//
	// for AdcCreatFile
	//
	// Common
	public AdcCreatFile getCreatFile(int id);
	public void saveOrUpdate(AdcCreatFile creatFile);
	public void deleteCreatFile(AdcCreatFile creatFile);
	public void deleteCreatFiles(List<AdcCreatFile> creatFiles);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreatFileList(DataSourceRequest request);

	// for DAO specific
	public List<AdcCreatFile> getCreatFileListByCreativeId(int creativeId);
	public AdcCreatFile getCreatFileByCreativeIdResolution(int creativeId, String resolution);
	public List<Tuple> getCreatFileCountGroupByMediumMediaType(int mediumId);
	public List<Tuple> getCreatFileCountGroupByCtntFolderId();
	public int getCreatFileCountByAdvertiserId(int advertiserId);
	public List<AdcCreatFile> getCreatFileListByMediumId(int mediumId);
	public List<AdcCreatFile> getCreatFileListIn(List<Integer> ids);


	
	//
	// for AdcCampaign
	//
	// Common
	public AdcCampaign getCampaign(int id);
	public void saveOrUpdate(AdcCampaign campaign);
	public void deleteCampaign(AdcCampaign campaign);
	public void deleteCampaigns(List<AdcCampaign> campaigns);

	// for Kendo Grid Remote Read
	public DataSourceResult getCampaignList(DataSourceRequest request);
	public DataSourceResult getCampaignList(DataSourceRequest request, int mediumId);

	// for DAO specific
	public AdcCampaign getCampaign(KnlMedium medium, String name);
	public List<AdcCampaign> getCampaignListByMediumId(int mediumId);
	public List<AdcCampaign> getCampaignList();
	public List<AdcCampaign> getCampaignLisyByAdvertiserId(int advertiserId);
	public List<Tuple> getCampaignBudgetStatGroupByCampaignId();
	public List<Tuple> getCampaignIdsByCreatPlayDate(int creatId, Date playDate);
	public List<Tuple> getCampaignCountGroupByMediumAdvertiserId(int mediumId);
	public int getCampaignCountByAdvertiserId(int advertiserId);

	
	//
	// for AdcAd
	//
	// Common
	public AdcAd getAd(int id);
	public void saveOrUpdate(AdcAd ad);
	public void deleteAd(AdcAd ad);
	public void deleteAds(List<AdcAd> ads);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdList(DataSourceRequest request);
	public DataSourceResult getAdList(DataSourceRequest request, int campaignId);

	// for DAO specific
	public AdcAd getAd(KnlMedium medium, String name);
	public List<AdcAd> getAdListByMediumId(int mediumId);
	public List<AdcAd> getAdListByCampaignId(int campaignId);
	public int getAdCountByCampaignId(int campaignId);
	public List<AdcAd> getAdList();
	public List<AdcAd> getAdListByMediumIdNameLike(int mediumId, String name);
	public List<Tuple> getAdCountGroupByMediumStatus(int mediumId);
	public List<AdcAd> getValidAdList();
	public List<Tuple> getAdGoalTypeCountByCampaignId(int campaignId);
	public Tuple getAdAccStatBeforePlayDate(int adId, Date playDate);
	public Tuple getAdAccStatBeforePlayDate(List<Integer> adIds, Date playDate);
	
	
	//
	// for AdcAdCreative
	//
	// Common
	public AdcAdCreative getAdCreative(int id);
	public void saveOrUpdate(AdcAdCreative adCreative);
	public void deleteAdCreative(AdcAdCreative adCreative);
	public void deleteAdCreatives(List<AdcAdCreative> adCreatives);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdCreativeList(DataSourceRequest request);

	// for DAO specific
	public List<AdcAdCreative> getAdCreativeListByAdId(int adId);
	public List<AdcAdCreative> getAdCreativeListByCreativeId(int creativeId);
	public int getAdCreativeCountByAdId(int adId);
	public List<AdcAdCreative> getCandiAdCreativeListByMediumIdDate(int mediumId, Date sDate, Date eDate);
	public AdcAdCreative getEffAdCreative(int id, int mediumId, Date sDate, Date eDate);
	public List<AdcAdCreative> getActiveAdCreativeListByAdId(int adId);
	public List<AdcAdCreative> getActiveAdCreativeListByCampaignId(int campaignId);
	public List<AdcAdCreative> getPlCandiAdCreativeListByMediumIdDate(int mediumId, Date date);
	public List<Tuple> getAdCreativeTupleListIn(List<Integer> ids);


	//
	// for AdcCreatTarget
	//
	// Common
	public AdcCreatTarget getCreatTarget(int id);
	public void saveOrUpdate(AdcCreatTarget creatTarget);
	public void deleteCreatTarget(AdcCreatTarget creatTarget);
	public void deleteCreatTargets(List<AdcCreatTarget> creatTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getCreatTargetList(DataSourceRequest request);

	// for DAO specific
	public List<AdcCreatTarget> getCreatTargetListByCreativeId(int creativeId);
	public void saveAndReorderCreatTarget(AdcCreatTarget creatTarget);
	public List<Tuple> getCreatTargetCountGroupByMediumCreativeId(int mediumId);
	public List<Tuple> getCreatTargetCountGroupByCreativeId();
	public List<AdcCreatTarget> getCreatTargetListByMediumId(int mediumId);

	
	//
	// for AdcAdTarget
	//
	// Common
	public AdcAdTarget getAdTarget(int id);
	public void saveOrUpdate(AdcAdTarget adTarget);
	public void deleteAdTarget(AdcAdTarget adTarget);
	public void deleteAdTargets(List<AdcAdTarget> adTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getAdTargetList(DataSourceRequest request);

	// for DAO specific
	public List<AdcAdTarget> getAdTargetListByAdId(int adId);
	public void saveAndReorderAdTarget(AdcAdTarget adTarget);
	public List<Tuple> getAdTargetCountGroupByMediumAdId(int mediumId);
	public List<Tuple> getAdTargetCountGroupByAdId();
	public List<AdcAdTarget> getAdTargetListByMediumId(int mediumId);

	
	//
	// for AdcPlaylist
	//
	// Common
	public AdcPlaylist getPlaylist(int id);
	public void saveOrUpdate(AdcPlaylist playlist);
	public void deletePlaylist(AdcPlaylist playlist);
	public void deletePlaylists(List<AdcPlaylist> playlists);

	// for Kendo Grid Remote Read
	public DataSourceResult getPlaylistList(DataSourceRequest request);
	public DataSourceResult getPlaylistList(DataSourceRequest request, String viewType);

	// for DAO specific
	public List<AdcPlaylist> getActivePlaylistListByChannelId(int channelId);
	public List<AdcPlaylist> getPlaylistListByChannelId(int channelId);

	
	//
	// for AdcMobTarget
	//
	// Common
	public AdcMobTarget getMobTarget(int id);
	public void saveOrUpdate(AdcMobTarget mobTarget);
	public void deleteMobTarget(AdcMobTarget mobTarget);
	public void deleteMobTargets(List<AdcMobTarget> mobTargets);

	// for Kendo Grid Remote Read

	// for DAO specific
	public List<AdcMobTarget> getMobTargetListByAdId(int adId);
	public void saveAndReorderMobTarget(AdcMobTarget mobTarget);
	public List<AdcMobTarget> getMobTargetListByMediumId(int mediumId);
	public int getMobTargetCountByAdId(int adId);
	public int getMobTargetCountByMobTypeTgtId(String mobType, int tgtId);
	
	
	//
	// for AdcMobTargetView
	//
	// Common

	// for Kendo Grid Remote Read
	public DataSourceResult getMobTargetViewList(DataSourceRequest request);

	// for DAO specific
	public List<Tuple> getMobTargetViewItemList();

	
	//
	// for Common
	//
	public int measureResolutionWithMedium(String resolution, int mediumId, int boundVal);
	public void refreshCampaignInfoBasedAds(int campaignId);
	public boolean refreshCampaignAdStatusBasedToday();
	public void deleteSoftCreatFile(AdcCreatFile creatFile, HttpSession session);
	public void deleteSoftCreative(AdcCreative creative, HttpSession session);
	public void deleteSoftCampaign(AdcCampaign campaign, HttpSession session);
	public void deleteSoftAd(AdcAd ad, HttpSession session);
	public void refreshAdCreativePeriodByAdDates(AdcAd ad, Date prevSDate, Date prevEDate);
	public List<Tuple> getChannelAdViewTypeTupleList();
	public List<Tuple> getChannelAdNoViewTypeTupleList();
	public List<Tuple> getChannelAdViewTypeTupleListByMediumId(int mediumId);
	public List<Tuple> getChannelAdNoViewTypeTupleListByMediumId(int mediumId);
	
}
