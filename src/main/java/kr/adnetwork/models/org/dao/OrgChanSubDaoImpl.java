package kr.adnetwork.models.org.dao;

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
import kr.adnetwork.models.org.OrgChanSub;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.utils.SolUtil;

@Transactional
@Component
public class OrgChanSubDaoImpl implements OrgChanSubDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgChanSub get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChanSub> criteria = cb.createQuery(OrgChanSub.class);
		Root<OrgChanSub> oRoot = criteria.from(OrgChanSub.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgChanSub> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgChanSub chanSub) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(chanSub);
	}

	@Override
	public void delete(OrgChanSub chanSub) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgChanSub.class, chanSub.getId());
	}

	@Override
	public void delete(List<OrgChanSub> chanSubs) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgChanSub chanSub : chanSubs) {
            session.delete(session.load(OrgChanSub.class, chanSub.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, String type, int channelId) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("channel", OrgChannel.class);
		
		Criterion criterion = Restrictions.and(
				Restrictions.eq("type", type), Restrictions.eq("channel.id", channelId));
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgChanSub.class, map, criterion);
	}

	@Override
	public OrgChanSub get(OrgChannel channel, String type, int objId) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChanSub> criteria = cb.createQuery(OrgChanSub.class);
		Root<OrgChanSub> oRoot = criteria.from(OrgChanSub.class);
		Join<OrgChanSub, OrgChannel> joinO = oRoot.join("channel");
		
		criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), channel.getId()), 
				cb.equal(oRoot.get("type"), type),
				cb.equal(oRoot.get("objId"), objId)
		);

		List<OrgChanSub> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public int getCountByChannelId(int channelId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		
		CriteriaQuery<Long> criteria = cb.createQuery(Long.class);
		Root<OrgChanSub> oRoot = criteria.from(OrgChanSub.class);
		Join<OrgChanSub, OrgChannel> joinO = oRoot.join("channel");
		
		criteria.select(cb.count(oRoot)).where(
				cb.equal(joinO.get("id"), channelId)
		);
		
		return (sessionFactory.getCurrentSession().createQuery(criteria).getSingleResult()).intValue();
	}

	@Override
	public List<Tuple> getScrTupleListByChannelId(int channelId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT s.short_name, s.name, s.screen_id
		//		FROM org_channels c, org_chan_subs cs, inv_screens s
		//      WHERE c.channel_id = cs.channel_id AND cs.obj_id = s.screen_id
		//      AND cs.type = 'S' AND c.channel_id = :channelId
		//      ORDER BY s.short_name
		//
		String sql = "SELECT s.short_name, s.name, s.screen_id " +
					"FROM org_channels c, org_chan_subs cs, inv_screens s " +
					"WHERE c.channel_id = cs.channel_id AND cs.obj_id = s.screen_id " +
					"AND cs.type = 'S' AND c.channel_id = :channelId " +
					"ORDER BY s.short_name";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("channelId", channelId)
				.getResultList();
	}

	@Override
	public List<Tuple> getSyncPackTupleListByChannelId(int channelId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT sp.short_name, sp.name, sp.sync_pack_id
		//		FROM org_channels c, org_chan_subs cs, inv_sync_packs sp
		//      WHERE c.channel_id = cs.channel_id AND cs.obj_id = sp.sync_pack_id
		//      AND cs.type = 'P' AND c.channel_id = :channelId
		//      ORDER BY sp.short_name
		//
		String sql = "SELECT sp.short_name, sp.name, sp.sync_pack_id " +
					"FROM org_channels c, org_chan_subs cs, inv_sync_packs sp " +
					"WHERE c.channel_id = cs.channel_id AND cs.obj_id = sp.sync_pack_id " +
					"AND cs.type = 'P' AND c.channel_id = :channelId " +
					"ORDER BY sp.short_name";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("channelId", channelId)
				.getResultList();
	}

	@Override
	public void deleteBySyncPackId(int syncPackId) {

		Session session = sessionFactory.getCurrentSession();

		String sql = "DELETE FROM org_chan_subs WHERE type = 'P' and obj_id = :syncPackId";
		
		session.createNativeQuery(sql)
				.setParameter("syncPackId", syncPackId)
				.executeUpdate();
	}

}
