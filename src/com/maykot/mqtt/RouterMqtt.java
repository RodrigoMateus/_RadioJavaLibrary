package com.maykot.mqtt;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.maykot.utils.DeviceConfig;

public class RouterMqtt implements MqttCallback {

	private DeviceConfig deviceConfig;
	private MqttClient mqttClient;
	private TreatMqttMessage treatMqttMessage;
	private DigiMeshDevice myDevice;
	private RemoteXBeeDevice remoteDevice;

	public RouterMqtt(DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice) throws IOException {
		this(DeviceConfig.getInstance());
		this.myDevice = myDevice;
		this.remoteDevice = remoteDevice;
		treatMqttMessage = new TreatMqttMessage();
	}

	private RouterMqtt(DeviceConfig deviceConfig) {
		this.deviceConfig = deviceConfig;
	}

	public MqttClient connect() throws IOException, MqttException {
		mqttClient = new MqttClient(deviceConfig.getBrokerURL(), deviceConfig.getClientId(), null);
		mqttClient.setCallback(this);
		mqttClient.connect();
		mqttClient.subscribe(deviceConfig.getSubscribedTopic(), deviceConfig.getQoS());
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
		treatMqttMessage.processMessage(myDevice, remoteDevice, topic, message);
	}

}
