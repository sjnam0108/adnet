package net.doohad.models.org.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAlimTalk;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class OrgAlimTalkDaoImpl implements OrgAlimTalkDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgAlimTalk get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgAlimTalk> criteria = cb.createQuery(OrgAlimTalk.class);
		Root<OrgAlimTalk> oRoot = criteria.from(OrgAlimTalk.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgAlimTalk> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgAlimTalk alimTalk) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(alimTalk);
	}

	@Override
	public void delete(OrgAlimTalk alimTalk) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgAlimTalk.class, alimTalk.getId());
	}

	@Override
	public void delete(List<OrgAlimTalk> alimTalks) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgAlimTalk alimTalk : alimTalks) {
            session.delete(session.load(OrgAlimTalk.class, alimTalk.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgAlimTalk.class, map);
	}

	@Override
	public OrgAlimTalk get(KnlMedium medium, String shortName) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgAlimTalk> criteria = cb.createQuery(OrgAlimTalk.class);
		Root<OrgAlimTalk> oRoot = criteria.from(OrgAlimTalk.class);
		Join<OrgAlimTalk, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("shortName"), shortName));

		List<OrgAlimTalk> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<OrgAlimTalk> getActiveList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgAlimTalk> criteria = cb.createQuery(OrgAlimTalk.class);
		Root<OrgAlimTalk> oRoot = criteria.from(OrgAlimTalk.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("activeStatus"), true)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
