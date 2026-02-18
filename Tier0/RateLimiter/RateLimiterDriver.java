package Tier0.RateLimiter;

import Tier0.RateLimiter.strategy.RateLimiterManager;

// ---------------------------------------------------------
// 5. DRIVER CLASS
// ---------------------------------------------------------
class RateLimiterDriver {
    public static void main(String[] args) throws InterruptedException {
        testTokenBucket();
        System.out.println("--------------------------------");
        testLeakyBucket();
    }

    private static void testTokenBucket() throws InterruptedException {
        System.out.println("Testing Token Bucket (Capacity 5, Rate 1/sec)");
        // Capacity 5, Refill 1 per second
        RateLimiterManager limiter = new RateLimiterManager(true, 5, 1);
        String client = "UserA";

        // Burst Scenario: Consume all 5 tokens immediately
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + ": " + (limiter.allowRequest(client) ? "Allowed" : "Blocked"));
        }

        // 6th Request (Bucket Empty) -> Should Block
        System.out.println("Request 6: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked"));

        // Wait 2 seconds (Refill 2 tokens)
        System.out.println("Sleeping 2 seconds...");
        Thread.sleep(2000);

        // Should allow 2 requests now
        System.out.println("Request 7: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked"));
        System.out.println("Request 8: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked"));
        System.out.println("Request 9: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked")); // Empty again
    }

    private static void testLeakyBucket() throws InterruptedException {
        System.out.println("Testing Leaky Bucket (Capacity 2, Rate 1/sec)");
        // Capacity 2, Outflow 1 per second
        RateLimiterManager limiter = new RateLimiterManager(false, 2, 1);
        String client = "UserB";

        // Fill the bucket
        System.out.println("Request 1: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked")); // Water=1
        System.out.println("Request 2: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked")); // Water=2 (Full)

        // Overflow
        System.out.println("Request 3: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked")); // Blocked

        // Wait 1.1 seconds (Drain 1 unit)
        System.out.println("Sleeping 1.1 seconds...");
        Thread.sleep(1100);

        System.out.println("Request 4: " + (limiter.allowRequest(client) ? "Allowed" : "Blocked")); // Allowed
    }
}


