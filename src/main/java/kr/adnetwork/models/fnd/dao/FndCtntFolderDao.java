package kr.adnetwork.models.fnd.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.fnd.FndCtntFolder;

public interface FndCtntFolderDao {
	// Common
	public FndCtntFolder get(int id);
	public void saveOrUpdate(FndCtntFolder ctntFolder);
	public void delete(FndCtntFolder ctntFolder);
	public void delete(List<FndCtntFolder> ctntFolders);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndCtntFolder get(String name);
	public int getCount();
	public List<FndCtntFolder> getList();

}
