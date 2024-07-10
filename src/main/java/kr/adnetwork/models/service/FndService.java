package kr.adnetwork.models.service;

import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.springframework.transaction.annotation.Transactional;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.fnd.FndCtntFolder;
import kr.adnetwork.models.fnd.FndLoginLog;
import kr.adnetwork.models.fnd.FndMobRegion;
import kr.adnetwork.models.fnd.FndPriv;
import kr.adnetwork.models.fnd.FndRegion;
import kr.adnetwork.models.fnd.FndSetupFile;
import kr.adnetwork.models.fnd.FndState;
import kr.adnetwork.models.fnd.FndUserPriv;
import kr.adnetwork.models.fnd.FndViewType;

@Transactional
public interface FndService {
	
	// Common
	public void flush();

	
	//
	// for FndRegion
	//
	// Common
	public FndRegion getRegion(int id);
	public void saveOrUpdate(FndRegion region);
	public void deleteRegion(FndRegion region);
	public void deleteRegions(List<FndRegion> regions);

	// for Kendo Grid Remote Read
	public DataSourceResult getRegionList(DataSourceRequest request);

	// for DAO specific
	public FndRegion getRegion(String code);
	public FndRegion getRegionByName(String name);
	public List<FndRegion> getRegionListByListIncluded(boolean listIncluded);
	public List<FndRegion> getRegionListByNameLike(String name);

	
	//
	// for FndLoginLog
	//
	// Common
	public FndLoginLog getLoginLog(int id);
	public void saveOrUpdate(FndLoginLog loginLog);
	public void deleteLoginLog(FndLoginLog loginLog);
	public void deleteLoginLogs(List<FndLoginLog> loginLogs);

	// for Kendo Grid Remote Read
	public DataSourceResult getLoginLogList(DataSourceRequest request);

	// for DAO specific
	public FndLoginLog getLastLoginLogByUserId(int userId);

	
	//
	// for FndPriv
	//
	// Common
	public FndPriv getPriv(int id);
	public void saveOrUpdate(FndPriv priv);
	public void deletePriv(FndPriv priv);
	public void deletePrivs(List<FndPriv> privs);

	// for Kendo Grid Remote Read
	public DataSourceResult getPrivList(DataSourceRequest request);

	// for DAO specific
	public FndPriv getPriv(String ukid);
	public FndPriv getPriv(org.hibernate.Session hnSession, String ukid);

	
	//
	// for FndUserPriv
	//
	// Common
	public FndUserPriv getUserPriv(int id);
	public void saveOrUpdate(FndUserPriv userPriv);
	public void deleteUserPriv(FndUserPriv userPriv);
	public void deleteUserPrivs(List<FndUserPriv> userPrivs);

	// for Kendo Grid Remote Read
	public DataSourceResult getUserPrivList(DataSourceRequest request);

	// for DAO specific
	public boolean isRegisteredUserPriv(int userId, int privId);
	public List<FndUserPriv> getUserPrivListByUserId(int userId);

	
	//
	// for FndCtntFolder
	//
	// Common
	public FndCtntFolder getCtntFolder(int id);
	public void saveOrUpdate(FndCtntFolder ctntFolder);
	public void deleteCtntFolder(FndCtntFolder ctntFolder);
	public void deleteCtntFolders(List<FndCtntFolder> ctntFolders);

	// for Kendo Grid Remote Read
	public DataSourceResult getCtntFolderList(DataSourceRequest request);

	// for DAO specific
	public FndCtntFolder getCtntFolder(String name);
	public int getCtntFolderCount();
	public List<FndCtntFolder> getCtntFolderList();

	
	//
	// for FndState
	//
	// Common
	public FndState getState(int id);
	public void saveOrUpdate(FndState state);
	public void deleteState(FndState state);
	public void deleteStates(List<FndState> states);

	// for Kendo Grid Remote Read
	public DataSourceResult getStateList(DataSourceRequest request);

	// for DAO specific
	public FndState getState(String name);
	public List<FndState> getStateListByListIncluded(boolean listIncluded);
	public List<FndState> getStateListByNameLike(String name);

	
	//
	// for FndViewType
	//
	// Common
	public FndViewType getViewType(int id);
	public void saveOrUpdate(FndViewType viewType);
	public void deleteViewType(FndViewType viewType);
	public void deleteViewTypes(List<FndViewType> viewTypes);

	// for Kendo Grid Remote Read
	public DataSourceResult getViewTypeList(DataSourceRequest request);

	// for DAO specific
	public FndViewType getViewType(String code, String resolution);
	public List<FndViewType> getViewTypeList();
	public String getViewTypeResoByCode(String code);
	public List<Tuple> getViewTypeMaxLaneGroupByMediumId();

	
	//
	// for FndSetupFile
	//
	// Common
	public FndSetupFile getSetupFile(int id);
	public void saveOrUpdate(FndSetupFile setupFile);
	public void deleteSetupFile(FndSetupFile setupFile);
	public void deleteSetupFiles(List<FndSetupFile> setupFiles);

	// for Kendo Grid Remote Read
	public DataSourceResult getSetupFileList(DataSourceRequest request);

	// for DAO specific
	public FndSetupFile getSetupFile(String filename);
	public FndSetupFile getLastVerSetupFile(String prodKeyword, int verNumber, String playKeyword);

	
	//
	// for FndMobRegion
	//
	// Common
	public FndMobRegion getMobRegion(int id);
	public void saveOrUpdate(FndMobRegion region);
	public void deleteMobRegion(FndMobRegion region);
	public void deleteMobRegions(List<FndMobRegion> regions);

	// for Kendo Grid Remote Read
	public DataSourceResult getMobRegionList(DataSourceRequest request);
	public DataSourceResult getActiveMobRegionList(DataSourceRequest request);

	// for DAO specific
	public FndMobRegion getMobRegion(String name);
	public List<FndMobRegion> getMobRegionListByActiveStatus(boolean activeStatus);

	
	//
	// for Common
	//
	public void logout(HttpSession session);
	public void logout(HttpSession session, boolean forcedMode);
	public List<String> getAllUserPrivs(int userId);
	public FndCtntFolder getDefCtntFolder();

}
