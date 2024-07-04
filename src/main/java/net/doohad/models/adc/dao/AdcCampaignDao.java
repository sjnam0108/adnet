package net.doohad.models.adc.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcCampaign;
import net.doohad.models.knl.KnlMedium;

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
