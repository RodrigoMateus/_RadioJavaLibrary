package com.maykot.radiolibrary.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.ZigBeeDevice;
import com.maykot.radiolibrary.utils.DeviceConfig;

public class MqttRouter implements MqttCallback {

	private static MqttRouter uniqueInstance;
	private ZigBeeDevice myDevice;
	private RemoteXBeeDevice remoteDevice;
	private MqttClient mqttClient;
	private MqttMessageHandler mqttMessageHandler;

	private MqttRouter() {
	}

	public static MqttRouter getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MqttRouter();
		}
		return uniqueInstance;
	}

	public MqttClient setMqttRouter(DeviceConfig deviceConfig, ZigBeeDevice myDevice, RemoteXBeeDevice remoteDevice)
			throws MqttException {
		this.myDevice = myDevice;
		this.remoteDevice = remoteDevice;

		mqttClient = new MqttClient(deviceConfig.getBrokerURL(), "MqttRouter_" + myDevice.getNodeID(), null);
		mqttClient.setCallback(this);
		mqttClient.connect();
		mqttClient.subscribe("maykot/request/#", deviceConfig.getQoS());

		mqttMessageHandler = new MqttMessageHandler();

		return mqttClient;
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		mqttMessageHandler.processMessage(mqttClient, myDevice, remoteDevice, topic, message);
	}

}
