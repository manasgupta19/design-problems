package Tier0.ConnectionPool.service;

import Tier0.ConnectionPool.exception.PoolExhaustedException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

// 2. The Implementation
public class SimpleConnectionPool implements ConnectionPool {

    // Core state: Thread-safe queue for idle connections [Source 186]
    private final BlockingQueue<MockConnection> pool;
    private final AtomicBoolean isShutdown;

    public SimpleConnectionPool(int maxCapacity) {
        this.pool = new ArrayBlockingQueue<>(maxCapacity);
        this.isShutdown = new AtomicBoolean(false);

        // Eager Initialization: Fill the pool
        for (int i = 0; i < maxCapacity; i++) {
            pool.offer(createNewConnection(i));
        }
        System.out.println("Pool initialized with " + maxCapacity + " connections.");
    }

    private MockConnection createNewConnection(int id) {
        // In real world: DriverManager.getConnection(...) [Source 1128]
        return new MockConnection(id);
    }

    @Override
    public MockConnection acquire(long timeoutMs) throws InterruptedException, PoolExhaustedException {
        if (isShutdown.get()) {
            throw new IllegalStateException("Pool is shutting down");
        }

        // 1. Attempt to borrow with timeout (Backpressure)
        MockConnection conn = pool.poll(timeoutMs, TimeUnit.MILLISECONDS);

        if (conn == null) {
            throw new PoolExhaustedException("Timeout waiting for connection");
        }

        // 2. Test-On-Borrow (Health Check)
        if (!conn.isValid()) {
            System.out.println("Detected stale connection " + conn + ". Replacing...");
            conn.close(); // Clean up old resource
            // Create new replacement synchronously to maintain pool size
            // Note: In production, we might verify this creation succeeds too
            conn = createNewConnection(conn.hashCode());
        }

        return conn;
    }

    @Override
    public void release(MockConnection connection) {
        if (isShutdown.get()) {
            connection.close();
            return;
        }

        if (connection != null) {
            // 3. Return to pool
            // offer() returns false if queue is full (protection against over-returning)
            boolean added = pool.offer(connection);
            if (!added) {
                // Should implies a bug in client code (returning duplicate/extra connections)
                // or pool resizing logic. Safe default: close it.
                System.out.println("Pool full. Discarding extra connection: " + connection);
                connection.close();
            }
        }
    }

    @Override
    public void shutdown() {
        if (isShutdown.compareAndSet(false, true)) {
            System.out.println("Shutting down pool...");
            // Drain and close all idle connections
            while (!pool.isEmpty()) {
                MockConnection conn = pool.poll();
                if (conn != null) conn.close();
            }
        }
    }

    // For testing verification
    public int getAvailableCount() {
        return pool.size();
    }
}
