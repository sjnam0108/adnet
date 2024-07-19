package kr.adnetwork.models.org.dao;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgChannel;
import kr.adnetwork.utils.SolUtil;

@Transactional
@Component
public class OrgChannelDaoImpl implements OrgChannelDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgChannel get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChannel> criteria = cb.createQuery(OrgChannel.class);
		Root<OrgChannel> oRoot = criteria.from(OrgChannel.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgChannel> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgChannel channel) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(channel);
	}

	@Override
	public void delete(OrgChannel channel) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), OrgChannel.class, channel.getId());
	}

	@Override
	public void delete(List<OrgChannel> channels) {

		Session session = sessionFactory.getCurrentSession();
		
        for (OrgChannel channel : channels) {
            session.delete(session.load(OrgChannel.class, channel.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), OrgChannel.class, map);
	}

	@Override
	public OrgChannel get(KnlMedium medium, String shortName) {
		
		if (medium == null) {
			return null;
		}

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChannel> criteria = cb.createQuery(OrgChannel.class);
		Root<OrgChannel> oRoot = criteria.from(OrgChannel.class);
		Join<OrgChannel, KnlMedium> joinO = oRoot.join("medium");
		
		criteria.select(oRoot).where(
				cb.and(cb.equal(joinO.get("id"), medium.getId())), cb.equal(oRoot.get("shortName"), shortName));

		List<OrgChannel> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public List<OrgChannel> getListByMediumId(int mediumId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChannel> criteria = cb.createQuery(OrgChannel.class);
		Root<OrgChannel> oRoot = criteria.from(OrgChannel.class);
		Join<OrgChannel, KnlMedium> joinO = oRoot.join("medium");

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
				cb.equal(joinO.get("id"), mediumId))).getResultList();
	}

	@Override
	public List<OrgChannel> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChannel> criteria = cb.createQuery(OrgChannel.class);
		Root<OrgChannel> oRoot = criteria.from(OrgChannel.class);
		Join<OrgChannel, KnlMedium> joinO = oRoot.join("medium");
		
		Expression<Boolean> exp1 = cb.equal(joinO.get("id"), mediumId);
		Expression<Boolean> exp2 = cb.equal(oRoot.get("activeStatus"), activeStatus);

		return sessionFactory.getCurrentSession()
				.createQuery(criteria.select(oRoot).where(cb.and(exp1, exp2)))
				.getResultList();
	}

	@Override
	public List<OrgChannel> getActiveList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChannel> criteria = cb.createQuery(OrgChannel.class);
		Root<OrgChannel> oRoot = criteria.from(OrgChannel.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("activeStatus"), true)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

	@Override
	public List<Tuple> getTupleListByTypeObjId(String type, int objId) {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT c.channel_id, c.name, c.active_status, c.view_type_code,
		//             c.priority, c.short_name
		//		FROM org_channels c, org_chan_subs cs
		//      WHERE c.channel_id = cs.channel_id
		//      AND cs.type = :type AND cs.obj_id = :objId
		//      ORDER BY c.priority
		//
		String sql = "SELECT c.channel_id, c.name, c.active_status, c.view_type_code, " +
					"c.priority, c.short_name " +
					"FROM org_channels c, org_chan_subs cs " +
					"WHERE c.channel_id = cs.channel_id " +
					"AND cs.type = :type AND cs.obj_id = :objId " +
					"ORDER BY c.priority, c.short_name";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.setParameter("type", type)
				.setParameter("objId", objId)
				.getResultList();
	}

	@Override
	public List<OrgChannel> getAdAppendableList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgChannel> criteria = cb.createQuery(OrgChannel.class);
		Root<OrgChannel> oRoot = criteria.from(OrgChannel.class);
		
		criteria.select(oRoot);
		criteria.where(
				cb.equal(oRoot.get("adAppended"), true)
		);

		return sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
	}

}
