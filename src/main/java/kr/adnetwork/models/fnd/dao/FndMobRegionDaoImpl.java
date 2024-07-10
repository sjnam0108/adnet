package kr.adnetwork.models.fnd.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import kr.adnetwork.models.fnd.FndMobRegion;

@Transactional
@Component
public class FndMobRegionDaoImpl implements FndMobRegionDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndMobRegion get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndMobRegion> criteria = cb.createQuery(FndMobRegion.class);
		Root<FndMobRegion> oRoot = criteria.from(FndMobRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndMobRegion> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndMobRegion region) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(region);
	}

	@Override
	public void delete(FndMobRegion region) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndMobRegion.class, region.getId()));
	}

	@Override
	public void delete(List<FndMobRegion> regions) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndMobRegion region : regions) {
            session.delete(session.load(FndMobRegion.class, region.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndMobRegion.class);
	}

	@Override
	public DataSourceResult getActiveList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		
		Criterion criterion = Restrictions.eq("activeStatus", true);

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndMobRegion.class, map, criterion);
	}

	@Override
	public FndMobRegion get(String name) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndMobRegion> criteria = cb.createQuery(FndMobRegion.class);
		Root<FndMobRegion> oRoot = criteria.from(FndMobRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("name"), name));

		List<FndMobRegion> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<FndMobRegion> getListByActiveStatus(boolean activeStatus) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndMobRegion> criteria = cb.createQuery(FndMobRegion.class);
		Root<FndMobRegion> oRoot = criteria.from(FndMobRegion.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("activeStatus"), activeStatus));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
