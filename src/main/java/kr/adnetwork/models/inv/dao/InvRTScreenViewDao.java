package kr.adnetwork.models.inv.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;

public interface InvRTScreenViewDao {
	// Common

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getListByScreenIdIn(DataSourceRequest request, List<Integer> list);

	// for DAO specific

}
