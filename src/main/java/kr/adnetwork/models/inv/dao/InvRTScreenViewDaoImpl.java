package kr.adnetwork.models.inv.dao;

import java.util.HashMap;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvRTScreenView;

@Transactional
@Component
public class InvRTScreenViewDaoImpl implements InvRTScreenViewDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvRTScreenView.class);
	}

	@Override
	public DataSourceResult getListByScreenIdIn(DataSourceRequest request, List<Integer> list) {
		
		HashMap<String, Class<?>> map = new HashMap<String, Class<?>>();
		
		Criterion criterion = Restrictions.in("id", list);
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), InvRTScreenView.class, 
        		map, criterion);
	}

}
