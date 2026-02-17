package Tier1.ApiGateway.service;

import Tier1.ApiGateway.filter.AuthenticationFilter;
import Tier1.ApiGateway.filter.GatewayFilter;
import Tier1.ApiGateway.filter.RateLimitFilter;
import Tier1.ApiGateway.filter.RoutingFilter;
import Tier1.ApiGateway.filter.SSLTerminationFilter;
import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpRequest;
import Tier1.ApiGateway.model.HttpResponse;
import java.util.ArrayList;
import java.util.List;

// ---------------------------------------------------------
// 4. GATEWAY DRIVER (The Engine)
// ---------------------------------------------------------
public class APIGatewayEngine {
    private final List<GatewayFilter> filters;

    public APIGatewayEngine() {
        filters = new ArrayList<>();
        filters.add(new SSLTerminationFilter());
        filters.add(new RoutingFilter());
        filters.add(new AuthenticationFilter());
        filters.add(new RateLimitFilter());
    }

    public HttpResponse handleRequest(HttpRequest req) {
        GatewayContext ctx = new GatewayContext(req);

        for (GatewayFilter filter : filters) {
            filter.execute(ctx);
            if (ctx.isTerminated()) break;
        }

        if (ctx.getResponse() == null) {
            // Mock Proxying to Backend
            return new HttpResponse(200, "Proxy Success -> " + ctx.getMatchedRoute().getBackendId());
        }
        return ctx.getResponse();
    }
}