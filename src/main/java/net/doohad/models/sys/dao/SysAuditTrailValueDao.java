package net.doohad.models.sys.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.sys.SysAuditTrailValue;

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
