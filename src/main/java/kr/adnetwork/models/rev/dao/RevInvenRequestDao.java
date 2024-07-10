package kr.adnetwork.models.rev.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.rev.RevInvenRequest;

public interface RevInvenRequestDao {
	// Common
	public RevInvenRequest get(int id);
	public void saveOrUpdate(RevInvenRequest invenRequest);
	public void delete(RevInvenRequest invenRequest);
	public void delete(List<RevInvenRequest> invenRequests);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific

}
