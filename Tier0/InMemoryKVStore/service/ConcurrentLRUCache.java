package Tier0.InMemoryKVStore.service;

import Tier0.InMemoryKVStore.model.Segment;

// ---------------------------------------------------------
// 3. THE COORDINATOR (Main Class)
// ---------------------------------------------------------
public class ConcurrentLRUCache<K, V> implements KeyValueStore<K, V> {
    private static final int SEGMENT_COUNT = 16; // Power of 2 for bitwise ops
    private final Segment<K, V>[] segments;

    @SuppressWarnings("unchecked")
    public ConcurrentLRUCache(int totalCapacity) {
        int capPerSegment = (int) Math.ceil((double) totalCapacity / SEGMENT_COUNT);
        this.segments = new Segment[SEGMENT_COUNT];
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            segments[i] = new Segment<>(capPerSegment);
        }
    }

    // Hash Logic: Map key to segment [Source 164]
    private int getSegmentIndex(K key) {
        // Use hash & (N-1) for efficiency
        return Math.abs(key.hashCode() % SEGMENT_COUNT);
    }

    @Override
    public V get(K key) {
        return segments[getSegmentIndex(key)].get(key);
    }

    @Override
    public void put(K key, V value) {
        segments[getSegmentIndex(key)].put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return segments[getSegmentIndex(key)].putIfAbsent(key, value);
    }

    @Override
    public void remove(K key) {
        segments[getSegmentIndex(key)].remove(key);
    }

    @Override
    public int size() {
        int total = 0;
        for (Segment<K, V> seg : segments) total += seg.size();
        return total;
    }
}
