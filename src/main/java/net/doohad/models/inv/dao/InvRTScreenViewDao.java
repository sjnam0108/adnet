package net.doohad.models.inv.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;

public interface InvRTScreenViewDao {
	// Common

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getListByScreenIdIn(DataSourceRequest request, List<Integer> list);

	// for DAO specific

}
