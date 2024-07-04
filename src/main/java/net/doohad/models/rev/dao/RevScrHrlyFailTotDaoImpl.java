package net.doohad.models.rev.dao;

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

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcAd;
import net.doohad.models.inv.InvScreen;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.rev.RevScrHrlyFailTot;

@Transactional
@Component
public class RevScrHrlyFailTotDaoImpl implements RevScrHrlyFailTotDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevScrHrlyFailTot get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyFailTot> criteria = cb.createQuery(RevScrHrlyFailTot.class);
		Root<RevScrHrlyFailTot> oRoot = criteria.from(RevScrHrlyFailTot.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevScrHrlyFailTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevScrHrlyFailTot hrlyFailTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hrlyFailTot);
	}

	@Override
	public void delete(RevScrHrlyFailTot hrlyFailTot) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevScrHrlyFailTot.class, hrlyFailTot.getId()));
	}

	@Override
	public void delete(List<RevScrHrlyFailTot> hrlyFailTots) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevScrHrlyFailTot hrlyFailTot : hrlyFailTots) {
            session.delete(session.load(RevScrHrlyFailTot.class, hrlyFailTot.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, Date playDate) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("screen", InvScreen.class);
		
		Criterion criterion = Restrictions.eq("playDate", playDate);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevScrHrlyFailTot.class, map, criterion);
	}

	@Override
	public RevScrHrlyFailTot get(InvScreen screen, Date playDate) {
		
		if (screen == null || playDate == null) {
			return null;
		}

		return get(screen.getId(), playDate);
	}

	@Override
	public RevScrHrlyFailTot get(int screenId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHrlyFailTot> criteria = cb.createQuery(RevScrHrlyFailTot.class);
		Root<RevScrHrlyFailTot> oRoot = criteria.from(RevScrHrlyFailTot.class);
		Join<RevScrHrlyFailTot, AdcAd> joinO = oRoot.join("screen");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), screenId), 
				cb.equal(oRoot.get("playDate"), playDate)
		);

		List<RevScrHrlyFailTot> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHrlyFailTot> oRoot = criteria.from(RevScrHrlyFailTot.class);
		Join<RevScrHrlyFailTot, KnlMedium> joinO = oRoot.join("medium");
		
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
