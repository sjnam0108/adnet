package net.doohad.models.org.dao;

import java.util.List;

import net.doohad.models.org.OrgRTChannel;

public interface OrgRTChannelDao {
	// Common
	public OrgRTChannel get(int id);
	public void saveOrUpdate(OrgRTChannel rtChannel);
	public void delete(OrgRTChannel rtChannel);
	public void delete(List<OrgRTChannel> rtChannels);

	// for Kendo Grid Remote Read

	// for DAO specific
	public OrgRTChannel getByChannelId(int channelId);


}
