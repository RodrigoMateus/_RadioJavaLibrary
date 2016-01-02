package com.maykot.radiolibrary;

public class TreatMessage extends Thread {

	private int contentType;
	private byte[] message;

	public TreatMessage(int contentType, byte[] message) {
		super();
		this.contentType = contentType;
		this.message = message;
	}

	@Override
	public void run() {
		super.run();

		switch (contentType) {
		case MessageParameter.ENDPOINT_TXT:
			Router.getInstance().getIProcessMessage().textMessageReceived(message);
			break;

		default:
			break;
		}
	}
}
