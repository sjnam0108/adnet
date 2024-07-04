package net.doohad.models.inv.dao;

import java.util.List;

import javax.persistence.Tuple;

import net.doohad.models.inv.InvRTScreen;

public interface InvRTScreenDao {
	// Common
	public InvRTScreen get(int id);
	public void saveOrUpdate(InvRTScreen rtScreen);
	public void delete(InvRTScreen rtScreen);
	public void delete(List<InvRTScreen> rtScreens);

	// for Kendo Grid Remote Read

	// for DAO specific
	public InvRTScreen getByScreenId(int screenId);
	public List<Tuple> getCmdTupleListByMediumId(int mediumId);

}
