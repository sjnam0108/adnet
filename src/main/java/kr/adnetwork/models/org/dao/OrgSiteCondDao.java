package kr.adnetwork.models.org.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgSiteCond;

public interface OrgSiteCondDao {
	// Common
	public OrgSiteCond get(int id);
	public void saveOrUpdate(OrgSiteCond siteCond);
	public void delete(OrgSiteCond siteCond);
	public void delete(List<OrgSiteCond> siteConds);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public OrgSiteCond get(KnlMedium medium, String code);
	public List<OrgSiteCond> getListByMediumId(int mediumId);
	public List<OrgSiteCond> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus);
	public List<OrgSiteCond> getListByMediumIdNameLike(int mediumId, String name);
}
