package strategy;

import repository.MockRedis;

public class TokenBucketStrategy implements RateLimiterStrategy {
    private final MockRedis redis;
    public TokenBucketStrategy(MockRedis redis) { this.redis = redis; }

    @Override
    public boolean allow(String key, int limit, int windowSec) {
        // limit = burst capacity, windowSec used to derive rate
        // Assuming rate = limit / windowSec for simplicity, or passed explicitly
        int rate = Math.max(1, limit / windowSec);
        return redis.luaTokenBucket(key, limit, rate, System.currentTimeMillis());
    }
}
