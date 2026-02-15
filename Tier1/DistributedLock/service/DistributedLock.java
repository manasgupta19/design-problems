package Tier1.DistributedLock.service;

public interface DistributedLock {
    /**
     * Tries to acquire the lock.
     * @param resourceKey Unique name of the resource (e.g., "order:123").
     * @param ttlMs Time-to-live in milliseconds (Safety Lease).
     * @return LockToken if acquired, null if failed.
     */
    String tryLock(String resourceKey, long ttlMs);

    /**
     * Releases the lock only if the token matches.
     * @param resourceKey Unique name of the resource.
     * @param token The token received during tryLock.
     * @return true if released, false if lock was lost/expired.
     */
    boolean unlock(String resourceKey, String token);
}


