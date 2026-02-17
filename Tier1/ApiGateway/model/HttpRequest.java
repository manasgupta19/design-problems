package Tier1.ApiGateway.model;

import java.util.Map;

public class HttpRequest {
    String path;
    Map<String, String> headers;
    String clientIp;

    public HttpRequest(String path, Map<String, String> headers, String ip) {
        this.path = path; this.headers = headers; this.clientIp = ip;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getClientIp() {
        return clientIp;
    }
}
