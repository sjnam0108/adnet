package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcCreative;
import net.doohad.models.rev.RevHourlyPlay;

public interface RevHourlyPlayDao {
	// Common
	public RevHourlyPlay get(int id);
	public void saveOrUpdate(RevHourlyPlay hourPlay);
	public void delete(RevHourlyPlay hourPlay);
	public void delete(List<RevHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public RevHourlyPlay get(AdcAd ad, AdcCreative creative, Date playDate);
	public List<Tuple> getStatGroupByAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getStatGroupByPlayDateAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getStatGroupByWeekDayAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getActualStatGroupByAdId();
	public List<Tuple> getActualStatGroupByCreatId();
	public List<Tuple> getActualStatGroupByCampaignId();
	public List<Tuple> getStatGroupByCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getStatGroupByPlayDateCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getStatGroupByWeekDayCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public Tuple getAccStatByAdIdPlayDate(int adId, Date playDate);
	public Tuple getAccStatByCampaignIdPlayDate(int campId, Date playDate);
	public List<Tuple> getCampIdListByMediumPlayDate(int mediumId, Date playDate);
	public List<Tuple> getAdIdListByMediumPlayDate(int mediumId, Date playDate);
	public List<Tuple> getCreatIdListByMediumPlayDate(int mediumId, Date playDate);
	public List<Tuple> getStatGroupByCampIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getActualStatGroupByAdIdByPlayDate(Date playDate);
	public List<Tuple> getActualStatGroupByCampaignIdByPlayDate(Date playDate);
	public boolean deleteByMediumIdPlayDate(int mediumId, List<Date> playDates);
	
}
