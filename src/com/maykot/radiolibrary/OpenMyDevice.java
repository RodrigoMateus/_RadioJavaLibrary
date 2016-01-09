package com.maykot.radiolibrary;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.models.APIOutputMode;
import com.digi.xbee.api.utils.SerialPorts;
import com.maykot.utils.DeviceConfig;

public class OpenMyDevice {

	public static DigiMeshDevice open(DeviceConfig deviceConfig) {
		DigiMeshDevice myDevice;

		try {
			myDevice = openDevice(deviceConfig.getXTendPort(), deviceConfig.getXTendBaudRate());
			myDevice.setReceiveTimeout(deviceConfig.getTimeOutForSyncOperations());
			myDevice.addExplicitDataListener(RouterRadio.getInstance());
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
				myDevice = openDevice(port, deviceConfig.getXTendBaudRate());
				myDevice.setReceiveTimeout(deviceConfig.getTimeOutForSyncOperations());
				myDevice.addExplicitDataListener(RouterRadio.getInstance());
				System.out.println("Was found LOCAL radio " + myDevice.getNodeID() + " (PowerLevel "
						+ myDevice.getPowerLevel() + ").");
				return myDevice;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("openDevice() ERROR");
			}
		}
		System.out.println("LOCAL Radio not found! Try openDevice() again.");
		return open(deviceConfig);
	}

	private static DigiMeshDevice openDevice(String port, int bd) throws Exception {
		DigiMeshDevice device = new DigiMeshDevice(port, bd);
		device.open();
		device.setAPIOutputMode(APIOutputMode.MODE_EXPLICIT);
		return device;
	}
}
