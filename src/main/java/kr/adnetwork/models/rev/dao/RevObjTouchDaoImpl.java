package kr.adnetwork.models.rev.dao;

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

import kr.adnetwork.models.rev.RevObjTouch;

@Transactional
@Component
public class RevObjTouchDaoImpl implements RevObjTouchDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevObjTouch get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevObjTouch> criteria = cb.createQuery(RevObjTouch.class);
		Root<RevObjTouch> oRoot = criteria.from(RevObjTouch.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevObjTouch> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevObjTouch objTouch) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(objTouch);
	}

	@Override
	public void delete(RevObjTouch objTouch) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevObjTouch.class, objTouch.getId()));
	}

	@Override
	public void delete(List<RevObjTouch> objTouches) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevObjTouch objTouch : objTouches) {
            session.delete(session.load(RevObjTouch.class, objTouch.getId()));
        }
	}

	@Override
	public RevObjTouch get(String type, int objId) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevObjTouch> criteria = cb.createQuery(RevObjTouch.class);
		Root<RevObjTouch> oRoot = criteria.from(RevObjTouch.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("type"), type),
				cb.equal(oRoot.get("objId"), objId)
		);

		List<RevObjTouch> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevObjTouch> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevObjTouch> criteria = cb.createQuery(RevObjTouch.class);
		Root<RevObjTouch> oRoot = criteria.from(RevObjTouch.class);
		
		criteria.select(oRoot);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getLastListIn(List<Integer> ids, int lastN) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT s.name, GREATEST(IFNULL(ot.date1, 0), IFNULL(ot.date2, 0), IFNULL(ot.date3, 0), IFNULL(ot.date4, 0), 
		//             IFNULL(ot.date5, 0), IFNULL(ot.date6, 0), IFNULL(ot.date7, 0)) as max_date
		//		FROM inv_screens s, rev_obj_touches ot
		//		WHERE s.screen_id = ot.obj_id AND ot.type = 'S'
		//		AND s.screen_id in (:ids)
		//		ORDER BY 2 desc, 1 asc LIMIT :lastN
		//
		String sql = "SELECT s.name, GREATEST(IFNULL(ot.date1, 0), IFNULL(ot.date2, 0), IFNULL(ot.date3, 0), IFNULL(ot.date4, 0), " +
					"IFNULL(ot.date5, 0), IFNULL(ot.date6, 0), IFNULL(ot.date7, 0)) as max_date " +
					"FROM inv_screens s, rev_obj_touches ot " +
					"WHERE s.screen_id = ot.obj_id AND ot.type = 'S' " +
					"AND s.screen_id IN (:ids) " +
					"ORDER BY 2 desc, 1 asc LIMIT :lastN";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("lastN", lastN)
				.getResultList();
	}

}
