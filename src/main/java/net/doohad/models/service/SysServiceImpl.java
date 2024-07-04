package net.doohad.models.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.sys.SysAuditTrail;
import net.doohad.models.sys.SysAuditTrailValue;
import net.doohad.models.sys.SysOpt;
import net.doohad.models.sys.SysRtUnit;
import net.doohad.models.sys.SysSvcRespTime;
import net.doohad.models.sys.dao.SysAuditTrailDao;
import net.doohad.models.sys.dao.SysAuditTrailValueDao;
import net.doohad.models.sys.dao.SysOptDao;
import net.doohad.models.sys.dao.SysRtUnitDao;
import net.doohad.models.sys.dao.SysSvcRespTimeDao;
import net.doohad.models.sys.dao.SysTmpHrlyEventDao;
import net.doohad.models.sys.dao.SysTmpStatusLineDao;

@Transactional
@Service("sysService")
public class SysServiceImpl implements SysService {
	

	//
    // General
    //
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public void flush() {
		
		sessionFactory.getCurrentSession().flush();
	}

	
    
    //
    // DAO
    //
    @Autowired
    private SysRtUnitDao rtUnitDao;

    @Autowired
    private SysSvcRespTimeDao svcRespTimeDao;

    @Autowired
    private SysTmpStatusLineDao tmpStatusLineDao;

    @Autowired
    private SysTmpHrlyEventDao tmpHrlyEventDao;

    @Autowired
    private SysOptDao optDao;

    @Autowired
    private SysAuditTrailDao auditTrailDao;

    @Autowired
    private SysAuditTrailValueDao auditTrailValueDao;

    
    
	//
	// for SysRtUnitDao
	//
	@Override
	public SysRtUnit getRtUnit(int id) {
		return rtUnitDao.get(id);
	}

	@Override
	public void saveOrUpdate(SysRtUnit rtUnit) {
		rtUnitDao.saveOrUpdate(rtUnit);
	}

	@Override
	public void deleteRtUnit(SysRtUnit rtUnit) {
		rtUnitDao.delete(rtUnit);
	}

	@Override
	public void deleteRtUnits(List<SysRtUnit> rtUnits) {
		rtUnitDao.delete(rtUnits);
	}

	@Override
	public DataSourceResult getRtUnitList(DataSourceRequest request) {
		return rtUnitDao.getList(request);
	}

	@Override
	public SysRtUnit getRtUnit(String ukid) {
		return rtUnitDao.get(ukid);
	}

    
    
	//
	// for SysSvcRespTimeDao
	//
	@Override
	public SysSvcRespTime getSvcRespTime(int id) {
		return svcRespTimeDao.get(id);
	}

	@Override
	public void saveOrUpdate(SysSvcRespTime svcRespTime) {
		svcRespTimeDao.saveOrUpdate(svcRespTime);
	}

	@Override
	public void deleteSvcRespTime(SysSvcRespTime svcRespTime) {
		svcRespTimeDao.delete(svcRespTime);
	}

	@Override
	public void deleteSvcRespTimes(List<SysSvcRespTime> svcRespTimes) {
		svcRespTimeDao.delete(svcRespTimes);
	}

	@Override
	public DataSourceResult getSvcRespTimeList(DataSourceRequest request) {
		return svcRespTimeDao.getList(request);
	}

	@Override
	public SysSvcRespTime getSvcRespTime(SysRtUnit rtUnit, Date checkDate) {
		return svcRespTimeDao.get(rtUnit, checkDate);
	}

    
    
	//
	// for SysTmpStatusLineDao
	//
	@Override
	public void insertTmpStatusLine(int screenId, Date playDate, String statusLine) {
		tmpStatusLineDao.insert(screenId, playDate, statusLine);
	}

	@Override
	public void deleteTmpStatusLineBulkRowsInIds(List<Integer> ids) {
		tmpStatusLineDao.deleteBulkRowsInIds(ids);
	}

	@Override
	public List<Tuple> getTmpStatusLineTupleList() {
		return tmpStatusLineDao.getTupleList();
	}

    
    
	//
	// for SysTmpHrlyEventDao
	//
	@Override
	public void insertTmpHrlyEvent(int screenId, Date eventDate, int type) {
		tmpHrlyEventDao.insert(screenId, eventDate, type);
	}

	@Override
	public void deleteTmpHrlyEventBulkRowsInIds(List<Integer> ids) {
		tmpHrlyEventDao.deleteBulkRowsInIds(ids);
	}

	@Override
	public List<Tuple> getTmpHrlyEventTupleList() {
		return tmpHrlyEventDao.getTupleList();
	}

    
    
	//
	// for SysOptDao
	//

	@Override
	public SysOpt getOpt(int id) {
		return optDao.get(id);
	}

	@Override
	public void saveOrUpdate(SysOpt opt) {
		optDao.saveOrUpdate(opt);
	}

	@Override
	public void deleteOpt(SysOpt opt) {
		optDao.delete(opt);
	}

	@Override
	public void deleteOpts(List<SysOpt> opts) {
		optDao.delete(opts);
	}

	@Override
	public SysOpt getOpt(String code) {
		return optDao.get(code);
	}

    
    
	//
	// for SysAuditTrailDao
	//
	@Override
	public SysAuditTrail getAuditTrail(int id) {
		return auditTrailDao.get(id);
	}

	@Override
	public void saveOrUpdate(SysAuditTrail auditTrail) {
		auditTrailDao.saveOrUpdate(auditTrail);
	}

	@Override
	public void deleteAuditTrail(SysAuditTrail auditTrail) {
		auditTrailDao.delete(auditTrail);
	}

	@Override
	public void deleteAuditTrails(List<SysAuditTrail> auditTrails) {
		auditTrailDao.delete(auditTrails);
	}

	@Override
	public DataSourceResult getAuditTrailList(DataSourceRequest request, String objType, int objId) {
		return auditTrailDao.getList(request, objType, objId);
	}

    
    
	//
	// for SysAuditTrailValueDao
	//
	@Override
	public SysAuditTrailValue getAuditTrailValue(int id) {
		return auditTrailValueDao.get(id);
	}

	@Override
	public void saveOrUpdate(SysAuditTrailValue auditTrailValue) {
		auditTrailValueDao.saveOrUpdate(auditTrailValue);
	}

	@Override
	public void deleteAuditTrailValue(SysAuditTrailValue auditTrailValue) {
		auditTrailValueDao.delete(auditTrailValue);
	}

	@Override
	public void deleteAuditTrailValues(List<SysAuditTrailValue> auditTrailValues) {
		auditTrailValueDao.delete(auditTrailValues);
	}

	@Override
	public DataSourceResult getAuditTrailValueList(DataSourceRequest request, int auditTrailId) {
		return auditTrailValueDao.getList(request, auditTrailId);
	}

}
