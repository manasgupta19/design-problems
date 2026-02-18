package Tier0.InMemoryKVStore.model;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

// ---------------------------------------------------------
// 2. THE SEGMENT (The Unit of Locking)
// ---------------------------------------------------------
public class Segment<K, V> {
    private final int capacity;
    private final HashMap<K, Node<K, V>> map;
    private final ReentrantLock lock;

    // LRU Pointers (Head = MRU, Tail = LRU)
    private Node<K, V> head;
    private Node<K, V> tail;
    private int size;

    public Segment(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.lock = new ReentrantLock();
        this.size = 0;
    }

    public V get(K key) {
        lock.lock(); // Enter Critical Section [Source 875]
        try {
            if (!map.containsKey(key)) return null;

            Node<K, V> node = map.get(key);
            moveToHead(node); // Record access
            return node.value;
        } finally {
            lock.unlock();
        }
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            if (map.containsKey(key)) {
                // Update existing
                Node<K, V> node = map.get(key);
                node.value = value;
                moveToHead(node);
            } else {
                // Insert new
                if (size >= capacity) {
                    evictLRU();
                }
                Node<K, V> newNode = new Node<>(key, value);
                addToHead(newNode);
                map.put(key, newNode);
                size++;
            }
        } finally {
            lock.unlock();
        }
    }

    // Atomic Operation [Source 171]
    public V putIfAbsent(K key, V value) {
        lock.lock();
        try {
            if (map.containsKey(key)) {
                Node<K, V> node = map.get(key);
                moveToHead(node);
                return node.value; // Return existing
            }
            // Perform put logic
            if (size >= capacity) evictLRU();
            Node<K, V> newNode = new Node<>(key, value);
            addToHead(newNode);
            map.put(key, newNode);
            size++;
            return null; // Return null indicating success
        } finally {
            lock.unlock();
        }
    }

    public void remove(K key) {
        lock.lock();
        try {
            if (!map.containsKey(key)) return;
            Node<K, V> node = map.get(key);
            removeNode(node);
            map.remove(key);
            size--;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try { return size; } finally { lock.unlock(); }
    }

    // --- Internal Helpers (Must be called within Lock) ---

    private void addToHead(Node<K, V> node) {
        if (head == null) {
            head = tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    private void removeNode(Node<K, V> node) {
        if (node.prev != null) node.prev.next = node.next;
        else head = node.next; // Removing head

        if (node.next != null) node.next.prev = node.prev;
        else tail = node.prev; // Removing tail

        node.next = node.prev = null; // Help GC
    }

    private void moveToHead(Node<K, V> node) {
        if (node == head) return;
        removeNode(node);
        addToHead(node);
    }

    private void evictLRU() {
        if (tail == null) return;
        map.remove(tail.key);
        removeNode(tail);
        size--;
    }
}
