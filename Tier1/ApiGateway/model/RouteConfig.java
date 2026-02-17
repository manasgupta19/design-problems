package Tier1.ApiGateway.model;

public class RouteConfig {
    String backendId;
    boolean authRequired;
    long rateLimit; // Requests per second

    public RouteConfig(String id, boolean auth, long limit) {
        this.backendId = id; this.authRequired = auth; this.rateLimit = limit;
    }

    public boolean isAuthRequired() {
        return authRequired;
    }

    public String getBackendId() {
        return backendId;
    }

    public long getRateLimit() {
        return rateLimit;
    }
}
