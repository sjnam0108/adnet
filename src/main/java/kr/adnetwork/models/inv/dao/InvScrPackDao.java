package kr.adnetwork.models.inv.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvScrPack;
import kr.adnetwork.models.knl.KnlMedium;

public interface InvScrPackDao {
	// Common
	public InvScrPack get(int id);
	public void saveOrUpdate(InvScrPack scrPack);
	public void delete(InvScrPack scrPack);
	public void delete(List<InvScrPack> scrPacks);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public InvScrPack get(KnlMedium medium, String name);
	public List<InvScrPack> getListByMediumId(int mediumId);
	public List<InvScrPack> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus);
	public List<InvScrPack> getListByMediumIdNameLike(int mediumId, String name);

}
