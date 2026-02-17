package Tier1.DistributedCache.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

// ---------------------------------------------------------
// 2. THREAD-SAFE SEGMENTED LRU (The Storage Engine)
// ---------------------------------------------------------
public class SegmentedLRUCache<K, V> {
    private final int capacity;
    private final Map<K, CacheNode<K, V>> map;
    private final CacheNode<K, V> head, tail;
    private final ReentrantLock lock = new ReentrantLock(); // Fine-grained lock

    public SegmentedLRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new ConcurrentHashMap<>();

        // Dummy head/tail for easier linking
        head = new CacheNode<>(null, null, 0);
        tail = new CacheNode<>(null, null, 0);
        head.next = tail;
        tail.prev = head;
    }

    public V get(K key) {
        lock.lock(); // Lock required to update LRU order
        try {
            if (!map.containsKey(key)) return null;

            CacheNode<K, V> node = map.get(key);
            if (node.isExpired()) {
                removeNode(node);
                map.remove(key);
                return null;
            }

            // Move to Head (Recently Used)
            moveToHead(node);
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value, long ttlMs) {
        lock.lock();
        try {
            if (map.containsKey(key)) {
                CacheNode<K, V> node = map.get(key);
                node.value = value;
                node.expiryTime = System.currentTimeMillis() + ttlMs;
                moveToHead(node);
            } else {
                if (map.size() >= capacity) {
                    evictLRU();
                }
                CacheNode<K, V> newNode = new CacheNode<>(key, value, ttlMs);
                map.put(key, newNode);
                addNode(newNode); // Adds to head
            }
        } finally {
            lock.unlock();
        }
    }

    // DLL Helper: Add to front
    private void addNode(CacheNode<K, V> node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    // DLL Helper: Remove node
    private void removeNode(CacheNode<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // DLL Helper: Move existing node to front
    private void moveToHead(CacheNode<K, V> node) {
        removeNode(node);
        addNode(node);
    }

    // Evict the Tail (Least Recently Used)
    private void evictLRU() {
        CacheNode<K, V> lru = tail.prev;
        if (lru != head) {
            removeNode(lru);
            map.remove(lru.key);
            System.out.println("[Eviction] Removed key: " + lru.key);
        }
    }
}

