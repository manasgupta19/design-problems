package Tier0.HashMap.service;

public interface MyMap<K, V> {
    // Inserts or updates a value. Returns old value if key existed.
    V put(K key, V value);

    // Retrieves value. Returns null if not found.
    V get(K key);

    // Removes key. Returns value if it existed.
    V remove(K key);

    // Returns current count of elements.
    int size();
}

