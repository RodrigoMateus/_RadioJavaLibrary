package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;

public interface IProcessMessage {

	public void textMessageReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void textMessageConfirm(byte[] message);

}
