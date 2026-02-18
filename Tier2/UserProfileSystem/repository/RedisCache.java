package Tier2.UserProfileSystem.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Tier2.UserProfileSystem.model.UserProfile;

public // 3. MOCK DISTRIBUTED CACHE (Redis / L2)
class RedisCache {
    private Map<String, UserProfile> cache = new ConcurrentHashMap<>();

    public UserProfile get(String key) {
        UserProfile p = cache.get(key);
        if (p != null) System.out.println("[L2-Redis] Hit: " + key);
        return p;
    }

    public void set(String key, UserProfile p) {
        cache.put(key, p);
    }

    public void delete(String key) {
        System.out.println("[L2-Redis] Invalidate: " + key);
        cache.remove(key);
    }
}
