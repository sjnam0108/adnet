package kr.adnetwork.models.fnd.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.fnd.FndLoginLog;

public interface FndLoginLogDao {
	// Common
	public FndLoginLog get(int id);
	public void saveOrUpdate(FndLoginLog loginLog);
	public void delete(FndLoginLog loginLog);
	public void delete(List<FndLoginLog> loginLogs);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndLoginLog getLastByUserId(int userId);

}
