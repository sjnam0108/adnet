package kr.adnetwork.models.knl.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlAccount;

public interface KnlAccountDao {
	// Common
	public KnlAccount get(int id);
	public void saveOrUpdate(KnlAccount account);
	public void delete(KnlAccount account);
	public void delete(List<KnlAccount> accounts);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public KnlAccount get(String name);
	public List<KnlAccount> getValidList();
}
