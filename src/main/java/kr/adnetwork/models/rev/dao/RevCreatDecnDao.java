package kr.adnetwork.models.rev.dao;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.rev.RevCreatDecn;

public interface RevCreatDecnDao {
	// Common
	public RevCreatDecn get(int id);
	public void saveOrUpdate(RevCreatDecn creatDecn);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific

}
