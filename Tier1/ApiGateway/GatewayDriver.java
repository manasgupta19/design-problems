package Tier1.ApiGateway;

import Tier1.ApiGateway.model.HttpRequest;
import Tier1.ApiGateway.model.HttpResponse;
import Tier1.ApiGateway.service.APIGatewayEngine;
import java.util.*;


public class GatewayDriver {
    public static void main(String[] args) throws InterruptedException {
        APIGatewayEngine gateway = new APIGatewayEngine();

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
            System.out.println("Req " + (i+1) + ": " + r.getStatusCode());
        }
    }
}
