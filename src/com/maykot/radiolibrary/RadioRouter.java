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
	private IProcessMessage processMessage;
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
		int numPackage = 0;

		// Calcula a quantidade de fragmentos para enviar
		int qtdPackages = 0;
		if ((dataSize % MessageParameter.PAYLOAD_SIZE) == 0) {
			qtdPackages = (dataSize / MessageParameter.PAYLOAD_SIZE);
		} else {
			qtdPackages = (dataSize / MessageParameter.PAYLOAD_SIZE) + 1;
		}

		// Cria um array de byte[] e inseri os fragmentos
		byte[][] fragmentArray = new byte[qtdPackages][];

		byte[] messageSize = String.valueOf(dataSize).getBytes();

		/**
		* Argumentos para sendExplicitData():
		* 1º: device de destino da mensagem.
		* 2º: define o tipo de fragmento (INIT, DATA ou END)- SourceEndpoint.
		* 3º: define o tipo de conteúdo da mensagem - DestinationEndpoint.
		* 4º: define o número do fragmento enviado - ClusterID.
		* 5º: define o tamanho do fragmento enviado - ProfileID.
		* 6º: byte[] com fragmento da mensagem original.
		*/
		myDevice.sendExplicitData(remoteDevice, MessageParameter.MESSAGE_INIT, contentType, numPackage,
				messageSize.length, messageSize);

		do {
			byte[] fragmentOfData = Arrays.copyOfRange(dataToSend, firstBytePosition, lastBytePosition);
			fragmentArray[numPackage] = fragmentOfData;
			myDevice.sendExplicitData(remoteDevice, MessageParameter.MESSAGE_DATA, contentType, numPackage,
					fragmentOfData.length, fragmentOfData);
			firstBytePosition = lastBytePosition;
			lastBytePosition = lastBytePosition + MessageParameter.PAYLOAD_SIZE;
			if (lastBytePosition > dataSize) {
				lastBytePosition = dataSize;
			}
			numPackage++;
		} while (firstBytePosition < dataSize);

		// Armazena os fragmentos em cache
		CacheMessage.getInstance().addMessage(new Date().getTime(), fragmentArray);

		myDevice.sendExplicitData(remoteDevice, MessageParameter.MESSAGE_END, contentType, qtdPackages, 0, messageSize);
	}

	@Override
	public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
		/**
		* Métodos de ExplicitXBeeMessage:
		* .getDevice(): retorna device de destino da mensagem.
		* .getSourceEndpoint() retorna o tipo de fragmento (INIT, DATA ou END).
		* .getDestinationEndpoint() retorna o tipo de conteúdo da mensagem.
		* .getClusterID() retorna o número do fragmento enviado.
		* .getProfileID() retorna o tamanho do fragmento enviado.
		* .getData() retorna o byte[] com fragmento da mensagem original
		*/
		byte[] byteArrayMessage;
		RemoteXBeeDevice sourceDeviceAddress = explicitXBeeMessage.getDevice();

		int endPoint = explicitXBeeMessage.getSourceEndpoint();

		switch (endPoint) {

		case MessageParameter.MESSAGE_INIT:
			byteArrayMessage = new byte[Integer.parseInt(new String(explicitXBeeMessage.getData()))];
			messageHashmap.put(sourceDeviceAddress, byteArrayMessage);

			System.out.println("MESSAGE_INIT");
			break;

		case MessageParameter.MESSAGE_DATA:
			byteArrayMessage = messageHashmap.get(sourceDeviceAddress);
			System.arraycopy(explicitXBeeMessage.getData(), 0, byteArrayMessage,
					(explicitXBeeMessage.getClusterID() * MessageParameter.PAYLOAD_SIZE),
					explicitXBeeMessage.getData().length);
			messageHashmap.put(sourceDeviceAddress, byteArrayMessage);
			break;

		case MessageParameter.MESSAGE_END:
			byteArrayMessage = messageHashmap.get(sourceDeviceAddress);
			messageHashmap.remove(sourceDeviceAddress);
			new MessageHandler(sourceDeviceAddress, explicitXBeeMessage.getDestinationEndpoint(), byteArrayMessage)
					.start();
			System.out.println("MESSAGE_END");
			break;

		default:
			break;
		}
	}

	public void addProcessMessageListener(IProcessMessage processMessage) {
		this.processMessage = processMessage;
	}

	public IProcessMessage getProcessMessage() {
		return processMessage;
	}
}
