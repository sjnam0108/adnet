package net.doohad.models.fnd.dao;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndSetupFile;

@Transactional
@Component
public class FndSetupFileDaoImpl implements FndSetupFileDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public FndSetupFile get(int id) {
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndSetupFile> criteria = cb.createQuery(FndSetupFile.class);
		Root<FndSetupFile> oRoot = criteria.from(FndSetupFile.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<FndSetupFile> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(FndSetupFile setupFile) {
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(setupFile);
	}

	@Override
	public void delete(FndSetupFile setupFile) {
		Session session = sessionFactory.getCurrentSession();
		
		session.delete(session.load(FndSetupFile.class, setupFile.getId()));
	}

	@Override
	public void delete(List<FndSetupFile> setupFiles) {
		Session session = sessionFactory.getCurrentSession();
		
        for (FndSetupFile setupFile : setupFiles) {
            session.delete(session.load(FndSetupFile.class, setupFile.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), FndSetupFile.class);
	}

	@Override
	public FndSetupFile get(String filename) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndSetupFile> criteria = cb.createQuery(FndSetupFile.class);
		Root<FndSetupFile> oRoot = criteria.from(FndSetupFile.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("filename"), filename));

		List<FndSetupFile> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public FndSetupFile getLastVer(String prodKeyword, int verNumber, String platKeyword) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<FndSetupFile> criteria = cb.createQuery(FndSetupFile.class);
		Root<FndSetupFile> oRoot = criteria.from(FndSetupFile.class);
		
		criteria.select(oRoot).where(
				cb.equal(oRoot.get("activeStatus"), true),
				cb.equal(oRoot.get("prodKeyword"), prodKeyword),
				cb.greaterThan(oRoot.get("verNumber"), verNumber),
				cb.equal(oRoot.get("platKeyword"), platKeyword)
		);
		criteria.orderBy(cb.desc(oRoot.get("verNumber")), cb.desc(oRoot.get("whoCreationDate")));

		List<FndSetupFile> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

}
