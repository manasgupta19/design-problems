package Tier0.ThreadPool.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

// 1. The Custom Thread Pool
public class SimpleThreadPool implements ThreadPool {

    // Core Components
    private final BlockingQueue<Runnable> taskQueue;
    private final List<Worker> workers;

    // State Management
    private final AtomicBoolean isShutdown;

    public SimpleThreadPool(int numThreads, int queueCapacity) {
        this.taskQueue = new LinkedBlockingQueue<>(queueCapacity);
        this.workers = new ArrayList<>(numThreads);
        this.isShutdown = new AtomicBoolean(false);

        // 2. Initialize and Start Workers
        for (int i = 0; i < numThreads; i++) {
            Worker worker = new Worker("Worker-" + i);
            workers.add(worker);
            worker.start();
        }
    }

    @Override
    public void submit(Runnable task) throws InterruptedException {
        if (isShutdown.get()) {
            throw new IllegalStateException("Pool is shut down. Cannot accept new tasks.");
        }
        // Blocks if queue is full (Backpressure) [Source 1230]
        taskQueue.put(task);
    }

    @Override
    public void shutdown() {
        // 3. Signal State Change
        if (isShutdown.compareAndSet(false, true)) {
            System.out.println("[Pool] Shutting down...");

            // 4. Interrupt Idle Workers
            // If a worker is blocked on queue.take(), this wakes it up
            for (Worker worker : workers) {
                worker.interrupt();
            }
        }
    }

    @Override
    public boolean isTerminated() {
        // Check if all threads are dead
        for (Worker w : workers) {
            if (w.isAlive()) return false;
        }
        return true;
    }

    // ---------------------------------------------------------
    // INTERNAL WORKER CLASS
    // ---------------------------------------------------------
    private class Worker extends Thread {

        public Worker(String name) {
            super(name);
        }

        @Override
        public void run() {
            while (true) {
                try {
                    // 5. The Critical Loop
                    // If shutdown is true AND queue is empty, we stop.
                    // We check this BEFORE taking to handle the case where we were interrupted
                    // but the queue was drained.
                    if (isShutdown.get() && taskQueue.isEmpty()) {
                        break;
                    }

                    // Attempt to fetch task. Blocks if empty.
                    Runnable task = taskQueue.take();

                    // Execute
                    task.run();

                } catch (InterruptedException e) {
                    // 6. Handle Interruption (Shutdown signal)
                    // The shutdown() method triggered this.
                    // Loop back to the top. The 'if' condition will catch
                    // (isShutdown && queue.isEmpty()) and exit.
                } catch (Exception e) {
                    // 7. Robustness: Isolation [Source 916]
                    // If the USER'S code throws an exception, catch it here.
                    // Do NOT let the Worker thread die.
                    System.err.println(Thread.currentThread().getName() + " encountered error: " + e.getMessage());
                }
            }
            System.out.println(getName() + " stopped.");
        }
    }
}