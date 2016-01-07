package com.maykot.radiolibrary;

import java.util.Arrays;
import java.util.Date;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.models.ExplicitXBeeMessage;

public class RouterRadio implements IExplicitDataReceiveListener {

	private static RouterRadio uniqueInstance;
	private TreatDataReceived treatDataReceived;
	private IProcessMessage iProcessMessage;

	private RouterRadio() {
		treatDataReceived = new TreatDataReceived();
	}

	public static RouterRadio getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new RouterRadio();
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

	public void processMyMessage(IProcessMessage iProcessMessage) {
		this.iProcessMessage = iProcessMessage;
	}

	public IProcessMessage getIProcessMessage() {
		return iProcessMessage;
	}

	@Override
	public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
		treatDataReceived.processDataReceived(explicitXBeeMessage);
	}
}
