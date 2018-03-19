package fr.aumjaud.antoine.services.common.http;

public enum HttpCode {
	OK(200),
	CREATED(201),
	NO_CONTENT(204),
	
	UNAUTHORIZE(401),
	FORBIDDEN(403),
	NOT_FOUND(404),
	
	SERVER_ERROR(500),
	
	UNDEFINED(-1);

	private final int code;

	private HttpCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public boolean isASucessCode() {
		return code >= 200 && code < 299;
	}

	public static HttpCode valueOf(int code) {
		for (HttpCode httpCode : HttpCode.values())
			if (httpCode.getCode() == code)
				return httpCode;
		return UNDEFINED;
	}

}