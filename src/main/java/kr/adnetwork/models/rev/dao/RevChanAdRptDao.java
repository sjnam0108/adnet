package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.rev.RevChanAdRpt;

public interface RevChanAdRptDao {
	// Common
	public RevChanAdRpt get(int id);
	public void saveOrUpdate(RevChanAdRpt chanAdRpt);
	public void delete(RevChanAdRpt chanAdRpt);
	public void delete(List<RevChanAdRpt> chanAdRpts);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, String type, int objId);

	// for DAO specific
	public int getCount();
	public int deleteBefore(Date date);

}
