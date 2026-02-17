package Tier1.ApiGateway.model;

public class HttpResponse {
    int statusCode;
    String body;
    public HttpResponse(int code, String body) { this.statusCode = code; this.body = body; }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
