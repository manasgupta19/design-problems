package Tier1.DistributedBlobStore.service;

import java.util.List;
import java.util.UUID;

import Tier1.DistributedBlobStore.model.BlobMetadata;
import Tier1.DistributedBlobStore.model.StorageNode;

// ---------------------------------------------------------
// 4. BLOB STORE ORCHESTRATOR (The S3 Service)
// ---------------------------------------------------------
public class SimpleS3 implements BlobStore {
    private final MetadataService metaService;
    private final List<StorageNode> storageNodes;
    private final int replicationFactor = 2; // For simulation

    public SimpleS3(MetadataService meta, List<StorageNode> nodes) {
        this.metaService = meta;
        this.storageNodes = nodes;
    }

    @Override
    public String putObject(String bucket, String key, byte[] data) {
        // 1. Generate unique Physical ID (Internal)
        String blobId = UUID.randomUUID().toString();

        // 2. DATA PLANE: Write to Storage Nodes (Replication)
        // In reality, use Consistent Hashing to pick nodes. Here, simple round-robin/all.
        // We simulate writing to first N nodes.
        for (int i = 0; i < Math.min(storageNodes.size(), replicationFactor); i++) {
            storageNodes.get(i).save(blobId, data);
        }

        // 3. CONTROL PLANE: Only update metadata after successful data write
        // This prevents "dangling pointers" (metadata pointing to non-existent data)
        metaService.saveMetadata(bucket, key, blobId, data.length);

        return blobId; // Return internal ID or ETag
    }

    @Override
    public byte[] getObject(String bucket, String key) {
        // 1. CONTROL PLANE: Lookup ID
        BlobMetadata meta = metaService.getMetadata(bucket, key);
        if (meta == null) return null;

        // 2. DATA PLANE: Fetch bytes
        // Try nodes until one succeeds
        for (StorageNode node : storageNodes) {
            byte[] data = node.read(meta.getBlobId());
            if (data != null) return data;
        }
        throw new RuntimeException("Data corruption: Metadata exists but blob missing!");
    }

    @Override
    public BlobMetadata getObjectMetadata(String bucket, String key) {
        return metaService.getMetadata(bucket, key);
    }
}
