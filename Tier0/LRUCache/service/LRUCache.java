package Tier0.LRUCache.service;

import java.util.HashMap;
import java.util.Map;

// ---------------------------------------------------------
// 1. THE CORE CLASS
// ---------------------------------------------------------
public class LRUCache<K, V> implements Cache<K, V> {

    // Internal Node Class (Doubly Linked)
    private class Node {
        K key;      // Stored to allow reverse-lookup for map removal
        V value;
        Node prev;
        Node next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<K, Node> map;

    // Sentinel Nodes [Source 300]
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();

        // Initialize Sentinels
        this.head = new Node(null, null); // Dummy Head
        this.tail = new Node(null, null); // Dummy Tail

        // Connect Sentinels: Head <-> Tail
        head.next = tail;
        tail.prev = head;
    }

    // ---------------------------------------------------------
    // 2. API METHODS
    // ---------------------------------------------------------

    @Override
    public V get(K key) {
        if (!map.containsKey(key)) {
            return null;
        }

        Node node = map.get(key);
        // Move accessed node to front (MRU position)
        removeNode(node);
        addNodeToHead(node);

        return node.value;
    }

    @Override
    public void put(K key, V value) {
        if (map.containsKey(key)) {
            // Update existing
            Node node = map.get(key);
            node.value = value;
            removeNode(node);
            addNodeToHead(node);
        } else {
            // Insert new
            if (map.size() == capacity) {
                // Eviction Logic: Remove LRU (node before tail)
                Node lruNode = tail.prev;
                map.remove(lruNode.key); // Key required here!
                removeNode(lruNode);
            }

            Node newNode = new Node(key, value);
            map.put(key, newNode);
            addNodeToHead(newNode);
        }
    }

    // ---------------------------------------------------------
    // 3. HELPER METHODS (DLL Manipulation)
    // ---------------------------------------------------------

    /**
     * Unplugs a node from the list. O(1).
     */
    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;

        prevNode.next = nextNode;
        nextNode.prev = prevNode;
    }

    /**
     * Inserts a node right after the dummy head. O(1).
     */
    private void addNodeToHead(Node node) {
        Node currentHead = head.next;

        // Connect node to head
        node.prev = head;
        node.next = currentHead;

        // Connect head/currentHead to node
        head.next = node;
        currentHead.prev = node;
    }

    // For testing/visualization
    public void printCache() {
        Node curr = head.next;
        System.out.print("MRU -> ");
        while (curr != tail) {
            System.out.print("[" + curr.key + ":" + curr.value + "] ");
            curr = curr.next;
        }
        System.out.println("<- LRU");
    }
}
