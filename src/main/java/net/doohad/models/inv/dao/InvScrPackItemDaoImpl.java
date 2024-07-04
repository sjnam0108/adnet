package net.doohad.models.inv.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
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
import net.doohad.models.inv.InvScrPack;
import net.doohad.models.inv.InvScrPackItem;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class InvScrPackItemDaoImpl implements InvScrPackItemDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvScrPackItem get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrPackItem> criteria = cb.createQuery(InvScrPackItem.class);
		Root<InvScrPackItem> oRoot = criteria.from(InvScrPackItem.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvScrPackItem> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvScrPackItem item) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(item);
	}

	@Override
	public void delete(InvScrPackItem item) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), InvScrPackItem.class, item.getId());
	}

	@Override
	public void delete(List<InvScrPackItem> items) {

		Session session = sessionFactory.getCurrentSession();
		
        for (InvScrPackItem item : items) {
            session.delete(session.load(InvScrPackItem.class, item.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int scrPackId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("scrPack", InvScrPack.class);
		
		Criterion criterion = Restrictions.eq("scrPack.id", scrPackId);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvScrPackItem.class, map, criterion);
	}

	@Override
	public InvScrPackItem get(InvScrPack scrPack, int screenId) {
		
		if (scrPack == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrPackItem> criteria = cb.createQuery(InvScrPackItem.class);
		Root<InvScrPackItem> oRoot = criteria.from(InvScrPackItem.class);
		Join<InvScrPackItem, InvScrPack> joinO = oRoot.join("scrPack");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), scrPack.getId()), 
				cb.equal(oRoot.get("screenId"), screenId)
		);

		List<InvScrPackItem> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public int getCountByScrPackId(int scrPackId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<InvScrPackItem> oRoot = criteria.from(InvScrPackItem.class);
		Join<InvScrPackItem, InvScrPack> joinO = oRoot.join("scrPack");
		
		criteria.select(cb.count(oRoot)).where(
				cb.equal(joinO.get("id"), scrPackId)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public List<Tuple> getScreenIdListByScrPackIdIn(List<Integer> ids) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT DISTINCT screen_id
		//		FROM inv_scr_pack_items
		//		WHERE scr_pack_id IN (:ids)
		//
		String sql = "SELECT DISTINCT screen_id " +
					"FROM inv_scr_pack_items " +
					"WHERE scr_pack_id IN (:ids)";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.getResultList();
	}

	@Override
	public List<InvScrPackItem> getListByScreenId(int screenId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrPackItem> criteria = cb.createQuery(InvScrPackItem.class);
		Root<InvScrPackItem> oRoot = criteria.from(InvScrPackItem.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("screenId"), screenId)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
