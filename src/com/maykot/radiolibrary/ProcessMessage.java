package com.maykot.radiolibrary;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProcessMessage extends Thread {

	private int contentType;
	private byte[] message;

	public ProcessMessage(int contentType, byte[] message) {
		super();
		this.contentType = contentType;
		this.message = message;
	}

	@Override
	public void run() {
		super.run();

		switch (contentType) {
		case MessageParameter.ENDPOINT_TXT:
			String fileName = (new String(new SimpleDateFormat("yyyy-MM-dd_HHmmss_").format(new Date()))) + ".txt";
			try {
				FileOutputStream fileChannel = new FileOutputStream(fileName);
				fileChannel.write(message);
				fileChannel.close();
			} catch (FileNotFoundException e) {
				System.out.println("ERRO FileChannel");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

}
