package com.numazu.export.common;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.sql.DataSource;

import com.notnoop.apns.ApnsService;
import com.numazu.export.model.ProducerBean;
import com.numazu.export.util.APNSInitialize;

import redis.clients.jedis.Jedis;


public class ConfigPool {
	private ConfigPool() {
	}
	
	 /** The Datasource */
    private static DataSource DATASOURCE = null;
    private static DataSource DATASOURCEHSQL = null;
    private static Integer resultAfterCreateFile = 0;
    private static Integer resultId = 0;
    private static Integer runningJob = 0;
    private static Integer checkFileTxt = 0;
    private static String checkFileTxtS = "n";
    private static Integer valueMaxRow = 0;
    private static Integer valueNextCreate = 0;
    private static Integer updateDate = 0;
    private static ConnectionFactory factory;
 	private static Connection connection;
 	private static Session session;
 	private static HashMap<String, ProducerBean> queueMap = new HashMap<String, ProducerBean>();
 	private static HashMap<String, ProducerBean> topicMap = new HashMap<String, ProducerBean>();
 	private static String urlJedis = "103.16.199.144";
 	private static String keySeq = "seq-service-android-new";
 	private static Jedis jedis = new Jedis();
 	private static String uriRabbit = "amqp://admin:admin@202.74.239.166";
 	private static ConfigRabbitMQ configRabbitMQ = new ConfigRabbitMQ();
 	private static Integer seq = 0;
 	private static ThreadPoolExecutor executor;
 	private static ApnsService serviceIOS;
 	
    
	public static ConfigRabbitMQ getConfigRabbitMQ() {
		return configRabbitMQ;
	}
	public static void setConfigRabbitMQ(ConfigRabbitMQ configRabbitMQ) {
		ConfigPool.configRabbitMQ = configRabbitMQ;
	}
	public static DataSource getDataSource() {
		return DATASOURCE;
	}
	public static void setDataSource(DataSource dATASOURCE) {
		DATASOURCE = dATASOURCE;
	}
	public static DataSource getDATASOURCEHSQL() {
		return DATASOURCEHSQL;
	}
	public static void setDATASOURCEHSQL(DataSource dATASOURCEHSQL) {
		DATASOURCEHSQL = dATASOURCEHSQL;
	}
	public static Integer getResultAfterCreateFile() {
		return resultAfterCreateFile;
	}
	public static void setResultAfterCreateFile(Integer resultAfterCreateFile) {
		ConfigPool.resultAfterCreateFile = resultAfterCreateFile;
	}
	public static Integer getResultId() {
		return resultId;
	}
	public static void setResultId(Integer resultId) {
		ConfigPool.resultId = resultId;
	}
	public static Integer getRunningJob() {
		return runningJob;
	}
	public static void setRunningJob(Integer runningJob) {
		ConfigPool.runningJob = runningJob;
	}
	public static Integer getCheckFileTxt() {
		return checkFileTxt;
	}
	public static void setCheckFileTxt(Integer checkFileTxt) {
		ConfigPool.checkFileTxt = checkFileTxt;
	}
	public static String getCheckFileTxtS() {
		return checkFileTxtS;
	}
	public static void setCheckFileTxtS(String checkFileTxtS) {
		ConfigPool.checkFileTxtS = checkFileTxtS;
	}
	public static Integer getValueMaxRow() {
		return valueMaxRow;
	}
	public static void setValueMaxRow(Integer valueMaxRow) {
		ConfigPool.valueMaxRow = valueMaxRow;
	}
	public static Integer getValueNextCreate() {
		return valueNextCreate;
	}
	public static void setValueNextCreate(Integer valueNextCreate) {
		ConfigPool.valueNextCreate = valueNextCreate;
	}
	public static Integer getUpdateDate() {
		return updateDate;
	}
	public static void setUpdateDate(Integer updateDate) {
		ConfigPool.updateDate = updateDate;
	}
	public static DataSource getDATASOURCE() {
		return DATASOURCE;
	}
	public static void setDATASOURCE(DataSource dATASOURCE) {
		DATASOURCE = dATASOURCE;
	}
	public static ConnectionFactory getFactory() {
		return factory;
	}
	public static void setFactory(ConnectionFactory factory) {
		ConfigPool.factory = factory;
	}
	public static Connection getConnection() {
		return connection;
	}
	public static void setConnection(Connection connection) {
		ConfigPool.connection = connection;
	}
	public static Session getSession() {
		return session;
	}
	public static void setSession(Session session) {
		ConfigPool.session = session;
	}
	public static HashMap<String, ProducerBean> getQueueMap() {
		return queueMap;
	}
	public static void setQueueMap(HashMap<String, ProducerBean> queueMap) {
		ConfigPool.queueMap = queueMap;
	}
	public static HashMap<String, ProducerBean> getTopicMap() {
		return topicMap;
	}
	public static void setTopicMap(HashMap<String, ProducerBean> topicMap) {
		ConfigPool.topicMap = topicMap;
	}
	public static String getUrlJedis() {
		return urlJedis;
	}
	public static String getKeySeq() {
		return keySeq;
	}
	public static Jedis getJedis() {
		return jedis;
	}
	public static void setJedis(Jedis jedis) {
		ConfigPool.jedis = jedis;
	}
	public static String getUriRabbit() {
		return uriRabbit;
	}
	public static Integer getSeq() {
		return seq;
	}
	public static void setSeq(Integer seq) {
		ConfigPool.seq = seq;
	}
	public static ThreadPoolExecutor getExecutor() {
		return executor;
	}
	public static void setExecutor(ThreadPoolExecutor executor) {
		ConfigPool.executor = executor;
	}
	public static ApnsService getServiceIOS() {
		if(serviceIOS == null){
			serviceIOS = APNSInitialize.initialize(serviceIOS);
		}
		return serviceIOS;
	}
	public static void setServiceIOS(ApnsService serviceIOS) {
		ConfigPool.serviceIOS = serviceIOS;
	}
	public static void setUrlJedis(String urlJedis) {
		ConfigPool.urlJedis = urlJedis;
	}
	public static void setKeySeq(String keySeq) {
		ConfigPool.keySeq = keySeq;
	}
	public static void setUriRabbit(String uriRabbit) {
		ConfigPool.uriRabbit = uriRabbit;
	}
	
	
	
}
