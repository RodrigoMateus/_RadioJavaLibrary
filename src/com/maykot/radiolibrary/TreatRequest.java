package com.maykot.radiolibrary;

import java.util.concurrent.ConcurrentHashMap;

import com.digi.xbee.api.models.ExplicitXBeeMessage;

public class TreatRequest {

	ConcurrentHashMap<String, byte[]> messageHashmap = new ConcurrentHashMap<String, byte[]>();

	// Métodos de ExplicitXBeeMessage:
	// .getDevice(): retorna device de destino da mensagem.
	// .getSourceEndpoint() retorna o tipo de fragmento (INIT, DATA ou END).
	// .getDestinationEndpoint() retorna o tipo de conteúdo da mensagem.
	// .getClusterID() retorna o tamanho da mensagem original.
	// .getProfileID() retorna a posição inicial do fragmento no byte[] da
	// mensagem original.
	// .getData() retorna o byte[] com fragmento da mensagem original
	ExplicitXBeeMessage explicitXBeeMessage;

	public void process(ExplicitXBeeMessage explicitXBeeMessage) {
		byte[] byteArrayMessage;
		this.explicitXBeeMessage = explicitXBeeMessage;

		if (messageHashmap.containsKey(explicitXBeeMessage.getDevice().get64BitAddress().toString())) {
			byteArrayMessage = messageHashmap.get(explicitXBeeMessage.getDevice().get64BitAddress().toString());
		} else {
			byteArrayMessage = new byte[explicitXBeeMessage.getClusterID()];
			messageHashmap.put(explicitXBeeMessage.getDevice().get64BitAddress().toString(), byteArrayMessage);
		}

		int endPoint = explicitXBeeMessage.getSourceEndpoint();

		switch (endPoint) {

		case MessageParameter.ENDPOINT_SEND_INIT:
			System.out.println("ENDPOINT_SEND_INIT");
			break;

		case MessageParameter.ENDPOINT_SEND_DATA:
			System.out.println("Inserir na posição " + explicitXBeeMessage.getProfileID());
			System.arraycopy(explicitXBeeMessage.getData(), 0, byteArrayMessage, explicitXBeeMessage.getProfileID(),
					explicitXBeeMessage.getData().length);
			messageHashmap.put(explicitXBeeMessage.getDevice().get64BitAddress().toString(), byteArrayMessage);
			break;

		case MessageParameter.ENDPOINT_SEND_END:
			messageHashmap.remove(explicitXBeeMessage.getDevice().get64BitAddress().toString());
			new ProcessMessage(explicitXBeeMessage.getDestinationEndpoint(), byteArrayMessage).run();
			break;

		default:
			break;
		}
	}

}
