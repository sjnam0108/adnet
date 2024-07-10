package kr.adnetwork.models.org.dao;

import java.util.HashMap;
import java.util.List;

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

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgRadRegion;
import kr.adnetwork.utils.SolUtil;

@Transactional
@Component
public class OrgRadRegionDaoImpl implements OrgRadRegionDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgRadRegion get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgRadRegion> criteria = cb.createQuery(OrgRadRegion.class);
		Root<OrgRadRegion> oRoot = criteria.from(OrgRadRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgRadRegion> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgRadRegion region) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(region);
	}

	@Override
	public void delete(OrgRadRegion region) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgRadRegion.class, region.getId());
	}

	@Override
	public void delete(List<OrgRadRegion> regions) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgRadRegion region : regions) {
            session.delete(session.load(OrgRadRegion.class, region.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgRadRegion.class, map);
	}

	@Override
	public DataSourceResult getActiveList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
		Criterion criterion = Restrictions.eq("activeStatus", true);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgRadRegion.class, map, criterion);
	}

	@Override
	public List<OrgRadRegion> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgRadRegion> criteria = cb.createQuery(OrgRadRegion.class);
		Root<OrgRadRegion> oRoot = criteria.from(OrgRadRegion.class);
		Join<OrgRadRegion, KnlMedium> joinO = oRoot.join("medium");

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId))).getResultList();
	}

	@Override
	public List<OrgRadRegion> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgRadRegion> criteria = cb.createQuery(OrgRadRegion.class);
		Root<OrgRadRegion> oRoot = criteria.from(OrgRadRegion.class);
		Join<OrgRadRegion, KnlMedium> joinO = oRoot.join("medium");
		
		Expression<Boolean> exp1 = cb.equal(joinO.get("id"), mediumId);
		Expression<Boolean> exp2 = cb.equal(oRoot.get("activeStatus"), activeStatus);

		return sessionFactory.getCurrentSession()
				.createQuery(criteria.select(oRoot).where(cb.and(exp1, exp2)))
				.getResultList();
	}

}
