package com.numazu.export.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.numazu.export.model.Model;
import com.numazu.export.model.ModelWrapper;

public class DummyDataDAO extends JdbcDaoSupport {
	/** the LOGGER */
	private static final Logger LOGGER = LogManager.getLogger(DummyDataDAO.class);

	private PreparedStatement getStatementSelectAll;
	private PreparedStatement getStatementSelectCount;
	private DataSource dataSource;
	private JdbcTemplate myTemplate;

	// private static String sqlCount = "SELECT COUNT(id) AS id FROM
	// service_dummy_small";
	// private static String sqlLastIndex = "SELECT MAX(id) AS id FROM
	// service_dummy_small";
	// private String sqlSelectAll = "SELECT id, user_id, content FROM
	// service_dummy_small where id > ?";

	// private static String sqlCount = "SELECT COUNT(SERVICE_ID) AS id FROM
	// SERVICE WHERE MSISDN <> 'mon-user2'";
	// private static String sqlLastIndex = "SELECT MAX(SERVICE_ID) AS id FROM
	// SERVICE WHERE MSISDN <> 'mon-user2'";
	// private String sqlSelectAll = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_JSON1_VW WHERE
	// SERVICE_ID > ? AND MSISDN <> 'mon-user2'";

	private static String sqlCount = "SELECT COUNT(SERVICE_ID) AS id FROM SERVICE";
	private static String sqlLastIndex = "SELECT MAX(SERVICE_ID) AS id FROM SERVICE";
	private String sqlSelectAll = "select SERVICE_ID, MSISDN,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_JSON1_VW WHERE SERVICE_ID > ?";
	private String sqlSelectAlllimit10 = "select SERVICE_ID, MSISDN ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_JSON1_VW WHERE SERVICE_ID > ? order by SERVICE_ID asc limit 10";

	
	// private static String sqlCount = "SELECT COUNT(SERVICE_ID) AS id FROM
	// SERVICE WHERE MSISDN = '6287885912761'";
	// private static String sqlLastIndex = "SELECT MAX(SERVICE_ID) AS id FROM
	// SERVICE WHERE MSISDN = '6287885912761'";
	// private String sqlSelectAll = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_JSON1_VW WHERE
	// SERVICE_ID > ? AND MSISDN = '6287885912761'";

	// private static String sqlCount = "SELECT COUNT(SERVICE_ID) AS id FROM
	// SERVICE_TEST";
	// private static String sqlLastIndex = "SELECT MAX(SERVICE_ID) AS id FROM
	// SERVICE_TEST";
	// private String sqlSelectAll = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_TEST_JSON1_VW
	// WHERE SERVICE_ID > ?";
	// private String sqlSelectBetween = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now()"
	// + " period from LB_SERVICE_TEST_JSON1_VW WHERE SERVICE_ID between ? and
	// ?";
	// private String sqlSelectAllLimit = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_TEST_JSON1_VW
	// WHERE SERVICE_ID > ? limit ?";
	// private String sqlSelectAll2 = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_TEST_JSON1_VW
	// WHERE SERVICE_ID > ";
	// private String sqlSelectAlllimit10 = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_TEST_JSON1_VW
	// WHERE SERVICE_ID > ? order by SERVICE_ID desc limit 10";

	// private static String sqlCount = "SELECT COUNT(SERVICE_ID) AS id FROM
	// SERVICE_TEST_LOAD_DB";
	// private static String sqlLastIndex = "SELECT MAX(SERVICE_ID) AS id FROM
	// SERVICE_TEST_LOAD_DB";
	// private String sqlSelectAll = "select SERVICE_ID, MSISDN
	// ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from
	// LB_SERVICE_TEST_LOAD_DB_JSON1_VW WHERE SERVICE_ID > ?";
	private String sqlSelectBetween = "select SERVICE_ID, MSISDN ,CONCAT(A,B,C,D,E,F,G) JSON1, now()"
			+ " period from LB_SERVICE_TEST_LOAD_DB_JSON1_VW WHERE SERVICE_ID between ? and ?";
	private String sqlSelectAllLimit = "select SERVICE_ID, MSISDN ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_TEST_LOAD_DB_JSON1_VW WHERE SERVICE_ID > ? limit ?";
	private String sqlSelectAll2 = "select SERVICE_ID, MSISDN ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_TEST_LOAD_DB_JSON1_VW WHERE SERVICE_ID > ";
//	private String sqlSelectAlllimit10 = "select SERVICE_ID, MSISDN ,CONCAT(A,B,C,D,E,F,G) JSON1, now() period from LB_SERVICE_TEST_LOAD_DB_JSON1_VW WHERE SERVICE_ID > ? order by SERVICE_ID desc limit 10";

