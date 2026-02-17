package Tier1.DistributedBlobStore.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Tier1.DistributedBlobStore.model.BlobMetadata;

// ---------------------------------------------------------
// 3. METADATA SERVICE (The Control Plane)
// ---------------------------------------------------------
// Stores the mapping from Logical Key (user path) to Physical BlobID.
public class MetadataService {
    // Key: "bucket/key", Value: Metadata
    private final Map<String, BlobMetadata> metaStore = new ConcurrentHashMap<>();

    public void saveMetadata(String bucket, String key, String blobId, long size) {
        String lookupKey = bucket + "/" + key;
        // Last-Write-Wins: Simply overwrite the reference
        metaStore.put(lookupKey, new BlobMetadata(bucket, key, blobId, size));
        System.out.println("[Meta] Linked " + lookupKey + " -> " + blobId);
    }

    public BlobMetadata getMetadata(String bucket, String key) {
        return metaStore.get(bucket + "/" + key);
    }
}
