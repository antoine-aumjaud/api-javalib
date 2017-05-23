package fr.aumjaud.antoine.services.common.security;

public class SecurityHelper {

	public void checkAccess(String configSecureToken, String requestSecureKey) {
		if (requestSecureKey == null) {
			throw new NoAccessException("secure key not found", "Try to access with no secure key");
		}
		if (!configSecureToken.equals(requestSecureKey)) {
			throw new NoAccessException("secure key not valid", "Try to access to API with invalid key: " + requestSecureKey);
		}
	}
}
