package Tier1.ApiGateway.filter;

import Tier1.ApiGateway.model.GatewayContext;
import Tier1.ApiGateway.model.HttpResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// D. Rate Limiting Filter (Token Bucket)
public class RateLimitFilter implements GatewayFilter {
    // In Prod: This Map would be Redis. Here: ConcurrentHashMap.
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    @Override
    public String getName() { return "Throttler"; }

    @Override
    public void execute(GatewayContext ctx) {
        if (ctx.isTerminated()) return;

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
    static class TokenBucket {
        private final long capacity;
        private final long refillRate; // tokens per second
        @SuppressWarnings("FieldMayBeFinal")
        private AtomicLong tokens;
        @SuppressWarnings("FieldMayBeFinal")
        private AtomicLong lastRefillTimestamp;

        public TokenBucket(long capacity, long refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.tokens = new AtomicLong(capacity);
            this.lastRefillTimestamp = new AtomicLong(System.nanoTime());
        }

        public boolean tryConsume(long cost) {
            long now = System.nanoTime();
            long last = lastRefillTimestamp.get();

            // 1. Lazy Refill Calculation
            long elapsedNanos = now - last;
            long tokensToAdd = (elapsedNanos / 1_000_000_000) * refillRate;

            if (tokensToAdd > 0) {
                // Update timestamp only if we added tokens
                if (lastRefillTimestamp.compareAndSet(last, now)) {
                    long currentTokens = tokens.get();
                    long newLevel = Math.min(capacity, currentTokens + tokensToAdd);
                    tokens.set(newLevel);
                }
            }

            // 2. Consume
            while (true) {
                long current = tokens.get();
                if (current < cost) return false; // Rejected
                if (tokens.compareAndSet(current, current - cost)) return true; // Accepted
            }
        }
    }
}
