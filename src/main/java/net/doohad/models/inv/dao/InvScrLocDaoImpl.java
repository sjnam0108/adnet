package net.doohad.models.inv.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.inv.InvScrLoc;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class InvScrLocDaoImpl implements InvScrLocDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvScrLoc get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrLoc> criteria = cb.createQuery(InvScrLoc.class);
		Root<InvScrLoc> oRoot = criteria.from(InvScrLoc.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvScrLoc> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvScrLoc scrLoc) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(scrLoc);
	}

	@Override
	public void delete(InvScrLoc scrLoc) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), InvScrLoc.class, scrLoc.getId());
	}

	@Override
	public void delete(List<InvScrLoc> scrLocs) {

		Session session = sessionFactory.getCurrentSession();
		
        for (InvScrLoc srcLoc : scrLocs) {
            session.delete(session.load(InvScrLoc.class, srcLoc.getId()));
        }
	}

	@Override
	public InvScrLoc getLastByScreenId(int screenId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrLoc> criteria = cb.createQuery(InvScrLoc.class);
		Root<InvScrLoc> oRoot = criteria.from(InvScrLoc.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("screenId"), screenId))
				.orderBy(cb.desc(oRoot.get("time1")));

		List<InvScrLoc> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<InvScrLoc> getListByScreenIdDate(int screenId, Date date) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<InvScrLoc> criteria = cb.createQuery(InvScrLoc.class);
		Root<InvScrLoc> oRoot = criteria.from(InvScrLoc.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("screenId"), screenId),
				cb.equal(
					cb.function("DATE_FORMAT", String.class, oRoot.get("time1"), cb.literal("%Y%m%d")), 
					Util.toSimpleString(date, "yyyyMMdd")
				)
		).orderBy(cb.asc(oRoot.get("time1")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Date> getDateListByScreenId(int screenId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT distinct DATE_FORMAT(time_1, '%Y-%m-%d')
		//		FROM inv_scr_locs
		//		WHERE screen_id = :screenId
		//
		String sql = "SELECT distinct DATE_FORMAT(time_1, '%Y-%m-%d') " +
					"FROM inv_scr_locs " +
					"WHERE screen_id = :screenId";
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId)
				.getResultList();

		ArrayList<Date> dateList = new ArrayList<Date>();
		for(Tuple tuple : list) {
			dateList.add(Util.parseDate((String)tuple.get(0)));
		}
		
		return dateList;
	}

}
