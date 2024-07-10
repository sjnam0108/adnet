package kr.adnetwork.models.adc.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.adc.AdcPlaylist;

public interface AdcPlaylistDao {
	// Common
	public AdcPlaylist get(int id);
	public void saveOrUpdate(AdcPlaylist playlist);
	public void delete(AdcPlaylist playlist);
	public void delete(List<AdcPlaylist> playlists);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, String viewType);

	// for DAO specific
	public List<AdcPlaylist> getActiveListByChannelId(int channelId);
	public List<AdcPlaylist> getListByChannelId(int channelId);
}
