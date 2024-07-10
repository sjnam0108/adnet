package kr.adnetwork.models.sys.dao;

import java.util.Date;
import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.sys.SysRtUnit;
import kr.adnetwork.models.sys.SysSvcRespTime;

public interface SysSvcRespTimeDao {
	// Common
	public SysSvcRespTime get(int id);
	public void saveOrUpdate(SysSvcRespTime svcRespTime);
	public void delete(SysSvcRespTime svcRespTime);
	public void delete(List<SysSvcRespTime> svcRespTimes);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public SysSvcRespTime get(SysRtUnit rtUnit, Date checkDate);

}
