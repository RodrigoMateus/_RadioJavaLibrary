package com.maykot.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttMessagePublisher extends Thread {

	private MqttClient mqttClient;
	private String topic;
	private MqttMessage mqttMessage;

	public MqttMessagePublisher(MqttClient mqttClient, String topic, MqttMessage mqttMessage) {
		this.mqttClient = mqttClient;
		this.topic = topic;
		this.mqttMessage = mqttMessage;
	}

	@Override
	public void run() {
		try {
			mqttClient.publish(topic, mqttMessage);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}