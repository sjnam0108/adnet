package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.rev.RevImpWave;
import kr.adnetwork.utils.Util;

@Transactional
@Component
public class RevImpWaveDaoImpl implements RevImpWaveDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public RevImpWave get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevImpWave> criteria = cb.createQuery(RevImpWave.class);
		Root<RevImpWave> oRoot = criteria.from(RevImpWave.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<RevImpWave> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(RevImpWave impWave) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(impWave);
	}

	@Override
	public void delete(RevImpWave impWave) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(RevImpWave.class, impWave.getId()));
	}

	@Override
	public void delete(List<RevImpWave> impWaves) {
		Session session = sessionFactory.getCurrentSession();
		
        for (RevImpWave impWave : impWaves) {
            session.delete(session.load(RevImpWave.class, impWave.getId()));
        }
	}

	@Override
	public List<RevImpWave> getEffList() {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<RevImpWave> criteria = cb.createQuery(RevImpWave.class);
		Root<RevImpWave> oRoot = criteria.from(RevImpWave.class);

		return sessionFactory.getCurrentSession().createQuery(criteria.select(oRoot).where(
					cb.greaterThanOrEqualTo(oRoot.get("whoCreationDate"), Util.addMinutes(new Date(), -5))
				).orderBy(cb.asc(oRoot.get("id"))))
				.getResultList();
	}

}
