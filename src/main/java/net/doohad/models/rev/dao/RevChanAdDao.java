package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.rev.RevChanAd;

public interface RevChanAdDao {
	// Common
	public RevChanAd get(int id);
	public void saveOrUpdate(RevChanAd chanAd);
	public void delete(RevChanAd chanAd);
	public void delete(List<RevChanAd> chanAds);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, int channelId);

	// for DAO specific
	public List<Tuple> getLastListGroupByChannelId();
	public List<RevChanAd> getListByChannelId(int channelId);
	public List<RevChanAd> getCurrListByChannelId(int channelId);
	public RevChanAd getLastByChannelIdSeq(int channelId, int seq);
	public int getCount();
	public int deleteBefore(Date date);

}
