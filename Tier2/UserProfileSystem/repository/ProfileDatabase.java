package Tier2.UserProfileSystem.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import Tier2.UserProfileSystem.model.UserProfile;

public // 2. MOCK DATABASE (Source of Truth)
class ProfileDatabase {
    private Map<String, UserProfile> db = new ConcurrentHashMap<>();

    public ProfileDatabase() {
        // Seed data
        db.put("u1", new UserProfile("u1", "alice", "Engineer"));
        db.put("u2", new UserProfile("u2", "bob", "Designer"));
    }

    public UserProfile read(String userId) {
        System.out.println("[DB] Reading from Disk: " + userId);
        try { Thread.sleep(100); } catch (Exception e) {} // Simulate Latency
        return db.get(userId);
    }

    public void write(UserProfile p) {
        System.out.println("[DB] Writing to Disk: " + p.getUserId());
        db.put(p.getUserId(), p);
    }
}
