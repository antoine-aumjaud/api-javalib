package fr.aumjaud.antoine.services.common.http;

import java.util.HashMap;
import java.util.Map;

public class HttpMessage {
    private String url, message;
    private boolean isJson;
    private Map<String, String> headers = new HashMap<>();
    
    //Limit access to constructor of this class
    HttpMessage() {}

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

    public String getCurlCommand() {
        StringBuilder command = new StringBuilder();
        command.append("curl");
        if(isJson) {
            command.append(" -H 'Content-Type: application/json; charset=utf-8'");
        } else {
            command.append(" -H 'Content-Type: application/x-www-form-urlencoded; charset=utf-8'");
        }
        for(Map.Entry<String, String> header : headers.entrySet()) {
            command.append(" -H '").append(header.getKey()).append(": ").append(header.getValue()).append("'");    
        }
        command.append(" -d '").append(message).append("' '").append(url).append("'");
        return command.toString();
    }
}
