package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.rev.RevDailyAchv;

@Transactional
@Component
public class RevDailyAchvDaoImpl implements RevDailyAchvDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevDailyAchv get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevDailyAchv> criteria = cb.createQuery(RevDailyAchv.class);
		Root<RevDailyAchv> oRoot = criteria.from(RevDailyAchv.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevDailyAchv> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevDailyAchv dailyAchv) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(dailyAchv);
	}

	@Override
	public void delete(RevDailyAchv dailyAchv) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevDailyAchv.class, dailyAchv.getId()));
	}

	@Override
	public void delete(List<RevDailyAchv> dailyAchves) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevDailyAchv dailyAchv : dailyAchves) {
            session.delete(session.load(RevDailyAchv.class, dailyAchv.getId()));
        }
	}

	@Override
	public RevDailyAchv getByTypeIdPlayDate(String type, int objId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevDailyAchv> criteria = cb.createQuery(RevDailyAchv.class);
		Root<RevDailyAchv> oRoot = criteria.from(RevDailyAchv.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("type"), type),
				cb.equal(oRoot.get("objId"), objId),
				cb.equal(oRoot.get("playDate"), playDate)
		);
		
		List<RevDailyAchv> list = sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevDailyAchv> getListByTypeId(String type, int objId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevDailyAchv> criteria = cb.createQuery(RevDailyAchv.class);
		Root<RevDailyAchv> oRoot = criteria.from(RevDailyAchv.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("type"), type),
				cb.equal(oRoot.get("objId"), objId)
		);
		criteria.orderBy(cb.asc(oRoot.get("playDate")));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
		
	}

}
