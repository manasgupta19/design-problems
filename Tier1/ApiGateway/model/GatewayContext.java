package Tier1.ApiGateway.model;

// Context passed through the filter chain
public class GatewayContext {
    HttpRequest request;
    HttpResponse response;
    boolean terminated = false;
    RouteConfig matchedRoute; // Result of routing phase

    public GatewayContext(HttpRequest req) { this.request = req; }

    public HttpRequest getRequest() {
        return request;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void terminate() {
        this.terminated = true;
    }

    public RouteConfig getMatchedRoute() {
        return matchedRoute;
    }

    public void setMatchedRoute(RouteConfig matchedRoute) {
        this.matchedRoute = matchedRoute;
    }

}
