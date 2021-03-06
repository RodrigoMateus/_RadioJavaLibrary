package com.maykot.radiolibrary.interfaces;

import com.digi.xbee.api.RemoteXBeeDevice;

public interface IProcessMessage {

	public void clientConnectionReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void clientConnectionConfirm(byte[] message);

	public void textFileReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void textFileConfirm(byte[] message);

	public void localPostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void localPostConfirm(byte[] message);

	public void mobilePostReceived(RemoteXBeeDevice sourceDeviceAddress, byte[] message);

	public void mobilePostConfirm(byte[] message);

}
