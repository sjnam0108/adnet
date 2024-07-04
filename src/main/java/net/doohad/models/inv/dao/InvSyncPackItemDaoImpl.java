package net.doohad.models.inv.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import net.doohad.models.inv.InvSyncPackItem;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class InvSyncPackItemDaoImpl implements InvSyncPackItemDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvSyncPackItem get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPackItem> criteria = cb.createQuery(InvSyncPackItem.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvSyncPackItem> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvSyncPackItem item) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(item);
	}

	@Override
	public void delete(InvSyncPackItem item) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), InvSyncPackItem.class, item.getId());
	}

	@Override
	public void delete(List<InvSyncPackItem> items) {

		Session session = sessionFactory.getCurrentSession();
		
        for (InvSyncPackItem item : items) {
            session.delete(session.load(InvSyncPackItem.class, item.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int syncPackId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("syncPack", InvSyncPack.class);
		
		Criterion criterion = Restrictions.eq("syncPack.id", syncPackId);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvSyncPackItem.class, map, criterion);
	}

	@Override
	public InvSyncPackItem getByScreenId(int screenId) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPackItem> criteria = cb.createQuery(InvSyncPackItem.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("screenId"), screenId)
		);

		List<InvSyncPackItem> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public int getCountBySyncPackId(int syncPackId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		Join<InvSyncPackItem, InvSyncPack> joinO = oRoot.join("syncPack");
		
		criteria.select(cb.count(oRoot)).where(
				cb.equal(joinO.get("id"), syncPackId)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public List<InvSyncPackItem> getListBySyncPackId(int syncPackId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPackItem> criteria = cb.createQuery(InvSyncPackItem.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		Join<InvSyncPackItem, InvSyncPack> joinO = oRoot.join("syncPack");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), syncPackId)
		);
		criteria.orderBy(cb.asc(oRoot.get("laneId")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public void reorder(int syncPackId) {
		
		List<InvSyncPackItem> list = getListBySyncPackId(syncPackId);
		
		int cnt = 1;
		for(InvSyncPackItem item : list) {
			item.setLaneId(cnt++);
			sessionFactory.getCurrentSession().saveOrUpdate(item);
		}
	}

	@Override
	public void saveAndReorder(InvSyncPackItem item) {
		
		saveOrUpdate(item);
		reorder(item.getSyncPack().getId());
	}

	@Override
	public List<InvSyncPackItem> getActiveParentListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPackItem> criteria = cb.createQuery(InvSyncPackItem.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		Join<InvSyncPackItem, InvSyncPack> join0 = oRoot.join("syncPack");
		Join<InvSyncPack, KnlMedium> join1 = join0.join("medium");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(join0.get("activeStatus"), true),
				cb.equal(join1.get("id"), mediumId)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public int getLaneIdByScreenId(int screenId) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPackItem> criteria = cb.createQuery(InvSyncPackItem.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("screenId"), screenId));

		List<InvSyncPackItem> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? 0 : list.get(0).getLaneId());
	}

	@Override
	public List<InvSyncPackItem> getList() {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPackItem> criteria = cb.createQuery(InvSyncPackItem.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		
		criteria.select(oRoot);
		
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public InvSyncPackItem getFirstLaneBySyncPackId(int syncPackId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvSyncPackItem> criteria = cb.createQuery(InvSyncPackItem.class);
		Root<InvSyncPackItem> oRoot = criteria.from(InvSyncPackItem.class);
		Join<InvSyncPackItem, InvSyncPack> joinO = oRoot.join("syncPack");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), syncPackId),
				cb.equal(oRoot.get("laneId"), 1)
		);

		List<InvSyncPackItem> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
