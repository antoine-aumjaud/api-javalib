package fr.aumjaud.antoine.services.common.http;

import fr.aumjaud.antoine.services.common.security.SecurityHelper;

public class HttpMessageBuilder {
    HttpMessage httpMessage = new HttpMessage();

    public HttpMessageBuilder(String url) {
        httpMessage.setUrl(url);
    }
    public HttpMessageBuilder setJsonMessage(String message) {
        httpMessage.setMessage(message);
        httpMessage.setIsJson(true);
        return this;
    }
    public HttpMessageBuilder setMessage(String message) {
        httpMessage.setMessage(message);
        httpMessage.setIsJson(false);
        return this;
    }
    public HttpMessageBuilder addHeader(String name, String value) {
        httpMessage.addHeader(name, value);
        return this;
    }
    public HttpMessageBuilder setSecureKey(String secureKey) {
        httpMessage.addHeader(SecurityHelper.SECURE_KEY_NAME, secureKey);
        return this;
    }
    public HttpMessage build() {
        return httpMessage;
    }
}