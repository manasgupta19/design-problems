package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpResponse;

// A. SSL Termination (Simulated)
public class SSLTerminationFilter implements GatewayFilter {
    @Override
    public String getName() { return "SSL Terminator"; }

    @Override
    public void execute(GatewayContext ctx) {
        // In reality, this handles the TLS Handshake and decryption.
        // Simulation: Ensure request came over "HTTPS" (mocked via header check)
        String protocol = ctx.getRequest().getHeaders().getOrDefault("X-Forwarded-Proto", "https");
        if (!"https".equals(protocol)) {
            System.out.println("[SSL] Rejecting non-secure request");
            ctx.setResponse(new HttpResponse(403, "SSL Required"));
            ctx.terminate();
        } else {
            System.out.println("[SSL] Decryption successful. Offloading CPU cost.");
        }
    }
}
