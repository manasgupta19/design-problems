package Tier2.ApiGateway;

import Tier2.ApiGateway.model.HttpRequest;
import Tier2.ApiGateway.model.HttpResponse;
import Tier2.ApiGateway.service.APIGatewayEngine;
import java.util.*;

public class GatewayDriver {
    public static void main(String[] args) throws InterruptedException {
        // 0. Configuration & Wiring (Composition Root)
        Tier2.ApiGateway.registry.RouteRegistry registry = new Tier2.ApiGateway.registry.RouteRegistry();
        registry.addRoute("/api/orders", new Tier2.ApiGateway.model.RouteConfig("order-service", true, 5));
        registry.addRoute("/api/public", new Tier2.ApiGateway.model.RouteConfig("public-service", false, 100));

        List<Tier2.ApiGateway.filter.GatewayFilter> filters = new ArrayList<>();
        filters.add(new Tier2.ApiGateway.filter.SSLTerminationFilter());
        filters.add(new Tier2.ApiGateway.filter.RoutingFilter(registry));
        filters.add(new Tier2.ApiGateway.filter.AuthenticationFilter());
        filters.add(new Tier2.ApiGateway.filter.RateLimitFilter());

        APIGatewayEngine gateway = new APIGatewayEngine(filters);

        // Scenario 1: Valid Authenticated Request
        System.out.println("--- 1. Happy Path ---");
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-Proto", "https");
        headers.put("Authorization", "Bearer valid-user");

        HttpResponse res1 = gateway.handleRequest(new HttpRequest("/api/orders/123", headers, "192.168.1.1"));
        System.out.println("Result: " + res1.getStatusCode() + " " + res1.getBody());

        // Scenario 2: SSL Required
        System.out.println("\n--- 2. SSL Rejection ---");
        Map<String, String> insecureHeaders = new HashMap<>(); // Missing HTTPS header
        HttpResponse res2 = gateway.handleRequest(new HttpRequest("/api/orders", insecureHeaders, "192.168.1.1"));
        System.out.println("Result: " + res2.getStatusCode() + " " + res2.getBody());

        // Scenario 3: Rate Limiting (Burst)
        System.out.println("\n--- 3. Rate Limit Attack (Limit 5 RPS) ---");
        for (int i = 0; i < 7; i++) {
            HttpResponse r = gateway.handleRequest(new HttpRequest("/api/orders", headers, "192.168.1.50"));
            System.out.println("Req " + (i + 1) + ": " + r.getStatusCode());
        }
    }
}
