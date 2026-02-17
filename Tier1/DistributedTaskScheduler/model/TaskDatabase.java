package Tier1.DistributedTaskScheduler.model;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// ---------------------------------------------------------
// 2. STORAGE LAYER (Mocking Postgres with Optimistic Lock)
// ---------------------------------------------------------
public class TaskDatabase {
    private final Map<String, Task> store = new ConcurrentHashMap<>();

    public void save(Task t) { store.put(t.getId(), t); }

    // Simulates: SELECT * FROM tasks WHERE partition=? AND time<=now
    public List<Task> getDueTasks(int partitionId, long currentTime) {
        return store.values().stream()
            .filter(t -> t.getPartitionId() == partitionId)
            .filter(t -> t.getStatus().equals("PENDING"))
            .filter(t -> t.getExecuteAt() <= currentTime)
            .collect(Collectors.toList());
    }

    // Simulates: UPDATE tasks SET status='RUNNING', version=v+1 WHERE id=? AND version=?
    public boolean attemptClaim(String taskId, int currentVersion, String workerId) {
        Task task = store.get(taskId);
        synchronized (task) { // Synchronized block simulates DB Row Lock
            if (task.getVersion() == currentVersion && task.getStatus().equals("PENDING")) {
                task.setStatus("RUNNING");
                task.incrementVersion();
                System.out.println("DB: Task " + taskId + " claimed by " + workerId);
                return true; // Lock acquired
            }
        }
        return false; // Stale version, claim failed
    }
}
