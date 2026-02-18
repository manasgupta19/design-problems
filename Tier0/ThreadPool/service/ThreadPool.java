package Tier0.ThreadPool.service;

public interface ThreadPool {
    /**
     * Submits a task for execution.
     * Blocks if the internal queue is full (Backpressure).
     * @throws IllegalStateException if pool is shutting down.
     */
    void submit(Runnable task) throws InterruptedException;

    /**
     * Initiates a graceful shutdown.
     * Previously submitted tasks are executed, but no new tasks accepted.
     */
    void shutdown();

    /**
     * Returns true if all tasks have completed and threads stopped.
     */
    boolean isTerminated();
}


