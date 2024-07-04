package net.doohad.models.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.LoginUser;
import net.doohad.models.fnd.FndCtntFolder;
import net.doohad.models.fnd.FndLoginLog;
import net.doohad.models.fnd.FndMobRegion;
import net.doohad.models.fnd.FndPriv;
import net.doohad.models.fnd.FndRegion;
import net.doohad.models.fnd.FndSetupFile;
import net.doohad.models.fnd.FndState;
import net.doohad.models.fnd.FndUserPriv;
import net.doohad.models.fnd.FndViewType;
import net.doohad.models.fnd.dao.FndCtntFolderDao;
import net.doohad.models.fnd.dao.FndLoginLogDao;
import net.doohad.models.fnd.dao.FndMobRegionDao;
import net.doohad.models.fnd.dao.FndPrivDao;
import net.doohad.models.fnd.dao.FndRegionDao;
import net.doohad.models.fnd.dao.FndSetupFileDao;
import net.doohad.models.fnd.dao.FndStateDao;
import net.doohad.models.fnd.dao.FndUserPrivDao;
import net.doohad.models.fnd.dao.FndViewTypeDao;

@Transactional
@Service("fndService")
public class FndServiceImpl implements FndService {

    //
    // General
    //
    @Autowired
    private SessionFactory sessionFactory;
    
	@Override
	public void flush() {
		
		sessionFactory.getCurrentSession().flush();
	}

	
    
    //
    // DAO
    //
    @Autowired
    private FndRegionDao regionDao;
    
    @Autowired
    private FndLoginLogDao loginLogDao;
    
    @Autowired
    private FndPrivDao privDao;
    
    @Autowired
    private FndUserPrivDao userPrivDao;
    
    @Autowired
    private FndCtntFolderDao ctntFolderDao;
    
    @Autowired
    private FndStateDao stateDao;
    
    @Autowired
    private FndViewTypeDao viewTypeDao;
    
    @Autowired
    private FndSetupFileDao setupFileDao;
    
    @Autowired
    private FndMobRegionDao mobRegionDao;

    
    
	//
	// for FndRegionDao
	//
	@Override
	public FndRegion getRegion(int id) {
		return regionDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndRegion region) {
		regionDao.saveOrUpdate(region);
	}

	@Override
	public void deleteRegion(FndRegion region) {
		regionDao.delete(region);
	}

	@Override
	public void deleteRegions(List<FndRegion> regions) {
		regionDao.delete(regions);
	}

	@Override
	public DataSourceResult getRegionList(DataSourceRequest request) {
		return regionDao.getList(request);
	}

	@Override
	public FndRegion getRegion(String code) {
		return regionDao.get(code);
	}

	@Override
	public FndRegion getRegionByName(String name) {
		return regionDao.getByName(name);
	}

	@Override
	public List<FndRegion> getRegionListByListIncluded(boolean listIncluded) {
		return regionDao.getListByListIncluded(listIncluded);
	}

	@Override
	public List<FndRegion> getRegionListByNameLike(String name) {
		return regionDao.getListByNameLike(name);
	}

    
    
	//
	// for FndLoginLogDao
	//
	@Override
	public FndLoginLog getLoginLog(int id) {
		return loginLogDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndLoginLog loginLog) {
		loginLogDao.saveOrUpdate(loginLog);
	}

	@Override
	public void deleteLoginLog(FndLoginLog loginLog) {
		loginLogDao.delete(loginLog);
	}

	@Override
	public void deleteLoginLogs(List<FndLoginLog> loginLogs) {
		loginLogDao.delete(loginLogs);
	}

	@Override
	public DataSourceResult getLoginLogList(DataSourceRequest request) {
		return loginLogDao.getList(request);
	}

	@Override
	public FndLoginLog getLastLoginLogByUserId(int userId) {
		return loginLogDao.getLastByUserId(userId);
	}

    
    
	//
	// for FndPrivDao
	//
	@Override
	public FndPriv getPriv(int id) {
		return privDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndPriv priv) {
		privDao.saveOrUpdate(priv);
	}

	@Override
	public void deletePriv(FndPriv priv) {
		privDao.delete(priv);
	}

