package com.maykot.radiolibrary;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.models.ExplicitXBeeMessage;
import com.maykot.radiolibrary.interfaces.IProcessMessage;
import com.maykot.radiolibrary.model.MessageParameter;

public class RadioRouter implements IExplicitDataReceiveListener {

	private static RadioRouter uniqueInstance;
	private IProcessMessage iProcessMessage;
	private ConcurrentHashMap<RemoteXBeeDevice, byte[]> messageHashmap = new ConcurrentHashMap<RemoteXBeeDevice, byte[]>();

	private RadioRouter() {
	}

	public static RadioRouter getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new RadioRouter();
		}
		return uniqueInstance;
	}

	public void sendMessage(DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice, int contentType, byte[] dataToSend)
			throws TimeoutException, XBeeException {

		int dataSize = dataToSend.length;
		int firstBytePosition = 0;
		int lastBytePosition = 0;

		if (dataSize >= MessageParameter.PAYLOAD_SIZE) {
			lastBytePosition = MessageParameter.PAYLOAD_SIZE;
		} else {
			lastBytePosition = dataSize;
		}
		int count = 0;

		// Calcula a quantidade de fragmentos para enviar
		int qtdPackages = 0;
		if ((dataSize % MessageParameter.PAYLOAD_SIZE) == 0) {
			qtdPackages = (dataSize / MessageParameter.PAYLOAD_SIZE);
		} else {
			qtdPackages = (dataSize / MessageParameter.PAYLOAD_SIZE) + 1;
		}

		// Cria um array de byte[] e inseri os fragmentos
		byte[][] fragmentArray = new byte[qtdPackages][];

		byte[] noMessage = new String("noMessage").getBytes();

		// Argumentos para sendExplicitData():
		// 1º: device de destino da mensagem.
		// 2º: define o tipo de fragmento (INIT, DATA ou END).
		// 3º: define o tipo de conteúdo da mensagem.
		// 4º: define o tamanho da mensagem original.
		// 5º: define a posição inicial do fragmento no byte[] da mensagem
		// original.
		// 6º: byte[] com fragmento da mensagem original.
		myDevice.sendExplicitData(remoteDevice, MessageParameter.MESSAGE_INIT, contentType, dataSize, firstBytePosition,
				noMessage);

		do {
			byte[] fragmentOfData = Arrays.copyOfRange(dataToSend, firstBytePosition, lastBytePosition);

			myDevice.sendExplicitData(remoteDevice, MessageParameter.MESSAGE_DATA, contentType, dataSize,
					firstBytePosition, fragmentOfData);
			fragmentArray[count] = fragmentOfData;
			firstBytePosition = lastBytePosition;
			lastBytePosition = lastBytePosition + MessageParameter.PAYLOAD_SIZE;
			if (lastBytePosition > dataSize) {
				lastBytePosition = dataSize;
			}
			count++;
		} while (firstBytePosition < dataSize);

		// Armazena os fragmentos em cache
		CacheMessage.getInstance().addMessage(new Date().getTime(), fragmentArray);

		myDevice.sendExplicitData(remoteDevice, MessageParameter.MESSAGE_END, contentType, qtdPackages, 0, noMessage);
	}

	@Override
	public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
		// Métodos de ExplicitXBeeMessage:
		// .getDevice(): retorna device de destino da mensagem.
		// .getSourceEndpoint() retorna o tipo de fragmento (INIT, DATA ou END).
		// .getDestinationEndpoint() retorna o tipo de conteúdo da mensagem.
		// .getClusterID() retorna o tamanho da mensagem original.
		// .getProfileID() retorna a posição inicial do fragmento no byte[] da
		// mensagem original.
		// .getData() retorna o byte[] com fragmento da mensagem original

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
			new MessageHandler(sourceDeviceAddress, explicitXBeeMessage.getDestinationEndpoint(), byteArrayMessage)
					.start();
			System.out.println("MESSAGE_END");
			break;

		default:
			break;
		}
	}

	public void addProcessMessageListener(IProcessMessage iProcessMessage) {
		this.iProcessMessage = iProcessMessage;
	}

	public IProcessMessage getIProcessMessage() {
		return iProcessMessage;
	}
}
