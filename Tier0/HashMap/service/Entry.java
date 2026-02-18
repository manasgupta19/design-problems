package Tier0.HashMap.service;

// 1. Core Entry Class (Singly Linked List Node)
public class Entry<K, V> {
    final K key;
    V value;
    Entry<K, V> next;
    final int hash; // Cache hash to avoid re-computation during resize

    public Entry(K key, V value, Entry<K, V> next, int hash) {
        this.key = key;
        this.value = value;
        this.next = next;
        this.hash = hash;
    }
}
