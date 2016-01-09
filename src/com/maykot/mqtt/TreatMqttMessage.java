package com.maykot.mqtt;

import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.ErrorMessage;
import com.maykot.radiolibrary.MessageParameter;
import com.maykot.radiolibrary.ProxyResponse;
import com.maykot.radiolibrary.RouterRadio;

public class TreatMqttMessage {

	private RouterRadio routerRadio;

	public TreatMqttMessage() {
		this(RouterRadio.getInstance());
	}

	public TreatMqttMessage(RouterRadio routerRadio) {
		this.routerRadio = routerRadio;
	}

	public void processMessage(DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice, String topic,
			MqttMessage message) {

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
				routerRadio.sendMessage(myDevice, remoteDevice, MessageParameter.SEND_HTTP_POST, dataToSend);
			} catch (TransmitException e) {
				System.out.println(
						ErrorMessage.TRANSMIT_EXCEPTION.value() + ": " + ErrorMessage.TRANSMIT_EXCEPTION.description());
				sendErrorMessage(ErrorMessage.TRANSMIT_EXCEPTION.value(), clientId, messageId,
						ErrorMessage.TRANSMIT_EXCEPTION.description());
			} catch (TimeoutException e) {
				System.out.println(
						"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
				sendErrorMessage(ErrorMessage.TIMEOUT_ERROR.value(), clientId, messageId,
						ErrorMessage.TIMEOUT_ERROR.description());
			} catch (XBeeException e) {
				System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
				sendErrorMessage(ErrorMessage.XBEE_EXCEPTION_ERROR.value(), clientId, messageId,
						ErrorMessage.XBEE_EXCEPTION_ERROR.description());
			} catch (Exception e) {
				System.out.println("Erro " + ErrorMessage.EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.EXCEPTION_ERROR.description());
				sendErrorMessage(ErrorMessage.EXCEPTION_ERROR.value(), clientId, messageId,
						ErrorMessage.EXCEPTION_ERROR.description());
			}
			break;

		default:
			break;
		}
	}

	public void sendErrorMessage(int statusCode, String clientId, String messageId, String errorCode) {
		ProxyResponse errorResponse = new ProxyResponse(statusCode, "application/json", errorCode.getBytes());
		errorResponse.setMqttClientId(clientId);
		errorResponse.setIdMessage(messageId);

		byte[] payload = SerializationUtils.serialize(errorResponse);

		new MQTTMonitor().sendMQTT(errorResponse, payload);
	}

}
