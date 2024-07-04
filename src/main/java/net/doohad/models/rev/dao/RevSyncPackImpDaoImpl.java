package net.doohad.models.rev.dao;

import java.math.BigInteger;
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

import net.doohad.models.rev.RevSyncPackImp;
import net.doohad.utils.Util;

@Transactional
@Component
public class RevSyncPackImpDaoImpl implements RevSyncPackImpDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevSyncPackImp get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevSyncPackImp> criteria = cb.createQuery(RevSyncPackImp.class);
		Root<RevSyncPackImp> oRoot = criteria.from(RevSyncPackImp.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevSyncPackImp> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevSyncPackImp syncPackImp) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(syncPackImp);
	}

	@Override
	public void delete(RevSyncPackImp syncPackImp) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevSyncPackImp.class, syncPackImp.getId()));
	}

	@Override
	public void delete(List<RevSyncPackImp> syncPackImps) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevSyncPackImp syncPackImp : syncPackImps) {
            session.delete(session.load(RevSyncPackImp.class, syncPackImp.getId()));
        }
	}

	@Override
	public List<Tuple> getLogTupleListByTime(String unit) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT short_name, grade, count(*)
		//		FROM rev_sync_pack_imps
		//      WHERE type = 'L'
		//      AND start_date >= date_add(NOW(), INTERVAL -5 MINUTE)
		//		GROUP BY short_name, grade
		//

		String sql = "SELECT short_name, grade, count(*) " +
					"FROM rev_sync_pack_imps " +
					"WHERE type = 'L' ";
		
		if (Util.isValid(unit)) {
			if (unit.equals("MINUTE")) {
				sql += "AND start_date >= date_add(NOW(), INTERVAL -5 MINUTE) ";
			} else if (unit.equals("HOUR")) {
				sql += "AND start_date >= date_add(NOW(), INTERVAL -1 HOUR) ";
			} else if (unit.equals("DAY")) {
				sql += "AND start_date >= date_add(NOW(), INTERVAL -1 DAY) ";
			} else if (unit.equals("TODAY")) {
				sql += "AND start_date >= CURRENT_DATE() ";
			}
		}

		sql +=      "GROUP BY short_name, grade";
		
		return session.createNativeQuery(sql, Tuple.class).getResultList();
	}

	@Override
	public List<Tuple> getControlTupleListByTime(String unit) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT short_name, type_name, count(*)
		//		FROM rev_sync_pack_imps
		//      WHERE type = 'C'
		//      AND start_date >= date_add(NOW(), INTERVAL -5 MINUTE)
		//		GROUP BY short_name, type_name
		//

		String sql = "SELECT short_name, type_name, count(*) " +
					"FROM rev_sync_pack_imps " +
					"WHERE type = 'C' ";
		
		if (Util.isValid(unit)) {
			if (unit.equals("MINUTE")) {
				sql += "AND start_date >= date_add(NOW(), INTERVAL -5 MINUTE) ";
			} else if (unit.equals("HOUR")) {
				sql += "AND start_date >= date_add(NOW(), INTERVAL -1 HOUR) ";
			} else if (unit.equals("DAY")) {
				sql += "AND start_date >= date_add(NOW(), INTERVAL -1 DAY) ";
			} else if (unit.equals("TODAY")) {
				sql += "AND start_date >= CURRENT_DATE() ";
			}
		}

		sql +=      "GROUP BY short_name, type_name";
		
		return session.createNativeQuery(sql, Tuple.class).getResultList();
	}

	@Override
	public List<Tuple> getLastTupleListGroupByShortName() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT short_name, diff, grade_queue, count_queue, ad, start_date
		//		FROM rev_sync_pack_imps
		//      WHERE sync_pack_imp_id IN (
		//        SELECT MAX(sync_pack_imp_id)
		//		  FROM rev_sync_pack_imps
		//        WHERE type = 'L'
		//        GROUP BY short_name)
		//

		String sql = "SELECT short_name, diff, grade_queue, count_queue, ad, start_date " +
					"FROM rev_sync_pack_imps " +
					"WHERE sync_pack_imp_id IN ( " +
					"SELECT MAX(sync_pack_imp_id) " +
					"FROM rev_sync_pack_imps " +
					"WHERE type = 'L' " +
					"GROUP BY short_name)";
		
		return session.createNativeQuery(sql, Tuple.class).getResultList();
	}

	@Override
	public RevSyncPackImp getLastByShortName(String shortName) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevSyncPackImp> criteria = cb.createQuery(RevSyncPackImp.class);
		Root<RevSyncPackImp> oRoot = criteria.from(RevSyncPackImp.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("shortName"), shortName))
				.orderBy(cb.desc(oRoot.get("id")));

		List<RevSyncPackImp> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public int getCount() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT count(*) FROM rev_sync_pack_imps
		//
		String sql = "SELECT count(*) FROM rev_sync_pack_imps";
		
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
		//		DELETE FROM rev_sync_pack_imps
		//      WHERE start_date < :date LIMIT 10000
		//
		String sql = "DELETE FROM rev_sync_pack_imps WHERE start_date < :date LIMIT 10000";
		
		session.createNativeQuery(sql)
				.setParameter("date", date)
				.executeUpdate();
		
		int diff = beforeRows - getCount();
		
		return diff < 0 ? 0 : diff;
	}

}
