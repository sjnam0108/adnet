package kr.adnetwork.models.org.dao;

import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;
import kr.adnetwork.models.org.OrgChannel;

public interface OrgChannelDao {
	// Common
	public OrgChannel get(int id);
	public void saveOrUpdate(OrgChannel channel);
	public void delete(OrgChannel channel);
	public void delete(List<OrgChannel> channels);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public OrgChannel get(KnlMedium medium, String shortName);
	public List<OrgChannel> getListByMediumId(int mediumId);
	public List<OrgChannel> getListByMediumIdActiveStatus(int mediumId, boolean activeStatus);
	public List<OrgChannel> getActiveList();
	public List<Tuple> getTupleListByTypeObjId(String type, int objId);
	public List<OrgChannel> getAdAppendableList();
}
