package com.maykot.radiolibrary;

import java.util.Arrays;
import java.util.Date;

import com.digi.xbee.api.DigiMeshDevice;
import com.digi.xbee.api.RemoteXBeeDevice;
import com.digi.xbee.api.exceptions.TimeoutException;
import com.digi.xbee.api.exceptions.XBeeException;
import com.digi.xbee.api.listeners.IExplicitDataReceiveListener;
import com.digi.xbee.api.models.ExplicitXBeeMessage;

public class Router implements IExplicitDataReceiveListener {

	private static Router uniqueInstance;
	private TreatRequest treatRequest;

	private Router() {
		treatRequest = new TreatRequest();
	}

	public static Router getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new Router();
		}
		return uniqueInstance;
	}

	public void sendMessage(DigiMeshDevice myDevice, RemoteXBeeDevice remoteDevice, int contentType,
			byte[] dataToSend) {

		int dataSize = dataToSend.length;
		int firstBytePosition = 0;
		int lastBytePosition = MessageParameter.PAYLOAD_SIZE;
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
		try {
			myDevice.sendExplicitData(remoteDevice, MessageParameter.ENDPOINT_SEND_INIT, contentType, dataSize,
					firstBytePosition, noMessage);

			do {
				byte[] fragmentOfData = Arrays.copyOfRange(dataToSend, firstBytePosition, lastBytePosition);

				myDevice.sendExplicitData(remoteDevice, MessageParameter.ENDPOINT_SEND_DATA, contentType, dataSize,
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

			myDevice.sendExplicitData(remoteDevice, MessageParameter.ENDPOINT_SEND_END, contentType, qtdPackages, 0,
					noMessage);

		} catch (TimeoutException e) {
			System.out.println(
					"Erro " + ErrorMessage.TIMEOUT_ERROR.value() + ": " + ErrorMessage.TIMEOUT_ERROR.description());
			e.printStackTrace();
		} catch (XBeeException e) {
			System.out.println("Erro " + ErrorMessage.XBEE_EXCEPTION_ERROR.value() + ": "
					+ ErrorMessage.XBEE_EXCEPTION_ERROR.description());
			e.printStackTrace();
		}
	}

	@Override
	public void explicitDataReceived(ExplicitXBeeMessage explicitXBeeMessage) {
		treatRequest.process(explicitXBeeMessage);
	}
}
