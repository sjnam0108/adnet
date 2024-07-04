package net.doohad.models.sys.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.sys.SysOpt;
import net.doohad.utils.SolUtil;

@Transactional
@Component
public class SysOptDaoImpl implements SysOptDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public SysOpt get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysOpt> criteria = cb.createQuery(SysOpt.class);
		Root<SysOpt> oRoot = criteria.from(SysOpt.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<SysOpt> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(SysOpt opt) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(opt);
	}

	@Override
	public void delete(SysOpt opt) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), SysOpt.class, opt.getId());
	}

	@Override
	public void delete(List<SysOpt> opts) {

		Session session = sessionFactory.getCurrentSession();
		
        for (SysOpt opt : opts) {
            session.delete(session.load(SysOpt.class, opt.getId()));
        }
	}

	@Override
	public SysOpt get(String code) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<SysOpt> criteria = cb.createQuery(SysOpt.class);
		Root<SysOpt> oRoot = criteria.from(SysOpt.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("code"), code));

		List<SysOpt> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
