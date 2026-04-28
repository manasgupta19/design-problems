package Tier2.ApiGateway.filter;

import Tier2.ApiGateway.model.GatewayContext;

public interface GatewayFilter {
    String getName();
    void execute(GatewayContext ctx);
}
