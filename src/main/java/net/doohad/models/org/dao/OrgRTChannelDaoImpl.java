package net.doohad.models.org.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.org.OrgRTChannel;

@Transactional
@Component
public class OrgRTChannelDaoImpl implements OrgRTChannelDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public OrgRTChannel get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgRTChannel> criteria = cb.createQuery(OrgRTChannel.class);
		Root<OrgRTChannel> oRoot = criteria.from(OrgRTChannel.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<OrgRTChannel> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(OrgRTChannel rtChannel) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rtChannel);
	}

	@Override
	public void delete(OrgRTChannel rtChannel) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(OrgRTChannel.class, rtChannel.getId()));
	}

	@Override
	public void delete(List<OrgRTChannel> rtChannels) {
		Session session = sessionFactory.getCurrentSession();
		
        for (OrgRTChannel rtChannel : rtChannels) {
            session.delete(session.load(OrgRTChannel.class, rtChannel.getId()));
        }
	}

	@Override
	public OrgRTChannel getByChannelId(int channelId) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<OrgRTChannel> criteria = cb.createQuery(OrgRTChannel.class);
		Root<OrgRTChannel> oRoot = criteria.from(OrgRTChannel.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("channelId"), channelId));

		List<OrgRTChannel> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
