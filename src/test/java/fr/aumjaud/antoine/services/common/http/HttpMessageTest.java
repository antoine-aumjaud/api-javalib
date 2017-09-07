package fr.aumjaud.antoine.services.common.http;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HttpMessageTest {

	@Test
	public void getCurlCommand_should_return_a_valid_command_when_message_is_json() {
		// Given
		HttpMessage message = new HttpMessageBuilder("myUrl")
			.setJsonMessage("{}")
			.addHeader("anotherHeader", "anotherHeaderValue")
			.setSecureKey("secureKey").build();

		// When
		String command = message.getCurlCommand();

		// Then
		assertEquals("curl -H 'Content-Type: application/json; charset=utf-8' -H 'secure-key: secureKey' -H 'anotherHeader: anotherHeaderValue' -d '{}' 'myUrl'", command);
	}

	@Test
	public void getCurlCommand_should_return_a_valid_command_when_message_is_form() {
		// Given
		HttpMessage message = new HttpMessageBuilder("myUrl")
			.setMessage("data1=value1;")
			.addHeader("anotherHeader", "anotherHeaderValue")
			.setSecureKey("secureKey").build();

		// When
		String command = message.getCurlCommand();

		// Then
		assertEquals("curl -H 'Content-Type: application/x-www-form-urlencoded; charset=utf-8' -H 'secure-key: secureKey' -H 'anotherHeader: anotherHeaderValue' -d 'data1=value1;' 'myUrl'", command);
	}
}
