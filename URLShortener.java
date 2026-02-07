import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

// ---------------------------------------------------------
// 1. COMPONENT: ID GENERATOR (Simulating Snowflake)
// ---------------------------------------------------------
class IdGenerator {
    // In a real system, this would be a distributed ID (Snowflake/UUID v7)
    // We start at a strictly positive number to avoid 0/null confusion
    private final AtomicLong counter = new AtomicLong(1000000L);

    public long nextId() {
        return counter.getAndIncrement();
    }
}

// ---------------------------------------------------------
// 2. COMPONENT: BASE62 ENCODER (The Core Logic)
// ---------------------------------------------------------
class Base62 {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final char[] BASE_62_CHARS = ALPHABET.toCharArray();
    private static final int BASE = 62;

    public static String encode(long id) {
        if (id == 0) return String.valueOf(BASE_62_CHARS);

        StringBuilder sb = new StringBuilder();
        while (id > 0) {
            int remainder = (int) (id % BASE);
            sb.append(BASE_62_CHARS[remainder]);
            id /= BASE;
        }
        return sb.reverse().toString();
    }

    public static long decode(String str) {
        long id = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            int val = ALPHABET.indexOf(c); // O(1) lookup in reality via int array
            id = id * BASE + val;
        }
        return id;
    }
}

// ---------------------------------------------------------
// 3. SERVICE LAYER (Orchestrator)
// ---------------------------------------------------------
class UrlShortenerServiceImpl implements UrlShortenerService {
    private final IdGenerator idGen;
    // Simulating NoSQL DB (Key-Value Store)
    private final Map<String, String> db = new ConcurrentHashMap<>();
    // Reverse index for idempotency (LongURL -> ShortURL)
    private final Map<String, String> reverseDb = new ConcurrentHashMap<>();

    public UrlShortenerServiceImpl() {
        this.idGen = new IdGenerator();
    }

    @Override
    public String createShortLink(String longUrl, String userId) {
        // 1. Idempotency Check: Don't create duplicates for same URL
        if (reverseDb.containsKey(longUrl)) {
            return reverseDb.get(longUrl);
        }

        // 2. Generate Unique ID (Snowflake)
        long id = idGen.nextId();

        // 3. Convert to Base62
        String shortKey = Base62.encode(id);

        // 4. Persistence (Simulating DB Write)
        db.put(shortKey, longUrl);
        reverseDb.put(longUrl, shortKey); // For idempotency

        // 5. In a real design, here we would push to Kafka for Analytics [Source 1292]
        return "http://tiny.url/" + shortKey;
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        // Extract key from full URL if necessary
        String key = shortUrl.replace("http://tiny.url/", "");

        // 1. Cache Lookaside would happen here (Redis) [Source 1505]

        // 2. DB Lookup
        if (!db.containsKey(key)) {
            throw new RuntimeException("404 Not Found");
        }
        return db.get(key);
    }
}

// ---------------------------------------------------------
// 4. DRIVER (Simulation)
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) {
        UrlShortenerService service = new UrlShortenerServiceImpl();

        System.out.println("--- Scenario 1: Basic Shortening ---");
        String longUrl = "https://www.google.com/search?q=system+design";
        String shortUrl = service.createShortLink(longUrl, "User1");
        System.out.println("Long: " + longUrl);
        System.out.println("Short: " + shortUrl);

        System.out.println("\n--- Scenario 2: Redirection ---");
        String original = service.getOriginalUrl(shortUrl);
        System.out.println("Retrieved: " + original);
        System.out.println("Match: " + original.equals(longUrl));

        System.out.println("\n--- Scenario 3: Idempotency ---");
        String retryShortUrl = service.createShortLink(longUrl, "User1");
        System.out.println("Retry Short: " + retryShortUrl);
        System.out.println("Is Same Object? " + (shortUrl.equals(retryShortUrl)));
    }
}

