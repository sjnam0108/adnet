package kr.adnetwork.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import kr.adnetwork.models.DataSourceRequest;
import kr.adnetwork.models.DataSourceResult;
import kr.adnetwork.models.inv.InvSite;
import kr.adnetwork.models.rev.RevSitHrlyPlyTot;

public interface RevSitHrlyPlyTotDao {
	// Common
	public RevSitHrlyPlyTot get(int id);
	public void saveOrUpdate(RevSitHrlyPlyTot hrlyPlyTot);
	public void delete(RevSitHrlyPlyTot hrlyPlyTot);
	public void delete(List<RevSitHrlyPlyTot> hrlyPlyTots);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request, Date playDate);

	// for DAO specific
	public RevSitHrlyPlyTot get(InvSite site, Date playDate);
	public List<RevSitHrlyPlyTot> getListByMediumIdPlayDate(
			int mediumId, Date playDate);
	public List<RevSitHrlyPlyTot> getListByPlayDate(Date playDate);
	public List<Tuple> getTupleListByPlayDate(Date playDate);
	public Tuple getStatByMediumIdPlayDate(int mediumId, Date playDate);
	public void deleteInactiveByPlaDate(Date playDate);
	
}
