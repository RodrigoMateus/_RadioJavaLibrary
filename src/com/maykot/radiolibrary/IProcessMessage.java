package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;

public interface IProcessMessage {

	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void textFileConfirm(byte[] message);

	public void httpPostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	void httpPostConfirm(byte[] message);

}
