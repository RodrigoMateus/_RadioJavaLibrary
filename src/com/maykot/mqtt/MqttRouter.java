package com.maykot.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.maykot.utils.DeviceConfig;

public class MqttRouter implements MqttCallback {

	private static MqttRouter uniqueInstance;
	private DigiMeshDevice myDevice;
	private RemoteXBeeDevice remoteDevice;
	private MqttClient mqttClient;
	private TreatMqttMessage treatMqttMessage;

	private MqttRouter() {
	}

	public static MqttRouter getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MqttRouter();
		}
		return uniqueInstance;
	}

	public void setMqttRouter(DeviceConfig deviceConfig, DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice)
			throws MqttException {
		this.myDevice = myDevice;
		this.remoteDevice = remoteDevice;

		mqttClient = new MqttClient(deviceConfig.getBrokerURL(), deviceConfig.getClientId(), null);
		mqttClient.setCallback(this);
		mqttClient.connect();
		mqttClient.subscribe(deviceConfig.getSubscribedTopic(), deviceConfig.getQoS());

		treatMqttMessage = new TreatMqttMessage();
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
		treatMqttMessage.processMessage(mqttClient, myDevice, remoteDevice, topic, message);
	}

}
