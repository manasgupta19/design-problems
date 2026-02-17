package Tier1.DistributedTaskScheduler;

import Tier1.DistributedTaskScheduler.model.ClusterCoordinator;
import Tier1.DistributedTaskScheduler.model.SchedulerNode;
import Tier1.DistributedTaskScheduler.model.Task;
import Tier1.DistributedTaskScheduler.model.TaskDatabase;


// ---------------------------------------------------------
// 5. DRIVER CLASS
// ---------------------------------------------------------
public class SchedulerDriver {
    public static void main(String[] args) throws InterruptedException {
        // Init Infrastructure
        TaskDatabase db = new TaskDatabase();
        ClusterCoordinator zk = new ClusterCoordinator();

        // 1. Submit Tasks (Hashing to different partitions)
        // Partition Logic: ID % 4
        db.save(new Task("101", "Email User A", System.currentTimeMillis(), 1)); // P1
        db.save(new Task("102", "Gen Report B", System.currentTimeMillis(), 2)); // P2
        db.save(new Task("103", "Cleanup C", System.currentTimeMillis(), 3));    // P3

        // 2. Start Worker Nodes
        SchedulerNode nodeA = new SchedulerNode("Node-A", db);
        SchedulerNode nodeB = new SchedulerNode("Node-B", db);

        Thread t1 = new Thread(nodeA);
        Thread t2 = new Thread(nodeB);
        t1.start(); t2.start();

        // 3. Nodes join cluster -> Coordinator assigns partitions
        zk.registerNode(nodeA); // Node A gets all initially
        Thread.sleep(500);
        zk.registerNode(nodeB); // Rebalance: Split partitions

        // Let them process
        Thread.sleep(2000);

        // 4. Simulate Split Brain / Race Condition
        // Both nodes try to claim a new task in Partition 1
        System.out.println("\n--- Scenario: Race Condition ---");
        Task conflictTask = new Task("999", "Critical Payment", System.currentTimeMillis(), 1);
        db.save(conflictTask);

        // Force both nodes to try claiming (Simulating overlapping assignment)
        // In real life, ZK prevents this, but network lag allows it.
        // DB Optimistic Lock saves us.
        boolean claim1 = db.attemptClaim("999", 1, "Node-A");
        boolean claim2 = db.attemptClaim("999", 1, "Node-B");

        System.out.println("Node A Claim Success: " + claim1);
        System.out.println("Node B Claim Success: " + claim2); // Should fail

        // Cleanup
        nodeA.setRunning(false);
        nodeB.setRunning(false);
        t1.join(); t2.join();
    }
}


