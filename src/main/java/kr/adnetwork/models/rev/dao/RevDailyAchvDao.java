package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import kr.adnetwork.models.rev.RevDailyAchv;

public interface RevDailyAchvDao {
	// Common
	public RevDailyAchv get(int id);
	public void saveOrUpdate(RevDailyAchv dailyAchv);
	public void delete(RevDailyAchv dailyAchv);
	public void delete(List<RevDailyAchv> dailyAchves);

	// for Kendo Grid Remote Read

	// for DAO specific
	public RevDailyAchv getByTypeIdPlayDate(String type, int objId, Date playDate);
	public List<RevDailyAchv> getListByTypeId(String type, int objId);
}
