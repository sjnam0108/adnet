package net.doohad.models.adc.dao;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.adc.AdcAd;
import net.doohad.models.adc.AdcMobTarget;
import net.doohad.models.knl.KnlMedium;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class AdcMobTargetDaoImpl implements AdcMobTargetDao {

    @Autowired
    private SessionFactory sessionFactory;
    

	@Override
	public AdcMobTarget get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcMobTarget> criteria = cb.createQuery(AdcMobTarget.class);
		Root<AdcMobTarget> oRoot = criteria.from(AdcMobTarget.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcMobTarget> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcMobTarget mobTarget) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(mobTarget);
	}

	@Override
	public void delete(AdcMobTarget mobTarget) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcMobTarget.class, mobTarget.getId());
	}

	@Override
	public void delete(List<AdcMobTarget> mobTargets) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcMobTarget mobTarget : mobTargets) {
            session.delete(session.load(AdcMobTarget.class, mobTarget.getId()));
        }
	}

	@Override
	public List<AdcMobTarget> getListByAdId(int adId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcMobTarget> criteria = cb.createQuery(AdcMobTarget.class);
		Root<AdcMobTarget> oRoot = criteria.from(AdcMobTarget.class);
		Join<AdcMobTarget, AdcAd> joinO = oRoot.join("ad");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), adId)
		);
		criteria.orderBy(cb.asc(oRoot.get("siblingSeq")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	private void reorder(int adId) {
		
		List<AdcMobTarget> list = getListByAdId(adId);
		
		int cnt = 1;
		for(AdcMobTarget item : list) {
			item.setSiblingSeq((cnt++) * 10);
			sessionFactory.getCurrentSession().saveOrUpdate(item);
		}
	}

	@Override
	public void saveAndReorder(AdcMobTarget mobTarget) {
		
		saveOrUpdate(mobTarget);
		reorder(mobTarget.getAd().getId());
	}

	@Override
	public List<AdcMobTarget> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcMobTarget> criteria = cb.createQuery(AdcMobTarget.class);
		Root<AdcMobTarget> oRoot = criteria.from(AdcMobTarget.class);
		Join<AdcMobTarget, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), mediumId)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public int getCountByAdId(int adId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT count(*) FROM adc_mob_targets
		//		WHERE ad_id = :ad_id
		//
		String sql = "SELECT count(*) FROM adc_mob_targets " +
					"WHERE ad_id = :adId";
		
		Tuple tuple = session.createNativeQuery(sql, Tuple.class)
				.setParameter("adId", adId)
				.getSingleResult();
		
		return ((BigInteger) tuple.get(0)).intValue();
	}

	@Override
	public int getCountByMobTypeTgtId(String mobType, int tgtId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT count(*) FROM adc_mob_targets
		//		WHERE mob_type = :mobType
		//      AND tgt_id = :tgtId
		//
		String sql = "SELECT count(*) FROM adc_mob_targets " +
					"WHERE mob_type = :mobType " +
					"AND tgt_id = :tgtId";
		
		Tuple tuple = session.createNativeQuery(sql, Tuple.class)
				.setParameter("mobType", mobType)
				.setParameter("tgtId", tgtId)
				.getSingleResult();
		
		return ((BigInteger) tuple.get(0)).intValue();
	}

}
