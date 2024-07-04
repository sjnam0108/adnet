package net.doohad.models.adc.dao;

import java.util.List;

import javax.persistence.Tuple;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.adc.AdcMobTargetView;

@Transactional
@Component
public class AdcMobTargetViewDaoImpl implements AdcMobTargetViewDao {

    @Autowired
    private SessionFactory sessionFactory;

	@Override
	public DataSourceResult getList(DataSourceRequest request) {
		
        return request.toDataSourceResult(sessionFactory.getCurrentSession(), AdcMobTargetView.class);
	}

	@Override
	public List<Tuple> getItemList() {
		
		Session session = sessionFactory.getCurrentSession();

		// SQL:
		//
		//		SELECT a.ad_id, mt.filter_type as filter_type, mt.mob_type as mob_type,
		//             IF(mob_type = 'RG', mr.active_status, rr.active_status) as active_status,
		//			   IF(mob_type = 'RG', mr.gc_name, '') as gc_name,
		//			   IF(mob_type = 'RG', 0, rr.lat) as lat,
		//			   IF(mob_type = 'RG', 0, rr.lng) as lng,
		//			   IF(mob_type = 'RG', 0, rr.radius) as radius
		//		FROM adc_ads a, adc_mob_targets mt
		//		LEFT OUTER JOIN fnd_mob_regions mr ON mr.region_id = mt.tgt_id AND mt.mob_type = 'RG' AND mr.active_status = 1 
		//		LEFT OUTER JOIN org_rad_regions rr ON rr.rad_region_id = mt.TGT_ID AND mt.mob_type = 'CR'  AND rr.active_status = 1
		//		WHERE mt.ad_id = a.ad_id
		//		ORDER BY ad_id, sibling_seq
		//
		String sql = "SELECT a.ad_id, mt.filter_type as filter_type, mt.mob_type as mob_type, " +
					"IF(mob_type = 'RG', mr.active_status, rr.active_status) as active_status, " +
					"IF(mob_type = 'RG', mr.gc_name, '') as gc_name, " +
					"IF(mob_type = 'RG', 0, rr.lat) as lat, " +
					"IF(mob_type = 'RG', 0, rr.lng) as lng, " +
					"IF(mob_type = 'RG', 0, rr.radius) as radius " +
					"FROM adc_ads a, adc_mob_targets mt " +
					"LEFT OUTER JOIN fnd_mob_regions mr ON mr.region_id = mt.tgt_id AND mt.mob_type = 'RG' AND mr.active_status = 1 " +
					"LEFT OUTER JOIN org_rad_regions rr ON rr.rad_region_id = mt.TGT_ID AND mt.mob_type = 'CR'  AND rr.active_status = 1 " +
					"WHERE mt.ad_id = a.ad_id " +
					"ORDER BY ad_id, sibling_seq";
		
		
		return session.createNativeQuery(sql, Tuple.class)
				.getResultList();
	}

}
