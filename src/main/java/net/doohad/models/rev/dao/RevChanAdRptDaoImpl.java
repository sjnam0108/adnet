package net.doohad.models.rev.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
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
import net.doohad.models.rev.RevChanAdRpt;
import net.doohad.utils.Util;

@Transactional
@Component
public class RevChanAdRptDaoImpl implements RevChanAdRptDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevChanAdRpt get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevChanAdRpt> criteria = cb.createQuery(RevChanAdRpt.class);
		Root<RevChanAdRpt> oRoot = criteria.from(RevChanAdRpt.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevChanAdRpt> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevChanAdRpt chanAdRpt) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(chanAdRpt);
	}

	@Override
	public void delete(RevChanAdRpt chanAdRpt) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevChanAdRpt.class, chanAdRpt.getId()));
	}

	@Override
	public void delete(List<RevChanAdRpt> chanAdRpts) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevChanAdRpt chanAdRpt : chanAdRpts) {
            session.delete(session.load(RevChanAdRpt.class, chanAdRpt.getId()));
        }
	}

	@Override
	public int getCount() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT count(*) FROM rev_chan_ad_rpts
		//
		String sql = "SELECT count(*) FROM rev_chan_ad_rpts";
		
		Tuple tuple = session.createNativeQuery(sql, Tuple.class)
				.getSingleResult();
		
		return ((BigInteger) tuple.get(0)).intValue();
	}

	@Override
	public int deleteBefore(Date date) {
		
		int beforeRows = getCount();

		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		DELETE FROM rev_chan_ad_rpts
		//      WHERE play_begin_date < :date LIMIT 10000
		//
		String sql = "DELETE FROM rev_chan_ad_rpts WHERE play_begin_date < :date LIMIT 10000";
		
		session.createNativeQuery(sql)
				.setParameter("date", date)
				.executeUpdate();
		
		int diff = beforeRows - getCount();
		
		return diff < 0 ? 0 : diff;
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, String type, int objId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		
		Criterion criterion = Restrictions.and(
				Restrictions.eq("type", type),
				Restrictions.eq("objId", objId),
				Restrictions.ge("realBeginDate", Util.addDays(new Date(), -1)));
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevChanAdRpt.class, map, criterion);
	}

}