	// ios
	//getTokenCount
	String selectApnsCount = "SELECT ApnsCount FROM IOS_TOKEN WHERE MSISDN = ?";
	// getDeviceToken
	String selectToken = "SELECT TOKEN FROM IOS_TOKEN WHERE MSISDN = ?";
	// getMessage
	String selectMessageHo = "SELECT * FROM MESSAGE_HO_SELECT WHERE MESSAGE_ID = ?";
	//getFile
	String selectFileTransNew = "SELECT FILE_PROCESS_ID,MSISDN_SOURCE,MSISDN_TARGET"
			+ ",FILE_NAME,FILE_SIZE,MESSAGE_TYPE,FILE_COUNT FROM FILE_TRANS_NEW WHERE FILE_PROCESS_ID = ?";
	//getUserName
	String selectUserData = "SELECT USERNAME FROM USER_DATA WHERE MSISDN = ?";
	//getGroupName
	String selectGroupName = "SELECT GROUP_NAME FROM GROUP_NAME WHERE GROUP_ID = ?";
	//addTokenCount
	String selectExecIosToken = "CALL exec_ios_token2(?)";

	public DummyDataDAO(DataSource dataSource) {
		this.dataSource = dataSource;
		this.myTemplate = new JdbcTemplate(dataSource);
		this.myTemplate.setFetchSize(30000);
	}

