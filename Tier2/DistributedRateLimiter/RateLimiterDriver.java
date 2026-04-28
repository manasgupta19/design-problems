import repository.DistributedRateLimiterSDK;
import repository.MockRedis;

// ---------------------------------------------------------
// 4. DRIVER CLASS
// ---------------------------------------------------------
public class RateLimiterDriver {
    public static void main(String[] args) throws InterruptedException {
        MockRedis redis = new MockRedis();
        DistributedRateLimiterSDK sdk = new DistributedRateLimiterSDK(redis);

        System.out.println("=== TEST 1: Token Bucket (Burst Allowance) ===");
        // Limit 2, Window 2s -> Rate 1 per sec. Capacity 2.
        System.out.println("Req 1: " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // True (Tokens=1)
        System.out.println("Req 2: " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // True (Tokens=0)
        System.out.println("Req 3: " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // False (Empty)
        Thread.sleep(1100); // Wait 1.1s -> Refill 1 token
        System.out.println("Req 4 (After 1s): " + sdk.allowRequest("TOKEN_BUCKET", "user1", 2, 2)); // True

        System.out.println("\n=== TEST 2: Sliding Window Log (Strict) ===");
        // Limit 2 per 1 sec
        System.out.println("Req 1: " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // True
        System.out.println("Req 2: " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // True
        System.out.println("Req 3: " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // False (Full)
        Thread.sleep(1100); // Slide window
        System.out.println("Req 4 (After 1.1s): " + sdk.allowRequest("SLIDING_LOG", "user2", 2, 1)); // True

        System.out.println("\n=== TEST 3: Sliding Window Counter (Approximation) ===");
        // Limit 10, Window 10s.
        // Simulate: 9 requests in previous window. 0 in current.
        // Weight: 0.5 (halfway through current window).
        // Est = 0 + 9 * 0.5 = 4.5.
        // Since logic relies on real clock, we simulate by "pre-filling" the mock redis manually or just basic run

        // Basic Run:
        String key = "user3";
        for(int i=0; i<5; i++) sdk.allowRequest("SLIDING_COUNTER", key, 5, 1); // Fill
        System.out.println("Req 6: " + sdk.allowRequest("SLIDING_COUNTER", key, 5, 1)); // False
    }
}

