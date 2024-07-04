package net.doohad.models.fnd.dao;

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

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndViewType;

@Transactional
@Component
public class FndViewTypeDaoImpl implements FndViewTypeDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndViewType get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndViewType> criteria = cb.createQuery(FndViewType.class);
		Root<FndViewType> oRoot = criteria.from(FndViewType.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndViewType> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndViewType viewType) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(viewType);
	}

	@Override
	public void delete(FndViewType viewType) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndViewType.class, viewType.getId()));
	}

	@Override
	public void delete(List<FndViewType> viewTypes) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndViewType viewType : viewTypes) {
            session.delete(session.load(FndViewType.class, viewType.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndViewType.class);
	}

	@Override
	public FndViewType get(String code, String resolution) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndViewType> criteria = cb.createQuery(FndViewType.class);
		Root<FndViewType> oRoot = criteria.from(FndViewType.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("code"), code),
				cb.equal(oRoot.get("resolution"), resolution)
		);

		List<FndViewType> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<FndViewType> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndViewType> criteria = cb.createQuery(FndViewType.class);
		Root<FndViewType> oRoot = criteria.from(FndViewType.class);
		
		criteria.select(oRoot);
		
		// 상태 및 삭제 여부와 상관없이 모든 자료 Query
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public String getResoByCode(String code) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndViewType> criteria = cb.createQuery(FndViewType.class);
		Root<FndViewType> oRoot = criteria.from(FndViewType.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("code"), code));

		List<FndViewType> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		if (list.isEmpty() || list.size() == 0) {
			return "";
		}
		
		return list.get(0).getResolution();
	}

	@Override
	public List<Tuple> getMaxLaneGroupByMediumId() {
		
		Session session = sessionFactory.getCurrentSession();

		String sql = "SELECT sp.medium_id, MAX(spi.lane_id) " +
					"FROM inv_sync_packs sp, inv_sync_pack_items spi " +
					"WHERE sp.sync_pack_id = spi.sync_pack_id " +
					"GROUP BY sp.medium_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

}
