package Tier1.DistributedBlobStore.model;

// ---------------------------------------------------------
// 1. DATA STRUCTURES (Internal Contracts)
// ---------------------------------------------------------
public class BlobMetadata {
    String bucket;
    String key;
    String blobId; // Internal physical ID
    long size;
    long timestamp;

    public BlobMetadata(String b, String k, String id, long s) {
        this.bucket = b; this.key = k; this.blobId = id;
        this.size = s; this.timestamp = System.currentTimeMillis();
    }

    public String getBlobId() { return blobId; }
    public long getSize() { return size; }
    public long getTimestamp() { return timestamp; }
    
}
