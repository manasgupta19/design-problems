package Tier1.DistributedCache.router;

import java.util.List;
import java.util.function.Function;

import Tier1.DistributedCache.model.SegmentedLRUCache;

// ---------------------------------------------------------
// 4. DISTRIBUTED CACHE CLIENT (The Driver)
// ---------------------------------------------------------
public class DistributedCacheClient {
    private final ConsistentHashRouter<SegmentedLRUCache<String, String>> router;
    private final Function<String, String> dbFallback; // Simulates DB

    public DistributedCacheClient(List<SegmentedLRUCache<String, String>> nodes, Function<String, String> dbFallback) {
        this.router = new ConsistentHashRouter<>(nodes, 10); // 10 VNodes
        this.dbFallback = dbFallback;
    }

    // Pattern: CACHE-ASIDE
    public String get(String key) {
        SegmentedLRUCache<String, String> node = router.route(key);
        String val = node.get(key);

        if (val == null) {
            System.out.println("[Cache Miss] Fetching from DB for: " + key);
            val = dbFallback.apply(key); // Read from DB
            if (val != null) {
                node.put(key, val, 5000); // Populate Cache (TTL 5s)
            }
        } else {
            System.out.println("[Cache Hit] " + key + " -> " + val);
        }
        return val;
    }

    // Pattern: WRITE-THROUGH
    public void put(String key, String value) {
        // 1. Write to DB (Source of Truth)
        System.out.println("[DB Write] " + key + " = " + value);
        // Simulation of DB write...

        // 2. Update Cache
        SegmentedLRUCache<String, String> node = router.route(key);
        node.put(key, value, 5000);
        System.out.println("[Cache Update] " + key);
    }
}

