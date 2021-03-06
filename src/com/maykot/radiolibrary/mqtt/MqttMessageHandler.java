package com.maykot.radiolibrary.mqtt;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.ZigBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.TransmitException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.maykot.radiolibrary.model.Point;
import com.maykot.radiolibrary.RadioRouter;
import com.maykot.radiolibrary.model.ErrorMessage;
import com.maykot.radiolibrary.model.MessageParameter;
import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.utils.LogRecord;

@SuppressWarnings("unused")
public class MqttMessageHandler {

	private RadioRouter radioRouter;

	public MqttMessageHandler() {
		this(RadioRouter.getInstance());
	}

	public MqttMessageHandler(RadioRouter radioRouter) {
		this.radioRouter = radioRouter;
	}

	public void processMessage(MqttClient mqttClient, DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice,
			String topic, MqttMessage mqttMessage) {

		// Regra de formatação de "topic":
		// maykot/CONTENT_TYPE/MQTT_CLIENT_ID/MESSAGE_ID
		String[] topicParameter = topic.split("/");
		String contentType = topicParameter[1];
		String clientId = topicParameter[2];
		String messageId = topicParameter[3];

		byte[] dataToSend = mqttMessage.getPayload();

		switch (contentType) {
		case "request":
			try {
				ProxyRequest proxyRequest = (ProxyRequest) SerializationUtils.deserialize(dataToSend);

				// Salva o Log das mensagens vindas do App Móvel
				saveCoordinates(proxyRequest.getBody());
				LogRecord.insertLog("MobileRequest_RouterLog",
						new String(clientId + ";" + messageId + ";"
								+ new String(new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss:SSS").format(new Date())) + ";"
								+ new String(proxyRequest.getBody())));
				radioRouter.sendMessage(myDevice, remoteDevice, MessageParameter.SEND_MOBILE_POST, dataToSend);
			} catch (TransmitException e) {
				System.out.println(
						ErrorMessage.TRANSMIT_EXCEPTION.value() + ": " + ErrorMessage.TRANSMIT_EXCEPTION.description());
				new MqttMessageSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.TRANSMIT_EXCEPTION.value(), ErrorMessage.TRANSMIT_EXCEPTION.description());
			} catch (TimeoutException e) {
				System.out.println(
						"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
				new MqttMessageSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.TIMEOUT_ERROR.value(), ErrorMessage.TIMEOUT_ERROR.description());
			} catch (XBeeException e) {
				System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
				new MqttMessageSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.XBEE_EXCEPTION_ERROR.value(), ErrorMessage.XBEE_EXCEPTION_ERROR.description());
			} catch (IllegalArgumentException e) {
				System.out.println("Erro " + ErrorMessage.ILLEGAL_ARGUMENT_EXCEPTION.value() + ": "
						+ ErrorMessage.ILLEGAL_ARGUMENT_EXCEPTION.description());
				new MqttMessageSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.ILLEGAL_ARGUMENT_EXCEPTION.value(),
						ErrorMessage.ILLEGAL_ARGUMENT_EXCEPTION.description());
			} catch (Exception e) {
				System.out.println("Erro " + ErrorMessage.EXCEPTION_ERROR.value() + ": "
						+ ErrorMessage.EXCEPTION_ERROR.description());
				new MqttMessageSender().sendErrorMessage(mqttClient, clientId, messageId,
						ErrorMessage.EXCEPTION_ERROR.value(), ErrorMessage.EXCEPTION_ERROR.description());
			}
			break;

		default:
			break;
		}
	}

	private void saveCoordinates(byte[] message) {
		Gson gson = new Gson();

		try {
			Point point = gson.fromJson(new String(message), Point.class);
			LogRecord.insertCoordinates("CoordinatesFromMobile", point.latitude + "," + point.longitude);
		} catch (JsonSyntaxException e1) {
			System.out.println("Mensagem não contém um JSON válido.");
		}
	}

}
