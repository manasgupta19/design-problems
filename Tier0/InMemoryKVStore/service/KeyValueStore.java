package Tier0.InMemoryKVStore.service;

public interface KeyValueStore<K, V> {
    // Retrieves value. Moves key to MRU (Most Recently Used).
    V get(K key);

    // Insert/Update. If full, evicts LRU.
    void put(K key, V value);

    // Atomic check-and-set. Only puts if key doesn't exist.
    // Returns existing value if present, null if put succeeded.
    V putIfAbsent(K key, V value);

    // Removes key.
    void remove(K key);

    // Current size (sum of all segments)
    int size();
}


