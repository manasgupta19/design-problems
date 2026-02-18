package Tier2.UserProfileSystem.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import Tier2.UserProfileSystem.model.UserProfile;
import Tier2.UserProfileSystem.repository.ProfileDatabase;
import Tier2.UserProfileSystem.repository.RedisCache;

// 4. THE CORE SERVICE (L1 + L2 + Coalescing)
public class ProfileService implements UserProfileSystem {
    private final ProfileDatabase db;
    private final RedisCache l2Cache;

    // L1 Cache: Simple LRU (Simulated with Map for brevity)
    private final Map<String, UserProfile> l1Cache = new ConcurrentHashMap<>();

    // Coalescing Map: Tracks in-flight requests to prevent Thundering Herd
    private final ConcurrentHashMap<String, CompletableFuture<UserProfile>> flightMap = new ConcurrentHashMap<>();

    public ProfileService(ProfileDatabase db, RedisCache l2) {
        this.db = db;
        this.l2Cache = l2;
    }

    @Override
    public UserProfile get(String userId) {
        // 1. Check L1 (Local Heap)
        if (l1Cache.containsKey(userId)) {
            System.out.println("[L1-Heap] Hit: " + userId);
            return l1Cache.get(userId);
        }

        // 2. Check L2 (Distributed)
        UserProfile p = l2Cache.get(userId);
        if (p != null) {
            l1Cache.put(userId, p); // Populate L1
            return p;
        }

        // 3. Request Coalescing (The "Singleflight" Pattern)
        // Only ONE thread enters the DB fetch block for a specific userId
        CompletableFuture<UserProfile> future = flightMap.computeIfAbsent(userId, k -> {
            return CompletableFuture.supplyAsync(() -> {
                // 4. Fetch from DB
                UserProfile dbProfile = db.read(k);

                // 5. Populate Caches
                if (dbProfile != null) {
                    l2Cache.set(k, dbProfile);
                    l1Cache.put(k, dbProfile);
                }
                return dbProfile;
            });
        });

        try {
            // All threads wait here for the single DB response
            UserProfile result = future.join();

            // Cleanup flight map (Standard double-check pattern omitted for brevity)
            flightMap.remove(userId, future);

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void update(String userId, String newBio) {
        // 1. Update DB (System of Truth)
        UserProfile current = db.read(userId);
        if (current != null) {
            UserProfile updated = new UserProfile(userId, current.getUsername(), newBio);
            db.write(updated);

            // 2. Invalidate L2 (Redis)
            l2Cache.delete(userId);

            // 3. Invalidate L1 (Local)
            // In a real system, this sends a Pub/Sub message to all nodes
            System.out.println("[L1-Heap] Invalidate: " + userId);
            l1Cache.remove(userId);
        }
    }
}

