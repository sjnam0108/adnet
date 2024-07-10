package kr.adnetwork.models.rev.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
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
import kr.adnetwork.models.rev.RevChanAd;
import kr.adnetwork.utils.Util;

@Transactional
@Component
public class RevChanAdDaoImpl implements RevChanAdDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevChanAd get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevChanAd> criteria = cb.createQuery(RevChanAd.class);
		Root<RevChanAd> oRoot = criteria.from(RevChanAd.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevChanAd> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevChanAd chanAd) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(chanAd);
	}

	@Override
	public void delete(RevChanAd chanAd) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevChanAd.class, chanAd.getId()));
	}

	@Override
	public void delete(List<RevChanAd> chanAds) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevChanAd chanAd : chanAds) {
            session.delete(session.load(RevChanAd.class, chanAd.getId()));
        }
	}

	@Override
	public List<Tuple> getLastListGroupByChannelId() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT channel_id, seq, play_begin_date, play_end_date, hint
		//		FROM rev_chan_ads
		//      WHERE (channel_id, play_begin_date) IN (
		//		    SELECT channel_id, MAX(play_begin_date)
		//		    FROM rev_chan_ads
		//		    GROUP BY channel_id)
		//
		String sql = "SELECT channel_id, seq, play_begin_date, play_end_date, hint " +
					"FROM rev_chan_ads " +
					"WHERE (channel_id, play_begin_date) IN ( " +
					"SELECT channel_id, MAX(play_begin_date) " +
					"FROM rev_chan_ads " +
					"GROUP BY channel_id)";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, int channelId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		
		Criterion criterion = Restrictions.and(
				Restrictions.eq("channelId", channelId),
				Restrictions.ge("playBeginDate", Util.addDays(new Date(), -1)));
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), RevChanAd.class, map, criterion);
	}

	@Override
	public List<RevChanAd> getListByChannelId(int channelId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevChanAd> criteria = cb.createQuery(RevChanAd.class);
		Root<RevChanAd> oRoot = criteria.from(RevChanAd.class);
		
		criteria.select(oRoot);
		criteria.where(cb.equal(oRoot.get("channelId"), channelId));
		criteria.orderBy(cb.asc(oRoot.get("playBeginDate")));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
	}

	@Override
	public List<RevChanAd> getCurrListByChannelId(int channelId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevChanAd> criteria = cb.createQuery(RevChanAd.class);
		Root<RevChanAd> oRoot = criteria.from(RevChanAd.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("channelId"), channelId),
				cb.greaterThanOrEqualTo(oRoot.get("playEndDate"), new Date())
		);
		criteria.orderBy(cb.asc(oRoot.get("playBeginDate")));
		
		return sessionFactory.getCurrentSession().createQuery(criteria)
				.getResultList();
	}

	@Override
	public RevChanAd getLastByChannelIdSeq(int channelId, int seq) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevChanAd> criteria = cb.createQuery(RevChanAd.class);
		Root<RevChanAd> oRoot = criteria.from(RevChanAd.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("channelId"), channelId),
				cb.equal(oRoot.get("seq") , seq)
		);
		criteria.orderBy(cb.desc(oRoot.get("playBeginDate")));

		List<RevChanAd> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public int getCount() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT count(*) FROM rev_chan_ads
		//
		String sql = "SELECT count(*) FROM rev_chan_ads";
		
		Tuple tuple = session.createNativeQuery(sql, Tuple.class)
				.getSingleResult();
		
		return ((BigInteger) tuple.get(0)).intValue();
	}

	@Override
	public int deleteBefore(Date date) {
		
		int beforeRows = getCount();

		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		DELETE FROM rev_chan_ads
		//      WHERE play_begin_date < :date LIMIT 10000
		//
		String sql = "DELETE FROM rev_chan_ads WHERE play_begin_date < :date LIMIT 10000";
		
		session.createNativeQuery(sql)
				.setParameter("date", date)
				.executeUpdate();
		
		int diff = beforeRows - getCount();
		
		return diff < 0 ? 0 : diff;
	}

}
