package Tier0.ConnectionPool;

import Tier0.ConnectionPool.exception.PoolExhaustedException;
import Tier0.ConnectionPool.service.MockConnection;
import Tier0.ConnectionPool.service.SimpleConnectionPool;

// ---------------------------------------------------------
// 3. DRIVER CLASS (Simulation)
// ---------------------------------------------------------
public class PoolDriver {
    public static void main(String[] args) throws InterruptedException, PoolExhaustedException {
        // Setup: Pool of size 2
        SimpleConnectionPool pool = new SimpleConnectionPool(2);

        System.out.println("--- Scenario 1: Happy Path ---");
        try {
            MockConnection c1 = pool.acquire(100);
            System.out.println("Acquired: " + c1);
            pool.release(c1);
            System.out.println("Released: " + c1);
        } catch (PoolExhaustedException | InterruptedException e) {}

        System.out.println("\n--- Scenario 2: Exhaustion & Timeout ---");
        Thread t1 = new Thread(() -> borrowAndHold(pool, 1000, "Thread-A"));
        Thread t2 = new Thread(() -> borrowAndHold(pool, 1000, "Thread-B"));

        t1.start();
        t2.start();
        Thread.sleep(100); // Ensure A and B get the 2 connections

        // Pool is now empty. Thread C tries to acquire.
        try {
            System.out.println("Thread-C attempting acquire (200ms timeout)...");
            pool.acquire(200);
        } catch (PoolExhaustedException e) {
            System.out.println("Thread-C Failed: " + e.getMessage()); // Should print this
        }

        t1.join(); t2.join();

        System.out.println("\n--- Scenario 3: Self-Healing (Bad Connection) ---");
        MockConnection c3 = pool.acquire(1000);
        c3.simulateNetworkFailure(); // Break it
        pool.release(c3); // Return broken connection
        System.out.println("Returned broken connection: " + c3);

        // Next acquire should detect broken, close it, and give a new one
        MockConnection c4 = pool.acquire(1000);
        System.out.println("Acquired new valid connection: " + c4);

        pool.shutdown();
    }

    private static void borrowAndHold(SimpleConnectionPool pool, int sleepMs, String name) {
        try {
            MockConnection c = pool.acquire(500);
            System.out.println(name + " acquired " + c);
            Thread.sleep(sleepMs);
            pool.release(c);
            System.out.println(name + " released " + c);
        } catch (PoolExhaustedException | InterruptedException e) {
            System.out.println(name + " error: " + e);
        }
    }
}


