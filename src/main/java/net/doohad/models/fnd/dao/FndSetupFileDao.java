package net.doohad.models.fnd.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.fnd.FndSetupFile;

public interface FndSetupFileDao {
	// Common
	public FndSetupFile get(int id);
	public void saveOrUpdate(FndSetupFile setupFile);
	public void delete(FndSetupFile setupFile);
	public void delete(List<FndSetupFile> setupFiles);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public FndSetupFile get(String filename);
	public FndSetupFile getLastVer(String prodKeyword, int verNumber, String platKeyword);

}
