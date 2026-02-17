package Tier1.DistributedBlobStore.service;

import Tier1.DistributedBlobStore.model.BlobMetadata;

public interface BlobStore {
    /**
     * Uploads an object.
     * @return ETag (Hash of the data)
     */
    String putObject(String bucket, String key, byte[] data);

    /**
     * Retrieves an object.
     * @return The binary data.
     */
    byte[] getObject(String bucket, String key);

    /**
     * Retrieves object metadata (size, content-type).
     */
    BlobMetadata getObjectMetadata(String bucket, String key);
}


