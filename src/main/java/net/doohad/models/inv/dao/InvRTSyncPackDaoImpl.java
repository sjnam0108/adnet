package net.doohad.models.inv.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.inv.InvRTSyncPack;

@Transactional
@Component
public class InvRTSyncPackDaoImpl implements InvRTSyncPackDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public InvRTSyncPack get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvRTSyncPack> criteria = cb.createQuery(InvRTSyncPack.class);
		Root<InvRTSyncPack> oRoot = criteria.from(InvRTSyncPack.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<InvRTSyncPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(InvRTSyncPack rtSyncPack) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(rtSyncPack);
	}

	@Override
	public void delete(InvRTSyncPack rtSyncPack) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(InvRTSyncPack.class, rtSyncPack.getId()));
	}

	@Override
	public void delete(List<InvRTSyncPack> rtSyncPacks) {
		Session session = sessionFactory.getCurrentSession();
		
        for (InvRTSyncPack rtSyncPack : rtSyncPacks) {
            session.delete(session.load(InvRTSyncPack.class, rtSyncPack.getId()));
        }
	}

	@Override
	public InvRTSyncPack getBySyncPackId(int syncPackId) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<InvRTSyncPack> criteria = cb.createQuery(InvRTSyncPack.class);
		Root<InvRTSyncPack> oRoot = criteria.from(InvRTSyncPack.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("syncPackId"), syncPackId));

		List<InvRTSyncPack> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
