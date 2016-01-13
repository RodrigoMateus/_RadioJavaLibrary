package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;

public interface IProcessMessage {

	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void textFileConfirm(byte[] message);

	public void localPostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void localPostConfirm(byte[] message);

	public void mobilePostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void mobilePostConfirm(byte[] message);

}
