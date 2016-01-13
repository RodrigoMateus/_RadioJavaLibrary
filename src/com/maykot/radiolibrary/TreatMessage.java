package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;

public class TreatMessage extends Thread {

	private int contentType;
	private byte[] message;
	private RemoteXBeeDevice sourceDeviceAddress;
	private RadioRouter routerRadio;

	public TreatMessage(RemoteXBeeDevice sourceDeviceAddress, int contentType, byte[] message) {
		this(RadioRouter.getInstance());
		this.sourceDeviceAddress = sourceDeviceAddress;
		this.contentType = contentType;
		this.message = message;
	}

	private TreatMessage(RadioRouter routerRadio) {
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

		case MessageParameter.SEND_LOCAL_POST:
			routerRadio.getIProcessMessage().localPostReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_LOCAL_POST:
			routerRadio.getIProcessMessage().localPostConfirm(message);
			break;

		case MessageParameter.SEND_MOBILE_POST:
			routerRadio.getIProcessMessage().localPostReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_MOBILE_POST:
			routerRadio.getIProcessMessage().localPostConfirm(message);
			break;

		default:
			break;
		}
	}
}
