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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcCreative;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.rev.RevHourlyPlay;

@Transactional
@Component
public class RevHourlyPlayDaoImpl implements RevHourlyPlayDao {

	private static final Logger logger = LoggerFactory.getLogger(RevHourlyPlayDaoImpl.class);

	
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevHourlyPlay get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevHourlyPlay> criteria = cb.createQuery(RevHourlyPlay.class);
		Root<RevHourlyPlay> oRoot = criteria.from(RevHourlyPlay.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hourPlay);
	}

	@Override
	public void delete(RevHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevHourlyPlay.class, hourPlay.getId()));
	}

	@Override
	public void delete(List<RevHourlyPlay> hourPlays) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevHourlyPlay hourPlay : hourPlays) {
            session.delete(session.load(RevHourlyPlay.class, hourPlay.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("ad", AdcAd.class);
		map.put("creative", AdcCreative.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevHourlyPlay.class, map);
	}

	@Override
	public RevHourlyPlay get(AdcAd ad, AdcCreative creative, Date playDate) {
		
		if (ad == null || creative == null || playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevHourlyPlay> criteria = cb.createQuery(RevHourlyPlay.class);
		Root<RevHourlyPlay> oRoot = criteria.from(RevHourlyPlay.class);
		Join<RevHourlyPlay, AdcAd> joinO1 = oRoot.join("ad");
		Join<RevHourlyPlay, AdcCreative> joinO2 = oRoot.join("creative");
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate),
				cb.equal(joinO1.get("id"), ad.getId()),
				cb.equal(joinO2.get("id"), creative.getId())
				);

		List<RevHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Tuple> getStatGroupByAdIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT ad_id, SUM(succ_tot), 
		//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//             sum(actual_amount)
		//		FROM rev_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND ad_id IN (:ids)
		//		GROUP BY ad_id
		//
		String sql = "SELECT ad_id, SUM(succ_tot), " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE play_date BETWEEN :startDate AND :endDate " +
					"AND ad_id IN (:ids) " +
					"GROUP BY ad_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByPlayDateAdIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT play_date, SUM(succ_tot), 
		//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//             sum(actual_amount)
		//		FROM rev_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND ad_id IN (:ids)
		//		GROUP BY play_date
		//
		String sql = "SELECT play_date, SUM(succ_tot), " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE play_date BETWEEN :startDate AND :endDate " +
					"AND ad_id IN (:ids) " +
					"GROUP BY play_date";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByWeekDayAdIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT DATE_FORMAT(play_date, '%w'), SUM(succ_tot), 
		//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//             sum(actual_amount)
		//		FROM rev_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND ad_id IN (:ids)
		//		GROUP BY DATE_FORMAT(play_date, '%w')
		//
		String sql = "SELECT DATE_FORMAT(play_date, '%w'), SUM(succ_tot), " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE play_date BETWEEN :startDate AND :endDate " +
					"AND ad_id IN (:ids) " +
					"GROUP BY DATE_FORMAT(play_date, '%w')";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getActualStatGroupByAdId() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT ad_id, SUM(succ_tot), SUM(actual_amount)
		//		FROM rev_hourly_plays
		//		GROUP BY ad_id
		//
		String sql = "SELECT ad_id, SUM(succ_tot), SUM(actual_amount) " +
					"FROM rev_hourly_plays " +
					"GROUP BY ad_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT creative_id, SUM(succ_tot), 
		//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//             sum(actual_amount)
		//		FROM rev_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND creative_id IN (:ids)
		//		GROUP BY creative_id
		//
		String sql = "SELECT creative_id, SUM(succ_tot), " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE play_date BETWEEN :startDate AND :endDate " +
					"AND creative_id IN (:ids) " +
					"GROUP BY creative_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByPlayDateCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT play_date, SUM(succ_tot), 
		//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//             sum(actual_amount)
		//		FROM rev_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND creative_id IN (:ids)
		//		GROUP BY play_date
		//
		String sql = "SELECT play_date, SUM(succ_tot), " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE play_date BETWEEN :startDate AND :endDate " +
					"AND creative_id IN (:ids) " +
					"GROUP BY play_date";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByWeekDayCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT DATE_FORMAT(play_date, '%w'), SUM(succ_tot), 
		//			   sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//             sum(actual_amount)
		//		FROM rev_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND creative_id IN (:ids)
		//		GROUP BY DATE_FORMAT(play_date, '%w')
		//
		String sql = "SELECT DATE_FORMAT(play_date, '%w'), SUM(succ_tot), " +
					"sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE play_date BETWEEN :startDate AND :endDate " +
					"AND creative_id IN (:ids) " +
					"GROUP BY DATE_FORMAT(play_date, '%w')";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getActualStatGroupByCreatId() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT creative_id, SUM(succ_tot), SUM(actual_amount)
		//		FROM rev_hourly_plays
		//		GROUP BY creative_id
		//
		String sql = "SELECT creative_id, SUM(succ_tot), SUM(actual_amount) " +
					"FROM rev_hourly_plays " +
					"GROUP BY creative_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public List<Tuple> getActualStatGroupByCampaignId() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT c.campaign_id, SUM(hp.succ_tot), SUM(hp.actual_amount)
		//		FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c
		//      WHERE hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id
		//		GROUP BY c.campaign_id
		//
		String sql = "SELECT c.campaign_id, SUM(hp.succ_tot), SUM(hp.actual_amount) " +
					"FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c " +
					"WHERE hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id " +
					"GROUP BY c.campaign_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public Tuple getAccStatByAdIdPlayDate(int adId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT SUM(succ_tot), SUM(actual_amount)
		//		FROM rev_hourly_plays
		//      WHERE ad_id = :adId
		//		AND play_date = :playDate
		//
		String sql = "SELECT SUM(succ_tot), SUM(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE ad_id = :adId " +
					"AND play_date = :playDate";
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("adId", adId)
				.setParameter("playDate", playDate)
				.getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public Tuple getAccStatByCampaignIdPlayDate(int campId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT SUM(hp.succ_tot), SUM(hp.actual_amount)
		//		FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c
		//      WHERE hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id
		//      AND c.campaign_id = :campId
		//		AND hp.play_date = :playDate
		//
		String sql = "SELECT SUM(hp.succ_tot), SUM(hp.actual_amount) " +
					"FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c " +
					"WHERE hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id " +
					"AND c.campaign_id = :campId " +
					"AND hp.play_date = :playDate";
		
		List<Tuple> list = session.createNativeQuery(sql, Tuple.class)
				.setParameter("campId", campId)
				.setParameter("playDate", playDate)
				.getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<Tuple> getCampIdListByMediumPlayDate(int mediumId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT a.campaign_id
		//		FROM rev_hourly_plays hp, adc_ads a
		//		WHERE hp.play_date = :playDate
		//		AND hp.medium_id = :mediumId AND hp.ad_id = a.ad_id
		//		GROUP BY a.campaign_id
		//
		String sql = "SELECT a.campaign_id " +
					"FROM rev_hourly_plays hp, adc_ads a " +
					"WHERE hp.play_date = :playDate " +
					"AND hp.medium_id = :mediumId AND hp.ad_id = a.ad_id " +
					"GROUP BY a.campaign_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("mediumId", mediumId)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getAdIdListByMediumPlayDate(int mediumId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// 동일 광고의 다른 소재가 존재할 수 있음
		
		// SQL:
		//
		//		SELECT ad_id
		//		FROM rev_hourly_plays
		//		WHERE play_date = :playDate
		//		AND medium_id = :mediumId
		//		GROUP BY ad_id
		//
		String sql = "SELECT ad_id " +
					"FROM rev_hourly_plays " +
					"WHERE play_date = :playDate " +
					"AND medium_id = :mediumId " +
					"GROUP BY ad_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("mediumId", mediumId)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getCreatIdListByMediumPlayDate(int mediumId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// 동일 소재가 다른 광고에 링크될 수 있기 때문에 group by 필요
		
		// SQL:
		//
		//		SELECT creative_id
		//		FROM rev_hourly_plays
		//		WHERE play_date = :playDate
		//		AND medium_id = :mediumId
		//		GROUP BY creative_id
		//
		String sql = "SELECT creative_id " +
					"FROM rev_hourly_plays " +
					"WHERE play_date = :playDate " +
					"AND medium_id = :mediumId " +
					"GROUP BY creative_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("mediumId", mediumId)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByCampIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT a.campaign_id, SUM(hp.succ_tot), 
		//			   sum(hp.cnt_00), sum(hp.cnt_01), sum(hp.cnt_02), sum(hp.cnt_03), sum(hp.cnt_04), sum(hp.cnt_05),
		//			   sum(hp.cnt_06), sum(hp.cnt_07), sum(hp.cnt_08), sum(hp.cnt_09), sum(hp.cnt_10), sum(hp.cnt_11),
		//			   sum(hp.cnt_12), sum(hp.cnt_13), sum(hp.cnt_14), sum(hp.cnt_15), sum(hp.cnt_16), sum(hp.cnt_17),
		//			   sum(hp.cnt_18), sum(hp.cnt_19), sum(hp.cnt_20), sum(hp.cnt_21), sum(hp.cnt_22), sum(hp.cnt_23),
		//             sum(hp.actual_amount)
		//		FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c
		//		WHERE hp.play_date BETWEEN :startDate AND :endDate
		//		AND c.campaign_id IN (:ids)
		//      AND hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id
		//		GROUP BY a.campaign_id
		//
		String sql = "SELECT a.campaign_id, SUM(hp.succ_tot), " +
					"sum(hp.cnt_00), sum(hp.cnt_01), sum(hp.cnt_02), sum(hp.cnt_03), sum(hp.cnt_04), sum(hp.cnt_05), " +
					"sum(hp.cnt_06), sum(hp.cnt_07), sum(hp.cnt_08), sum(hp.cnt_09), sum(hp.cnt_10), sum(hp.cnt_11), " +
					"sum(hp.cnt_12), sum(hp.cnt_13), sum(hp.cnt_14), sum(hp.cnt_15), sum(hp.cnt_16), sum(hp.cnt_17), " +
					"sum(hp.cnt_18), sum(hp.cnt_19), sum(hp.cnt_20), sum(hp.cnt_21), sum(hp.cnt_22), sum(hp.cnt_23), " +
					"sum(hp.actual_amount) " +
					"FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c " +
					"WHERE hp.play_date BETWEEN :startDate AND :endDate " +
					"AND a.campaign_id IN (:ids) " +
					"AND hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id " +
					"GROUP BY a.campaign_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getActualStatGroupByAdIdByPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT ad_id, SUM(succ_tot), SUM(actual_amount)
		//		FROM rev_hourly_plays
		//		WHERE play_date = :playDate
		//		GROUP BY ad_id
		//
		String sql = "SELECT ad_id, SUM(succ_tot), SUM(actual_amount) " +
					"FROM rev_hourly_plays " +
					"WHERE play_date = :playDate " +
					"GROUP BY ad_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getActualStatGroupByCampaignIdByPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT c.campaign_id, SUM(hp.succ_tot), SUM(hp.actual_amount)
		//		FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c
		//      WHERE hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id AND hp.play_date = :playDate
		//		GROUP BY c.campaign_id
		//
		String sql = "SELECT c.campaign_id, SUM(hp.succ_tot), SUM(hp.actual_amount) " +
					"FROM rev_hourly_plays hp, adc_ads a, adc_campaigns c " +
					"WHERE hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id AND hp.play_date = :playDate " +
					"GROUP BY c.campaign_id";
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public boolean deleteByMediumIdPlayDate(int mediumId, List<Date> playDates) {

		Session session = sessionFactory.getCurrentSession();
		if (session != null) {

			// SQL:
			//
			//		DELETE FROM rev_hourly_plays
			//		WHERE medium_id = :mediumId
			//		AND play_date IN :playDates
			//
	    	
	    	try {
	    		
				String sql = "DELETE FROM rev_hourly_plays " +
							"WHERE medium_id = :mediumId " +
							"AND play_date IN :playDates";
				
				session.createNativeQuery(sql)
						.setParameter("mediumId", mediumId)
						.setParameterList("playDates", playDates)
						.executeUpdate();
				
				return true;
				
	    	} catch (Exception e) {
	    		logger.error("deleteByMediumIdPlayDate", e);
	    	}
		}
		
		return false;
	}

}