	@Override
	public void deletePrivs(List<FndPriv> privs) {
		privDao.delete(privs);
	}

	@Override
	public DataSourceResult getPrivList(DataSourceRequest request) {
		return privDao.getList(request);
	}

	@Override
	public FndPriv getPriv(String ukid) {
		return privDao.get(ukid);
	}

	@Override
	public FndPriv getPriv(Session hnSession, String ukid) {
		return privDao.get(hnSession, ukid);
	}

    
    
	//
	// for FndPrivDao
	//
	@Override
	public FndUserPriv getUserPriv(int id) {
		return userPrivDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndUserPriv userPriv) {
		userPrivDao.saveOrUpdate(userPriv);
	}

	@Override
	public void deleteUserPriv(FndUserPriv userPriv) {
		userPrivDao.delete(userPriv);
	}

	@Override
	public void deleteUserPrivs(List<FndUserPriv> userPrivs) {
		userPrivDao.delete(userPrivs);
	}

	@Override
	public DataSourceResult getUserPrivList(DataSourceRequest request) {
		return userPrivDao.getList(request);
	}

	@Override
	public boolean isRegisteredUserPriv(int userId, int privId) {
		return userPrivDao.isRegistered(userId, privId);
	}

	@Override
	public List<FndUserPriv> getUserPrivListByUserId(int userId) {
		return userPrivDao.getListByUserId(userId);
	}

    
    
	//
	// for FndCtntFolderDao
	//
	@Override
	public FndCtntFolder getCtntFolder(int id) {
		return ctntFolderDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndCtntFolder ctntFolder) {
		ctntFolderDao.saveOrUpdate(ctntFolder);
	}

	@Override
	public void deleteCtntFolder(FndCtntFolder ctntFolder) {
		ctntFolderDao.delete(ctntFolder);
	}

	@Override
	public void deleteCtntFolders(List<FndCtntFolder> ctntFolders) {
		ctntFolderDao.delete(ctntFolders);
	}

	@Override
	public DataSourceResult getCtntFolderList(DataSourceRequest request) {
		return ctntFolderDao.getList(request);
	}

	@Override
	public FndCtntFolder getCtntFolder(String name) {
		return ctntFolderDao.get(name);
	}

	@Override
	public int getCtntFolderCount() {
		return ctntFolderDao.getCount();
	}

	@Override
	public List<FndCtntFolder> getCtntFolderList() {
		return ctntFolderDao.getList();
	}

    
    
	//
	// for FndStateDao
	//
	@Override
	public FndState getState(int id) {
		return stateDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndState state) {
		stateDao.saveOrUpdate(state);
	}

	@Override
	public void deleteState(FndState state) {
		stateDao.delete(state);
	}

	@Override
	public void deleteStates(List<FndState> states) {
		stateDao.delete(states);
	}

	@Override
	public DataSourceResult getStateList(DataSourceRequest request) {
		return stateDao.getList(request);
	}

	@Override
	public FndState getState(String name) {
		return stateDao.get(name);
	}

	@Override
	public List<FndState> getStateListByListIncluded(boolean listIncluded) {
		return stateDao.getListByListIncluded(listIncluded);
	}

	@Override
	public List<FndState> getStateListByNameLike(String name) {
		return stateDao.getListByNameLike(name);
	}

    
    
	//
	// for FndViewTypeDao
	//
	@Override
	public FndViewType getViewType(int id) {
		return viewTypeDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndViewType viewType) {
		viewTypeDao.saveOrUpdate(viewType);
	}

	@Override
	public void deleteViewType(FndViewType viewType) {
		viewTypeDao.delete(viewType);
	}

	@Override
	public void deleteViewTypes(List<FndViewType> viewTypes) {
		viewTypeDao.delete(viewTypes);
	}

	@Override
	public DataSourceResult getViewTypeList(DataSourceRequest request) {
		return viewTypeDao.getList(request);
	}

	@Override
	public FndViewType getViewType(String code, String resolution) {
		return viewTypeDao.get(code, resolution);
	}

