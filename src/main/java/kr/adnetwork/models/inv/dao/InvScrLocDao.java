package kr.adnetwork.models.inv.dao;

import java.util.Date;
import java.util.List;

import kr.adnetwork.models.inv.InvScrLoc;

public interface InvScrLocDao {
	// Common
	public InvScrLoc get(int id);
	public void saveOrUpdate(InvScrLoc scrLoc);
	public void delete(InvScrLoc scrLoc);
	public void delete(List<InvScrLoc> scrLocs);

	// for Kendo Grid Remote Read

	// for DAO specific
	public InvScrLoc getLastByScreenId(int screenId);
	public List<InvScrLoc> getListByScreenIdDate(int screenId, Date date);
	public List<Date> getDateListByScreenId(int screenId);

}
