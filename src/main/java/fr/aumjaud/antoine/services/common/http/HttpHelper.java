package fr.aumjaud.antoine.services.common.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHelper {

	private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);

	/**
	 * POST message to an URL
	 * 
	 * @param url the target URL
	 * @param message the message to send
	 * @return the GET response
	 */
	public HttpResponse postData(String url, String message) {
		return postData(new HttpMessageBuilder(url)
			.setMessage(message)
			.build());
	}
	
	/**
	 * POST message to an URL
	 * 
	 * @param url the target URL
	 * @param message the message to send
	 * @param secureKey the secretKey to call this service 
	 * @return the GET response
	 */
	public HttpResponse postData(String url, String secureKey, String message) {
		return postData(new HttpMessageBuilder(url)
			.setSecureKey(secureKey)
			.setMessage(message)
			.build());
	}
	
	/**
	 * POST message to an URL
	 * 
	 * @param httpMessage the message definition
	 * @return the POST response
	 */
	public HttpResponse postData(HttpMessage httpMessage) {
		logger.debug("Send POST data to {}", httpMessage.getUrl());

		byte[] postData = httpMessage.getMessage().getBytes(StandardCharsets.UTF_8);
		try {
			URL url = new URL(httpMessage.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", httpMessage.isJson() 
				? "application/json; charset=utf-8" 
				: "application/x-www-form-urlencoded; charset=utf-8");
			for(Map.Entry<String, String> header : httpMessage.getHeaders().entrySet()) {
				conn.setRequestProperty(header.getKey(), header.getValue());
			}
			conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
			conn.setRequestMethod("POST");
			//Write POST data
			try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
				dos.write(postData);
			} 
			//Read reponse
			String resultContent = getResultContent(conn);
			return new HttpResponse(conn.getResponseCode(), conn.getResponseMessage(), resultContent);
		} catch (IOException e) {
			logger.error("Can't do a POST", e);
			return new HttpResponse(HttpCode.SERVER_ERROR.getCode(), "SERVER ERROR", "Can't call server");
		}
	}

	/**
	 * GET message from an URL
	 * 
	 * @param url the target URL
	 * @return the GET response
	 */
	public HttpResponse getData(String url) {
		return getData(new HttpMessageBuilder(url)
			.build());
	}
	
	/**
	 * GET message from an URL
	 * 
	 * @param url the target URL
	 * @param secureKey the secretKey to call this service 
	 * @return the GET response
	 */
	public HttpResponse getData(String url, String secureKey) {
		return getData(new HttpMessageBuilder(url)
			.setSecureKey(secureKey)
			.build());
	}

	/**
	 * GET message from an URL
	 * 
	 * @param httpMessage the message definition
	 * @return the GET response
	 */
	public HttpResponse getData(HttpMessage httpMessage) {
		logger.debug("Send GET data to {}", httpMessage.getUrl());
		try {
			URL url = new URL(httpMessage.getUrl());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setUseCaches(false);
			for(Map.Entry<String, String> header : httpMessage.getHeaders().entrySet()) {
				conn.setRequestProperty(header.getKey(), header.getValue());
			}
			conn.setRequestMethod("GET");
			//Read reponse
			String resultContent = getResultContent(conn);
			return new HttpResponse(conn.getResponseCode(), conn.getResponseMessage(), resultContent);
		} catch (IOException e) {
			logger.error("Can't do a GET", e);
			return new HttpResponse(HttpCode.SERVER_ERROR.getCode(), "SERVER ERROR", "Can't call server");
		}
	}


	/*
	 * PRIVATE
	 */

	/**
	 * Return the response of an HttpURLConnection in a String
	 * 
	 * @param conn the connection
	 * @return the response message in a String
	 */
	private String getResultContent(HttpURLConnection conn) throws IOException {
		StringBuilder result = new StringBuilder();
		try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		}
		return result.toString();
	}
}