	@Override
	public List<FndViewType> getViewTypeList() {
		return viewTypeDao.getList();
	}

	@Override
	public String getViewTypeResoByCode(String code) {
		return viewTypeDao.getResoByCode(code);
	}

	@Override
	public List<Tuple> getViewTypeMaxLaneGroupByMediumId() {
		return viewTypeDao.getMaxLaneGroupByMediumId();
	}

    
    
	//
	// for FndSetupFileDao
	//
	@Override
	public FndSetupFile getSetupFile(int id) {
		return setupFileDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndSetupFile setupFile) {
		setupFileDao.saveOrUpdate(setupFile);
	}

	@Override
	public void deleteSetupFile(FndSetupFile setupFile) {
		setupFileDao.delete(setupFile);
	}

	@Override
	public void deleteSetupFiles(List<FndSetupFile> setupFiles) {
		setupFileDao.delete(setupFiles);
	}

	@Override
	public DataSourceResult getSetupFileList(DataSourceRequest request) {
		return setupFileDao.getList(request);
	}

	@Override
	public FndSetupFile getSetupFile(String filename) {
		return setupFileDao.get(filename);
	}

	@Override
	public FndSetupFile getLastVerSetupFile(String prodKeyword, int verNumber, String playKeyword) {
		return setupFileDao.getLastVer(prodKeyword, verNumber, playKeyword);
	}

    
    
	//
	// for FndMobRegionDao
	//
	@Override
	public FndMobRegion getMobRegion(int id) {
		return mobRegionDao.get(id);
	}

	@Override
	public void saveOrUpdate(FndMobRegion region) {
		mobRegionDao.saveOrUpdate(region);
	}

	@Override
	public void deleteMobRegion(FndMobRegion region) {
		mobRegionDao.delete(region);
	}

	@Override
	public void deleteMobRegions(List<FndMobRegion> regions) {
		mobRegionDao.delete(regions);
	}

	@Override
	public DataSourceResult getMobRegionList(DataSourceRequest request) {
		return mobRegionDao.getList(request);
	}

	@Override
	public DataSourceResult getActiveMobRegionList(DataSourceRequest request) {
		return mobRegionDao.getActiveList(request);
	}

	@Override
	public FndMobRegion getMobRegion(String name) {
		return mobRegionDao.get(name);
	}

	@Override
	public List<FndMobRegion> getMobRegionListByActiveStatus(boolean activeStatus) {
		return mobRegionDao.getListByActiveStatus(activeStatus);
	}

    
    
	//
	// for Common
	//
	@Override
	public void logout(HttpSession session) {
		logout(session, false);
	}

	@Override
	public void logout(HttpSession session, boolean forcedMode) {
    	if (session != null) {
        	LoginUser loginUser = (LoginUser) session.getAttribute("loginUser");
    		
        	if (loginUser != null) {
        		FndLoginLog loginLog = loginLogDao.get(loginUser.getLoginId());
        		
        		if (loginLog != null) {
        			loginLog.setLogout(true);
        			loginLog.setLogoutDate(new Date());
        			loginLog.setForcedLogout(forcedMode);
        			
        			loginLog.touchWho(session);
        			
        			saveOrUpdate(loginLog);
        		}
        	}

        	session.removeAttribute("loginUser");
        	session.removeAttribute("recentMenus");
    	}
	}


	@Override
	public List<String> getAllUserPrivs(int userId) {
		ArrayList<String> userPrivKeys = new ArrayList<String>();
		
		List<FndUserPriv> userPrivList = getUserPrivListByUserId(userId);
		
		for (FndUserPriv userPriv : userPrivList) {
			FndPriv priv = userPriv.getPriv();
			
			if (!userPrivKeys.contains(priv.getUkid())) {
				userPrivKeys.add(priv.getUkid());
			}
		}
		
    	return userPrivKeys;
	}

	@Override
	public FndCtntFolder getDefCtntFolder() {
		
		List<FndCtntFolder> list = ctntFolderDao.getList();
		for(FndCtntFolder ctntFolder : list) {
			if (ctntFolder.isCurr()) {
				return ctntFolder;
			}
		}
		
		return null;
	}

}
