package com.maykot.radiolibrary;

import java.util.concurrent.ConcurrentHashMap;

import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.models.ExplicitXBeeMessage;

public class TreatDataReceived {

	ConcurrentHashMap<RemoteXBeeDevice, byte[]> messageHashmap = new ConcurrentHashMap<RemoteXBeeDevice, byte[]>();

	// Métodos de ExplicitXBeeMessage:
	// .getDevice(): retorna device de destino da mensagem.
	// .getSourceEndpoint() retorna o tipo de fragmento (INIT, DATA ou END).
	// .getDestinationEndpoint() retorna o tipo de conteúdo da mensagem.
	// .getClusterID() retorna o tamanho da mensagem original.
	// .getProfileID() retorna a posição inicial do fragmento no byte[] da
	// mensagem original.
	// .getData() retorna o byte[] com fragmento da mensagem original

	public void processDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
		byte[] byteArrayMessage;
		RemoteXBeeDevice sourceDeviceAddress = explicitXBeeMessage.getDevice();

		if (messageHashmap.containsKey(sourceDeviceAddress)) {
			byteArrayMessage = messageHashmap.get(sourceDeviceAddress);
		} else {
			byteArrayMessage = new byte[explicitXBeeMessage.getClusterID()];
			messageHashmap.put(sourceDeviceAddress, byteArrayMessage);
		}

		int endPoint = explicitXBeeMessage.getSourceEndpoint();

		switch (endPoint) {

		case MessageParameter.MESSAGE_INIT:
			System.out.println("MESSAGE_INIT");
			break;

		case MessageParameter.MESSAGE_DATA:
			System.out.println("Inserir na posição " + explicitXBeeMessage.getProfileID());
			System.arraycopy(explicitXBeeMessage.getData(), 0, byteArrayMessage, explicitXBeeMessage.getProfileID(),
					explicitXBeeMessage.getData().length);
			messageHashmap.put(sourceDeviceAddress, byteArrayMessage);
			break;

		case MessageParameter.MESSAGE_END:
			messageHashmap.remove(sourceDeviceAddress);
			new TreatMessage(sourceDeviceAddress, explicitXBeeMessage.getDestinationEndpoint(), byteArrayMessage)
					.start();
			System.out.println("MESSAGE_END");
			break;

		default:
			break;
		}
	}

}
