package fr.aumjaud.antoine.services.common.http;

public class PostResponse {
	private int httpCode;
	private String content;

	PostResponse(int httpCode, String content) {
		this.httpCode = httpCode;
		this.content = content;
	}
	
	@Override
	public String toString() {
		return String.format("[code: %d, content: %s]", httpCode, content);
	}
}