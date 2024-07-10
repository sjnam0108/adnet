package kr.adnetwork.models.rev.dao;

import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.rev.RevAdSelCache;

public interface RevAdSelCacheDao {
	// Common
	public RevAdSelCache get(int id);
	public void saveOrUpdate(RevAdSelCache adSelCache);
	public void delete(RevAdSelCache adSelCache);
	public void delete(List<RevAdSelCache> adSelCaches);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public RevAdSelCache getLastByScreenIdAdCreativeId(int screenId, int adCreativeId);
	public Tuple getLastTupleByScreenId(int screenId);
	public Tuple getLastTupleByScreenIdAdId(int screenId, int adId);
	public Tuple getLastTupleByScreenIdAdvertiserId(int screenId, int advertiserId);
	public List<Tuple> getTupleListByScreenId(int screenId);
}
