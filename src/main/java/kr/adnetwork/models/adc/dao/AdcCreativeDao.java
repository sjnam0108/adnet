package kr.adnetwork.models.adc.dao;

import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcCreative;

public interface AdcCreativeDao {
	// Common
	public AdcCreative get(int id);
	public void saveOrUpdate(AdcCreative creative);
	public void delete(AdcCreative creative);
	public void delete(List<AdcCreative> creatives);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getPendApprList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, int advertiserId);

	// for DAO specific
	public List<AdcCreative> getListByMediumIdNameLike(int mediumId, String name);
	public List<Tuple> getCountGroupByMediumAdvertiserId(int mediumId);
	public List<AdcCreative> getListByAdvertiserId(int advertiserId);
	public List<AdcCreative> getListByAdvertiserIdViewTypeCode(int advertiserId, String viewTypeCode);
	public List<AdcCreative> getValidList();
	public List<AdcCreative> getValidFallbackListByMediumId(int mediumId);
	public List<AdcCreative> getListByMediumIdName(int mediumId, String name);
	public int getCountByAdvertiserId(int advertiserId);
	public List<Tuple> getIdListByCampaignId(int campaignId);
	public List<AdcCreative> getListByMediumId(int mediumId);

}
