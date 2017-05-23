package fr.aumjaud.antoine.services.common.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpHelper {

	private static Logger logger = LoggerFactory.getLogger(HttpHelper.class);

	/**
	 * POST message to an URL
	 * @param targetUrl: the target URL
	 * @param message: the message to post
	 * @return the post response
	 */
	public PostResponse postData(String targetUrl, String message) {
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
			conn.setRequestMethod("POST");
			try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
				dos.write(postData);
				return new PostResponse(conn.getResponseCode(), conn.getResponseMessage());
			} catch (IOException e) {
				logger.error("Can't write message", e);
			}
		} catch (IOException e) {
			logger.error("Can't POST message", e);
		}
		return null;
	}



}
