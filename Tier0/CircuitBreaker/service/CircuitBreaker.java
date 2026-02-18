package Tier0.CircuitBreaker.service;

import java.util.function.Supplier;

import Tier0.CircuitBreaker.exception.CircuitBreakerOpenException;

// ---------------------------------------------------------
// 2. THE CIRCUIT BREAKER
// ---------------------------------------------------------
public class CircuitBreaker {

    // Configuration
    private final int failureThreshold; // e.g., 50%
    private final int windowSize;       // e.g., 10 requests
    private final long waitDurationMs;  // e.g., 1000ms

    // State
    private volatile State state = State.CLOSED;
    private long openStartTime = 0;

    // Sliding Window (Circular Buffer)
    private final boolean[] window;
    private int head = 0;
    private int currentWindowSize = 0;

    // Statistics for O(1) reads
    private int failureCount = 0;

    public CircuitBreaker(int failureThresholdRate, int windowSize, long waitDurationMs) {
        this.failureThreshold = failureThresholdRate;
        this.windowSize = windowSize;
        this.waitDurationMs = waitDurationMs;
        this.window = new boolean[windowSize]; // true = success, false = failure
    }

    public <T> T execute(Supplier<T> operation) throws Exception {
        // 1. Check State (Pre-Execution)
        checkState();

        T result;
        try {
            // 2. Execute Operation
            result = operation.get();
            // 3. Record Success
            recordResult(true);
            return result;
        } catch (Exception e) {
            // 3. Record Failure
            recordResult(false);
            throw e; // Re-throw to caller
        }
    }

    // Critical Section: Checking if we can proceed
    private void checkState() {
        if (state == State.OPEN) {
            // Check if timeout expired to transition to HALF_OPEN
            if (System.currentTimeMillis() - openStartTime > waitDurationMs) {
                synchronized (this) {
                    // Double-checked locking to ensure only 1 thread moves to HALF_OPEN
                    if (state == State.OPEN && (System.currentTimeMillis() - openStartTime > waitDurationMs)) {
                        state = State.HALF_OPEN;
                        System.out.println(">>> Circuit transitioning to HALF_OPEN (Probe Phase)");
                        return; // Allow this request to proceed as the "Probe"
                    }
                }
            }
            // Still OPEN and timeout hasn't passed
            throw new CircuitBreakerOpenException("Circuit is OPEN. Fail fast.");
        }

        // If HALF_OPEN, we generally only allow 1 probe.
        // In this simplified impl, the "transitioner" thread becomes the probe.
        // Real libs use a Semaphore/Atomic to limit concurrent probes.
    }

    // Critical Section: Updating Window and State
    private synchronized void recordResult(boolean success) {
        if (state == State.HALF_OPEN) {
            if (success) {
                reset(); // Probe succeeded -> CLOSED
                System.out.println(">>> Probe Successful. Circuit CLOSED.");
            } else {
                trip(); // Probe failed -> OPEN
                System.out.println(">>> Probe Failed. Circuit Re-OPENED.");
            }
            return;
        }

        // Logic for CLOSED state: Update Sliding Window

        // 1. Remove effect of the overwritten item (if buffer is full)
        if (currentWindowSize == windowSize) {
            boolean oldResult = window[head];
            if (!oldResult) failureCount--; // If old was failure, decrement count
        } else {
            currentWindowSize++;
        }

        // 2. Add new result
        window[head] = success;
        if (!success) failureCount++;

        // 3. Move pointer
        head = (head + 1) % windowSize;

        // 4. Check Threshold
        if (state == State.CLOSED && currentWindowSize == windowSize) {
            double failureRate = (double) failureCount / windowSize * 100;
            if (failureRate >= failureThreshold) {
                trip(); // Threshold exceeded -> OPEN
                System.out.println(">>> Failure Rate " + failureRate + "%. Circuit TRIPPED to OPEN.");
            }
        }
    }

    private void trip() {
        state = State.OPEN;
        openStartTime = System.currentTimeMillis();
    }

    private void reset() {
        state = State.CLOSED;
        // Reset window stats
        failureCount = 0;
        currentWindowSize = 0;
        head = 0;
        // Note: In production, you might keep the window to be conservative
    }
}