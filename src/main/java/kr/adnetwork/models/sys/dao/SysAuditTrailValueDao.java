package kr.adnetwork.models.sys.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.sys.SysAuditTrailValue;

public interface SysAuditTrailValueDao {
	// Common
	public SysAuditTrailValue get(int id);
	public void saveOrUpdate(SysAuditTrailValue auditTrailValue);
	public void delete(SysAuditTrailValue auditTrailValue);
	public void delete(List<SysAuditTrailValue> auditTrailValues);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, int auditTrailId);

	// for DAO specific

}
