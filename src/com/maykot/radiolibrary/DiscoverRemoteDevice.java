package com.maykot.radiolibrary;

import java.io.IOException;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.XBeeNetwork;
import com.digi.xbee.api.exceptions.XBeeException;
import com.maykot.utils.DeviceConfig;

public class DiscoverRemoteDevice {

	private static RemoteXBeeDevice remoteDevice;

	public static RemoteXBeeDevice discover(DeviceConfig deviceConfig, DigiMeshDevice myDevice)
			throws IOException, XBeeException {

		// Obtain the remote XBee device from the XBee network.
		XBeeNetwork xbeeNetwork = myDevice.getNetwork();
		do {
			remoteDevice = xbeeNetwork.discoverDevice(deviceConfig.getRemoteNodeID());
			if (remoteDevice == null) {
				System.out.println("Couldn't find the Radio " + deviceConfig.getRemoteNodeID() + ".");
			}
		} while (remoteDevice == null);
		System.out.println("Was found REMOTE radio " + deviceConfig.getRemoteNodeID() + " (PowerLevel "
				+ remoteDevice.getPowerLevel() + ").");
		return remoteDevice;
	}
}
