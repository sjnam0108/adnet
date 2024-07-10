package kr.adnetwork.models.org.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.org.OrgRadRegion;

public interface OrgRadRegionDao {
	// Common
	public OrgRadRegion get(int id);
	public void saveOrUpdate(OrgRadRegion region);
	public void delete(OrgRadRegion region);
	public void delete(List<OrgRadRegion> regions);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getActiveList(DataSourceRequest request);

	// for DAO specific
	public List<OrgRadRegion> getListByMediumId(int mediumId);
	public List<OrgRadRegion> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus);

}
