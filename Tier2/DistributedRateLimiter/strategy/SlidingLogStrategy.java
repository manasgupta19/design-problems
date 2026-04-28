package strategy;

import repository.MockRedis;

public class SlidingLogStrategy implements RateLimiterStrategy {
    private final MockRedis redis;
    public SlidingLogStrategy(MockRedis redis) { this.redis = redis; }

    @Override
    public boolean allow(String key, int limit, int windowSec) {
        return redis.luaSlidingWindowLog(key, limit, windowSec * 1000, System.currentTimeMillis());
    }
}