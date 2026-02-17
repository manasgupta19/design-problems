package Tier1.DistributedTaskScheduler.model;

import java.util.ArrayList;
import java.util.List;

// ---------------------------------------------------------
// 3. WORKER NODE (The Scheduler Instance)
// ---------------------------------------------------------
public class SchedulerNode implements Runnable {
    String nodeId;
    TaskDatabase db;
    List<Integer> assignedPartitions; // Assigned by Leader
    boolean running = true;

    public SchedulerNode(String id, TaskDatabase db) {
        this.nodeId = id;
        this.db = db;
        this.assignedPartitions = new ArrayList<>();
    }

    public void assignPartition(int pId) {
        this.assignedPartitions.add(pId);
        System.out.println("Node " + nodeId + " assigned Partition " + pId);
    }

    public void setRunning(boolean running) { this.running = running; }

    @Override
    public void run() {
        while (running) {
            try {
                long now = System.currentTimeMillis();

                // 1. Iterate over my assigned partitions
                for (int pId : assignedPartitions) {
                    List<Task> tasks = db.getDueTasks(pId, now);

                    for (Task t : tasks) {
                        // 2. Try to claim (Optimistic Lock)
                        if (db.attemptClaim(t.id, t.version, nodeId)) {
                            // 3. Execute
                            processTask(t);
                        }
                    }
                }
                Thread.sleep(1000); // Polling interval
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    private void processTask(Task t) {
        System.out.println(">>> Node " + nodeId + " EXECUTING: " + t.payload);
        t.status = "COMPLETED"; // In real DB, this is another update
    }
}
