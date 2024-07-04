package net.doohad.models.rev.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.rev.RevSyncPackImp;

public interface RevSyncPackImpDao {
	// Common
	public RevSyncPackImp get(int id);
	public void saveOrUpdate(RevSyncPackImp syncPackImp);
	public void delete(RevSyncPackImp syncPackImp);
	public void delete(List<RevSyncPackImp> syncPackImps);

	// for Kendo Grid Remote Read

	// for DAO specific
	public List<Tuple> getLogTupleListByTime(String unit);
	public List<Tuple> getControlTupleListByTime(String unit);
	public List<Tuple> getLastTupleListGroupByShortName();
	public RevSyncPackImp getLastByShortName(String shortName);
	public int getCount();
	public int deleteBefore(Date date);
}
