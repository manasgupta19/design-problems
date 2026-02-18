package Tier0.RateLimiter.strategy;

// ---------------------------------------------------------
// 2. TOKEN BUCKET IMPLEMENTATION (Bursty)
// ---------------------------------------------------------
public class TokenBucket implements BucketStrategy {
    private final long capacity;
    private final double refillRatePerNs; // Tokens per nanosecond

    private double currentTokens;
    private long lastRefillTimestamp;

    public TokenBucket(long capacity, long tokensPerSecond) {
        this.capacity = capacity;
        this.refillRatePerNs = tokensPerSecond / 1_000_000_000.0;
        this.currentTokens = capacity; // Start full
        this.lastRefillTimestamp = System.nanoTime();
    }

    @Override
    public synchronized boolean tryConsume(long tokens) {
        refill();

        if (currentTokens >= tokens) {
            currentTokens -= tokens;
            return true; // Request Allowed
        }
        return false; // Request Throttled
    }

    // Lazy Refill: Only calculate when a request comes in [Source 1120]
    private void refill() {
        long now = System.nanoTime();
        long durationNs = now - lastRefillTimestamp;

        if (durationNs > 0) {
            double tokensToAdd = durationNs * refillRatePerNs;
            currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}
