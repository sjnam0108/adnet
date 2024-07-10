package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.rev.RevScrHrlyPlyTot;

public interface RevScrHrlyPlyTotDao {
	// Common
	public RevScrHrlyPlyTot get(int id);
	public void saveOrUpdate(RevScrHrlyPlyTot hrlyPlyTot);
	public void delete(RevScrHrlyPlyTot hrlyPlyTot);
	public void delete(List<RevScrHrlyPlyTot> hrlyPlyTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyPlyTot get(InvScreen screen, Date playDate);
	public List<RevScrHrlyPlyTot> getListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public List<RevScrHrlyPlyTot> getListByPlayDate(Date playDate);
	public List<Tuple> getTupleListByPlayDate(Date playDate);
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate);
	public Double getStdByMediumIdPlayDate(int mediumId, Date playDate);
	public Tuple getAvgByMediumIdBetweenPlayDates(int mediumId, Date date1, Date date2);
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate);
	public void deleteInactive();
	
}
