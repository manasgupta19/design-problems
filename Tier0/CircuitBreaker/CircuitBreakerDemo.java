package Tier0.CircuitBreaker;

import Tier0.CircuitBreaker.exception.CircuitBreakerOpenException;
import Tier0.CircuitBreaker.service.CircuitBreaker;

// ---------------------------------------------------------
// DRIVER CLASS (Simulation)
// ---------------------------------------------------------
public class CircuitBreakerDemo {
    public static void main(String[] args) throws InterruptedException {
        // Config: Trip if 50% failures in last 4 requests. Retry after 1 sec.
        CircuitBreaker cb = new CircuitBreaker(50, 4, 1000);

        // Scenario 1: Happy Path
        System.out.println("--- Scenario 1: 3 Successes ---");
        performRequest(cb, true); // S
        performRequest(cb, true); // S
        performRequest(cb, true); // S
        // Window: [S, S, S, _]. Fail Rate: 0%

        // Scenario 2: Failures triggering Open
        System.out.println("\n--- Scenario 2: 2 Failures (Trip) ---");
        performRequest(cb, false); // F. Window: [S, S, S, F]. Rate: 25%
        performRequest(cb, false); // F. Window [S, S, F, F] (Wrap). Rate: 50% -> TRIP!

        // Scenario 3: Fail Fast
        System.out.println("\n--- Scenario 3: Fail Fast (Open) ---");
        performRequest(cb, true); // Should be rejected immediately

        // Scenario 4: Recovery
        System.out.println("\n--- Scenario 4: Recovery (Half-Open) ---");
        Thread.sleep(1100); // Wait for timeout

        // First request is the Probe -> Succeeds
        performRequest(cb, true);

        // Next request should be normal
        performRequest(cb, true);
    }

    private static void performRequest(CircuitBreaker cb, boolean shouldSucceed) {
        try {
            cb.execute(() -> {
                if (!shouldSucceed) throw new RuntimeException("Service Failed");
                return "Success";
            });
            System.out.println("Request: OK");
        } catch (CircuitBreakerOpenException e) {
            System.out.println("Request: BLOCKED (Circuit Open)");
        } catch (Exception e) {
            System.out.println("Request: FAILED (Service Error)");
        }
    }
}

