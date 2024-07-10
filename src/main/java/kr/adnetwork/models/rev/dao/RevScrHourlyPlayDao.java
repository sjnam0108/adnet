package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.rev.RevScrHourlyPlay;

public interface RevScrHourlyPlayDao {
	// Common
	public RevScrHourlyPlay get(int id);
	public void saveOrUpdate(RevScrHourlyPlay hourPlay);
	public void delete(RevScrHourlyPlay hourPlay);
	public void delete(List<RevScrHourlyPlay> hourPlays);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, int adId, Date sDate, Date eDate);

	// for DAO specific
	public RevScrHourlyPlay get(InvScreen screen, AdcAdCreative adCreative, Date playDate);
	public RevScrHourlyPlay get(int screenId, int adCreativeId, Date playDate);
	public List<RevScrHourlyPlay> getListBySiteIdAdIdPlayDate(
			int siteId, int adId, Date playDate);
	public List<Tuple> getScrSumListByPlayDate(Date playDate);
	public List<Tuple> getSitSumListByPlayDate(Date playDate);
	public List<Tuple> getPlayDateListByLastUpdateDate(Date date);
	public List<RevScrHourlyPlay> getListByAdIdPlayDate(int adId, Date playDate);
	public List<Tuple> getAdStatListByScreenIdPlayDate(int screenId, Date playDate);
	public List<Tuple> getStatGroupByAdCreatPlayDate(Date playDate);
	public List<Tuple> getScrCntGroupByAdIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrCntGroupByCreatIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrCntGroupByPlayDateAdIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrCntGroupByPlayDateCreatIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrCntGroupByWeekDayAdIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<Tuple> getScrCntGroupByWeekDayCreatIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public List<RevScrHourlyPlay> getListByPlayDate(Date playDate);
	public List<Tuple> getUpdateCpmListByPlayDate(Date playDate);
	public void updateCpmValues(int id, int floorCpm, int adCpm);
	public int getScrCntAdIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public int getScrCntCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate);
	public List<Tuple> getScrCntGroupByCampIdInBetween(List<Integer> ids, 
			Date startDate, Date endDate);
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate);
	public boolean deleteByAdIdScreenIdPlayDate(Date sDate, Date eDate, 
			List<Integer> adIds, List<Integer> screenIds);
	public List<Tuple> getPlayDateListByAdIdScreenIdPlayDate(Date sDate, Date eDate, 
			List<Integer> adIds, List<Integer> screenIds);
}
