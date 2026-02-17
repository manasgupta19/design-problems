package Tier1.DistributedBlobStore;

import java.util.*;

import Tier1.DistributedBlobStore.model.BlobMetadata;
import Tier1.DistributedBlobStore.model.StorageNode;
import Tier1.DistributedBlobStore.service.BlobStore;
import Tier1.DistributedBlobStore.service.MetadataService;
import Tier1.DistributedBlobStore.service.SimpleS3;

// ---------------------------------------------------------
// 5. DRIVER CLASS
// ---------------------------------------------------------
public class BlobStoreDriver {
    public static void main(String[] args) {
        // Init Infrastructure
        MetadataService meta = new MetadataService();
        List<StorageNode> nodes = Arrays.asList(new StorageNode("1"), new StorageNode("2"), new StorageNode("3"));
        BlobStore s3 = new SimpleS3(meta, nodes);

        String bucket = "my-images";
        String key = "vacation.jpg";
        byte[] imageBytes = "BinaryDataOfImage".getBytes();

        System.out.println("--- Scenario 1: Upload (PUT) ---");
        s3.putObject(bucket, key, imageBytes);

        System.out.println("\n--- Scenario 2: Metadata Access (HEAD) ---");
        BlobMetadata md = s3.getObjectMetadata(bucket, key);
        System.out.println("Meta: Size=" + md.getSize() + ", ID=" + md.getBlobId());

        System.out.println("\n--- Scenario 3: Download (GET) ---");
        byte[] result = s3.getObject(bucket, key);
        System.out.println("Downloaded: " + new String(result));

        System.out.println("\n--- Scenario 4: Overwrite (Versioning/LWW) ---");
        // User uploads new version of same file
        byte[] newBytes = "UpdatedImageV2".getBytes();
        String newId = s3.putObject(bucket, key, newBytes);

        byte[] resultV2 = s3.getObject(bucket, key);
        System.out.println("Downloaded V2: " + new String(resultV2) + ", ID=" + newId);
        // Note: Old blob still exists on disk (orphaned) until GC runs.
    }
}


