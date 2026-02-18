package Tier0.LFUCache.service;

// ---------------------------------------------------------
// 2. DOUBLY LINKED LIST (The Bucket)
// ---------------------------------------------------------
public class DoublyLinkedList {
    Node head;
    Node tail;
    int size;

    public DoublyLinkedList() {
        // Sentinel Nodes to simplify boundary checks [Source 293, 300]
        head = new Node(-1, -1);
        tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    // Add to Head (MRU position within this frequency)
    public void addFirst(Node node) {
        Node afterHead = head.next;
        head.next = node;
        node.prev = head;
        node.next = afterHead;
        afterHead.prev = node;
        size++;
    }

    // Remove specific node (O(1))
    public void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        prevNode.next = nextNode;
        nextNode.prev = prevNode;
        size--;
    }

    // Remove Tail (LRU position within this frequency)
    public Node removeLast() {
        if (size == 0) return null;
        Node lastNode = tail.prev;
        removeNode(lastNode);
        return lastNode;
    }
}

