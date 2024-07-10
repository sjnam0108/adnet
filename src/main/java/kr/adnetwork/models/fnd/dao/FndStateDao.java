package kr.adnetwork.models.fnd.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.fnd.FndState;

public interface FndStateDao {
	// Common
	public FndState get(int id);
	public void saveOrUpdate(FndState state);
	public void delete(FndState state);
	public void delete(List<FndState> states);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndState get(String name);
	public List<FndState> getListByListIncluded(boolean listIncluded);
	public List<FndState> getListByNameLike(String name);
}
