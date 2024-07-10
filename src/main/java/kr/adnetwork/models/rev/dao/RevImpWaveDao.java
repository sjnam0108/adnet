package kr.adnetwork.models.rev.dao;

import java.util.List;

import kr.adnetwork.models.rev.RevImpWave;

public interface RevImpWaveDao {
	// Common
	public RevImpWave get(int id);
	public void saveOrUpdate(RevImpWave impWave);
	public void delete(RevImpWave impWave);
	public void delete(List<RevImpWave> impWaves);

	// for Kendo Grid Remote Read

	// for DAO specific
	public List<RevImpWave> getEffList();
}
