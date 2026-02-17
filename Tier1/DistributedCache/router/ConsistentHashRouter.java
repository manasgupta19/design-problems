package Tier1.DistributedCache.router;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

// ---------------------------------------------------------
// 3. CONSISTENT HASH ROUTER (The Network Layer)
// ---------------------------------------------------------
public class ConsistentHashRouter<T> {
    private final TreeMap<Long, T> ring = new TreeMap<>();
    private final int numberOfReplicas; // Virtual nodes

    public ConsistentHashRouter(Collection<T> nodes, int replicas) {
        this.numberOfReplicas = replicas;
        for (T node : nodes) {
            addNode(node);
        }
    }

    public void addNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = hash(node.toString() + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = hash(node.toString() + i);
            ring.remove(hash);
        }
    }

    public T route(String key) {
        if (ring.isEmpty()) return null;
        long hash = hash(key);
        if (!ring.containsKey(hash)) {
            SortedMap<Long, T> tailMap = ring.tailMap(hash);
            hash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        }
        return ring.get(hash);
    }

    // Helper: Simple hash for demo (use MurmurHash in prod)
    private long hash(String key) {
        return key.hashCode() & 0xFFFFFFFFL;
    }
}

