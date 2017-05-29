package fr.aumjaud.antoine.services.common.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.aumjaud.antoine.services.common.security.SecurityHelper;

public class HttpHelper {

	private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);

	/**
	 * POST message to an URL
	 * 
	 * @param targetUrl the target URL
	 * @param message the message to post
	 * @return the POST response
	 */
	public HttpResponse postData(String targetUrl, String message) {
		return postData(targetUrl, message, null);
	}
	/**
	 * POST message to an URL
	 * 
	 * @param targetUrl the target URL
	 * @param message the message to post
	 * @param secureKey the secure-key token (send in header)
	 * @return the POST response
	 */
	public HttpResponse postData(String targetUrl, String message, String secureKey) {

		byte[] postData = message.getBytes(StandardCharsets.UTF_8);
		try {
			URL url = new URL(targetUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
			if (secureKey != null) {
				conn.setRequestProperty(SecurityHelper.SECURE_KEY_NAME, secureKey);
			}
			conn.setRequestMethod("POST");
			try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
				dos.write(postData);
				return new HttpResponse(conn.getResponseCode(), conn.getResponseMessage());
			} catch (IOException e) {
				logger.error("Can't write message", e);
			}
		} catch (IOException e) {
			logger.error("Can't do a POST", e);
		}
		return null;
	}

	/**
	 * GET message from an URL
	 * 
	 * @param targetUrl the target URL
	 * @return the GET response
	 */
	public HttpResponse getData(String targetUrl) {
		return getData(targetUrl, null);
	}

	/**
	 * GET message from an URL
	 * 
	 * @param targetUrl the target URL
	 * @param secureKey the secure-key token (send in header)
	 * @return the GET response
	 */

	public HttpResponse getData(String targetUrl, String secureKey) {
		try {
			URL url = new URL(targetUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(false);
			conn.setUseCaches(false);
			conn.setRequestProperty("charset", "utf-8");
			if (secureKey != null) {
				conn.setRequestProperty(SecurityHelper.SECURE_KEY_NAME, secureKey);
			}
			conn.setRequestMethod("GET");
			StringBuilder result = new StringBuilder();
			try (BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				String line;
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
			}
			return new HttpResponse(conn.getResponseCode(), result.toString());
		} catch (IOException e) {
			logger.error("Can't do a GET", e);
		}
		return null;
	}

}
