package kr.adnetwork.models.sys.dao;

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

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.sys.SysAuditTrailValue;

@Transactional
@Component
public class SysAuditTrailValueDaoImpl implements SysAuditTrailValueDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public SysAuditTrailValue get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysAuditTrailValue> criteria = cb.createQuery(SysAuditTrailValue.class);
		Root<SysAuditTrailValue> oRoot = criteria.from(SysAuditTrailValue.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<SysAuditTrailValue> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(SysAuditTrailValue auditTrailValue) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(auditTrailValue);
	}

	@Override
	public void delete(SysAuditTrailValue auditTrailValue) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(SysAuditTrailValue.class, auditTrailValue.getId()));
	}

	@Override
	public void delete(List<SysAuditTrailValue> auditTrailValues) {
		Session session = sessionFactory.getCurrentSession();
		
        for (SysAuditTrailValue auditTrailValue : auditTrailValues) {
            session.delete(session.load(SysAuditTrailValue.class, auditTrailValue.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int auditTrailId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("auditTrail", SysAuditTrailValue.class);
		
		Criterion criterion = Restrictions.eq("auditTrail.id", auditTrailId);

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), SysAuditTrailValue.class, map, criterion);
	}

}
