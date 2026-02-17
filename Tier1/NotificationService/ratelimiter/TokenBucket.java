package Tier1.NotificationService.ratelimiter;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// ---------------------------------------------------------
// 4. RATE LIMITER (Token Bucket)
// ---------------------------------------------------------
public class TokenBucket {
    private final AtomicInteger tokens;

    public TokenBucket(int capacity) {
        this.tokens = new AtomicInteger(capacity);
        // Refill logic simulated: Add 1 token every 100ms
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            if (tokens.get() < capacity) tokens.incrementAndGet();
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    public boolean tryConsume() {
        return tokens.updateAndGet(current -> current > 0 ? current - 1 : 0) > 0;
    }
}
