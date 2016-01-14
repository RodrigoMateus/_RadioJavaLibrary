package com.maykot.radiolibrary.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;

public class MqttMessagePublisher extends Thread {

	private MqttClient mqttClient;
	private String topic;
	private byte[] message;

	public MqttMessagePublisher(MqttClient mqttClient, String topic, byte[] message) {
		this.mqttClient = mqttClient;
		this.topic = topic;
		this.message = message;
	}

	@Override
	public void run() {

		MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(message);

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