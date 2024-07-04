package net.doohad.models.adc.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.DataSourceRequest;
import net.doohad.models.DataSourceResult;

public interface AdcMobTargetViewDao {
	// Common

	// for Kendo Grid Remote Read
	public DataSourceResult getList(DataSourceRequest request);

	// for DAO specific
	public List<Tuple> getItemList();
}
