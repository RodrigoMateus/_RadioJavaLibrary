package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;

public class TreatMessage extends Thread {

	private int contentType;
	private byte[] message;
	private RemoteXBeeDevice sourceDeviceAddress;

	public TreatMessage(RemoteXBeeDevice sourceDeviceAddress, int contentType, byte[] message) {
		super();
		this.sourceDeviceAddress = sourceDeviceAddress;
		this.contentType = contentType;
		this.message = message;
	}

	@Override
	public void run() {
		super.run();

		switch (contentType) {
		case MessageParameter.SEND_TXT_FILE:
			RouterRadio.getInstance().getIProcessMessage().textFileReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_TXT_FILE:
			RouterRadio.getInstance().getIProcessMessage().textFileConfirm(message);
			break;

		case MessageParameter.SEND_HTTP_POST:
			RouterRadio.getInstance().getIProcessMessage().httpPostReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_HTTP_POST:
			RouterRadio.getInstance().getIProcessMessage().httpPostConfirm(message);
			break;

		default:
			break;
		}
	}
}
