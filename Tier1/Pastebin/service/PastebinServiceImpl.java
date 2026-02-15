package Tier1.Pastebin.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import Tier1.Pastebin.exception.PasteNotFoundException;
import Tier1.Pastebin.exception.StorageLimitExceededException;
import Tier1.Pastebin.repo.MockObjectStore;
import Tier1.Pastebin.repo.PasteMetadata;
import Tier1.Pastebin.util.Base62Encoder;

// ---------------------------------------------------------
// 3. CORE SERVICE
// ---------------------------------------------------------
public class PastebinServiceImpl implements PastebinService {

    private static final long MAX_CONTENT_SIZE = 1024 * 10; // 10 KB limit for demo
    private final MockObjectStore objectStore;
    private final Map<String, PasteMetadata> metadataDb; // Simulating DynamoDB
    private final AtomicLong idCounter = new AtomicLong(1000); // Simulating Snowflake ID

    public PastebinServiceImpl() {
        this.objectStore = new MockObjectStore();
        this.metadataDb = new ConcurrentHashMap<>();
    }

    @Override
    public String createPaste(String content, int durationSeconds) {
        // 1. Validation & Limits [Source 1204]
        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        if (content.length() > MAX_CONTENT_SIZE) {
            throw new StorageLimitExceededException("Content exceeds limit of " + MAX_CONTENT_SIZE + " bytes");
        }

        // 2. Generate Key (Simulating Base62 encoding of unique ID) [Source 1415]
        long uniqueId = idCounter.getAndIncrement();
        String key = Base62Encoder.encode(uniqueId);

        // 3. Upload to Object Store (S3) first
        // We assume S3 path is simply the key for this design
        objectStore.put(key, content);

        // 4. Save Metadata with Expiration
        long expiryTime = Instant.now().getEpochSecond() + durationSeconds;
        PasteMetadata metadata = new PasteMetadata(key, expiryTime, key);
        metadataDb.put(key, metadata);

        return key;
    }

    @Override
    public String getPaste(String key) {
        // 1. Lookup Metadata
        PasteMetadata meta = metadataDb.get(key);

        if (meta == null) {
            throw new PasteNotFoundException("Paste not found: " + key);
        }

        // 2. Lazy Expiration Check [Source 117]
        // If current time > expiration, we treat it as deleted
        if (Instant.now().getEpochSecond() > meta.getExpirationTimestamp()) {
            // Optional: Trigger async delete here
            System.out.println("[Lazy Delete] Found expired key on read: " + key);
            metadataDb.remove(key); // Remove metadata immediately
            throw new PasteNotFoundException("Paste expired: " + key);
        }

        // 3. Fetch from Object Store
        String content = objectStore.get(meta.getS3Path());
        if (content == null) {
            throw new RuntimeException("Data corruption: Metadata exists but S3 object missing");
        }

        return content;
    }
}


