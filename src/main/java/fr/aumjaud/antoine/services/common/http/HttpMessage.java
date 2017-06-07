package fr.aumjaud.antoine.services.common.http;

import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    private String url, message;
    private boolean isJson;
    private Map<String, String> headers = new HashMap<>();
    
    public void setUrl(String url) {
        this.url = url;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public void setIsJson(boolean isJson) {
        this.isJson = isJson;
    }
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getUrl() {
        return url;
    }
    public String getMessage() {
        return message;
    }
    public boolean isJson() {
        return isJson;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
}
