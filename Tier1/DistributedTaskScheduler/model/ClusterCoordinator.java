package Tier1.DistributedTaskScheduler.model;

import java.util.ArrayList;
import java.util.List;

// ---------------------------------------------------------
// 4. COORDINATOR (Simulates Zookeeper/Etcd)
// ---------------------------------------------------------
public class ClusterCoordinator {
    List<SchedulerNode> nodes = new ArrayList<>();
    int totalPartitions = 4; // Simplified for demo

    public void registerNode(SchedulerNode node) {
        nodes.add(node);
        rebalance(); // Simple rebalance on every join
    }

    // Logic to distribute partitions evenly across nodes
    private void rebalance() {
        System.out.println("\n--- Coordinator: Rebalancing Cluster ---");
        for (SchedulerNode n : nodes) n.assignedPartitions.clear();

        for (int p = 0; p < totalPartitions; p++) {
            // Consistent Hash / Round Robin assignment
            SchedulerNode owner = nodes.get(p % nodes.size());
            owner.assignPartition(p);
        }
        System.out.println("----------------------------------------\n");
    }
}
