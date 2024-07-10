package kr.adnetwork.models.rev.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.rev.RevInvenRequest;
import kr.adnetwork.utils.SolUtil;

@Transactional
@Component
public class RevInvenRequestDaoImpl implements RevInvenRequestDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevInvenRequest get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevInvenRequest> criteria = cb.createQuery(RevInvenRequest.class);
		Root<RevInvenRequest> oRoot = criteria.from(RevInvenRequest.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevInvenRequest> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevInvenRequest invenRequest) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(invenRequest);
	}

	@Override
	public void delete(RevInvenRequest invenRequest) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), RevInvenRequest.class, invenRequest.getId());
	}

	@Override
	public void delete(List<RevInvenRequest> invenRequests) {

		Session session = sessionFactory.getCurrentSession();
		
        for (RevInvenRequest invenRequest : invenRequests) {
            session.delete(session.load(RevInvenRequest.class, invenRequest.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevInvenRequest.class, map);
	}

}
