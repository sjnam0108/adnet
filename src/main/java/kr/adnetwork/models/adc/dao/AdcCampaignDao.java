package kr.adnetwork.models.adc.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.knl.KnlMedium;

public interface AdcCampaignDao {
	// Common
	public AdcCampaign get(int id);
	public void saveOrUpdate(AdcCampaign campaign);
	public void delete(AdcCampaign campaign);
	public void delete(List<AdcCampaign> campaigns);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, int mediumId);

	// for DAO specific
	public AdcCampaign get(KnlMedium medium, String name);
	public List<AdcCampaign> getListByMediumId(int mediumId);
	public List<AdcCampaign> getList();
	public List<AdcCampaign> getLisyByAdvertiserId(int advertiserId);
	public List<Tuple> getBudgetStatGroupByCampaignId();
	public List<Tuple> getIdsByCreatPlayDate(int creatId, Date playDate);
	public List<Tuple> getCountGroupByMediumAdvertiserId(int mediumId);
	public int getCountByAdvertiserId(int advertiserId);
}
