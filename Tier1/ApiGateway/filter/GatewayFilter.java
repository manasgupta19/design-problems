package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;

public interface GatewayFilter {
    String getName();
    void execute(GatewayContext ctx);
}
