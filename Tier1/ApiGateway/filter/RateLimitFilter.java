package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// D. Rate Limiting Filter (Token Bucket)
public class RateLimitFilter implements GatewayFilter {
    // In Prod: This Map would be Redis. Here: ConcurrentHashMap.
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    @Override
    public String getName() {
        return "Throttler";
    }

    @Override
    public void execute(GatewayContext ctx) {
        if (ctx.isTerminated())
            return;

        // Rate limit by Client IP + Service ID
        String key = ctx.getRequest().getClientIp() + ":" + ctx.getMatchedRoute().getBackendId();
        long limit = ctx.getMatchedRoute().getRateLimit();

        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(limit, limit));

        if (!bucket.tryConsume(1)) {
            System.out.println("[RateLimit] Throttling " + key);
            ctx.setResponse(new HttpResponse(429, "Too Many Requests"));
            ctx.terminate();
        }
    }

    // Thread-Safe Token Bucket Implementation
    // Switched to synchronized to ensure atomicity of refill + consume.
    static class TokenBucket {
        private final long capacity;
        private final long refillRate; // tokens per second
        private double tokens;
        private long lastRefillTimestamp;

        public TokenBucket(long capacity, long refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = capacity;
            this.lastRefillTimestamp = System.nanoTime();
        }

        public synchronized boolean tryConsume(long cost) {
            refill();
            if (tokens >= cost) {
                tokens -= cost;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            long elapsedNanos = now - lastRefillTimestamp;
            if (elapsedNanos > 0) {
                double tokensToAdd = (elapsedNanos / 1_000_000_000.0) * refillRate;
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTimestamp = now;
            }
        }
    }
}
