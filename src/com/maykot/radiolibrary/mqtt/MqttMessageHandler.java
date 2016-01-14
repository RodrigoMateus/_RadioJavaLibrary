package com.maykot.radiolibrary.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.radiolibrary.RadioRouter;
import com.maykot.radiolibrary.model.ErrorMessage;
import com.maykot.radiolibrary.model.MessageParameter;

public class MqttMessageHandler {

	private RadioRouter radioRouter;

	public MqttMessageHandler() {
		this(RadioRouter.getInstance());
	}

	public MqttMessageHandler(RadioRouter radioRouter) {
		this.radioRouter = radioRouter;
	}

	public void processMessage(MqttClient mqttClient, DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice,
			String topic, MqttMessage message) {

		// Regra de formatação de "topic":
		// maykot/CONTENT_TYPE/MQTT_CLIENT_ID/MESSAGE_ID
		String[] topicParameter = topic.split("/");
		String contentType = topicParameter[1];
		String clientId = topicParameter[2];
		String messageId = topicParameter[3];

		byte[] dataToSend = message.getPayload();

		switch (contentType) {
		case "request":
			try {
				radioRouter.sendMessage(myDevice, remoteDevice, MessageParameter.SEND_MOBILE_POST, dataToSend);
			} catch (TransmitException e) {
				System.out.println(
						ErrorMessage.TRANSMIT_EXCEPTION.value() + ": " + ErrorMessage.TRANSMIT_EXCEPTION.description());
				new ProxyResponseSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.TRANSMIT_EXCEPTION.value(), ErrorMessage.TRANSMIT_EXCEPTION.description());
			} catch (TimeoutException e) {
				System.out.println(
						"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
				new ProxyResponseSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.TIMEOUT_ERROR.value(), ErrorMessage.TIMEOUT_ERROR.description());
			} catch (XBeeException e) {
				System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
				new ProxyResponseSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.XBEE_EXCEPTION_ERROR.value(), ErrorMessage.XBEE_EXCEPTION_ERROR.description());
			} catch (Exception e) {
				System.out.println("Erro " + ErrorMessage.EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.EXCEPTION_ERROR.description());
				new ProxyResponseSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.EXCEPTION_ERROR.value(), ErrorMessage.EXCEPTION_ERROR.description());
			}
			break;

		default:
			break;
		}
	}

}