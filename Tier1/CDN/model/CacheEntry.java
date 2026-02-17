package Tier1.CDN.model;

// 2. CACHE ENTRY WRAPPER
public class CacheEntry {
    byte[] data;
    long expiry;

    public CacheEntry(byte[] data, long ttlMs) {
        this.data = data;
        this.expiry = System.currentTimeMillis() + ttlMs;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiry;
    }

    public byte[] getData() {
        return data;
    }
}