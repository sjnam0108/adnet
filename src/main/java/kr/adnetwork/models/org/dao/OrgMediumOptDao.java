package kr.adnetwork.models.org.dao;

import java.util.List;

import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgMediumOpt;

public interface OrgMediumOptDao {
	// Common
	public OrgMediumOpt get(int id);
	public void saveOrUpdate(OrgMediumOpt mediumOpt);
	public void delete(OrgMediumOpt mediumOpt);
	public void delete(List<OrgMediumOpt> mediumOpts);

	// for Kendo Grid Remote Read

	// for DAO specific
	public OrgMediumOpt get(KnlMedium medium, String code);
	public List<OrgMediumOpt> getListByMediumId(int mediumId);
	public String getValue(int mediumId, String code);

}
