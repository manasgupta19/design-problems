package Tier0.InMemoryKVStore.model;

// ---------------------------------------------------------
// 1. THE NODE (Doubly Linked List Entry)
// ---------------------------------------------------------
public class Node<K, V> {
    K key;
    V value;
    Node<K, V> prev;
    Node<K, V> next;

    public Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}