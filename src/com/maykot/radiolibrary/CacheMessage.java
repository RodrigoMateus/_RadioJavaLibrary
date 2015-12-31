package com.maykot.radiolibrary;

import java.util.HashMap;

public class CacheMessage {

	private static CacheMessage uniqueInstance;

	HashMap<Long, byte[][]> messageHashMap = new HashMap<>();

	private CacheMessage() {
	}

	public static CacheMessage getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new CacheMessage();
		}
		return uniqueInstance;
	}

	public void addMessage(Long idMessage, byte[][] messageFragments) {
		messageHashMap.put(idMessage, messageFragments);
	}

}
