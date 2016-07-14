package com.numazu.export.model;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ModelWrapper implements RowMapper<Model> {

	@Override
	public Model mapRow(ResultSet arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Model model = new Model();
		model.setId(arg0.getInt("SERVICE_ID"));
		model.setUserId(arg0.getString("MSISDN"));
		model.setContent(arg0.getString("JSON1"));
		return model;
	}

}
