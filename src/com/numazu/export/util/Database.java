package com.numazu.export.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

public final class Database {

    private static final BasicDataSource dataSource = new BasicDataSource();

    static {
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://103.16.199.108:3307/litebigc_dev");
        dataSource.setUsername("litebigc_devadm");
        dataSource.setPassword("lBdeV@8#$!_");
        dataSource.setInitialSize(100);
        dataSource.setMaxIdle(100);
        dataSource.setMinIdle(20);
        dataSource.setMaxTotal(750);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setValidationQueryTimeout(10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(true);
        dataSource.setTimeBetweenEvictionRunsMillis(5000);
        dataSource.setNumTestsPerEvictionRun(5);
        dataSource.setMinEvictableIdleTimeMillis(1000);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static BasicDataSource getDatasource() throws SQLException {
        return dataSource;
    }
    
    public static void getConnectionStatus(boolean printStatus){
    	if(printStatus){
    		int active = dataSource.getNumActive();
        	int idle = dataSource.getNumIdle();
        	System.out.println("Active = "+active+" | idle = "+idle);
    	}
    }

}