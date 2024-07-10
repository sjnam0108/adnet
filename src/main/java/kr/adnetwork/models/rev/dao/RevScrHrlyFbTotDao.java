package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.rev.RevScrHrlyFbTot;

public interface RevScrHrlyFbTotDao {
	// Common
	public RevScrHrlyFbTot get(int id);
	public void saveOrUpdate(RevScrHrlyFbTot hrlyFbTot);
	public void delete(RevScrHrlyFbTot hrlyFbTot);
	public void delete(List<RevScrHrlyFbTot> hrlyFbTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFbTot get(InvScreen screen, Date playDate);
	public RevScrHrlyFbTot get(int screenId, Date playDate);
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate);
	public List<RevScrHrlyFbTot> getListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate);

}
