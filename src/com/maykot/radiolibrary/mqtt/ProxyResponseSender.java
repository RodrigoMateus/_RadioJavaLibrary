package com.maykot.radiolibrary.mqtt;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;

import com.maykot.radiolibrary.model.ProxyResponse;

public class ProxyResponseSender {

	public void sendErrorMessage(MqttClient mqttClient, String clientId, String messageId, int errorCode,
			String errorDescription) {

		String topic = "maykot/response/" + clientId + "/" + messageId;

		ProxyResponse proxyResponse = new ProxyResponse(errorCode, errorDescription.getBytes());
		proxyResponse.setMqttClientId(clientId);
		proxyResponse.setIdMessage(messageId);

		byte[] message = SerializationUtils.serialize(proxyResponse);

		new MqttMessagePublisher(mqttClient, topic, message).start();
	}

	public void sendResponseMessage(MqttClient mqttClient, String clientId, String messageId, byte[] message) {

		String topic = "maykot/response/" + clientId + "/" + messageId;

		new MqttMessagePublisher(mqttClient, topic, message).start();
	}

}
