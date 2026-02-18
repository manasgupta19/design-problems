package Tier0.RateLimiter.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ---------------------------------------------------------
// 4. MANAGER / FACTORY (The Public API)
// ---------------------------------------------------------
public class RateLimiterManager {
    // Thread-safe map for O(1) lookup [Source 1364]
    private final Map<String, BucketStrategy> buckets = new ConcurrentHashMap<>();

    // Configuration
    private final boolean useTokenBucket;
    private final long capacity;
    private final long rate;

    public RateLimiterManager(boolean useTokenBucket, long capacity, long rate) {
        this.useTokenBucket = useTokenBucket;
        this.capacity = capacity;
        this.rate = rate;
    }

    public boolean allowRequest(String clientId) {
        // computeIfAbsent is atomic; ensures we don't create duplicate buckets
        BucketStrategy bucket = buckets.computeIfAbsent(clientId, k -> createBucket());
        return bucket.tryConsume(1);
    }

    private BucketStrategy createBucket() {
        if (useTokenBucket) {
            return new TokenBucket(capacity, rate);
        } else {
            return new LeakyBucket(capacity, rate);
        }
    }
}

