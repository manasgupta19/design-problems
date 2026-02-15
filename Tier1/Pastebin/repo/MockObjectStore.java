package Tier1.Pastebin.repo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockObjectStore {
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
