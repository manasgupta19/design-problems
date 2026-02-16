package Tier1.DistributedKVStore;

import java.util.*;

import Tier1.DistributedKVStore.model.StorageNode;
import Tier1.DistributedKVStore.service.DistributedKVCoordinator;
import Tier1.DistributedKVStore.service.DistributedKVStore;

// ---------------------------------------------------------
// 3. DRIVER CLASS
// ---------------------------------------------------------
public class KVStoreDriver {
    public static void main(String[] args) {
        // Setup Cluster
        List<StorageNode> nodes = Arrays.asList(
            new StorageNode("NodeA"),
            new StorageNode("NodeB"),
            new StorageNode("NodeC")
        );

        // Create Coordinator with Replication Factor N=3
        DistributedKVStore store = new DistributedKVCoordinator(nodes, 3);

        System.out.println("--- Scenario 1: Strong Consistency Write (W=3) ---");
        // Must wait for all 3
        store.put("User:1", "{name: Alice}", 3);

        System.out.println("\n--- Scenario 2: Eventual Consistency Write (W=1) ---");
        // Fast! Returns after 1 ACK.
        store.put("User:1", "{name: Alice Updated}", 1);

        System.out.println("\n--- Scenario 3: Read (R=2) ---");
        // Queries 2 nodes, returns latest timestamp (LWW)
        System.out.println("Result: " + store.get("User:1", 2));
    }
}

