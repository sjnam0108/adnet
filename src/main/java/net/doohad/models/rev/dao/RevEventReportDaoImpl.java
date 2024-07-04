package net.doohad.models.rev.dao;

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
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevEventReport;

@Transactional
@Component
public class RevEventReportDaoImpl implements RevEventReportDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevEventReport get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevEventReport> criteria = cb.createQuery(RevEventReport.class);
		Root<RevEventReport> oRoot = criteria.from(RevEventReport.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevEventReport> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevEventReport eventReport) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(eventReport);
	}

	@Override
	public void delete(RevEventReport eventReport) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevEventReport.class, eventReport.getId()));
	}

	@Override
	public void delete(List<RevEventReport> eventReports) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevEventReport eventReport : eventReports) {
            session.delete(session.load(RevEventReport.class, eventReport.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevEventReport.class, map);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int mediumId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
		Criterion criterion = Restrictions.eq("medium.id", mediumId);
				
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevEventReport.class, map, criterion);
	}

}
