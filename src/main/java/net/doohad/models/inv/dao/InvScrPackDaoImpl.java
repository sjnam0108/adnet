package net.doohad.models.inv.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvScrPack;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;
import net.doohad.utils.Util;

@Transactional
@Component
public class InvScrPackDaoImpl implements InvScrPackDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvScrPack get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrPack> criteria = cb.createQuery(InvScrPack.class);
		Root<InvScrPack> oRoot = criteria.from(InvScrPack.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvScrPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvScrPack scrPack) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(scrPack);
	}

	@Override
	public void delete(InvScrPack scrPack) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), InvScrPack.class, scrPack.getId());
	}

	@Override
	public void delete(List<InvScrPack> scrPacks) {

		Session session = sessionFactory.getCurrentSession();
		
        for (InvScrPack scrPack : scrPacks) {
            session.delete(session.load(InvScrPack.class, scrPack.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvScrPack.class, map);
	}

	@Override
	public InvScrPack get(KnlMedium medium, String name) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrPack> criteria = cb.createQuery(InvScrPack.class);
		Root<InvScrPack> oRoot = criteria.from(InvScrPack.class);
		Join<InvScrPack, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), medium.getId()), 
				cb.equal(oRoot.get("name"), name)
		);

		List<InvScrPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<InvScrPack> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrPack> criteria = cb.createQuery(InvScrPack.class);
		Root<InvScrPack> oRoot = criteria.from(InvScrPack.class);
		Join<InvScrPack, KnlMedium> joinO = oRoot.join("medium");

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId))).getResultList();
	}

	@Override
	public List<InvScrPack> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvScrPack> criteria = cb.createQuery(InvScrPack.class);
		Root<InvScrPack> oRoot = criteria.from(InvScrPack.class);
		Join<InvScrPack, KnlMedium> joinO = oRoot.join("medium");
		
		Expression<Boolean> exp1 = cb.equal(joinO.get("id"), mediumId);
		Expression<Boolean> exp2 = cb.equal(oRoot.get("activeStatus"), activeStatus);

		return sessionFactory.getCurrentSession()
				.createQuery(criteria.select(oRoot).where(cb.and(exp1, exp2)))
				.getResultList();
	}

	@Override
	public List<InvScrPack> getListByMediumIdNameLike(int mediumId, String name) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<InvScrPack> criteria = cb.createQuery(InvScrPack.class);
		Root<InvScrPack> oRoot = criteria.from(InvScrPack.class);
		Join<InvScrPack, KnlMedium> joinO = oRoot.join("medium");

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

}
