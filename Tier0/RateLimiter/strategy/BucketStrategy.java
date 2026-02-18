package Tier0.RateLimiter.strategy;

// ---------------------------------------------------------
// 1. STRATEGY INTERFACE
// ---------------------------------------------------------
public interface BucketStrategy {
    boolean tryConsume(long tokens);
}