package net.doohad.models.inv.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.inv.InvScrPack;
import net.doohad.models.knl.KnlMedium;

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
