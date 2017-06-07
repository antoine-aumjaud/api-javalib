package fr.aumjaud.antoine.services.common.http;

public class HttpResponse {
	private final HttpCode httpCode;
	private final String codeDescription;
	private final String content;

	HttpResponse(int httpCode, String codeDescription, String content) {
		this.httpCode = HttpCode.valueOf(httpCode);
		this.codeDescription = codeDescription;
		this.content = content;
	}

	public HttpCode getHttpCode() {
		return httpCode;
	}

	public String getCodeDescription() {
		return codeDescription;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return String.format("[code: (%d: %s), content: %s]", httpCode.getCode(), codeDescription, content);
	}

}