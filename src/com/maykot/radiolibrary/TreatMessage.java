package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;

public class TreatMessage extends Thread {

	private int contentType;
	private byte[] message;
	private RemoteXBeeDevice sourceDeviceAddress;

	public TreatMessage(RemoteXBeeDevice sourceDeviceAddress2, int contentType, byte[] message) {
		super();
		this.sourceDeviceAddress = sourceDeviceAddress2;
		this.contentType = contentType;
		this.message = message;
	}

	@Override
	public void run() {
		super.run();

		switch (contentType) {
		case MessageParameter.SEND_TXT_FILE:
			Router.getInstance().getIProcessMessage().textFileReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_TXT_FILE:
			Router.getInstance().getIProcessMessage().textFileConfirm(message);
			break;

		default:
			break;
		}
	}
}
