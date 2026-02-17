package Tier1.DistributedCache.model;

// ---------------------------------------------------------
// 1. CORE DATA STRUCTURES (Node & Policy)
// ---------------------------------------------------------
public class CacheNode<K, V> {
    K key;
    V value;
    long expiryTime;
    CacheNode<K, V> prev, next;

    public CacheNode(K key, V value, long ttlMs) {
        this.key = key;
        this.value = value;
        this.expiryTime = System.currentTimeMillis() + ttlMs;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }

    public CacheNode<K, V> getPrev() {
        return prev;
    }

    public CacheNode<K, V> getNext() {
        return next;
    }

    public V getValue() {
        return value;
    }

    public K getKey() {
        return key;
    }
}
