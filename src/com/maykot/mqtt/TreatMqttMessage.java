package com.maykot.mqtt;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.ErrorMessage;
import com.maykot.radiolibrary.MessageParameter;
import com.maykot.radiolibrary.ProxyResponse;
import com.maykot.radiolibrary.RadioRouter;

public class TreatMqttMessage {

	private MqttClient mqttClient;
	private RadioRouter routerRadio;

	public TreatMqttMessage() {
		this(RadioRouter.getInstance());
	}

	public TreatMqttMessage(RadioRouter routerRadio) {
		this.routerRadio = routerRadio;
	}

	public void processMessage(MqttClient mqttClient, DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice,
			String topic, MqttMessage message) {
		this.mqttClient = mqttClient;

		// Regra de formatação de "topic":
		// maykot/CONTENT_TYPE/MQTT_CLIENT_ID/MESSAGE_ID
		String[] topicParameter = topic.split("/");
		String contentType = topicParameter[1];
		String clientId = topicParameter[2];
		String messageId = topicParameter[3];

		byte[] dataToSend = message.getPayload();

		switch (contentType) {
		case "http_post":
			try {
				routerRadio.sendMessage(myDevice, remoteDevice, MessageParameter.SEND_MOBILE_POST, dataToSend);
			} catch (TransmitException e) {
				System.out.println(
						ErrorMessage.TRANSMIT_EXCEPTION.value() + ": " + ErrorMessage.TRANSMIT_EXCEPTION.description());
				sendErrorMessage(ErrorMessage.TRANSMIT_EXCEPTION.value(), ErrorMessage.TRANSMIT_EXCEPTION.description(),
						clientId, messageId, contentType);
			} catch (TimeoutException e) {
				System.out.println(
						"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
				sendErrorMessage(ErrorMessage.TIMEOUT_ERROR.value(), ErrorMessage.TIMEOUT_ERROR.description(), clientId,
						messageId, contentType);
			} catch (XBeeException e) {
				System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
				sendErrorMessage(ErrorMessage.XBEE_EXCEPTION_ERROR.value(),
						ErrorMessage.XBEE_EXCEPTION_ERROR.description(), clientId, messageId, contentType);
			} catch (Exception e) {
				System.out.println("Erro " + ErrorMessage.EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.EXCEPTION_ERROR.description());
				sendErrorMessage(ErrorMessage.EXCEPTION_ERROR.value(), ErrorMessage.EXCEPTION_ERROR.description(),
						clientId, messageId, contentType);
			}
			break;

		default:
			break;
		}
	}

	public void sendErrorMessage(int errorCode, String errorDescription, String clientId, String messageId,
			String contentType) {

		// Regra de formatação de "topic":
		// maykot/CONTENT_TYPE/MQTT_CLIENT_ID/MESSAGE_ID
		String topic = "maykot/" + contentType + "/" + clientId + "/" + messageId;

		ProxyResponse errorResponse = new ProxyResponse(errorCode, contentType, errorDescription.getBytes());
		errorResponse.setMqttClientId(clientId);
		errorResponse.setIdMessage(messageId);

		byte[] payload = SerializationUtils.serialize(errorResponse);
		MqttMessage mqttMessage = new MqttMessage();
		mqttMessage.setPayload(payload);

		new MqttMessagePublisher(mqttClient, topic, mqttMessage).start();
	}

}
