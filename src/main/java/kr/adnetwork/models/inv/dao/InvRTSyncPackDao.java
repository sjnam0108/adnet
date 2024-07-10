package kr.adnetwork.models.inv.dao;

import java.util.List;

import kr.adnetwork.models.inv.InvRTSyncPack;

public interface InvRTSyncPackDao {
	// Common
	public InvRTSyncPack get(int id);
	public void saveOrUpdate(InvRTSyncPack rtSyncPack);
	public void delete(InvRTSyncPack rtSyncPack);
	public void delete(List<InvRTSyncPack> rtSyncPacks);

	// for Kendo Grid Remote Read

	// for DAO specific
	public InvRTSyncPack getBySyncPackId(int syncPackId);

}
