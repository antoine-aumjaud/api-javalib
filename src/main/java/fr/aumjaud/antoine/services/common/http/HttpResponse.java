package fr.aumjaud.antoine.services.common.http;

public class HttpResponse {
	private final HttpCode httpCode;
	private final String content;

	HttpResponse(int httpCode, String content) {
		this.httpCode = HttpCode.valueOf(httpCode);
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public HttpCode getHttpCode() {
		return httpCode;
	}

	@Override
	public String toString() {
		return String.format("[code: %d, content: %s]", httpCode.getCode(), content);
	}

}