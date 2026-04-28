package Tier1.RateLimiter.strategy;

// ---------------------------------------------------------
// 1. STRATEGY INTERFACE
// ---------------------------------------------------------
public interface BucketStrategy {
    boolean tryConsume(long tokens);
}