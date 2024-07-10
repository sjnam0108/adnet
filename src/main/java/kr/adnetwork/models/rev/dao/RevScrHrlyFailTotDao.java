package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvScreen;
import kr.adnetwork.models.rev.RevScrHrlyFailTot;

public interface RevScrHrlyFailTotDao {
	// Common
	public RevScrHrlyFailTot get(int id);
	public void saveOrUpdate(RevScrHrlyFailTot hrlyFailTot);
	public void delete(RevScrHrlyFailTot hrlyFailTot);
	public void delete(List<RevScrHrlyFailTot> hrlyFailTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevScrHrlyFailTot get(InvScreen screen, Date playDate);
	public RevScrHrlyFailTot get(int screenId, Date playDate);
	public Tuple getHourStatByMediumIdPlayDate(int mediumId, Date playDate);

}
