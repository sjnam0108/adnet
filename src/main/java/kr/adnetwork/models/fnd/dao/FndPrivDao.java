package kr.adnetwork.models.fnd.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.fnd.FndPriv;

public interface FndPrivDao {
	// Common
	public FndPriv get(int id);
	public void saveOrUpdate(FndPriv priv);
	public void delete(FndPriv priv);
	public void delete(List<FndPriv> privs);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndPriv get(String ukid);
	public FndPriv get(org.hibernate.Session hnSession, String ukid);
}
