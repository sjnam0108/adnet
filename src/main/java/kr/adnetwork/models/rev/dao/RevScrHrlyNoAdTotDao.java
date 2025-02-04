package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.rev.RevScrHrlyNoAdTot;

public interface RevScrHrlyNoAdTotDao {
	// Common
	public RevScrHrlyNoAdTot get(int id);
	public void saveOrUpdate(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void delete(RevScrHrlyNoAdTot hrlyNoAdTot);
	public void delete(List<RevScrHrlyNoAdTot> hrlyNoAdTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyNoAdTot get(InvScreen screen, Date playDate);
	public RevScrHrlyNoAdTot get(int screenId, Date playDate);
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate);
	public List<RevScrHrlyNoAdTot> getListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate);
	
}
