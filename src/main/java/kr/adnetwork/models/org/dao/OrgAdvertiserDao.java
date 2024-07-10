package kr.adnetwork.models.org.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgAdvertiser;

public interface OrgAdvertiserDao {
	// Common
	public OrgAdvertiser get(int id);
	public void saveOrUpdate(OrgAdvertiser advertiser);
	public void delete(OrgAdvertiser advertiser);
	public void delete(List<OrgAdvertiser> advertisers);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public OrgAdvertiser get(KnlMedium medium, String name);
	public List<OrgAdvertiser> getListByMediumId(int mediumId);
}
