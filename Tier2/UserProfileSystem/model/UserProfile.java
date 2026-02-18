package Tier2.UserProfileSystem.model;

public // 1. DOMAIN ENTITY
class UserProfile {
    String userId;
    String username;
    String bio;

    // Constructor
    public UserProfile(String id, String name, String bio) {
        this.userId = id; this.username = name; this.bio = bio;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getBio() { return bio; }

    @Override
    public String toString() { return String.format("[%s] %s: %s", userId, username, bio); }
}
