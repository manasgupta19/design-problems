package Tier2.UserProfileSystem.service;

import Tier2.UserProfileSystem.model.UserProfile;

public interface UserProfileSystem {
    /**
     * Retrieves profile.
     * Uses L1 -> L2 -> DB hierarchy with Request Coalescing.
     */
    UserProfile get(String userId);

    /**
     * Updates profile.
     * Writes to DB and invalidates caches.
     */
    void update(String userId, String bio);
}


