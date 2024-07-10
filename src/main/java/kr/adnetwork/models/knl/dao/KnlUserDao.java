package kr.adnetwork.models.knl.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlUser;

public interface KnlUserDao {
	// Common
	public KnlUser get(int id);
	public void saveOrUpdate(KnlUser user);
	public void delete(KnlUser user);
	public void delete(List<KnlUser> users);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public KnlUser get(String shortName);
	public List<KnlUser> getListByMediumId(int mediumId);
	public List<KnlUser> getM1List();
}
