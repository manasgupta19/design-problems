package Tier1.DistributedKVStore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import Tier1.DistributedKVStore.model.DataRecord;
import Tier1.DistributedKVStore.model.StorageNode;

// ---------------------------------------------------------
// 2. COORDINATOR SERVICE (The Router)
// ---------------------------------------------------------
public class DistributedKVCoordinator implements DistributedKVStore {

    private final TreeMap<Long, StorageNode> ring = new TreeMap<>();
    private final List<StorageNode> allNodes;
    private final int N; // Replication Factor (e.g., 3)

    public DistributedKVCoordinator(List<StorageNode> nodes, int replicationFactor) {
        this.allNodes = nodes;
        this.N = replicationFactor;

        // Setup Consistent Hash Ring (Simplified: 1 vNode per physical node)
        for (StorageNode node : nodes) {
            long hash = hash(node.getId());
            ring.put(hash, node);
        }
    }

    // Hash function (simulated MD5)
    private long hash(String key) {
        return key.hashCode() & 0xFFFFFFFFL;
    }

    // Step 1: Find Preference List (N distinct nodes)
    private List<StorageNode> getPreferenceList(String key) {
        List<StorageNode> prefList = new ArrayList<>();
        long hash = hash(key);

        // Find first node clockwise
        Map.Entry<Long, StorageNode> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry(); // Wrap around

        // Walk ring to collect N distinct nodes
        // In prod, we skip virtual nodes belonging to same physical server
        // Iterator<StorageNode> it = allNodes.iterator(); // Simplified ring walk
        while (prefList.size() < N) {
             // Logic to pick next N successors in ring...
             // For demo, we just pick simple round-robin from sorted list starting at hash
             // (Abbreviated for clarity)
             prefList.add(entry.getValue());
             // Moving to next logic omitted for brevity, assuming uniform distribution
             break;
        }

        // *Mocking* the Preference List for this Driver:
        // Always pick Node A, B, C for simplicity in output
        return allNodes.subList(0, Math.min(allNodes.size(), N));
    }

    @Override
    public void put(String key, String value, int w) {
        List<StorageNode> replicas = getPreferenceList(key);
        long timestamp = System.currentTimeMillis();

        AtomicInteger acks = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(w);

        // Parallel Write to Replicas
        for (StorageNode node : replicas) {
            CompletableFuture.runAsync(() -> {
                try {
                    // Simulate network delay
                    Thread.sleep((long) (Math.random() * 50));
                    node.writeLocally(key, value, timestamp);
                    acks.incrementAndGet();
                    latch.countDown();
                } catch (Exception e) {}
            });
        }

        try {
            // Wait for W acks
            boolean success = latch.await(200, TimeUnit.MILLISECONDS);
            if (!success) {
                throw new RuntimeException("Write timeout: Only got " + acks.get() + " ACKs");
            }
            System.out.println("Write Successful (W=" + w + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public String get(String key, int r) {
        List<StorageNode> replicas = getPreferenceList(key);
        List<DataRecord> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(r);

        // Parallel Read
        for (StorageNode node : replicas) {
            CompletableFuture.runAsync(() -> {
                try {
                    DataRecord rec = node.readLocally(key);
                    if (rec != null) results.add(rec);
                    latch.countDown();
                } catch (Exception e) {}
            });
        }

        try {
            latch.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {}

        if (results.isEmpty()) return null;

        // Read Repair / Conflict Resolution: Last Write Wins
        DataRecord newest = results.get(0);
        for (DataRecord rec : results) {
            if (rec.getTimestamp() > newest.getTimestamp()) {
                newest = rec;
            }
        }

        // (Optional) Trigger Async Read Repair for stale nodes here

        return newest.getValue();
    }
}
