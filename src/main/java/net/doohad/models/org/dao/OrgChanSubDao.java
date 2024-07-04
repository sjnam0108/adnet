package net.doohad.models.org.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.org.OrgChanSub;
import net.doohad.models.org.OrgChannel;

public interface OrgChanSubDao {
	// Common
	public OrgChanSub get(int id);
	public void saveOrUpdate(OrgChanSub chanSub);
	public void delete(OrgChanSub chanSub);
	public void delete(List<OrgChanSub> chanSubs);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, String type, int channelId);

	// for DAO specific
	public OrgChanSub get(OrgChannel channel, String type, int objId);
	public int getCountByChannelId(int channelId);
	public List<Tuple> getScrTupleListByChannelId(int channelId);
	public List<Tuple> getSyncPackTupleListByChannelId(int channelId);
	public void deleteBySyncPackId(int syncPackId);
}
