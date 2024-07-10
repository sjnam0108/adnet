package kr.adnetwork.models.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.sys.SysAuditTrail;
import kr.adnetwork.models.sys.SysAuditTrailValue;
import kr.adnetwork.models.sys.SysOpt;
import kr.adnetwork.models.sys.SysRtUnit;
import kr.adnetwork.models.sys.SysSvcRespTime;

@Transactional
public interface SysService {
	
	// Common
	public void flush();

	
	//
	// for SysRtUnit
	//
	// Common
	public SysRtUnit getRtUnit(int id);
	public void saveOrUpdate(SysRtUnit rtUnit);
	public void deleteRtUnit(SysRtUnit rtUnit);
	public void deleteRtUnits(List<SysRtUnit> rtUnits);

	// for Kendo Grid Remote Read
	public DataSourceResult getRtUnitList(DataSourceRequest request);

	// for DAO specific
	public SysRtUnit getRtUnit(String ukid);

	
	//
	// for SysSvcRespTime
	//
	// Common
	public SysSvcRespTime getSvcRespTime(int id);
	public void saveOrUpdate(SysSvcRespTime svcRespTime);
	public void deleteSvcRespTime(SysSvcRespTime svcRespTime);
	public void deleteSvcRespTimes(List<SysSvcRespTime> svcRespTimes);

	// for Kendo Grid Remote Read
	public DataSourceResult getSvcRespTimeList(DataSourceRequest request);

	// for DAO specific
	public SysSvcRespTime getSvcRespTime(SysRtUnit rtUnit, Date checkDate);

	
	//
	// for SysTmpStatusLine
	//
	// Common

	// for Kendo Grid Remote Read

	// for DAO specific
	public void insertTmpStatusLine(int screenId, Date playDate, String statusLine);
	public void deleteTmpStatusLineBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getTmpStatusLineTupleList();

	
	//
	// for SysTmpHrlyEvent
	//
	// Common

	// for Kendo Grid Remote Read

	// for DAO specific
	public void insertTmpHrlyEvent(int screenId, Date eventDate, int type);
	public void deleteTmpHrlyEventBulkRowsInIds(List<Integer> ids);
	public List<Tuple> getTmpHrlyEventTupleList();

	
	//
	// for SysOpt
	//
	// Common
	public SysOpt getOpt(int id);
	public void saveOrUpdate(SysOpt opt);
	public void deleteOpt(SysOpt opt);
	public void deleteOpts(List<SysOpt> opts);

	// for Kendo Grid Remote Read

	// for DAO specific
	public SysOpt getOpt(String code);

	
	//
	// for SysAuditTrail
	//
	// Common
	public SysAuditTrail getAuditTrail(int id);
	public void saveOrUpdate(SysAuditTrail auditTrail);
	public void deleteAuditTrail(SysAuditTrail auditTrail);
	public void deleteAuditTrails(List<SysAuditTrail> auditTrails);

	// for Kendo Grid Remote Read
	public DataSourceResult getAuditTrailList(DataSourceRequest request, String objType, int objId);

	// for DAO specific

	
	//
	// for SysAuditTrailValue
	//
	// Common
	public SysAuditTrailValue getAuditTrailValue(int id);
	public void saveOrUpdate(SysAuditTrailValue auditTrailValue);
	public void deleteAuditTrailValue(SysAuditTrailValue auditTrailValue);
	public void deleteAuditTrailValues(List<SysAuditTrailValue> auditTrailValues);

	// for Kendo Grid Remote Read
	public DataSourceResult getAuditTrailValueList(DataSourceRequest request, int auditTrailId);

	// for DAO specific
	
}
