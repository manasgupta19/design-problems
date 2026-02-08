import java.time.Instant;

// 1. Custom Exception for Fail-Closed Scenario
class ClockMovedBackwardsException extends RuntimeException {
    public ClockMovedBackwardsException(String message) { super(message); }
}

public class SnowflakeIdGenerator {
    // 2. Configuration Constants (The Physics of the System)
    private static final long EPOCH = 1704067200000L; // Custom Epoch (Jan 1, 2024)

    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;

    // Max values to prevent overflow
    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1; // 1023
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;   // 4095

    // Bit Shift Positions
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS; // 12
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 22

    // 3. State Variables
    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long workerId) {
        // Sanity Check: Worker ID must fit in 10 bits
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker Id can't be greater than %d or less than 0", MAX_WORKER_ID));
        }
        this.workerId = workerId;
    }

    // 4. Core Generation Method (Synchronized for Thread Safety)
    public synchronized long nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // FAIL CLOSED: Clock moved backwards [Source 390]
        if (currentTimestamp < lastTimestamp) {
            throw new ClockMovedBackwardsException(
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - currentTimestamp)
            );
        }

        // Same Millisecond: Increment Sequence
        if (lastTimestamp == currentTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;

            // Sequence Exhaustion: We hit 4096 IDs in 1ms
            if (sequence == 0) {
                // Busy wait until next millisecond
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // New Millisecond: Reset Sequence
            sequence = 0L;
        }

        // Update state
        lastTimestamp = currentTimestamp;

        // 5. Construct ID via Bitwise OR
        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) |
               (workerId << WORKER_ID_SHIFT) |
               sequence;
    }

    // Busy-wait loop for sequence exhaustion
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}

// ---------------------------------------------------------
// Driver Class to Simulate Scenarios
// ---------------------------------------------------------
class Driver {
    public static void main(String[] args) {
        // Initialize Generator with Worker ID 1
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);

        System.out.println("--- Scenario 1: Single ID ---");
        long id = generator.nextId();
        System.out.println("Generated ID: " + id);
        printBinary(id);

        System.out.println("\n--- Scenario 2: Burst Mode (Same Millisecond) ---");
        // Generate 3 IDs quickly
        for(int i=0; i<3; i++) {
            long burstId = generator.nextId();
            System.out.println("Burst ID: " + burstId + " | Seq: " + (burstId & 0xFFF)); // Mask to show sequence
        }
    }

    private static void printBinary(long id) {
        System.out.println("Binary: " + String.format("%64s", Long.toBinaryString(id)).replace(' ', '0'));
    }
}

