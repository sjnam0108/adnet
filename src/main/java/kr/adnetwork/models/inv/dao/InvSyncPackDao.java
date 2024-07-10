package kr.adnetwork.models.inv.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvSyncPack;
import kr.adnetwork.models.knl.KnlMedium;

public interface InvSyncPackDao {
	// Common
	public InvSyncPack get(int id);
	public void saveOrUpdate(InvSyncPack syncPack);
	public void delete(InvSyncPack syncPack);
	public void delete(List<InvSyncPack> syncPacks);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getListBySyncPackIdIn(DataSourceRequest request, List<Integer> list);

	// for DAO specific
	public InvSyncPack get(KnlMedium medium, String name);
	public List<InvSyncPack> getListByMediumId(int mediumId);
	public List<InvSyncPack> getListByMediumIdNameLike(int mediumId, String name);
	public List<InvSyncPack> getEffListByAdSelSecs(int adSelSecs);
	public int getActiveCountByMediumId(int mediumId);
	public List<InvSyncPack> getActiveList();
	public InvSyncPack getActiveByShortName(String shortName);
	public InvSyncPack getByShortName(String shortName);

}
