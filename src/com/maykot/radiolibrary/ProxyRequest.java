package com.maykot.radiolibrary;

import java.io.Serializable;
import java.util.HashMap;

public class ProxyRequest implements Serializable {

	private static final long serialVersionUID = -4707248583815599159L;
	private String verb;
	private String url;
	private HashMap<String, String> header;
	private String idMessage;
	private byte[] body;

	public String getVerb() { return verb; }

	public void setVerb(String verb) { this.verb = verb; }

	public String getUrl() { return url; }

	public void setUrl(String url) { this.url = url; }

	public HashMap<String, String> getHeader() { return header; }

	public void setHeader(HashMap<String, String> header) { this.header = header; }
	
	public String getIdMessage() { return idMessage; }

	public void setIdMessage(String idMessage) { this.idMessage = idMessage; }

	public byte[] getBody() { return body; }

	public void setBody(byte[] body) { this.body = body; }
}
