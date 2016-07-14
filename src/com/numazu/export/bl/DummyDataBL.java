package com.numazu.export.bl;

import java.sql.SQLException;
import java.util.List;

import com.numazu.export.common.ConfigPool;
import com.numazu.export.dao.DummyDataDAO;
import com.numazu.export.model.Model;

public class DummyDataBL {

	private DummyDataDAO dummyDataDAO;
	
	public DummyDataBL() {
		dummyDataDAO = new DummyDataDAO(ConfigPool.getDataSource());
	}
	
	public List<Model> getSelectAll(int resultid) throws SQLException {
		return dummyDataDAO.getSelectAllDAO(resultid);
	}
	
	public List<Model> getSelectAllBetween(int start, int finish) throws SQLException {
		return dummyDataDAO.getSelectAllBetween(start, finish);
	}
	
	public List<Model> getSelectAllLimit(int resultid, int limit) throws SQLException {
		return dummyDataDAO.getSelectAllLimit(resultid, limit);
	}
	
	public List<Model> getMaxId() throws SQLException {
		return dummyDataDAO.resultListLastIndexDAO();
	}
	
	public List<Model> getSelectAllMapper(int resultid) throws SQLException {
		return dummyDataDAO.getSelectAllDAOMapper(resultid);
	}
	
	public List<Model> getSelectAllMapperLimit10(int resultid) throws SQLException {
		return dummyDataDAO.getSelectAllDAOMapperLimit10(resultid);
	}
	
	public Integer getMaxIdMapper() throws SQLException {
		return dummyDataDAO.resultListLastIndexDAOMapper();
	}
	
	public List<Model> getMaxId2() throws SQLException {
		return dummyDataDAO.resultListLastIndexDAO2();
	}
	
	public List<Model> getSelectAll2(int resultid) throws SQLException {
		return dummyDataDAO.getSelectAllDAO2(resultid);
	}
}
