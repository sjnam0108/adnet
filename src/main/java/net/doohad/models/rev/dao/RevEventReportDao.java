package net.doohad.models.rev.dao;

import java.util.List;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;
import net.doohad.models.rev.RevEventReport;

public interface RevEventReportDao {
	// Common
	public RevEventReport get(int id);
	public void saveOrUpdate(RevEventReport eventReport);
	public void delete(RevEventReport eventReport);
	public void delete(List<RevEventReport> eventReports);

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);
	public DataSourceResult getList(DataSourceRequest request, int mediumId);

	// for DAO specific

}
