import java.util.*;
import java.util.concurrent.*;

// ---------------------------------------------------------
// 1. MOCK REDIS (Simulating Atomic Lua Scripts)
// ---------------------------------------------------------
class MockRedis {
    // Stores for different data structures
    private final Map<String, Map<String, String>> hashes = new ConcurrentHashMap<>();
    private final Map<String, TreeMap<Long, List<String>>> zsets = new ConcurrentHashMap<>();
    private final Map<String, Integer> counters = new ConcurrentHashMap<>();

    // --- LUA SCRIPT: TOKEN BUCKET ---
    public synchronized boolean luaTokenBucket(String key, int capacity, int refillRatePerSec, long now) {
        Map<String, String> bucket = hashes.computeIfAbsent(key, k -> new HashMap<>());

        long lastRefill = Long.parseLong(bucket.getOrDefault("last_refill", String.valueOf(now)));
        double tokens = Double.parseDouble(bucket.getOrDefault("tokens", String.valueOf(capacity)));

        // Refill
        long deltaSec = (now - lastRefill) / 1000;
        if (deltaSec > 0) {
            tokens = Math.min(capacity, tokens + (deltaSec * refillRatePerSec));
            lastRefill = now;
        }

        // Consume
        boolean allowed = false;
        if (tokens >= 1) {
            tokens -= 1;
            allowed = true;
        }

        // Save State
        bucket.put("last_refill", String.valueOf(lastRefill));
        bucket.put("tokens", String.valueOf(tokens));
        return allowed;
    }

    // --- LUA SCRIPT: SLIDING WINDOW LOG ---
    public synchronized boolean luaSlidingWindowLog(String key, int limit, int windowMillis, long now) {
        zsets.putIfAbsent(key, new TreeMap<>());
        TreeMap<Long, List<String>> log = zsets.get(key);

        // 1. Prune (ZREMRANGEBYSCORE)
        long windowStart = now - windowMillis;
        log.headMap(windowStart).clear();

        // 2. Count (ZCARD)
        int count = log.values().stream().mapToInt(List::size).sum();

        // 3. Decide
        if (count < limit) {
            // Add unique member (ZADD)
            log.computeIfAbsent(now, k -> new ArrayList<>()).add(UUID.randomUUID().toString());
            return true;
        }
        return false;
    }

    // --- LUA SCRIPT: SLIDING WINDOW COUNTER ---
    public synchronized boolean luaSlidingWindowCounter(String key, int limit, int windowMillis, long now) {
        long currentWindowKey = now / windowMillis;
        long prevWindowKey = currentWindowKey - 1;

        String currKey = key + ":" + currentWindowKey;
        String prevKey = key + ":" + prevWindowKey;

        // Get counts (GET)
        int currCount = counters.getOrDefault(currKey, 0);
        int prevCount = counters.getOrDefault(prevKey, 0);

        // Calculate Weighted Count
        // Formula: curr + prev * (1 - (time_elapsed / window_size))
        long timeInCurrentWindow = now % windowMillis;
        double weight = 1.0 - ((double) timeInCurrentWindow / windowMillis);
        double estimatedCount = currCount + (prevCount * weight);

        if (estimatedCount < limit) {
            // INCR current window
            counters.put(currKey, currCount + 1);
            return true;
        }
        return false;
    }
}

// ---------------------------------------------------------
// 2. STRATEGY INTERFACE & IMPLEMENTATIONS
// ---------------------------------------------------------
interface RateLimiterStrategy {
    boolean allow(String key, int limit, int windowSec);
}

class TokenBucketStrategy implements RateLimiterStrategy {
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

class SlidingLogStrategy implements RateLimiterStrategy {
    private final MockRedis redis;
    public SlidingLogStrategy(MockRedis redis) { this.redis = redis; }

    @Override
    public boolean allow(String key, int limit, int windowSec) {
        return redis.luaSlidingWindowLog(key, limit, windowSec * 1000, System.currentTimeMillis());
    }
}

class SlidingCounterStrategy implements RateLimiterStrategy {
    private final MockRedis redis;
    public SlidingCounterStrategy(MockRedis redis) { this.redis = redis; }

    @Override
    public boolean allow(String key, int limit, int windowSec) {
        return redis.luaSlidingWindowCounter(key, limit, windowSec * 1000, System.currentTimeMillis());
    }
}

// ---------------------------------------------------------
// 3. MAIN FACTORY / SDK
// ---------------------------------------------------------
class DistributedRateLimiterSDK {
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

// ---------------------------------------------------------
// 4. DRIVER CLASS
// ---------------------------------------------------------
public class RateLimiterDriver {
    public static void main(String[] args) throws InterruptedException {
        MockRedis redis = new MockRedis();
        DistributedRateLimiterSDK sdk = new DistributedRateLimiterSDK(redis);

        System.out.println("=== TEST 1: Token Bucket (Burst Allowance) ===");
        // Limit 2, Window 2s -> Rate 1 per sec. Capacity 2.
        System.out.println("Req 1: " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // True (Tokens=1)
        System.out.println("Req 2: " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // True (Tokens=0)
        System.out.println("Req 3: " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // False (Empty)
        Thread.sleep(1100); // Wait 1.1s -> Refill 1 token
        System.out.println("Req 4 (After 1s): " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // True

        System.out.println("\n=== TEST 2: Sliding Window Log (Strict) ===");
        // Limit 2 per 1 sec
        System.out.println("Req 1: " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // True
        System.out.println("Req 2: " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // True
        System.out.println("Req 3: " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // False (Full)
        Thread.sleep(1100); // Slide window
        System.out.println("Req 4 (After 1.1s): " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // True

        System.out.println("\n=== TEST 3: Sliding Window Counter (Approximation) ===");
        // Limit 10, Window 10s.
        // Simulate: 9 requests in previous window. 0 in current.
        // Weight: 0.5 (halfway through current window).
        // Est = 0 + 9 * 0.5 = 4.5.
        // Since logic relies on real clock, we simulate by "pre-filling" the mock redis manually or just basic run

        // Basic Run:
        String key = "user3";
        for(int i=0; i<5; i++) sdk.allowRequest("SLIDING_COUNTER", key, 5, 1); // Fill
        System.out.println("Req 6: " + sdk.allowRequest("SLIDING_COUNTER", key, 5, 1)); // False
    }
}

