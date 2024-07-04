package net.doohad.models.sys.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.sys.SysAuditTrail;

public interface SysAuditTrailDao {
	// Common
	public SysAuditTrail get(int id);
	public void saveOrUpdate(SysAuditTrail auditTrail);
	public void delete(SysAuditTrail auditTrail);
	public void delete(List<SysAuditTrail> auditTrails);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, String objType, int objId);

	// for DAO specific

}
