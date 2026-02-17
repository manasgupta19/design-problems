package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpResponse;

// C. Authentication Filter (JWT)
public class AuthenticationFilter implements GatewayFilter {
    @Override
    public String getName() { return "Auth"; }

    @Override
    public void execute(GatewayContext ctx) {
        if (ctx.isTerminated()) return;
        if (!ctx.getMatchedRoute().isAuthRequired()) return; // Public route

        String token = ctx.getRequest().getHeaders().get("Authorization");
        // Mock JWT Validation
        if (token == null || !token.startsWith("Bearer valid-")) {
            System.out.println("[Auth] Invalid Token");
            ctx.setResponse(new HttpResponse(401, "Unauthorized"));
            ctx.terminate();
        } else {
            System.out.println("[Auth] Identity Verified: " + token.substring(7));
        }
    }
}



