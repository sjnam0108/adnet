package kr.adnetwork.models.adc.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.knl.KnlMedium;

public interface AdcAdDao {
	// Common
	public AdcAd get(int id);
	public void saveOrUpdate(AdcAd ad);
	public void delete(AdcAd ad);
	public void delete(List<AdcAd> ads);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, int campaignId);

	// for DAO specific
	public AdcAd get(KnlMedium medium, String name);
	public List<AdcAd> getListByMediumId(int mediumId);
	public List<AdcAd> getListByCampaignId(int campaignId);
	public int getCountByCampaignId(int campaignId);
	public List<AdcAd> getList();
	public List<AdcAd> getListByMediumIdNameLike(int mediumId, String name);
	public List<Tuple> getCountGroupByMediumStatus(int mediumId);
	public List<AdcAd> getValidList();
	public List<Tuple> getGoalTypeCountByCampaignId(int campaignId);
	public Tuple getAccStatBeforePlayDate(int adId, Date playDate);
	public Tuple getAccStatBeforePlayDate(List<Integer> adIds, Date playDate);
}
