package net.doohad.models.inv.dao;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
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
import net.doohad.models.inv.InvSyncPack;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class InvSyncPackDaoImpl implements InvSyncPackDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvSyncPack get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvSyncPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvSyncPack syncPack) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(syncPack);
	}

	@Override
	public void delete(InvSyncPack syncPack) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), InvSyncPack.class, syncPack.getId());
	}

	@Override
	public void delete(List<InvSyncPack> syncPacks) {

		Session session = sessionFactory.getCurrentSession();
		
        for (InvSyncPack syncPack : syncPacks) {
            session.delete(session.load(InvSyncPack.class, syncPack.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvSyncPack.class, map);
	}

	@Override
	public InvSyncPack get(KnlMedium medium, String name) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);
		Join<InvSyncPack, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), medium.getId()), 
				cb.equal(oRoot.get("name"), name)
		);

		List<InvSyncPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<InvSyncPack> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);
		Join<InvSyncPack, KnlMedium> joinO = oRoot.join("medium");

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId))).getResultList();
	}

	@Override
	public List<InvSyncPack> getListByMediumIdNameLike(int mediumId, String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);
		Join<InvSyncPack, KnlMedium> joinO = oRoot.join("medium");

		Expression<Boolean> expW1 = cb.equal(joinO.get("id"), mediumId);
		
		
		if (Util.isValid(name)) {
			return session.createQuery(criteria.select(oRoot).where(
					cb.and(
							cb.like(oRoot.get("name"), "%" + name + "%"), 
							expW1)
					))
					.getResultList();
		} else {
			return session.createQuery(criteria.select(oRoot).where(
					expW1)).getResultList();
		}
	}

	@Override
	public List<InvSyncPack> getEffListByAdSelSecs(int adSelSecs) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
					cb.equal(oRoot.get("adSelSecs"), adSelSecs)
				)).getResultList();
	}

	@Override
	public int getActiveCountByMediumId(int mediumId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT count(*) FROM inv_sync_packs
		//		WHERE medium_id = :mediumId
		//		AND active_status = 1
		//
		String sql = "SELECT count(*) FROM inv_sync_packs " +
					"WHERE medium_id = :mediumId " +
					"AND active_status = 1";
		
		Tuple tuple = session.createNativeQuery(sql, Tuple.class)
				.setParameter("mediumId", mediumId)
				.getSingleResult();
		
		return ((BigInteger) tuple.get(0)).intValue();
	}

	@Override
	public List<InvSyncPack> getActiveList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("activeStatus"), true)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public DataSourceResult getListBySyncPackIdIn(DataSourceRequest request, List<Integer> list) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
		Criterion criterion = Restrictions.in("id", list);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvSyncPack.class, map, criterion);
	}

	@Override
	public InvSyncPack getActiveByShortName(String shortName) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("shortName"), shortName), 
				cb.equal(oRoot.get("activeStatus"), true)
		);

		List<InvSyncPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public InvSyncPack getByShortName(String shortName) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPack> criteria = cb.createQuery(InvSyncPack.class);
		Root<InvSyncPack> oRoot = criteria.from(InvSyncPack.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("shortName"), shortName)
		);

		List<InvSyncPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
