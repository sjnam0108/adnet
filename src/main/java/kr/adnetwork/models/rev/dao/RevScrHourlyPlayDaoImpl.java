package kr.adnetwork.models.rev.dao;

import java.math.BigInteger;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcAd;
import kr.adnetwork.models.adc.AdcAdCreative;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.rev.RevScrHourlyPlay;

@Transactional
@Component
public class RevScrHourlyPlayDaoImpl implements RevScrHourlyPlayDao {

	private static final Logger logger = LoggerFactory.getLogger(RevScrHourlyPlayDaoImpl.class);

	
    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevScrHourlyPlay get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevScrHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevScrHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(hourPlay);
	}

	@Override
	public void delete(RevScrHourlyPlay hourPlay) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevScrHourlyPlay.class, hourPlay.getId()));
	}

	@Override
	public void delete(List<RevScrHourlyPlay> hourPlays) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevScrHourlyPlay hourPlay : hourPlays) {
            session.delete(session.load(RevScrHourlyPlay.class, hourPlay.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("ad", AdcAd.class);
		map.put("screen", InvScreen.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevScrHourlyPlay.class, map);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int adId, Date sDate, Date eDate) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		map.put("ad", AdcAd.class);
		map.put("screen", InvScreen.class);
		
		Criterion rest1 = Restrictions.eq("ad.id", adId);
		Criterion rest2 = Restrictions.ge("playDate", sDate);
		Criterion rest3 = Restrictions.le("playDate", eDate);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevScrHourlyPlay.class, map, 
        		Restrictions.and(rest1, Restrictions.and(rest2, rest3)));
	}

	@Override
	public RevScrHourlyPlay get(InvScreen screen, AdcAdCreative adCreative, Date playDate) {
		
		if (screen == null || adCreative == null || playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAdCreative> joinO2 = oRoot.join("adCreative");
		
		criteria.select(oRoot).where(
				cb.and(
					cb.and(cb.equal(joinO1.get("id"), screen.getId()), cb.equal(oRoot.get("playDate"), playDate)),
					cb.equal(joinO2.get("id"), adCreative.getId()))
				);

		List<RevScrHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public RevScrHourlyPlay get(int screenId, int adCreativeId, Date playDate) {
		
		if (playDate == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAdCreative> joinO2 = oRoot.join("adCreative");
		
		criteria.select(oRoot).where(
				cb.and(
					cb.and(cb.equal(joinO1.get("id"), screenId), cb.equal(oRoot.get("playDate"), playDate)),
					cb.equal(joinO2.get("id"), adCreativeId))
				);

		List<RevScrHourlyPlay> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<RevScrHourlyPlay> getListBySiteIdAdIdPlayDate(int siteId, int adId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		Join<InvScreen, InvSite> joinO1O = joinO1.join("site");
		
		criteria.select(oRoot).where(
				cb.and(
					cb.and(cb.equal(joinO1O.get("id"), siteId), cb.equal(oRoot.get("playDate"), playDate)),
					cb.equal(joinO2.get("id"), adId))
				);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getScrSumListByPlayDate(Date playDate) {

		// 관련 SQL)
		//
		//		select screen_id, count(ad_id), 
		//		sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//		sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//		sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//		sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//		sum(succ_tot), sum(fail_tot), sum(date_tot) 
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		
		criteria.multiselect(
				joinO1.get("id"), cb.count(joinO2.get("id")),
				cb.sum(oRoot.get("cnt00")), cb.sum(oRoot.get("cnt01")), cb.sum(oRoot.get("cnt02")), 
				cb.sum(oRoot.get("cnt03")), cb.sum(oRoot.get("cnt04")), cb.sum(oRoot.get("cnt05")), 
				cb.sum(oRoot.get("cnt06")), cb.sum(oRoot.get("cnt07")), cb.sum(oRoot.get("cnt08")), 
				cb.sum(oRoot.get("cnt09")), cb.sum(oRoot.get("cnt10")), cb.sum(oRoot.get("cnt11")), 
				cb.sum(oRoot.get("cnt12")), cb.sum(oRoot.get("cnt13")), cb.sum(oRoot.get("cnt14")), 
				cb.sum(oRoot.get("cnt15")), cb.sum(oRoot.get("cnt16")), cb.sum(oRoot.get("cnt17")), 
				cb.sum(oRoot.get("cnt18")), cb.sum(oRoot.get("cnt19")), cb.sum(oRoot.get("cnt20")), 
				cb.sum(oRoot.get("cnt21")), cb.sum(oRoot.get("cnt22")), cb.sum(oRoot.get("cnt23")),
				cb.sum(oRoot.get("succTotal")), cb.sum(oRoot.get("failTotal")), cb.sum(oRoot.get("dateTotal"))
		);
		criteria.where(
				cb.equal(oRoot.get("playDate"), playDate)
		);
		criteria.groupBy(joinO1.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getSitSumListByPlayDate(Date playDate) {

		// 관련 SQL)
		//
		//		select site_id, count(ad_id), 
		//		sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//		sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//		sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//		sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//		sum(succ_tot), sum(fail_tot), sum(date_tot) 
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, InvScreen> joinO1 = oRoot.join("screen");
		Join<RevScrHourlyPlay, AdcAd> joinO2 = oRoot.join("ad");
		Join<InvScreen, InvSite> joinO3 = joinO1.join("site");
		
		criteria.multiselect(
				joinO3.get("id"), cb.countDistinct(joinO2.get("id")),
				cb.sum(oRoot.get("cnt00")), cb.sum(oRoot.get("cnt01")), cb.sum(oRoot.get("cnt02")), 
				cb.sum(oRoot.get("cnt03")), cb.sum(oRoot.get("cnt04")), cb.sum(oRoot.get("cnt05")), 
				cb.sum(oRoot.get("cnt06")), cb.sum(oRoot.get("cnt07")), cb.sum(oRoot.get("cnt08")), 
				cb.sum(oRoot.get("cnt09")), cb.sum(oRoot.get("cnt10")), cb.sum(oRoot.get("cnt11")), 
				cb.sum(oRoot.get("cnt12")), cb.sum(oRoot.get("cnt13")), cb.sum(oRoot.get("cnt14")), 
				cb.sum(oRoot.get("cnt15")), cb.sum(oRoot.get("cnt16")), cb.sum(oRoot.get("cnt17")), 
				cb.sum(oRoot.get("cnt18")), cb.sum(oRoot.get("cnt19")), cb.sum(oRoot.get("cnt20")), 
				cb.sum(oRoot.get("cnt21")), cb.sum(oRoot.get("cnt22")), cb.sum(oRoot.get("cnt23")),
				cb.sum(oRoot.get("succTotal")), cb.sum(oRoot.get("failTotal")), cb.sum(oRoot.get("dateTotal"))
		);
		criteria.where(
				cb.equal(oRoot.get("playDate"), playDate)
		);
		criteria.groupBy(joinO3.get("id"));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getPlayDateListByLastUpdateDate(Date date) {

		// 관련 SQL)
		//
		//		select play_date from rev_scr_hourly_plays
		//      where last_update_date >= '2023-03-27'
		//      group by play_date
		//
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		
		criteria.multiselect(
				oRoot.get("playDate")
		);
		criteria.where(
				cb.greaterThanOrEqualTo(oRoot.get("whoLastUpdateDate"), date)
		);
		criteria.groupBy(oRoot.get("playDate"));
		
		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<RevScrHourlyPlay> getListByAdIdPlayDate(int adId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, AdcAd> joinO1 = oRoot.join("ad");
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate),
				cb.equal(joinO1.get("id"), adId)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getAdStatListByScreenIdPlayDate(int screenId, Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// 한 광고가 여러 광고 소재로 될 수도 있기 때문에 sum 적용
		//
		// SQL:
		//
		//		SELECT sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05),
		//			   sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11),
		//			   sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17),
		//			   sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23),
		//             sum(succ_tot), ad_id, sum(curr_hour_goal)
		//      FROM rev_scr_hourly_plays
		//      WHERE screen_id = :screenId AND play_date = :playDate
		//      GROUP BY ad_id
		//
		String sql = "SELECT sum(cnt_00), sum(cnt_01), sum(cnt_02), sum(cnt_03), sum(cnt_04), sum(cnt_05), " +
					"sum(cnt_06), sum(cnt_07), sum(cnt_08), sum(cnt_09), sum(cnt_10), sum(cnt_11), " +
					"sum(cnt_12), sum(cnt_13), sum(cnt_14), sum(cnt_15), sum(cnt_16), sum(cnt_17), " +
					"sum(cnt_18), sum(cnt_19), sum(cnt_20), sum(cnt_21), sum(cnt_22), sum(cnt_23), " +
					"sum(succ_tot), ad_id, sum(curr_hour_goal) " +
					"FROM rev_scr_hourly_plays " +
					"WHERE screen_id = :screenId AND play_date = :playDate " +
					"GROUP BY ad_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("screenId", screenId)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getStatGroupByAdCreatPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT hp.ad_id, c.creative_id,
		//			   sum(hp.cnt_00), sum(hp.cnt_01), sum(hp.cnt_02), sum(hp.cnt_03), sum(hp.cnt_04), sum(hp.cnt_05),
		//			   sum(hp.cnt_06), sum(hp.cnt_07), sum(hp.cnt_08), sum(hp.cnt_09), sum(hp.cnt_10), sum(hp.cnt_11),
		//			   sum(hp.cnt_12), sum(hp.cnt_13), sum(hp.cnt_14), sum(hp.cnt_15), sum(hp.cnt_16), sum(hp.cnt_17),
		//			   sum(hp.cnt_18), sum(hp.cnt_19), sum(hp.cnt_20), sum(hp.cnt_21), sum(hp.cnt_22), sum(hp.cnt_23),
		//			   sum(hp.fail_tot), count(*), sum(round(if(hp.ad_cpm = 0, hp.floor_cpm, hp.ad_cpm) * hp.succ_tot / 1000))
		//		FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c
		//		WHERE hp.ad_creative_id = ac.ad_creative_id 
		//		AND ac.creative_id = c.creative_id
		//		AND hp.play_date = :playDate
		//		GROUP BY hp.ad_id, c.creative_id
		//
		String sql = "SELECT hp.ad_id, c.creative_id, " +
					"sum(hp.cnt_00), sum(hp.cnt_01), sum(hp.cnt_02), sum(hp.cnt_03), sum(hp.cnt_04), sum(hp.cnt_05), " +
					"sum(hp.cnt_06), sum(hp.cnt_07), sum(hp.cnt_08), sum(hp.cnt_09), sum(hp.cnt_10), sum(hp.cnt_11), " +
					"sum(hp.cnt_12), sum(hp.cnt_13), sum(hp.cnt_14), sum(hp.cnt_15), sum(hp.cnt_16), sum(hp.cnt_17), " +
					"sum(hp.cnt_18), sum(hp.cnt_19), sum(hp.cnt_20), sum(hp.cnt_21), sum(hp.cnt_22), sum(hp.cnt_23), " +
					"sum(hp.fail_tot), count(*), sum(round(if(hp.ad_cpm = 0, hp.floor_cpm, hp.ad_cpm) * hp.succ_tot / 1000)) " +
					"FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c " +
					"WHERE hp.ad_creative_id = ac.ad_creative_id " +
					"AND ac.creative_id = c.creative_id " +
					"AND hp.play_date = :playDate " +
					"GROUP BY hp.ad_id, c.creative_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getScrCntGroupByAdIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT ad_id, COUNT(DISTINCT screen_id) 
		//		FROM rev_scr_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND ad_id IN (:ids)
		//		GROUP BY ad_id
		//
		String sql = "SELECT ad_id, COUNT(DISTINCT screen_id) " +
					"FROM rev_scr_hourly_plays " +
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
	public List<Tuple> getScrCntGroupByCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT c.creative_id, COUNT(DISTINCT hp.screen_id) 
		//		FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c
		//		WHERE hp.ad_creative_id = ac.ad_creative_id
		//		AND ac.creative_id = c.creative_id
		//		AND hp.play_date BETWEEN :startDate AND :endDate
		//		AND c.creative_id IN (:ids)
		//		GROUP BY c.creative_id
		//
		String sql = "SELECT c.creative_id, COUNT(DISTINCT hp.screen_id) " +
					"FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c " +
					"WHERE hp.ad_creative_id = ac.ad_creative_id " +
					"AND ac.creative_id = c.creative_id " +
					"AND hp.play_date BETWEEN :startDate AND :endDate " +
					"AND c.creative_id IN (:ids) " +
					"GROUP BY c.creative_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getScrCntGroupByPlayDateAdIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT play_date, COUNT(DISTINCT screen_id) 
		//		FROM rev_scr_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND ad_id IN (:ids)
		//		GROUP BY play_date
		//
		String sql = "SELECT play_date, COUNT(DISTINCT screen_id) " +
					"FROM rev_scr_hourly_plays " +
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
	public List<Tuple> getScrCntGroupByPlayDateCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT hp.play_date, COUNT(DISTINCT hp.screen_id) 
		//		FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c
		//		WHERE hp.ad_creative_id = ac.ad_creative_id
		//		AND ac.creative_id = c.creative_id
		//		AND hp.play_date BETWEEN :startDate AND :endDate
		//		AND c.creative_id IN (:ids)
		//		GROUP BY hp.play_date
		//
		String sql = "SELECT hp.play_date, COUNT(DISTINCT hp.screen_id) " +
					"FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c " +
					"WHERE hp.ad_creative_id = ac.ad_creative_id " +
					"AND ac.creative_id = c.creative_id " +
					"AND hp.play_date BETWEEN :startDate AND :endDate " +
					"AND c.creative_id IN (:ids) " +
					"GROUP BY hp.play_date";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<Tuple> getScrCntGroupByWeekDayAdIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT DATE_FORMAT(play_date, '%w'), COUNT(DISTINCT screen_id) 
		//		FROM rev_scr_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND ad_id IN (:ids)
		//		GROUP BY DATE_FORMAT(play_date, '%w')
		//
		String sql = "SELECT DATE_FORMAT(play_date, '%w'), COUNT(DISTINCT screen_id) " +
					"FROM rev_scr_hourly_plays " +
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
	public List<Tuple> getScrCntGroupByWeekDayCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT DATE_FORMAT(hp.play_date, '%w'), COUNT(DISTINCT hp.screen_id) 
		//		FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c
		//		WHERE hp.ad_creative_id = ac.ad_creative_id
		//		AND ac.creative_id = c.creative_id
		//		AND hp.play_date BETWEEN :startDate AND :endDate
		//		AND c.creative_id IN (:ids)
		//		GROUP BY DATE_FORMAT(hp.play_date, '%w')
		//
		String sql = "SELECT DATE_FORMAT(hp.play_date, '%w'), COUNT(DISTINCT hp.screen_id) " +
					"FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c " +
					"WHERE hp.ad_creative_id = ac.ad_creative_id " +
					"AND ac.creative_id = c.creative_id " +
					"AND hp.play_date BETWEEN :startDate AND :endDate " +
					"AND c.creative_id IN (:ids) " +
					"GROUP BY DATE_FORMAT(hp.play_date, '%w')";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public List<RevScrHourlyPlay> getListByPlayDate(Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevScrHourlyPlay> criteria = cb.createQuery(RevScrHourlyPlay.class);
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		
		// lazy loading
		//oRoot.fetch("medium");
		//oRoot.fetch("screen");
		//oRoot.fetch("ad");
		//oRoot.fetch("adCreative");
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("playDate"), playDate)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getUpdateCpmListByPlayDate(Date playDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT hp.scr_hourly_play_id, s.floor_cpm as fcpm1, a.cpm, 
		//             hp.floor_cpm as fcpm2, hp.ad_cpm, a.budget, a.goal_value, a.goal_type
		//		FROM rev_scr_hourly_plays hp, inv_screens s, adc_ads a
		//		WHERE hp.screen_id = s.screen_id and hp.ad_id = a.ad_id
		//		AND hp.play_date = :playDate
		//
		String sql = "SELECT hp.scr_hourly_play_id, s.floor_cpm as fcpm1, a.cpm, " +
					"hp.floor_cpm as fcpm2, hp.ad_cpm, a.budget, a.goal_value, a.goal_type " +
					"FROM rev_scr_hourly_plays hp, inv_screens s, adc_ads a " +
					"WHERE hp.screen_id = s.screen_id and hp.ad_id = a.ad_id " +
					"AND hp.play_date = :playDate";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("playDate", playDate)
				.getResultList();
	}

	@Override
	public void updateCpmValues(int id, int floorCpm, int adCpm) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		UPDATE rev_scr_hourly_plays
		//		SET floor_cpm = :floorCpm, ad_cpm = :adCpm
		//		WHERE scr_hourly_play_id = :id
		//
		String sql = "UPDATE rev_scr_hourly_plays " +
				"SET floor_cpm = :floorCpm, ad_cpm = :adCpm " +
				"WHERE scr_hourly_play_id = :id";
		
		session.createNativeQuery(sql)
				.setParameter("id", id)
				.setParameter("floorCpm", floorCpm)
				.setParameter("adCpm", adCpm)
				.executeUpdate();
	}

	@Override
	public int getScrCntAdIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT COUNT(DISTINCT screen_id) 
		//		FROM rev_scr_hourly_plays
		//		WHERE play_date BETWEEN :startDate AND :endDate
		//		AND ad_id IN (:ids)
		//
		String sql = "SELECT COUNT(DISTINCT screen_id) " +
					"FROM rev_scr_hourly_plays " +
					"WHERE play_date BETWEEN :startDate AND :endDate " +
					"AND ad_id IN (:ids)";
		
		
		Tuple tuple = session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getSingleResult();
		
		return ((BigInteger) tuple.get(0)).intValue();
	}

	@Override
	public int getScrCntCreatIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT COUNT(DISTINCT hp.screen_id) 
		//		FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c
		//		WHERE hp.ad_creative_id = ac.ad_creative_id
		//		AND ac.creative_id = c.creative_id
		//		AND hp.play_date BETWEEN :startDate AND :endDate
		//		AND c.creative_id IN (:ids)
		//
		String sql = "SELECT COUNT(DISTINCT hp.screen_id) " +
					"FROM rev_scr_hourly_plays hp, adc_ad_creatives ac, adc_creatives c " +
					"WHERE hp.ad_creative_id = ac.ad_creative_id " +
					"AND ac.creative_id = c.creative_id " +
					"AND hp.play_date BETWEEN :startDate AND :endDate " +
					"AND c.creative_id IN (:ids)";
		
		
		Tuple tuple = session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getSingleResult();
		
		return ((BigInteger) tuple.get(0)).intValue();
	}

	@Override
	public List<Tuple> getScrCntGroupByCampIdInBetween(List<Integer> ids, Date startDate, Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT c.campaign_id, COUNT(DISTINCT hp.screen_id) 
		//		FROM rev_scr_hourly_plays hp, adc_ads a, adc_campaigns c
		//		WHERE hp.play_date BETWEEN :startDate AND :endDate
		//		AND hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id
		//		AND c.campaign_id IN (:ids)
		//		GROUP BY c.campaign_id
		//
		String sql = "SELECT c.campaign_id, COUNT(DISTINCT hp.screen_id) " +
					"FROM rev_scr_hourly_plays hp, adc_ads a, adc_campaigns c " +
					"WHERE hp.play_date BETWEEN :startDate AND :endDate " +
					"AND hp.ad_id = a.ad_id AND a.campaign_id = c.campaign_id " +
					"AND c.campaign_id IN (:ids) " +
					"GROUP BY c.campaign_id";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("ids", ids)
				.setParameter("startDate", startDate)
				.setParameter("endDate", endDate)
				.getResultList();
	}

	@Override
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<Tuple> criteria = cb.createTupleQuery();
		Root<RevScrHourlyPlay> oRoot = criteria.from(RevScrHourlyPlay.class);
		Join<RevScrHourlyPlay, KnlMedium> joinO = oRoot.join("medium");
		
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

	@Override
	public boolean deleteByAdIdScreenIdPlayDate(Date sDate, Date eDate, List<Integer> adIds, List<Integer> screenIds) {

		Session session = sessionFactory.getCurrentSession();
		if (session != null) {

			// SQL:
			//
			//		DELETE FROM rev_scr_hourly_plays
			//		WHERE play_date BETWEEN :sDate AND :eDate
			//      AND ad_id IN :adIds
			//		AND screen_id IN :screenIds
			//
	    	
	    	try {
	    		
				String sql = "DELETE FROM rev_scr_hourly_plays " +
							"WHERE play_date BETWEEN :sDate AND :eDate ";
				
				if (adIds != null && adIds.size() > 0) {
					if (screenIds != null && screenIds.size() > 0) {
						sql += "AND ad_id IN :adIds " +
								"AND screen_id IN :screenIds";
						
						session.createNativeQuery(sql)
								.setParameter("sDate", sDate)
								.setParameter("eDate", eDate)
								.setParameterList("adIds", adIds)
								.setParameterList("screenIds", screenIds)
								.executeUpdate();
					} else {
						sql += "AND ad_id IN :adIds";
						
						session.createNativeQuery(sql)
								.setParameter("sDate", sDate)
								.setParameter("eDate", eDate)
								.setParameterList("adIds", adIds)
								.executeUpdate();
					}
				} else {
					if (screenIds != null && screenIds.size() > 0) {
						sql += "AND screen_id IN :screenIds";
						
						session.createNativeQuery(sql)
								.setParameter("sDate", sDate)
								.setParameter("eDate", eDate)
								.setParameterList("screenIds", screenIds)
								.executeUpdate();
					} else {
						session.createNativeQuery(sql)
								.setParameter("sDate", sDate)
								.setParameter("eDate", eDate)
								.executeUpdate();
					}
				}
				
				return true;
				
	    	} catch (Exception e) {
	    		logger.error("deleteByAdIdScreenIdPlayDate", e);
	    	}
		}
		
		return false;
	}

	@Override
	public List<Tuple> getPlayDateListByAdIdScreenIdPlayDate(Date sDate, Date eDate, List<Integer> adIds,
			List<Integer> screenIds) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT DISTINCT play_date FROM rev_scr_hourly_plays
		//		WHERE play_date BETWEEN :sDate AND :eDate
		//      AND ad_id IN :adIds
		//		AND screen_id IN :screenIds
		//
		
		String sql = "SELECT DISTINCT play_date FROM rev_scr_hourly_plays " +
				"WHERE play_date BETWEEN :sDate AND :eDate ";
	
		if (adIds != null && adIds.size() > 0) {
			if (screenIds != null && screenIds.size() > 0) {
				sql += "AND ad_id IN :adIds " +
						"AND screen_id IN :screenIds";
				
				return session.createNativeQuery(sql, Tuple.class)
						.setParameter("sDate", sDate)
						.setParameter("eDate", eDate)
						.setParameterList("adIds", adIds)
						.setParameterList("screenIds", screenIds)
						.getResultList();
			} else {
				sql += "AND ad_id IN :adIds";
				
				return session.createNativeQuery(sql, Tuple.class)
						.setParameter("sDate", sDate)
						.setParameter("eDate", eDate)
						.setParameterList("adIds", adIds)
						.getResultList();
			}
		} else {
			if (screenIds != null && screenIds.size() > 0) {
				sql += "AND screen_id IN :screenIds";
				
				return session.createNativeQuery(sql, Tuple.class)
						.setParameter("sDate", sDate)
						.setParameter("eDate", eDate)
						.setParameterList("screenIds", screenIds)
						.getResultList();
			} else {
				return session.createNativeQuery(sql, Tuple.class)
						.setParameter("sDate", sDate)
						.setParameter("eDate", eDate)
						.getResultList();
			}
		}
	}

}
