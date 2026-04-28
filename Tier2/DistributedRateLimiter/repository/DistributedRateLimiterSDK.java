package repository;
import java.util.HashMap;
import java.util.Map;

import strategy.*;

// ---------------------------------------------------------
// 3. MAIN FACTORY / SDK
// ---------------------------------------------------------
public class DistributedRateLimiterSDK {
    private final Map<String, RateLimiterStrategy> strategies = new HashMap<>();

    public DistributedRateLimiterSDK(MockRedis redis) {
        strategies.put("TOKEN_BUCKET", new TokenBucketStrategy(redis));
        strategies.put("SLIDING_LOG", new SlidingLogStrategy(redis));
        strategies.put("SLIDING_COUNTER", new SlidingCounterStrategy(redis));
    }

    public boolean allowRequest(String algo, String key, int limit, int windowSec) {
        return strategies.get(algo).allow(key, limit, windowSec);
    }
}