package kr.adnetwork.models.adc.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import kr.adnetwork.models.adc.AdcPlaylist;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.utils.SolUtil;
import kr.adnetwork.utils.Util;

@Transactional
@Component
public class AdcPlaylistDaoImpl implements AdcPlaylistDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public AdcPlaylist get(int id) {
		
		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcPlaylist> criteria = cb.createQuery(AdcPlaylist.class);
		Root<AdcPlaylist> oRoot = criteria.from(AdcPlaylist.class);
		
		criteria.select(oRoot).where(cb.equal(oRoot.get("id"), id));

		List<AdcPlaylist> list = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
		
		return (list.isEmpty() ? null : list.get(0));
	}

	@Override
	public void saveOrUpdate(AdcPlaylist playlist) {
		
		Session session = sessionFactory.getCurrentSession();
		
		session.saveOrUpdate(playlist);
	}

	@Override
	public void delete(AdcPlaylist playlist) {
		
		SolUtil.delete(sessionFactory.getCurrentSession(), AdcPlaylist.class, playlist.getId());
	}

	@Override
	public void delete(List<AdcPlaylist> playlists) {

		Session session = sessionFactory.getCurrentSession();
		
        for (AdcPlaylist playlist : playlists) {
            session.delete(session.load(AdcPlaylist.class, playlist.getId()));
        }
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcPlaylist.class, map);
	}

	@Override
	public DataSourceResult getList(DataSourceRequest request, String viewType) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		map.put("medium", KnlMedium.class);
		
		// 조회 필터는 2가지: 전체(A) or 사용중/계획중(F)
		// 전체 옵션을 기본으로 하기 때문에 값의 유효성 검사는 F에 대해서만 하고
		// F가 유효할 경우만 해당 프로세스로, 아니면 원래대로 진행
		if (Util.isValid(viewType) && viewType.equals("F")) {
			
			Criterion criterion = Restrictions.or(
					Restrictions.eq("activeStatus", true),
					Restrictions.gt("startDate", new Date()));
			
			return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcPlaylist.class, map, criterion);
		} else {
			
	        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcPlaylist.class, map);
		}
	}

	@Override
	public List<AdcPlaylist> getActiveListByChannelId(int channelId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcPlaylist> criteria = cb.createQuery(AdcPlaylist.class);
		Root<AdcPlaylist> oRoot = criteria.from(AdcPlaylist.class);
		
		return sessionFactory.getCurrentSession()
				.createQuery(criteria.select(oRoot).where(
						cb.equal(oRoot.get("activeStatus"), true),
						cb.equal(oRoot.get("channelId"), channelId))
					.orderBy(cb.asc(oRoot.get("startDate"))))
				.getResultList();
	}

	@Override
	public List<AdcPlaylist> getListByChannelId(int channelId) {

		CriteriaBuilder cb = sessionFactory.getCurrentSession().getCriteriaBuilder();
		
		CriteriaQuery<AdcPlaylist> criteria = cb.createQuery(AdcPlaylist.class);
		Root<AdcPlaylist> oRoot = criteria.from(AdcPlaylist.class);
		
		return sessionFactory.getCurrentSession()
				.createQuery(criteria.select(oRoot).where(
						cb.equal(oRoot.get("channelId"), channelId))
					.orderBy(cb.asc(oRoot.get("startDate"))))
				.getResultList();
	}

}
