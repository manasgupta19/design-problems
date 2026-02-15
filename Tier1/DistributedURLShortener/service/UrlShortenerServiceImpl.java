package Tier1.DistributedURLShortener.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Tier1.DistributedURLShortener.util.Base62;
import Tier1.DistributedURLShortener.util.IdGenerator;

// ---------------------------------------------------------
// 3. SERVICE LAYER (Orchestrator)
// ---------------------------------------------------------
public class UrlShortenerServiceImpl implements UrlShortenerService {
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
