package kr.adnetwork.models.inv.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.knl.KnlMedium;

public interface InvScreenDao {
	// Common
	public InvScreen get(int id);
	public void saveOrUpdate(InvScreen screen);
	public void delete(InvScreen screen);
	public void delete(List<InvScreen> screens);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, int siteId);
	public DataSourceResult getMonitList(DataSourceRequest request);
	public DataSourceResult getMonitListByScreenIdIn(DataSourceRequest request, List<Integer> list);

	// for DAO specific
	public InvScreen get(KnlMedium medium, String shortName);
	public InvScreen getByName(KnlMedium medium, String name);
	public InvScreen getByMediumIdShortName(int mediumId, String shortName);
	public List<InvScreen> getListBySiteId(int siteId);
	public List<Tuple> getCountGroupByMediumSiteCondId(int mediumId);
	public List<InvScreen> getMonitListByMediumId(int mediumId);
	public List<InvScreen> getMonitList();
	public List<Tuple> getCountGroupByMediumResolution(int mediumId);
	public List<InvScreen> getMonitListByMediumNameLike(int mediumId, String name);
	public InvScreen getMonit(int id);
	public List<Integer> getMonitIdsByMediumId(int mediumId);
	public List<Tuple> getIdResoListByScreenIdIn(List<Integer> list);
	public List<InvScreen> getList();
	public void updateLastAdReportDate(int id, Date lastAdReportDate);
	public void updateLastAdRequestDate(int id, Date lastAdRequestDate);
	public List<InvScreen> getListByMediumIdNameLike(int mediumId, String name);
	
	public int getMonitCountByMediumRegionCodeIn(int mediumId, List<String> list);
	public int getMonitCountByMediumStateCodeIn(int mediumId, List<String> list);
	public int getMonitCountByMediumScreenIdIn(int mediumId, List<Integer> list);
	public int getMonitCountByMediumSiteIdIn(int mediumId, List<Integer> list);
	public int getMonitCountByMediumSiteCondCodeIn(int mediumId, List<String> list);
	
	public List<Integer> getMonitIdsByMediumRegionCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitIdsByMediumStateCodeIn(int mediumId, List<String> list);
	public List<Integer> getMonitIdsByMediumScreenIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitIdsByMediumSiteIdIn(int mediumId, List<Integer> list);
	public List<Integer> getMonitIdsByMediumSiteCondCodeIn(int mediumId, List<String> list);

	public int getAvailCountByMediumId(int mediumId);
	public int getActiveCountByMediumId(int mediumId);
}
