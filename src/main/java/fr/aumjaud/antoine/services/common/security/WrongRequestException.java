package fr.aumjaud.antoine.services.common.security;

public class WrongRequestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String clientMessage;

	public WrongRequestException(String clientMessage, String logMessage) {
		super(logMessage);
		this.clientMessage = clientMessage;
	}

	public String getClientMessage() {
		return clientMessage;
	}

}