package kr.adnetwork.models.adc.dao;

import java.util.List;

import kr.adnetwork.models.adc.AdcMobTarget;

public interface AdcMobTargetDao {
	// Common
	public AdcMobTarget get(int id);
	public void saveOrUpdate(AdcMobTarget mobTarget);
	public void delete(AdcMobTarget mobTarget);
	public void delete(List<AdcMobTarget> mobTargets);

	// for Kendo Grid Remote Read

	// for DAO specific
	public List<AdcMobTarget> getListByAdId(int adId);
	public void saveAndReorder(AdcMobTarget mobTarget);
	public List<AdcMobTarget> getListByMediumId(int mediumId);
	public int getCountByAdId(int adId);
	public int getCountByMobTypeTgtId(String mobType, int tgtId);

}
