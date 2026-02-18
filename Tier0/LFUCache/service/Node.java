package Tier0.LFUCache.service;

// ---------------------------------------------------------
// 1. NODE CLASS
// ---------------------------------------------------------
public class Node {
    int key;
    int value;
    int freq;
    Node prev;
    Node next;

    public Node(int key, int value) {
        this.key = key;
        this.value = value;
        this.freq = 1; // Default starting frequency
    }
}