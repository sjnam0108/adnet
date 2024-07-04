package net.doohad.models.org.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.knl.KnlMedium;
import net.doohad.models.org.OrgAlimTalk;

public interface OrgAlimTalkDao {
	// Common
	public OrgAlimTalk get(int id);
	public void saveOrUpdate(OrgAlimTalk alimTalk);
	public void delete(OrgAlimTalk alimTalk);
	public void delete(List<OrgAlimTalk> alimTalks);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public OrgAlimTalk get(KnlMedium medium, String shortName);
	public List<OrgAlimTalk> getActiveList();

}
