package com.maykot.radiolibrary.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class DeviceConfig {

	private static DeviceConfig uniqueInstance;
	HashMap<String, String> propertiesHashMap = new HashMap<String, String>();

	private DeviceConfig() {
		Properties properties = new Properties();
		try {
			FileInputStream file = new FileInputStream("DeviceConfig.properties");
			properties.load(file);
		} catch (IOException e1) {
			System.out.println("File DeviceConfig.properties NOT found!");
		}

		for (String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			propertiesHashMap.put(key, value);
		}
	}

	public static DeviceConfig getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new DeviceConfig();
		}
		return uniqueInstance;
	}

	public String getDeviceType() {
		return propertiesHashMap.get("DeviceType");
	}

	public String getDevicePort() {
		return propertiesHashMap.get("DevicePort");
	}

	public int getDeviceBaudRate() {
		return Integer.parseInt(propertiesHashMap.get("DeviceBaudRate"));
	}

	public int getTimeOutForSyncOperations() {
		return Integer.parseInt(propertiesHashMap.get("TimeOutForSyncOperations"));
	}

	public String getBrokerURL() {
		return propertiesHashMap.get("BrokerURL");
	}

	public String getRemoteNodeID() {
		return propertiesHashMap.get("RemoteNodeID");
	}

	public int getQoS() {
		return Integer.parseInt(propertiesHashMap.get("QoS"));
	}

}
