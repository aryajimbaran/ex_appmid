package com.numazu.export.common;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class ConfigRabbitMQ {

	private static ConnectionFactory factory = new ConnectionFactory();
	private static Connection connection;
	private static Channel channel;

	public ConnectionFactory getFactory() {
		return factory;
	}

	public static void setFactory(ConnectionFactory factory) {
		ConfigRabbitMQ.factory = factory;
	}

	public Connection getConnection() {
		return connection;
	}
	
	public Connection getConnectionRabbitMQ() {
		if (getConnection() == null){
			try {
				setConnection(getFactory().newConnection());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (getConnection().isOpen() == false){
			try {
				setConnection(getFactory().newConnection());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return connection;
	}

	public void setConnection(Connection connection) {
		ConfigRabbitMQ.connection = connection;
	}

	public Channel getChannel() {
		return channel;
	}
	
	public Channel getChannelRabbitMQ() {
		if(getChannel() == null){
			try {
				setChannel(getConnectionRabbitMQ().createChannel());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(getChannel().isOpen() == false){
			try {
				setChannel(getConnectionRabbitMQ().createChannel());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return channel;
	}

	public void setChannel(Channel channel) {
		ConfigRabbitMQ.channel = channel;
	}

}
