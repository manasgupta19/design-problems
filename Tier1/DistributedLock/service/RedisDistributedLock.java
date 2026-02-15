package Tier1.DistributedLock.service;

import java.util.UUID;

import Tier1.DistributedLock.repository.MockRedisClient;

public class RedisDistributedLock implements DistributedLock {
    private final MockRedisClient redis;

    // LUA SCRIPT: "if redis.call('get', KEYS) == ARGV then return redis.call('del', KEYS) else return 0 end"
    private static final String UNLOCK_SCRIPT = "if get(key) == val then del(key)";

    public RedisDistributedLock(MockRedisClient redis) {
        this.redis = redis;
    }

    @Override
    public String tryLock(String resourceKey, long ttlMs) {
        String uniqueToken = UUID.randomUUID().toString(); // Token ensures ownership

        // SET resource token NX PX ttl
        String result = redis.set(resourceKey, uniqueToken, "NX", "PX", ttlMs);

        if ("OK".equals(result)) {
            return uniqueToken;
        }
        return null;
    }

    @Override
    public boolean unlock(String resourceKey, String token) {
        // Must use Lua to ensure we don't delete SOMEONE ELSE'S lock
        // if ours expired just before this call.
        Long result = redis.eval(UNLOCK_SCRIPT, resourceKey, token);
        return result == 1L;
    }
}

