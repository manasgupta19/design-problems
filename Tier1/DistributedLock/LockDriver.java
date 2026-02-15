package Tier1.DistributedLock;

import Tier1.DistributedLock.repository.MockRedisClient;
import Tier1.DistributedLock.service.RedisDistributedLock;

// 3. DRIVER CLASS
public class LockDriver {
    public static void main(String[] args) throws InterruptedException {
        MockRedisClient redis = new MockRedisClient();
        RedisDistributedLock dlm = new RedisDistributedLock(redis);
        String resource = "db_write_access";

        System.out.println("--- Scenario 1: Basic Acquire/Release ---");
        String tokenA = dlm.tryLock(resource, 5000);
        System.out.println("Client A acquired: " + (tokenA != null)); // True

        String tokenB = dlm.tryLock(resource, 5000);
        System.out.println("Client B acquired: " + (tokenB != null)); // False (Locked)

        boolean releasedA = dlm.unlock(resource, tokenA);
        System.out.println("Client A released: " + releasedA); // True

        System.out.println("\n--- Scenario 2: Expiry & Safe Release (The Race Condition) ---");
        // A acquires with short TTL
        tokenA = dlm.tryLock(resource, 100);
        System.out.println("Client A acquired (100ms TTL)");

        // Simulate GC Pause / Network Delay > TTL
        Thread.sleep(200);
        System.out.println("(Client A hangs for 200ms...)");

        // Lock expires naturally. Client B acquires.
        tokenB = dlm.tryLock(resource, 5000);
        System.out.println("Client B acquired: " + (tokenB != null)); // True (A expired)

        // Client A wakes up and tries to release OLD token
        // THIS MUST FAIL. If it succeeds, it deletes B's lock!
        boolean releasedA_Late = dlm.unlock(resource, tokenA);
        System.out.println("Client A release late: " + releasedA_Late); // False (Safety Check)

        // B should still hold the lock
        boolean releasedB = dlm.unlock(resource, tokenB);
        System.out.println("Client B releases: " + releasedB); // True
    }
}
