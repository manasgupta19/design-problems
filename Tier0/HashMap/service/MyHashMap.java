package Tier0.HashMap.service;

import java.util.Objects;

// 2. The Hash Map Implementation
public class MyHashMap<K, V> {
    // Defaults per industry standard [Source 215]
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        this.table = new Entry[INITIAL_CAPACITY];
        this.threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
    }

    // ---------------------------------------------------------
    // CORE: Index Calculation
    // ---------------------------------------------------------
    private int getIndex(int hash, int capacity) {
        // Bitwise AND is faster than Modulo (%) for power-of-2 capacities [Source 217]
        return hash & (capacity - 1);
    }

    private int hash(Object key) {
        // Handle null keys by mapping to 0, otherwise mix bits
        return (key == null) ? 0 : key.hashCode();
    }

    // ---------------------------------------------------------
    // OPERATION: PUT (Insert/Update)
    // ---------------------------------------------------------
    public V put(K key, V value) {
        int hash = hash(key);
        int index = getIndex(hash, table.length);

        // 1. Check for update (Collision traversal)
        Entry<K, V> current = table[index];
        while (current != null) {
            // Reference check first (fast), then equals (slow) [Source 222]
            if (current.hash == hash && Objects.equals(current.key, key)) {
                V oldValue = current.value;
                current.value = value; // Update existing
                return oldValue;
            }
            current = current.next;
        }

        // 2. Insert new node at HEAD (O(1) insertion)
        // Note: Java 8 inserts at TAIL to handle Treeify, but HEAD is standard for simple maps
        Entry<K, V> newEntry = new Entry<>(key, value, table[index], hash);
        table[index] = newEntry;
        size++;

        // 3. Check Resize
        if (size >= threshold) {
            resize(table.length * 2);
        }

        return null;
    }

    // ---------------------------------------------------------
    // OPERATION: GET (Retrieval)
    // ---------------------------------------------------------
    public V get(K key) {
        int hash = hash(key);
        int index = getIndex(hash, table.length);

        Entry<K, V> current = table[index];
        while (current != null) {
            // Must check both Hash and Equality contract [Source 222]
            if (current.hash == hash && Objects.equals(current.key, key)) {
                return current.value;
            }
            current = current.next;
        }
        return null;
    }

    // ---------------------------------------------------------
    // OPERATION: RESIZE (Rehashing) [Source 220]
    // ---------------------------------------------------------
    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] newTable = new Entry[newCapacity];

        // Iterate over every bucket in old table
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> e = table[i];

            while (e != null) {
                Entry<K, V> next = e.next; // Save next pointer

                // Recompute index for new capacity
                int newIndex = getIndex(e.hash, newCapacity);

                // Head Insertion into new bucket
                e.next = newTable[newIndex];
                newTable[newIndex] = e;

                e = next; // Move to next in old chain
            }
        }

        this.table = newTable;
        this.threshold = (int) (newCapacity * LOAD_FACTOR);
    }

    public int size() { return size; }
    public int capacity() { return table.length; } // For testing
}

