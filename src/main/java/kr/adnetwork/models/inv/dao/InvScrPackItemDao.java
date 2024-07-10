package kr.adnetwork.models.inv.dao;

import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvScrPack;
import kr.adnetwork.models.inv.InvScrPackItem;

public interface InvScrPackItemDao {
	// Common
	public InvScrPackItem get(int id);
	public void saveOrUpdate(InvScrPackItem item);
	public void delete(InvScrPackItem item);
	public void delete(List<InvScrPackItem> items);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, int scrPackId);

	// for DAO specific
	public InvScrPackItem get(InvScrPack scrPack, int screenId);
	public int getCountByScrPackId(int scrPackId);
	public List<Tuple> getScreenIdListByScrPackIdIn(List<Integer> ids);
	public List<InvScrPackItem> getListByScreenId(int screenId);

}