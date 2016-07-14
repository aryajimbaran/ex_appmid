package com.numazu.export.model;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Topic;


public class ProducerBean {
	private MessageProducer producer;
	private Topic topic;
	private Queue queue;
	
	public MessageProducer getProducer() {
		return producer;
	}
	public void setProducer(MessageProducer producer) {
		this.producer = producer;
	}
	public Topic getTopic() {
		return topic;
	}
	public void setTopic(Topic topic) {
		this.topic = topic;
	}
	public Queue getQueue() {
		return queue;
	}
	public void setQueue(Queue queue) {
		this.queue = queue;
	}
}
