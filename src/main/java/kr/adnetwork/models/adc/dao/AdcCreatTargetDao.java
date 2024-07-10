package kr.adnetwork.models.adc.dao;

import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcCreatTarget;

public interface AdcCreatTargetDao {
	// Common
	public AdcCreatTarget get(int id);
	public void saveOrUpdate(AdcCreatTarget creatTarget);
	public void delete(AdcCreatTarget creatTarget);
	public void delete(List<AdcCreatTarget> creatTargets);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public List<AdcCreatTarget> getListByCreativeId(int creativeId);
	public void saveAndReorder(AdcCreatTarget creatTarget);
	public List<Tuple> getCountGroupByMediumCreativeId(int mediumId);
	public List<Tuple> getCountGroupByCreativeId();
	public List<AdcCreatTarget> getListByMediumId(int mediumId);
	
}
