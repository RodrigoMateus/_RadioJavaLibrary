package com.maykot.radiolibrary;

public final class MessageParameter {

	// Payload é a quantidade de bytes que cabe em uma mensagem
	// ou MTU (Maximum Transmission Unit)
	public static final int PAYLOAD_SIZE = 250;

	// Parâmetros usados em ExplicitXBeeMessage
	public static final int CLUSTER_ID = 0;
	public static final int PROFILE_ID = 0;
	public static final int ENDPOINT_SEND_INIT = 11;
	public static final int ENDPOINT_SEND_DATA = 12;
	public static final int ENDPOINT_SEND_END = 13;
	public static final int ENDPOINT_RESPONSE_INIT = 21;
	public static final int ENDPOINT_RESPONSE_DATA = 22;
	public static final int ENDPOINT_RESPONSE_END = 33;
	public static final int ENDPOINT_TXT = 41;

}
