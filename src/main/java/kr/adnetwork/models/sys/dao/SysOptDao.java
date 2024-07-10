package kr.adnetwork.models.sys.dao;

import java.util.List;

import kr.adnetwork.models.sys.SysOpt;

public interface SysOptDao {
	// Common
	public SysOpt get(int id);
	public void saveOrUpdate(SysOpt opt);
	public void delete(SysOpt opt);
	public void delete(List<SysOpt> opts);

	// for Kendo Grid Remote Read

	// for DAO specific
	public SysOpt get(String code);

}
