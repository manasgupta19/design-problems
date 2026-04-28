package Tier1.ConnectionPool.service;

import Tier1.ConnectionPool.exception.PoolExhaustedException;

public interface ConnectionPool {
    /**
     * Retrieves a connection from the pool.
     * Blocks up to timeoutMs if pool is empty.
     * @throws PoolExhaustedException if timeout is reached.
     */
    MockConnection acquire(long timeoutMs) throws InterruptedException, PoolExhaustedException;

    /**
     * Returns a connection to the pool.
     */
    void release(MockConnection connection);

    /**
     * Closes all connections and shuts down the pool.
     */
    void shutdown();
}


