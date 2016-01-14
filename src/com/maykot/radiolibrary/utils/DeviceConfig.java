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

	public String getXTendPort() {
		return propertiesHashMap.get("XTendPort");
	}

	public int getXTendBaudRate() {
		return Integer.parseInt(propertiesHashMap.get("XTendBaudRate"));
	}

	public int getTimeOutForSyncOperations() {
		return Integer.parseInt(propertiesHashMap.get("TimeOutForSyncOperations"));
	}

	public String getXbeePort() {
		return propertiesHashMap.get("XBeePort");
	}

	public int getXbeeBaudRate() {
		return Integer.parseInt(propertiesHashMap.get("XBeeBaudRate"));
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
