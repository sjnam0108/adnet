package kr.adnetwork.models.inv.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvSyncPackItem;

public interface InvSyncPackItemDao {
	// Common
	public InvSyncPackItem get(int id);
	public void saveOrUpdate(InvSyncPackItem item);
	public void delete(InvSyncPackItem item);
	public void delete(List<InvSyncPackItem> items);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, int syncPackId);

	// for DAO specific
	public InvSyncPackItem getByScreenId(int screenId);
	public int getCountBySyncPackId(int syncPackId);
	public List<InvSyncPackItem> getListBySyncPackId(int syncPackId);
	public void saveAndReorder(InvSyncPackItem item);
	public void reorder(int syncPackId);
	public List<InvSyncPackItem> getActiveParentListByMediumId(int mediumId);
	public int getLaneIdByScreenId(int screenId);
	public List<InvSyncPackItem> getList();
	public InvSyncPackItem getFirstLaneBySyncPackId(int syncPackId);

}
