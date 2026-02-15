package repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// ---------------------------------------------------------
// 1. MOCK REDIS (Simulating Atomic Lua Scripts)
// ---------------------------------------------------------
public class MockRedis {
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