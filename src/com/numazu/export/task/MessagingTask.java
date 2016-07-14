package com.numazu.export.task;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.notnoop.apns.ApnsService;
import com.numazu.export.common.ConfigPool;
import com.numazu.export.model.ProducerBean;
import com.numazu.export.model.ServiceBean;
import com.numazu.export.util.PushIOSThread;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

public class MessagingTask {

	public static void doSendTopic(String message, String msisdn) throws JMSException {
		ProducerBean producerBean = ConfigPool.getTopicMap().get(msisdn);
		if (producerBean == null) {
			producerBean = new ProducerBean();
			Topic topic = ConfigPool.getSession().createTopic(msisdn);
			MessageProducer topicProducer = ConfigPool.getSession().createProducer(topic);
			topicProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			producerBean.setProducer(topicProducer);
			producerBean.setTopic(topic);

			ConfigPool.getTopicMap().put(msisdn, producerBean);
		}

		MessageProducer producer = producerBean.getProducer();
		// Create and send the message
		TextMessage msg = ConfigPool.getSession().createTextMessage();
		msg.setText(message);

		producer.setTimeToLive(6 * 60 * 60 * 1000);

		producer.send(msg);
		// connection.close();
	}

	public static void doSendQueue(String message, String msisdn) throws JMSException {
		ProducerBean producerBean = ConfigPool.getQueueMap().get(msisdn);
		if (producerBean == null) {
			producerBean = new ProducerBean();
			Queue queue = ConfigPool.getSession().createQueue(msisdn);
			MessageProducer queueProducer = ConfigPool.getSession().createProducer(queue);
			queueProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			producerBean.setProducer(queueProducer);
			producerBean.setQueue(queue);

			ConfigPool.getQueueMap().put(msisdn, producerBean);
		}

		MessageProducer producer = producerBean.getProducer();

		// Create and send the message
		TextMessage msg = ConfigPool.getSession().createTextMessage();
		msg.setText(message);
		producer.setTimeToLive(6 * 60 * 60 * 1000);
		producer.send(msg);
		// connection.close();
	}

	public static void doSendRabbitMQ(String message, String msisdn, Channel channel) {
		try {
			AMQP.BasicProperties properties = new AMQP.BasicProperties();
			properties.builder().deliveryMode(2).expiration("64800000");
			channel.basicPublish("chat", msisdn + ".chat", properties, message.getBytes());

			// channel.basicPublish("chat", msisdn + ".chat", null,
			// message.getBytes());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void sendPush(String msisdn, String serviceBean, String serviceId, ThreadPoolExecutor executor,
			ApnsService serviceIOS) {
		try {
			Gson gson = new Gson();
			ServiceBean service = gson.fromJson(serviceBean, ServiceBean.class);
			if (service.getServiceType().equals("MESSAGE_NEW")) {
				executor.execute(new PushIOSThread(serviceIOS, msisdn, service.getServiceValue(), 0, serviceId,
						ConfigPool.getDataSource()));
			} else if (service.getServiceType().equals("MESSAGE_NEW_GR")) {
				executor.execute(new PushIOSThread(serviceIOS, msisdn, service.getServiceValue(), 0, serviceId,
						ConfigPool.getDataSource()));
			} else if (service.getServiceType().equals("FRIEND_REQUEST")) {
				executor.execute(new PushIOSThread(serviceIOS, msisdn, "You have New Friend Request", 1, serviceId,
						ConfigPool.getDataSource()));
			} else if (service.getServiceType().equals("FILE_DOWNLOAD_NEW")) {
				executor.execute(new PushIOSThread(serviceIOS, msisdn, service.getServiceValue(), 2, serviceId,
						ConfigPool.getDataSource()));
			} else if (service.getServiceType().equals("FILE_DOWNLOAD_GROUP_NEW")) {
				executor.execute(new PushIOSThread(serviceIOS, msisdn, service.getServiceValue(), 2, serviceId,
						ConfigPool.getDataSource()));
			} else if (service.getServiceType().equals("FRIEND_ADD")) {
				executor.execute(new PushIOSThread(serviceIOS, msisdn, "You have New Friend", 1, serviceId,
						ConfigPool.getDataSource()));
			} 
		} catch (JsonSyntaxException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
