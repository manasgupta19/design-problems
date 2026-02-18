package Tier0.LFUCache.service;

import java.util.HashMap;
import java.util.Map;

// ---------------------------------------------------------
// 3. LFU CACHE IMPLEMENTATION
// ---------------------------------------------------------
public class LFUCache {
    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> keyMap;
    private final Map<Integer, DoublyLinkedList> freqMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    // ------------------- GET OPERATION -------------------
    public int get(int key) {
        if (!keyMap.containsKey(key)) {
            return -1;
        }

        Node node = keyMap.get(key);
        updateFrequency(node); // The core promotion logic
        return node.value;
    }

    // ------------------- PUT OPERATION -------------------
    public void put(int key, int value) {
        if (capacity == 0) return;

        if (keyMap.containsKey(key)) {
            // Case 1: Update existing
            Node node = keyMap.get(key);
            node.value = value;
            updateFrequency(node);
            return;
        }

        // Case 2: Insert new
        if (keyMap.size() >= capacity) {
            // Evict LFU
            DoublyLinkedList minList = freqMap.get(minFreq);
            Node victim = minList.removeLast(); // Tie-breaker: LRU within min bucket [Source 64]
            keyMap.remove(victim.key);
        }

        // Add new node
        Node newNode = new Node(key, value);
        keyMap.put(key, newNode);

        // New nodes always have freq 1
        minFreq = 1;
        freqMap.computeIfAbsent(1, k -> new DoublyLinkedList()).addFirst(newNode);
    }

    // ------------------- HELPER: PROMOTION -------------------
    private void updateFrequency(Node node) {
        int currentFreq = node.freq;
        DoublyLinkedList currentList = freqMap.get(currentFreq);

        // 1. Remove from current bucket
        currentList.removeNode(node);

        // 2. Critical: Update minFreq if necessary [Source 63]
        // If this was the minimal frequency bucket and it's now empty,
        // the new global minimum MUST be the next frequency up.
        if (currentFreq == minFreq && currentList.size == 0) {
            minFreq++;
        }

        // 3. Increment frequency and add to new bucket
        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new DoublyLinkedList()).addFirst(node);
    }

    // Debug helper
    public void printState() {
        System.out.println("MinFreq: " + minFreq);
        for(Integer freq : freqMap.keySet()) {
            if(freqMap.get(freq).size > 0) {
                System.out.print("Freq " + freq + ": ");
                Node curr = freqMap.get(freq).head.next;
                while(curr != freqMap.get(freq).tail) {
                    System.out.print("[" + curr.key + "]");
                    curr = curr.next;
                }
                System.out.println();
            }
        }
        System.out.println("---");
    }
}
