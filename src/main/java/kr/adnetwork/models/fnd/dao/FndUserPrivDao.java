package kr.adnetwork.models.fnd.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.fnd.FndUserPriv;

public interface FndUserPrivDao {
	// Common
	public FndUserPriv get(int id);
	public void saveOrUpdate(FndUserPriv userPriv);
	public void delete(FndUserPriv userPriv);
	public void delete(List<FndUserPriv> userPrivs);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public boolean isRegistered(int userId, int privId);
	public List<FndUserPriv> getListByUserId(int userId);
}
