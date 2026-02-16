package Tier1.DistributedKVStore.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ---------------------------------------------------------
// 1. STORAGE NODE (The Replica)
// ---------------------------------------------------------
public class StorageNode {
    String id;
    // Local storage: Key -> {Value, Timestamp}
    Map<String, DataRecord> localStore = new ConcurrentHashMap<>();

    public StorageNode(String id) { this.id = id; }

    public String getId() { return id; }

    public void writeLocally(String key, String value, long timestamp) {
        // LWW (Last Write Wins) implementation at node level
        localStore.compute(key, (k, oldVal) -> {
            if (oldVal == null || timestamp > oldVal.timestamp) {
                return new DataRecord(value, timestamp);
            }
            return oldVal; // Ignore older write (out of order message)
        });
        System.out.println("Node " + id + " stored [" + key + "=" + value + "] @ " + timestamp);
    }

    public DataRecord readLocally(String key) {
        return localStore.get(key);
    }
}
