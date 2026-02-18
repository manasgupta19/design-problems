package Tier0.LRUCache.service;

public interface Cache<K, V> {
    /**
     * Retrieves value for key. Moves item to MRU position.
     * Returns null if not found.
     */
    V get(K key);

    /**
     * Inserts/Updates value.
     * If capacity exceeded, evicts LRU item.
     * New/Updated item becomes MRU.
     */
    void put(K key, V value);
}


