package com.maykot.radiolibrary;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.maykot.radiolibrary.model.MessageParameter;

public class MessageHandler extends Thread {

	private int contentType;
	private byte[] message;
	private RemoteXBeeDevice sourceDeviceAddress;
	private RadioRouter radioRouter;

	public MessageHandler(RemoteXBeeDevice sourceDeviceAddress, int contentType, byte[] message) {
		this(RadioRouter.getInstance());
		this.sourceDeviceAddress = sourceDeviceAddress;
		this.contentType = contentType;
		this.message = message;
	}

	private MessageHandler(RadioRouter radioRouter) {
		this.radioRouter = radioRouter;
	}

	@Override
	public void run() {
		super.run();

		switch (contentType) {
		case MessageParameter.SEND_TXT_FILE:
			radioRouter.getIProcessMessage().textFileReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_TXT_FILE:
			radioRouter.getIProcessMessage().textFileConfirm(message);
			break;

		case MessageParameter.SEND_LOCAL_POST:
			radioRouter.getIProcessMessage().localPostReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_LOCAL_POST:
			radioRouter.getIProcessMessage().localPostConfirm(message);
			break;

		case MessageParameter.SEND_MOBILE_POST:
			radioRouter.getIProcessMessage().mobilePostReceived(sourceDeviceAddress, message);
			break;

		case MessageParameter.CONFIRM_MOBILE_POST:
			radioRouter.getIProcessMessage().mobilePostConfirm(message);
			break;

		default:
			break;
		}
	}
}
