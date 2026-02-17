package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpResponse;

// C. Authentication Filter (JWT)
public class AuthenticationFilter implements GatewayFilter {
    @Override
    public String getName() {
        return "Auth";
    }

    @Override
    public void execute(GatewayContext ctx) {
        if (ctx.isTerminated())
            return;
        if (!ctx.getMatchedRoute().isAuthRequired())
            return; // Public route

        String token = ctx.getRequest().getHeaders().get("Authorization");
        // Mock JWT Validation
        if (token == null || !token.startsWith("Bearer ") || token.length() < 7) {
            System.out.println("[Auth] Invalid Token Format");
            ctx.setResponse(new HttpResponse(401, "Unauthorized"));
            ctx.terminate();
        } else if (!token.substring(7).startsWith("valid-")) {
            System.out.println("[Auth] Invalid Token Signature");
            ctx.setResponse(new HttpResponse(403, "Forbidden"));
            ctx.terminate();
        } else {
            System.out.println("[Auth] Identity Verified: " + token.substring(7));
        }
    }
}
