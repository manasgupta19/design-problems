package Tier1.DistributedTaskScheduler.model;

// ---------------------------------------------------------
// 1. DATA MODELS
// ---------------------------------------------------------
public class Task {
    String id;
    String payload;
    long executeAt;
    String status; // PENDING, RUNNING, COMPLETED
    int version;   // For Optimistic Locking
    int partitionId;

    public Task(String id, String payload, long executeAt, int partitionId) {
        this.id = id; this.payload = payload;
        this.executeAt = executeAt; this.partitionId = partitionId;
        this.status = "PENDING"; this.version = 1;
    }

    public String getId() { return id; }
    public String getPayload() { return payload; }
    public long getExecuteAt() { return executeAt; }
    public String getStatus() { return status; }
    public int getVersion() { return version; }
    public int getPartitionId() { return partitionId; }

    public void setStatus(String status) { this.status = status; }
    public void incrementVersion() { this.version++; }
    
}
