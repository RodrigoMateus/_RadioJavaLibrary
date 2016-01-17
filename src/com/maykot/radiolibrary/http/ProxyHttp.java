package com.maykot.radiolibrary.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.maykot.radiolibrary.model.ProxyRequest;
import com.maykot.radiolibrary.model.ProxyResponse;
import com.maykot.radiolibrary.utils.LogRecord;

public class ProxyHttp {

	public static ProxyResponse postFile(ProxyRequest proxyRequest) {

		ProxyResponse proxyResponse = null;

		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		String response = null;
		// System.out.println(proxyRequest.toString());

		try {
			HttpPost request = new HttpPost(proxyRequest.getUrl());

			setHeader(proxyRequest, request);

			InputStream inputStream = new ByteArrayInputStream(proxyRequest.getBody());
			InputStreamEntity inputStreamEntity = new InputStreamEntity(inputStream, -1);
			request.setEntity(inputStreamEntity);

			httpResponse = httpClient.execute(request);

			// Faz alguma coisa com a resposta
			response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

			proxyResponse = new ProxyResponse(httpResponse.getStatusLine().getStatusCode(), response.getBytes());
			proxyResponse.setIdMessage(proxyRequest.getIdMessage());
			getHeader(proxyResponse, httpResponse);

			System.out.println(proxyResponse.toString());

			LogRecord.insertLog("MobileRequest_ServerLog",
					new String(proxyRequest.getMqttClientId() + ";" + proxyRequest.getIdMessage() + ";"
							+ new String(new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss:SSS").format(new Date())) + ";"
							+ new String(proxyRequest.getBody())));
		} catch (IOException ex) {
			System.out.println("ERRO Proxy");
		}
		return proxyResponse;
	}

	private static void setHeader(ProxyRequest proxyRequest, HttpRequestBase request) {
		try {
			for (String key : proxyRequest.getHeader().keySet())
				request.addHeader(key, proxyRequest.getHeader().get(key));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	private static void getHeader(ProxyResponse proxyResponse, HttpResponse httpResponse) {
		HashMap<String, String> headerResponse = new HashMap<>();
		for (Header header : httpResponse.getAllHeaders())
			headerResponse.put(header.getName(), header.getValue());

		proxyResponse.setHeader(headerResponse);
	}

	public static ProxyResponse getFile(ProxyRequest proxyRequest) {

		ProxyResponse proxyResponse = null;

		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpResponse = null;
		String response = null;

		try {
			HttpGet request = new HttpGet(proxyRequest.getUrl());

			setHeader(proxyRequest, request);

			httpResponse = httpClient.execute(request);

			// Faz alguma coisa com a resposta
			response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

			proxyResponse = new ProxyResponse(httpResponse.getStatusLine().getStatusCode(), response.getBytes());
			proxyResponse.setIdMessage(proxyRequest.getIdMessage());
			getHeader(proxyResponse, httpResponse);

			System.out.println(proxyResponse.toString());

		} catch (IOException ex) {
			proxyResponse = new ProxyResponse(600, "fail request".getBytes());
			proxyResponse.setIdMessage(proxyRequest.getIdMessage());
			System.out.println("ERRO Proxy");
		}
		return proxyResponse;
	}
}
