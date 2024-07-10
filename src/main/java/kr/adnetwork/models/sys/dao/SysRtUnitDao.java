package kr.adnetwork.models.sys.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.sys.SysRtUnit;

public interface SysRtUnitDao {
	// Common
	public SysRtUnit get(int id);
	public void saveOrUpdate(SysRtUnit rtUnit);
	public void delete(SysRtUnit rtUnit);
	public void delete(List<SysRtUnit> rtUnits);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public SysRtUnit get(String ukid);

}
