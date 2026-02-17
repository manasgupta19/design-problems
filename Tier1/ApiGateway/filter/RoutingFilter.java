package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpResponse;

import Tier1.ApiGateway.registry.RouteRegistry;

// B. Routing Filter (Pattern Matching delegated to Registry)
public class RoutingFilter implements GatewayFilter {
    private final RouteRegistry routeRegistry;

    public RoutingFilter(RouteRegistry registry) {
        this.routeRegistry = registry;
    }

    @Override
    public String getName() {
        return "Router";
    }

    @Override
    public void execute(GatewayContext ctx) {
        if (ctx.isTerminated())
            return;

        var route = routeRegistry.findRoute(ctx.getRequest().getPath());

        if (route.isPresent()) {
            ctx.setMatchedRoute(route.get());
            System.out.println("[Router] Matched " + ctx.getRequest().getPath() + " -> "
                    + ctx.getMatchedRoute().getBackendId());
        } else {
            ctx.setResponse(new HttpResponse(404, "Service Not Found"));
            ctx.terminate();
        }
    }
}
