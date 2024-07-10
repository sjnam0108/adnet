package kr.adnetwork.models.adc.dao;

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
import kr.adnetwork.models.adc.AdcCampaign;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgAdvertiser;
import kr.adnetwork.utils.SolUtil;

@Transactional
@Component
public class AdcCampaignDaoImpl implements AdcCampaignDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public AdcCampaign get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcCampaign> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcCampaign campaign) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(campaign);
	}

	@Override
	public void delete(AdcCampaign campaign) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcCampaign.class, campaign.getId());
	}

	@Override
	public void delete(List<AdcCampaign> campaigns) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcCampaign campaign : campaigns) {
            session.delete(session.load(AdcCampaign.class, campaign.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("advertiser", OrgAdvertiser.class);
		
		Criterion criterion = Restrictions.eq("deleted", false);

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCampaign.class, map, criterion);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int mediumId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("advertiser", OrgAdvertiser.class);
		
		Criterion criterion = Restrictions.and(
				Restrictions.eq("deleted", false),
				Restrictions.eq("medium.id", mediumId));

        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcCampaign.class, map, criterion);
	}

	@Override
	public AdcCampaign get(KnlMedium medium, String name) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		Join<AdcCampaign, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("name"), name));

		List<AdcCampaign> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<AdcCampaign> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		Join<AdcCampaign, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), mediumId), cb.equal(oRoot.get("deleted"), false)));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcCampaign> getList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		
		criteria.select(oRoot);
		criteria.where(cb.equal(oRoot.get("deleted"), false));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<AdcCampaign> getLisyByAdvertiserId(int advertiserId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcCampaign> criteria = cb.createQuery(AdcCampaign.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		Join<AdcCampaign, OrgAdvertiser> joinO = oRoot.join("advertiser");
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(joinO.get("id"), advertiserId),
				cb.equal(oRoot.get("deleted"), false)
		);
		criteria.orderBy(cb.desc(oRoot.get("startDate")), cb.asc(oRoot.get("name")));

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getBudgetStatGroupByCampaignId() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT c.campaign_id, SUM(a.budget), SUM(a.goal_value), SUM(a.sys_value)
		//		FROM adc_ads a, adc_campaigns c
		//      WHERE a.campaign_id = c.campaign_id AND a.status <> 'T' AND c.status <> 'T' AND c.self_managed = 0
		//		GROUP BY c.campaign_id
		//
		String sql = "SELECT c.campaign_id, SUM(a.budget), SUM(a.goal_value), SUM(a.sys_value) " +
					"FROM adc_ads a, adc_campaigns c " +
					"WHERE a.campaign_id = c.campaign_id AND a.status <> 'T' AND c.status <> 'T' AND c.self_managed = 0 " +
					"GROUP BY c.campaign_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public List<Tuple> getIdsByCreatPlayDate(int creatId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT DISTINCT c.campaign_id
		//		FROM adc_ad_creatives ac, adc_ads a, adc_campaigns c
		//      WHERE ac.creative_id = :creatId
		//      AND ac.ad_id = a.ad_id AND a.campaign_id = c.campaign_id
		//		AND :playDate BETWEEN ac.start_date AND ac.end_date
		//
		String sql = "SELECT DISTINCT c.campaign_id " +
					"FROM adc_ad_creatives ac, adc_ads a, adc_campaigns c " +
					"WHERE ac.creative_id = :creatId " +
					"AND ac.ad_id = a.ad_id AND a.campaign_id = c.campaign_id " +
					"AND :playDate BETWEEN ac.start_date AND ac.end_date";
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("creatId", creatId)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getCountGroupByMediumAdvertiserId(int mediumId) {

		// 결과 예)
		//
		//     1  5
		//     2  3
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> q = cb.createTupleQuery();
		Root<AdcCampaign> oRoot = q.from(AdcCampaign.class);
		Join<AdcCampaign, OrgAdvertiser> joinO1 = oRoot.join("advertiser");
		Join<AdcCampaign, KnlMedium> joinO2 = oRoot.join("medium");
		
		q.multiselect(joinO1.get("id"), cb.count(oRoot));
		q.where(
				cb.equal(joinO2.get("id"), mediumId),
				cb.equal(oRoot.get("deleted"), false)
		);
		q.groupBy(joinO1.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(q).getResultList();
	}

	@Override
	public int getCountByAdvertiserId(int advertiserId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		CriteriaBuilder cb = session.getCriteriaBuilder();
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<AdcCampaign> oRoot = criteria.from(AdcCampaign.class);
		Join<AdcCampaign, OrgAdvertiser> joinO = oRoot.join("advertiser");
		
		criteria.select(cb.count(oRoot));
		criteria.where(
				cb.equal(joinO.get("id"), advertiserId),
				cb.equal(joinO.get("deleted"), false),
				cb.equal(oRoot.get("deleted"), false)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult().intValue();
	}

}
