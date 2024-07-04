package net.doohad.models.fnd.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndViewType;

public interface FndViewTypeDao {
	// Common
	public FndViewType get(int id);
	public void saveOrUpdate(FndViewType viewType);
	public void delete(FndViewType viewType);
	public void delete(List<FndViewType> viewTypes);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndViewType get(String code, String resolution);
	public List<FndViewType> getList();
	public String getResoByCode(String code);
	public List<Tuple> getMaxLaneGroupByMediumId();
}
