package fr.aumjaud.antoine.services.common.http;

public enum HttpCode {
	OK(200),
	SERVER_ERROR(500);

	private final int code;

	private HttpCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public static HttpCode valueOf(int code) {
		for (HttpCode httpCode : HttpCode.values())
			if (httpCode.getCode() == code)
				return httpCode;
		return null;
	}

}