	public List<Model> getSelectAllDAO(int resultId) throws SQLException {
		Connection con = null;
		List<Model> listPcAgeTargets = new ArrayList<Model>();
		try {
			con = dataSource.getConnection();
			// getStatementSelectAll = con.prepareStatement(sqlSelectAll);
			getStatementSelectAll = con.prepareStatement(sqlSelectAll, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			getStatementSelectAll.setInt(1, resultId);
			getStatementSelectAll.setFetchSize(Integer.MIN_VALUE);
			ResultSet rs = getStatementSelectAll.executeQuery();
			while (rs.next()) {
				Model pcAgeTarget = new Model();
				pcAgeTarget.setId(rs.getInt("SERVICE_ID"));
				pcAgeTarget.setUserId(rs.getString("MSISDN"));
				pcAgeTarget.setContent(rs.getString("JSON1"));
				// System.out.println(pcAgeTarget.getUserId() + " - " +
				// pcAgeTarget.getContent());
				listPcAgeTargets.add(pcAgeTarget);
			}
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			con.close();
		}
		return listPcAgeTargets;
	}

	public List<Model> getSelectAllBetween(int start, int finish) throws SQLException {
		Connection con = null;
		List<Model> listPcAgeTargets = new ArrayList<Model>();
		try {
			con = dataSource.getConnection();
			getStatementSelectAll = con.prepareStatement(sqlSelectBetween);
			getStatementSelectAll.setInt(1, start);
			getStatementSelectAll.setInt(2, finish);
			ResultSet rs = getStatementSelectAll.executeQuery();
			while (rs.next()) {
				Model pcAgeTarget = new Model();
				pcAgeTarget.setId(rs.getInt("SERVICE_ID"));
				pcAgeTarget.setUserId(rs.getString("MSISDN"));
				pcAgeTarget.setContent(rs.getString("JSON1"));
				// System.out.println(pcAgeTarget.getUserId() + " - " +
				// pcAgeTarget.getContent());
				listPcAgeTargets.add(pcAgeTarget);
			}
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			con.close();
		}
		return listPcAgeTargets;
	}

	public List<Model> getSelectAllLimit(int resultId, int limit) throws SQLException {
		Connection con = null;
		List<Model> listPcAgeTargets = new ArrayList<Model>();
		try {
			con = dataSource.getConnection();
			getStatementSelectAll = con.prepareStatement(sqlSelectAllLimit);
			getStatementSelectAll.setInt(1, resultId);
			getStatementSelectAll.setInt(2, limit);
			ResultSet rs = getStatementSelectAll.executeQuery();
			while (rs.next()) {
				Model pcAgeTarget = new Model();
				pcAgeTarget.setId(rs.getInt("SERVICE_ID"));
				pcAgeTarget.setUserId(rs.getString("MSISDN"));
				pcAgeTarget.setContent(rs.getString("JSON1"));
				listPcAgeTargets.add(pcAgeTarget);
			}
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			con.close();
		}
		return listPcAgeTargets;
	}

	public List<Model> resultCountDAO() throws SQLException {
		Connection con = null;
		List<Model> modelList = new ArrayList<Model>();
		try {
			con = dataSource.getConnection();
			getStatementSelectCount = con.prepareStatement(sqlCount);
			ResultSet rs = getStatementSelectCount.executeQuery();
			while (rs.next()) {
				Model pcAgeTarget = new Model();
				pcAgeTarget.setId(rs.getInt("id"));
				modelList.add(pcAgeTarget);
			}
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			con.close();
		}
		return modelList;
	}

	public List<Model> resultListLastIndexDAO() throws SQLException {
		Connection con = null;
		List<Model> modelList = new ArrayList<Model>();
		try {
			con = dataSource.getConnection();
			// getStatementSelectCount = con.prepareStatement(sqlLastIndex);
			getStatementSelectCount = con.prepareStatement(sqlLastIndex, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			getStatementSelectCount.setFetchSize(Integer.MIN_VALUE);
			ResultSet rs = getStatementSelectCount.executeQuery();
			while (rs.next()) {
				Model pcAgeTarget = new Model();
				pcAgeTarget.setId(rs.getInt("id"));
				modelList.add(pcAgeTarget);
			}
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			con.close();
		}
		return modelList;
	}

	public Model resultLastIndexDAO() throws SQLException {
		Connection con = null;
		Model pcAgeTarget = new Model();
		try {
			con = dataSource.getConnection();
			getStatementSelectCount = con.prepareStatement(sqlLastIndex);
			ResultSet rs = getStatementSelectCount.executeQuery();
			while (rs.next()) {
				pcAgeTarget.setId(rs.getInt("id"));
			}
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			con.close();
		}
		return pcAgeTarget;
	}

	public List<Model> getSelectAllDAOMapper(int resultId) throws SQLException {
		myTemplate.setFetchSize(30000);
		List<Model> listPcAgeTargets = myTemplate.query(sqlSelectAll, new Object[] { resultId }, new ModelWrapper());
		return listPcAgeTargets;
	}

	public List<Model> getSelectAllDAOMapperLimit10(int resultId) throws SQLException {
		myTemplate.setFetchSize(30000);
		List<Model> listPcAgeTargets = myTemplate.query(sqlSelectAlllimit10, new Object[] { resultId },
				new ModelWrapper());
		return listPcAgeTargets;
	}

	public Integer resultListLastIndexDAOMapper() throws SQLException {
		int total = myTemplate.queryForInt(sqlLastIndex);
		return total;
	}

	public List<Model> resultListLastIndexDAO2() throws SQLException {
		Connection con = null;
		List<Model> modelList = new ArrayList<Model>();
		con = dataSource.getConnection();
		Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		// stmt.setFetchSize(Integer.MIN_VALUE);
		stmt.setFetchSize(100000);
		ResultSet rs = stmt.executeQuery(sqlLastIndex);
		rs.setFetchSize(50000);
		while (rs.next()) {
			Model pcAgeTarget = new Model();
			pcAgeTarget.setId(rs.getInt("id"));
			modelList.add(pcAgeTarget);
		}
		return modelList;
	}

	public List<Model> getSelectAllDAO2(int resultId) throws SQLException {
		Connection con = null;
		List<Model> listPcAgeTargets = new ArrayList<Model>();
		try {
			con = dataSource.getConnection();
			Statement stmt = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			// stmt.setFetchSize(Integer.MIN_VALUE);
			stmt.setFetchSize(100000);
			String string = sqlSelectAll2 + resultId;
			ResultSet rs = stmt.executeQuery(string);
			rs.setFetchSize(50000);
			while (rs.next()) {
				Model pcAgeTarget = new Model();
				pcAgeTarget.setId(rs.getInt("SERVICE_ID"));
				pcAgeTarget.setUserId(rs.getString("MSISDN"));
				pcAgeTarget.setContent(rs.getString("JSON1"));
				listPcAgeTargets.add(pcAgeTarget);
			}
		} catch (SQLException ex) {
			LOGGER.error(ex.getMessage(), ex);
		} finally {
			con.close();
		}
		return listPcAgeTargets;
	}

}
