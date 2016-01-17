package com.maykot.radiolibrary.mqtt;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;

import com.maykot.radiolibrary.model.ProxyResponse;
import com.maykot.radiolibrary.utils.LogRecord;

public class ProxyResponseSender {

	public void sendErrorMessage(MqttClient mqttClient, String clientId, String messageId, int errorCode,
			String errorDescription) {

		String topic = "maykot/response/" + clientId + "/" + messageId;

		ProxyResponse proxyResponse = new ProxyResponse(errorCode, errorDescription.getBytes());
		proxyResponse.setMqttClientId(clientId);
		proxyResponse.setIdMessage(messageId);

		byte[] message = SerializationUtils.serialize(proxyResponse);

		new MqttMessagePublisher(mqttClient, topic, message).start();

		// Registro da hora de envio de uma mensagem de erro
		LogRecord.insertLog("MobileError_RouterLog", new String(clientId + ";" + messageId + ";"
				+ new String(new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss:SSS").format(new Date()))));
	}

	public void sendResponseMessage(MqttClient mqttClient, String clientId, String messageId, byte[] message) {

		String topic = "maykot/response/" + clientId + "/" + messageId;

		new MqttMessagePublisher(mqttClient, topic, message).start();

		// Registro da hora de envio de uma mensagem vinda do App Móvel
		LogRecord.insertLog("MobileResponse_RouterLog", new String(clientId + ";" + messageId + ";"
				+ new String(new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss:SSS").format(new Date()))));
	}

}
