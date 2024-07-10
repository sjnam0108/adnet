package kr.adnetwork.models.rev.dao;

import java.util.Date;
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

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.rev.RevScrHrlyFbTot;

@Transactional
@Component
public class RevScrHrlyFbTotDaoImpl implements RevScrHrlyFbTotDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevScrHrlyFbTot get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyFbTot> criteria = cb.createQuery(RevScrHrlyFbTot.class);
		Root<RevScrHrlyFbTot> oRoot = criteria.from(RevScrHrlyFbTot.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevScrHrlyFbTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevScrHrlyFbTot hrlyFbTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hrlyFbTot);
	}

	@Override
	public void delete(RevScrHrlyFbTot hrlyFbTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevScrHrlyFbTot.class, hrlyFbTot.getId()));
	}

	@Override
	public void delete(List<RevScrHrlyFbTot> hrlyFbTots) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevScrHrlyFbTot hrlyFbTot : hrlyFbTots) {
            session.delete(session.load(RevScrHrlyFbTot.class, hrlyFbTot.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, Date playDate) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("screen", InvScreen.class);
		
		Criterion criterion = Restrictions.eq("playDate", playDate);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevScrHrlyFbTot.class, map, criterion);
	}

	@Override
	public RevScrHrlyFbTot get(InvScreen screen, Date playDate) {
		
		if (screen == null || playDate == null) {
			return null;
		}

		return get(screen.getId(), playDate);
	}

	@Override
	public RevScrHrlyFbTot get(int screenId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyFbTot> criteria = cb.createQuery(RevScrHrlyFbTot.class);
		Root<RevScrHrlyFbTot> oRoot = criteria.from(RevScrHrlyFbTot.class);
		Join<RevScrHrlyFbTot, AdcAd> joinO = oRoot.join("screen");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), screenId), 
				cb.equal(oRoot.get("playDate"), playDate)
		);

		List<RevScrHrlyFbTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHrlyFbTot> oRoot = criteria.from(RevScrHrlyFbTot.class);
		Join<RevScrHrlyFbTot, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.multiselect(
				cb.sum(oRoot.get("dateTotal")), 
				cb.countDistinct(oRoot.get("id")), 
				cb.avg(oRoot.get("dateTotal"))
		);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult();
	}

	@Override
	public List<RevScrHrlyFbTot> getListByMediumIdPlayDate(int mediumId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyFbTot> criteria = cb.createQuery(RevScrHrlyFbTot.class);
		Root<RevScrHrlyFbTot> oRoot = criteria.from(RevScrHrlyFbTot.class);
		Join<RevScrHrlyFbTot, KnlMedium> joinO = oRoot.join("medium");
		
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);

		return session.createQuery(criteria).getResultList();
	}

	@Override
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHrlyFbTot> oRoot = criteria.from(RevScrHrlyFbTot.class);
		Join<RevScrHrlyFbTot, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.multiselect(
				cb.sum(oRoot.get("cnt00")), cb.sum(oRoot.get("cnt01")), cb.sum(oRoot.get("cnt02")), cb.sum(oRoot.get("cnt03")),
				cb.sum(oRoot.get("cnt04")), cb.sum(oRoot.get("cnt05")), cb.sum(oRoot.get("cnt06")), cb.sum(oRoot.get("cnt07")),
				cb.sum(oRoot.get("cnt08")), cb.sum(oRoot.get("cnt09")), cb.sum(oRoot.get("cnt10")), cb.sum(oRoot.get("cnt11")),
				cb.sum(oRoot.get("cnt12")), cb.sum(oRoot.get("cnt13")), cb.sum(oRoot.get("cnt14")), cb.sum(oRoot.get("cnt15")),
				cb.sum(oRoot.get("cnt16")), cb.sum(oRoot.get("cnt17")), cb.sum(oRoot.get("cnt18")), cb.sum(oRoot.get("cnt19")),
				cb.sum(oRoot.get("cnt20")), cb.sum(oRoot.get("cnt21")), cb.sum(oRoot.get("cnt22")), cb.sum(oRoot.get("cnt23"))
		);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId),
				cb.equal(oRoot.get("playDate"), playDate)
		);
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult();
	}

}
