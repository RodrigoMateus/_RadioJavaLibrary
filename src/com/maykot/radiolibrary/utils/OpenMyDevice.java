package com.maykot.radiolibrary.utils;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.ZigBeeDevice;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.utils.SerialPorts;
import com.maykot.radiolibrary.RadioRouter;

public class OpenMyDevice {

	public static ZigBeeDevice open(DeviceConfig deviceConfig, ZigBeeDevice myDevice) {

		try {
			myDevice = new ZigBeeDevice(deviceConfig.getDevicePort(), deviceConfig.getDeviceBaudRate());
			myDevice.open();
			myDevice.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
			myDevice.setReceiveTimeout(deviceConfig.getTimeOutForSyncOperations());
			myDevice.addExplicitDataListener(RadioRouter.getInstance());
			System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel "
					+ myDevice.getPowerLevel() + ").");
			return myDevice;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String port : SerialPorts.getSerialPortList()) {
			try {
				System.out.println("Try " + port);
				myDevice = new ZigBeeDevice(port, deviceConfig.getDeviceBaudRate());
				myDevice.open();
				myDevice.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
				myDevice.setReceiveTimeout(deviceConfig.getTimeOutForSyncOperations());
				myDevice.addExplicitDataListener(RadioRouter.getInstance());
				System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel "
						+ myDevice.getPowerLevel() + ").");
				return myDevice;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("openDevice() ERROR");
			}
		}
		System.out.println("LOCAL Radio not found! Try openDevice() again.");
		return open(deviceConfig, myDevice);
	}

	public static DigiMeshDevice open(DeviceConfig deviceConfig, DigiMeshDevice myDevice) {

		try {
			myDevice = openDevice(deviceConfig.getDevicePort(), deviceConfig.getDeviceBaudRate());
			myDevice.setReceiveTimeout(deviceConfig.getTimeOutForSyncOperations());
			myDevice.addExplicitDataListener(RadioRouter.getInstance());
			System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel "
					+ myDevice.getPowerLevel() + ").");
			return myDevice;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String port : SerialPorts.getSerialPortList()) {
			try {
				System.out.println("Try " + port);
				myDevice = openDevice(port, deviceConfig.getDeviceBaudRate());
				myDevice.setReceiveTimeout(deviceConfig.getTimeOutForSyncOperations());
				myDevice.addExplicitDataListener(RadioRouter.getInstance());
				System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel "
						+ myDevice.getPowerLevel() + ").");
				return myDevice;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("openDevice() ERROR");
			}
		}
		System.out.println("LOCAL Radio not found! Try openDevice() again.");
		return open(deviceConfig, myDevice);
	}

	private static DigiMeshDevice openDevice(String port, int baudRate) throws XBeeException {
		DigiMeshDevice device = new DigiMeshDevice(port, baudRate);
		device.open();
		device.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
		return device;
	}
}
