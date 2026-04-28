package strategy;

import repository.MockRedis;

public class SlidingCounterStrategy implements RateLimiterStrategy {
    private final MockRedis redis;
    public SlidingCounterStrategy(MockRedis redis) { this.redis = redis; }

    @Override
    public boolean allow(String key, int limit, int windowSec) {
        return redis.luaSlidingWindowCounter(key, limit, windowSec * 1000, System.currentTimeMillis());
    }
}