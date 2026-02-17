package Tier1.CDN.service;

import Tier1.CDN.model.CacheEntry;
import Tier1.CDN.model.OriginServer;
import Tier1.CDN.model.Response;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

// 3. EDGE SERVER IMPLEMENTATION
public class EdgeServer implements EdgeCDN {
    private final OriginServer origin;
    private final int ttlMs;

    // Thread-safe Cache (LRU logic omitted for brevity, using simple ConcurrentMap)
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    // The "Flight Map" for Request Coalescing
    private final ConcurrentHashMap<String, CompletableFuture<byte[]>> inFlightRequests = new ConcurrentHashMap<>();

    public EdgeServer(OriginServer origin, int ttlMs) {
        this.origin = origin;
        this.ttlMs = ttlMs;
    }

    @Override
    public Response get(String path) {
        // 1. FAST PATH: Check Cache
        CacheEntry entry = cache.get(path);
        if (entry != null && !entry.isExpired()) {
            return new Response(path, entry.getData(), true); // HIT
        }

        // 2. SLOW PATH: Fetch (With Coalescing)
        // computeIfAbsent is ATOMIC. Only ONE thread enters the lambda.
        CompletableFuture<byte[]> future = inFlightRequests.computeIfAbsent(path, k -> {
            return CompletableFuture.supplyAsync(() -> {
                byte[] data = origin.fetch(k);
                // Update Cache
                cache.put(k, new CacheEntry(data, ttlMs));
                return data;
            });
        });

        try {
            // All threads (including the one that started it) wait here
            byte[] result = future.join();

            // Cleanup: Remove from in-flight map once done
            // Only remove if it's the SAME future we just finished (avoid race with new request)
            inFlightRequests.remove(path, future);

            // Determine if WE triggered the fetch or just piggybacked
            @SuppressWarnings("unused")
            boolean isHit = (entry != null && !entry.isExpired());
            // Note: In real coalescing, the first user is a MISS, subsequent waiters are "SHIELD HITS"
            // For simplicity, we mark all as MISS if they waited on origin.

            return new Response(path, result, false);
        } catch (Exception e) {
            return new Response(path, null, false);
        }
    }

    @Override
    public boolean purge(String path) {
        System.out.println("[Edge] Purging: " + path);
        cache.remove(path);
        return true;
    }
}
