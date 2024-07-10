package kr.adnetwork.models.org.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgAlimTalk;

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
