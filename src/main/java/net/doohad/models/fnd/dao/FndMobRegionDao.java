package net.doohad.models.fnd.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndMobRegion;

public interface FndMobRegionDao {
	// Common
	public FndMobRegion get(int id);
	public void saveOrUpdate(FndMobRegion region);
	public void delete(FndMobRegion region);
	public void delete(List<FndMobRegion> regions);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getActiveList(DataSourceRequest request);

	// for DAO specific
	public FndMobRegion get(String name);
	public List<FndMobRegion> getListByActiveStatus(boolean activeStatus);
}
