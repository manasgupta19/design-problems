package Tier1.ConsistentHashing;

import Tier1.ConsistentHashing.service.ConsistentHashRing;

// ---------------------------------------------------------
// DRIVER CLASS
// ---------------------------------------------------------
class Driver {
    public static void main(String[] args) {
        // Init ring with 3 virtual nodes per physical node for readability
        // In prod, this would be 100-200 [Source 1019]
        ConsistentHashRing router = new ConsistentHashRing(3);

        // Scenario 1: Add Nodes
        System.out.println("\n--- 1. Initial Cluster Setup ---");
        router.addNode("Server-A");
        router.addNode("Server-B");
        router.addNode("Server-C");

        // Scenario 2: Route Keys
        System.out.println("\n--- 2. Routing Keys ---");
        String[] keys = {"User1", "User2", "User3", "User4", "User5"};
        for (String k : keys) {
            System.out.println(k + " routed to -> " + router.getNode(k));
        }

        // Scenario 3: Scale Out (Add Server-D)
        // Only keys mapping to D's new ranges should move.
        System.out.println("\n--- 3. Scale Out (Adding Server-D) ---");
        router.addNode("Server-D");

        System.out.println("Re-evaluating Key Placement:");
        for (String k : keys) {
            System.out.println(k + " routed to -> " + router.getNode(k));
        }

        // Scenario 4: Failover (Remove Server-A)
        // Keys on Server-A should migrate to their next clockwise neighbor
        System.out.println("\n--- 4. Node Failure (Removing Server-A) ---");
        router.removeNode("Server-A");

        System.out.println("Failover Key Placement:");
        for (String k : keys) {
            System.out.println(k + " routed to -> " + router.getNode(k));
        }
    }
}


