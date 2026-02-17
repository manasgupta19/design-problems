package Tier1.DistributedBlobStore.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// ---------------------------------------------------------
// 2. STORAGE NODE (The Data Plane)
// ---------------------------------------------------------
// Simulates a physical disk or storage server.
public class StorageNode {
    private final String nodeId;
    // Maps BlobID -> Raw Bytes
    private final Map<String, byte[]> disk = new ConcurrentHashMap<>();

    public StorageNode(String id) { this.nodeId = id; }

    public void save(String blobId, byte[] data) {
        disk.put(blobId, data);
        System.out.println("[Disk-" + nodeId + "] Wrote " + data.length + " bytes for BlobID: " + blobId);
    }

    public byte[] read(String blobId) {
        return disk.get(blobId);
    }
}
