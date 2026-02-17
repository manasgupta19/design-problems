package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpResponse;
import Tier1.ApiGateway.model.RouteConfig;
import java.util.HashMap;
import java.util.Map;

// B. Routing Filter (Pattern Matching)
public class RoutingFilter implements GatewayFilter {
    // Mock Routing Table: Path Prefix -> Config
    private final Map<String, RouteConfig> routes = new HashMap<>();

    public RoutingFilter() {
        routes.put("/api/orders", new RouteConfig("order-service", true, 5)); // 5 RPS
        routes.put("/api/public", new RouteConfig("public-service", false, 100));
    }

    @Override
    public String getName() { return "Router"; }

    @Override
    public void execute(GatewayContext ctx) {
        if (ctx.isTerminated()) return;

        // Simple Prefix Match Strategy
        for (String prefix : routes.keySet()) {
            if (ctx.getRequest().getPath().startsWith(prefix)) {
                ctx.setMatchedRoute(routes.get(prefix));
                System.out.println("[Router] Matched " + ctx.getRequest().getPath() + " -> " + ctx.getMatchedRoute().getBackendId());
                return;
            }
        }

        ctx.setResponse(new HttpResponse(404, "Service Not Found"));
        ctx.terminate();
    }
}
