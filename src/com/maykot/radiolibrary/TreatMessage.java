package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;

public class TreatMessage extends Thread {

	private int contentType;
	private byte[] message;
	private RemoteXBeeDevice sourceDeviceAddress;
	private RouterRadio routerRadio;

	public TreatMessage(RemoteXBeeDevice sourceDeviceAddress, int contentType, byte[] message) {
		this(RouterRadio.getInstance());
		this.sourceDeviceAddress = sourceDeviceAddress;
		this.contentType = contentType;
		this.message = message;
	}

	private TreatMessage(RouterRadio routerRadio) {
		this.routerRadio = routerRadio;
	}

	@Override
	public void run() {
		super.run();

		switch (contentType) {
		case MessageParameter.SEND_TXT_FILE:
			routerRadio.getIProcessMessage().textFileReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_TXT_FILE:
			routerRadio.getIProcessMessage().textFileConfirm(message);
			break;

		case MessageParameter.SEND_HTTP_POST:
			routerRadio.getIProcessMessage().httpPostReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_HTTP_POST:
			routerRadio.getIProcessMessage().httpPostConfirm(message);
			break;

		default:
			break;
		}
	}
}
