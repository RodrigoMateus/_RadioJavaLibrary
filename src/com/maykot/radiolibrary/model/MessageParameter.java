package com.maykot.radiolibrary.model;

public final class MessageParameter {

	/**
	 * Payload ou MTU (Maximum Transmission Unit) é a quantidade de bytes que cabe em uma mensagem.
	 * Para XTend usar PAYLOAD_SIZE = 250 e 
	 * para XBee usar PAYLOAD_SIZE = 100.
	 */
	public static final int PAYLOAD_SIZE = 100;

	// Parâmetros usados em ExplicitXBeeMessage
	public static final int CLUSTER_ID					 = 0;  // Representa o nº do fragmento enviado (numPackage) - 1º pacote = nº0
	public static final int PROFILE_ID					 = 0;  // Representa o tamanho do fragmento enviado (fragmentOfData.length)
	public static final int MESSAGE_INIT				 = 11; // 0x0B SourceEndpoint 
	public static final int MESSAGE_DATA				 = 12; // 0x0C SourceEndpoint
	public static final int MESSAGE_END					 = 13; // 0x0D SourceEndpoint
	public static final int SEND_CLIENT_CONNECTION		 = 21; // 0x15 DestinationEndpoint e contentType
	public static final int CONFIRM_CLIENT_CONNECTION	 = 22; // 0x16 DestinationEndpoint e contentType
	public static final int SEND_TXT_FILE				 = 23; // 0x17 DestinationEndpoint e contentType
	public static final int CONFIRM_TXT_FILE			 = 24; // 0x18 DestinationEndpoint e contentType
	public static final int SEND_LOCAL_POST				 = 25; // 0x19 DestinationEndpoint e contentType
	public static final int CONFIRM_LOCAL_POST			 = 26; // 0x1A DestinationEndpoint e contentType
	public static final int SEND_MOBILE_POST			 = 27; // 0x1B DestinationEndpoint e contentType
	public static final int CONFIRM_MOBILE_POST			 = 28; // 0x1C DestinationEndpoint e contentType
}
