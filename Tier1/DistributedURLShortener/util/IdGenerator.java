package Tier1.DistributedURLShortener.util;

import java.util.concurrent.atomic.AtomicLong;

// ---------------------------------------------------------
// 1. COMPONENT: ID GENERATOR (Simulating Snowflake)
// ---------------------------------------------------------
public class IdGenerator {
    // In a real system, this would be a distributed ID (Snowflake/UUID v7)
    // We start at a strictly positive number to avoid 0/null confusion
    private final AtomicLong counter = new AtomicLong(1000000L);

    public long nextId() {
        return counter.getAndIncrement();
    }
}
