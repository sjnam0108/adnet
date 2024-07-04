package net.doohad.models.sys.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.sys.SysAuditTrail;

@Transactional
@Component
public class SysAuditTrailDaoImpl implements SysAuditTrailDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public SysAuditTrail get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysAuditTrail> criteria = cb.createQuery(SysAuditTrail.class);
		Root<SysAuditTrail> oRoot = criteria.from(SysAuditTrail.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<SysAuditTrail> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(SysAuditTrail auditTrail) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(auditTrail);
	}

	@Override
	public void delete(SysAuditTrail auditTrail) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(SysAuditTrail.class, auditTrail.getId()));
	}

	@Override
	public void delete(List<SysAuditTrail> auditTrails) {
		Session session = sessionFactory.getCurrentSession();
		
        for (SysAuditTrail auditTrail : auditTrails) {
            session.delete(session.load(SysAuditTrail.class, auditTrail.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, String objType, int objId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		
		Criterion criterion = Restrictions.and(
				Restrictions.eq("objType", objType),
				Restrictions.eq("objId", objId));

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), SysAuditTrail.class, map, criterion);
	}

}
