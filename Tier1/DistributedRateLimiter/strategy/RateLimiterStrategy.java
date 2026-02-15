package strategy;

// ---------------------------------------------------------
// 2. STRATEGY INTERFACE & IMPLEMENTATIONS
// ---------------------------------------------------------
public interface RateLimiterStrategy {
    boolean allow(String key, int limit, int windowSec);
}
