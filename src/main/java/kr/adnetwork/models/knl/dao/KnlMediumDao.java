package kr.adnetwork.models.knl.dao;

import java.util.List;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.knl.KnlMedium;

public interface KnlMediumDao {
	// Common
	public KnlMedium get(int id);
	public void saveOrUpdate(KnlMedium medium);
	public void delete(KnlMedium medium);
	public void delete(List<KnlMedium> media);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, String viewType);

	// for DAO specific
	public KnlMedium get(String shortName);
	public KnlMedium getByApiKey(String apiKey);
	public List<KnlMedium> getListByShortNameLike(String shortName);
	public List<KnlMedium> getValidList();
}
