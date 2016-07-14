package com.numazu.export;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Session;
import javax.sql.DataSource;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;


import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.numazu.export.common.ConfigPool;
import com.numazu.export.util.APNSInitialize;
import com.numazu.export.util.Database;
import com.numazu.export.util.HikariDb;

import redis.clients.jedis.Jedis;

public class Application {
	
	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
			Application deliveryNoteJobManager = new Application();
			deliveryNoteJobManager.runJob("job-definition.xml", "robust-export");
		  }
	
	public void runJob(String jobConfig, String jobName) {
		try {
			
			String[] springConfig = { jobConfig };
//			LOGGER.info("Loading Servisis Batch v.1.0.1");
			ApplicationContext context = new ClassPathXmlApplicationContext("job-definition.xml");

//			DataSource dataSource = (DataSource) context.getBean("targetDataSource");
//			ConfigPool.setDataSource(dataSource);
			
//			ConfigPool.setDataSource(Database.getDatasource());
			ConfigPool.setDataSource(HikariDb.dataSource());
			
			JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
			Job job = (Job) context.getBean(jobName);
			
			ConnectionFactory factory;
			Connection connection;
			Session session;
			
			//set AMQ
//			factory = new ActiveMQConnectionFactory("failover:tcp://103.16.199.101:61616");
//			connection = factory.createConnection();
//			session = connection.createSession(false, 
//					Session.AUTO_ACKNOWLEDGE);
//			ConfigPool.setFactory(factory);
//			ConfigPool.setConnection(connection);
//			ConfigPool.setSession(session);
			
			ConfigPool.setJedis(new Jedis(ConfigPool.getUrlJedis()));
			
			//set rabboiitMQ
			ConfigPool.getConfigRabbitMQ().getFactory().setAutomaticRecoveryEnabled(true);
			ConfigPool.getConfigRabbitMQ().getFactory().setUri(ConfigPool.getUriRabbit());
			ConfigPool.getConfigRabbitMQ().getFactory().setHandshakeTimeout(60000);
			ConfigPool.getConfigRabbitMQ().getFactory().setNetworkRecoveryInterval(5000);
			ConfigPool.getConfigRabbitMQ().getFactory().setTopologyRecoveryEnabled(true);
			ConfigPool.getConfigRabbitMQ().setConnection(ConfigPool.getConfigRabbitMQ().getFactory().newConnection());
			ConfigPool.getConfigRabbitMQ().setChannel(ConfigPool.getConfigRabbitMQ().getConnection().createChannel());
			ConfigPool.setServiceIOS(APNSInitialize.initialize());
			ConfigPool.setExecutor((ThreadPoolExecutor) Executors.newFixedThreadPool(200));
			
			
			JobParametersBuilder builder = new JobParametersBuilder();
			builder.addDate("date", new Date());
			builder.addLong("maxThread", new Long(2));

			JobExecution execution = jobLauncher.run(job,builder.toJobParameters());
			LOGGER.info("Exit status Servisis = " + execution.getStatus());
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
}
