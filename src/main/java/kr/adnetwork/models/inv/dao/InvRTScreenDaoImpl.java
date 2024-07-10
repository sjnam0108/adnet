package kr.adnetwork.models.inv.dao;

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

import kr.adnetwork.models.inv.InvRTScreen;

@Transactional
@Component
public class InvRTScreenDaoImpl implements InvRTScreenDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvRTScreen get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvRTScreen> criteria = cb.createQuery(InvRTScreen.class);
		Root<InvRTScreen> oRoot = criteria.from(InvRTScreen.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvRTScreen> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvRTScreen rtScreen) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rtScreen);
	}

	@Override
	public void delete(InvRTScreen rtScreen) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(InvRTScreen.class, rtScreen.getId()));
	}

	@Override
	public void delete(List<InvRTScreen> rtScreens) {
		Session session = sessionFactory.getCurrentSession();
		
        for (InvRTScreen rtScreen : rtScreens) {
            session.delete(session.load(InvRTScreen.class, rtScreen.getId()));
        }
	}

	@Override
	public InvRTScreen getByScreenId(int screenId) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvRTScreen> criteria = cb.createQuery(InvRTScreen.class);
		Root<InvRTScreen> oRoot = criteria.from(InvRTScreen.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("screenId"), screenId));

		List<InvRTScreen> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Tuple> getCmdTupleListByMediumId(int mediumId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT s.screen_id, rt.next_cmd, rt.cmd_failed
		//		FROM inv_screens s, inv_rt_screens rt
		//		WHERE s.medium_id = :mediumId
		//		AND s.effective_start_date <= CURRENT_TIMESTAMP()
		//		AND (s.effective_end_date IS NULL OR s.effective_end_date >= CURRENT_TIMESTAMP())
		//		AND s.deleted = 0 AND s.active_status = 1 AND s.ad_server_available = 1
		//		AND s.screen_id = rt.screen_id
		//		AND rt.next_cmd <> ''
		//
		String sql = "SELECT s.screen_id, rt.next_cmd, rt.cmd_failed " +
					"FROM inv_screens s, inv_rt_screens rt " +
					"WHERE s.medium_id = :mediumId " +
					"AND s.effective_start_date <= CURRENT_TIMESTAMP() " +
					"AND (s.effective_end_date IS NULL OR s.effective_end_date >= CURRENT_TIMESTAMP()) " +
					"AND s.deleted = 0 AND s.active_status = 1 AND s.ad_server_available = 1 " +
					"AND s.screen_id = rt.screen_id " +
					"AND rt.next_cmd <> '' ";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("mediumId", mediumId)
				.getResultList();
	}

}
