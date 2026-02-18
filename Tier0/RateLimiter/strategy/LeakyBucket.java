package Tier0.RateLimiter.strategy;

// ---------------------------------------------------------
// 3. LEAKY BUCKET IMPLEMENTATION (Smoothed)
// ---------------------------------------------------------
public class LeakyBucket implements BucketStrategy {
    private final long capacity;
    private final double leakRatePerNs;

    private double currentWaterLevel;
    private long lastLeakTimestamp;

    public LeakyBucket(long capacity, long outflowRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerNs = outflowRatePerSecond / 1_000_000_000.0;
        this.currentWaterLevel = 0; // Start empty
        this.lastLeakTimestamp = System.nanoTime();
    }

    @Override
    public synchronized boolean tryConsume(long waterAmount) {
        leak();

        // If adding water doesn't overflow capacity
        if (currentWaterLevel + waterAmount <= capacity) {
            currentWaterLevel += waterAmount;
            return true; // Allowed
        }
        return false; // Overflow (Throttled)
    }

    // Lazy Leak: Calculate how much drained since last check
    private void leak() {
        long now = System.nanoTime();
        long durationNs = now - lastLeakTimestamp;

        if (durationNs > 0) {
            double leakedAmount = durationNs * leakRatePerNs;
            currentWaterLevel = Math.max(0, currentWaterLevel - leakedAmount);
            lastLeakTimestamp = now;
        }
    }
}
