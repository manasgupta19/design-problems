import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// ---------------------------------------------------------
// 1. EXCEPTIONS
// ---------------------------------------------------------
class StorageLimitExceededException extends RuntimeException {
    public StorageLimitExceededException(String message) { super(message); }
}

class PasteNotFoundException extends RuntimeException {
    public PasteNotFoundException(String message) { super(message); }
}

// ---------------------------------------------------------
// 2. ABSTRACTIONS (Simulating Infrastructure)
// ---------------------------------------------------------

// Simulating S3 (Object Storage)
class MockObjectStore {
    private final Map<String, String> bucket = new ConcurrentHashMap<>();

    public void put(String key, String content) {
        bucket.put(key, content);
        System.out.println("[S3] Uploaded object: " + key + " (" + content.length() + " bytes)");
    }

    public String get(String key) {
        return bucket.get(key);
    }

    // For cleanup simulation
    public boolean exists(String key) { return bucket.containsKey(key); }
}

// Simulating Database with TTL support
class PasteMetadata {
    String key;
    long expirationTimestamp; // Unix Epoch
    String s3Path;

    public PasteMetadata(String key, long expirationTimestamp, String s3Path) {
        this.key = key;
        this.expirationTimestamp = expirationTimestamp;
        this.s3Path = s3Path;
    }
}

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
        if (Instant.now().getEpochSecond() > meta.expirationTimestamp) {
            // Optional: Trigger async delete here
            System.out.println("[Lazy Delete] Found expired key on read: " + key);
            metadataDb.remove(key); // Remove metadata immediately
            throw new PasteNotFoundException("Paste expired: " + key);
        }

        // 3. Fetch from Object Store
        String content = objectStore.get(meta.s3Path);
        if (content == null) {
            throw new RuntimeException("Data corruption: Metadata exists but S3 object missing");
        }

        return content;
    }
}

// ---------------------------------------------------------
// 4. UTILITY: BASE62 ENCODER
// ---------------------------------------------------------
class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public static String encode(long value) {
        StringBuilder sb = new StringBuilder();
        while (value > 0) {
            sb.append(ALPHABET.charAt((int) (value % 62)));
            value /= 62;
        }
        return sb.reverse().toString();
    }
}

// ---------------------------------------------------------
// 5. DRIVER CLASS (Simulation)
// ---------------------------------------------------------
class PastebinDriver {
    public static void main(String[] args) throws InterruptedException {
        PastebinService service = new PastebinServiceImpl();

        System.out.println("--- Scenario 1: Basic Create & Get ---");
        String key1 = service.createPaste("Hello System Design!", 5); // 5 seconds TTL
        System.out.println("Created Paste: " + key1);
        System.out.println("Retrieved Content: " + service.getPaste(key1));

        System.out.println("\n--- Scenario 2: Expiration (Lazy Delete) ---");
        System.out.println("Sleeping for 6 seconds...");
        Thread.sleep(6000); // Wait for expiration
        try {
            service.getPaste(key1);
        } catch (PasteNotFoundException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }

        System.out.println("\n--- Scenario 3: Storage Limit ---");
        try {
            // Generate 11KB string
            String largeContent = "A".repeat(1024 * 11);
            service.createPaste(largeContent, 60);
        } catch (StorageLimitExceededException e) {
            System.out.println("Caught Expected Exception: " + e.getMessage());
        }
    }
}

