package Tier1.DistributedLock.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 1. MOCK REDIS CLIENT (Simulating Network/Atomic constraints)
public class MockRedisClient {
    
    // Key -> {Value, ExpiryTime}
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    private static class Entry {
        String value;
        long expiryTime;
        Entry(String v, long e) { this.value = v; this.expiryTime = e; }
    }

    // Simulates "SET key value NX PX ttl"
    public synchronized String set(String key, String value, String nxxx, String expx, long time) {
        long now = System.currentTimeMillis();

        // Clean expired keys (Simulate Redis passive expiry)
        if (store.containsKey(key) && store.get(key).expiryTime < now) {
            store.remove(key);
        }

        if ("NX".equals(nxxx) && store.containsKey(key)) {
            return null; // Lock exists
        }

        store.put(key, new Entry(value, now + time));
        return "OK";
    }

    // Simulates Lua Script for Atomic Unlock
    public synchronized Long eval(String script, String key, String expectedValue) {
        long now = System.currentTimeMillis();

        // Check Expiry
        if (store.containsKey(key) && store.get(key).expiryTime < now) {
            store.remove(key);
            return 0L; // Lock expired already
        }

        Entry e = store.get(key);
        if (e != null && e.value.equals(expectedValue)) {
            store.remove(key);
            return 1L; // Released
        }
        return 0L; // Not owner or missing
    }
}
