package Tier1.DistributedIdGenerator.service;

import Tier1.DistributedIdGenerator.exception.ClockMovedBackwardsException;
import Tier1.DistributedIdGenerator.model.SnowflakeConfig;

public class SnowflakeIdGenerator implements IdGenerator {

    // Max values to prevent overflow
    private static final long MAX_WORKER_ID = (1L << SnowflakeConfig.WORKER_ID_BITS) - 1;
    private static final long MAX_SEQUENCE = (1L << SnowflakeConfig.SEQUENCE_BITS) - 1;

    // Bit Shift Positions
    private static final long WORKER_ID_SHIFT = SnowflakeConfig.SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SnowflakeConfig.SEQUENCE_BITS + SnowflakeConfig.WORKER_ID_BITS;

    // State Variables
    private final long workerId;
    private final TimeSource timeSource;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long workerId) {
        this(workerId, new SystemTimeSource());
    }

    public SnowflakeIdGenerator(long workerId, TimeSource timeSource) {
        // Sanity Check
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        this.workerId = workerId;
        this.timeSource = timeSource;
    }

    // Core Generation Method (Synchronized for Thread Safety)
    @Override
    public synchronized long nextId() {
        long currentTimestamp = timeSource.getCurrentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            long offset = lastTimestamp - currentTimestamp;
            // If clock rollback is small (< 5ms), wait it out
            if (offset <= 5) {
                try {
                    wait(offset + 1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                currentTimestamp = timeSource.getCurrentTimeMillis();
                if (currentTimestamp < lastTimestamp) {
                    throw new ClockMovedBackwardsException(
                            String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                                    lastTimestamp - currentTimestamp));
                }
            } else {
                throw new ClockMovedBackwardsException(
                        String.format("Clock moved backwards. Refusing to generate id for %d milliseconds",
                                lastTimestamp - currentTimestamp));
            }
        }

        // Same Millisecond: Increment Sequence
        if (lastTimestamp == currentTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // Sequence Exhaustion
            if (sequence == 0) {
                // Busy wait until next millisecond
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // New Millisecond: Reset Sequence
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // Construct ID via Bitwise OR
        return ((currentTimestamp - SnowflakeConfig.EPOCH) << TIMESTAMP_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = timeSource.getCurrentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = timeSource.getCurrentTimeMillis();
        }
        return timestamp;
    }
}
