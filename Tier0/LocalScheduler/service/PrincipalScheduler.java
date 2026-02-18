package Tier0.LocalScheduler.service;

import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import Tier0.LocalScheduler.model.ScheduledTask;

public class PrincipalScheduler implements LocalScheduler {

    // 1. Min-Heap for O(1) retrieval of next task [Source 182]
    private final PriorityQueue<ScheduledTask> queue = new PriorityQueue<>();

    // 2. Concurrency Primitives [Source 955, 960]
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition newEarlierTaskSignal = lock.newCondition();

    // 3. Worker Pool for execution
    private final ExecutorService workerPool = Executors.newFixedThreadPool(10);
    private volatile boolean isRunning = true;

    public PrincipalScheduler() {
        Thread poller = new Thread(this::runLoop);
        poller.setName("Scheduler-Poller");
        poller.start();
    }

    @Override
    public void schedule(Runnable job, long delayMs) {
        long executionTime = System.currentTimeMillis() + delayMs;
        ScheduledTask task = new ScheduledTask(job, executionTime, 0);

        lock.lock(); // Critical Section Start
        try {
            queue.offer(task);
            // Optimization: Only wake up the poller if the new task
            // is the NEW HEAD of the queue (i.e., it's sooner than everything else).
            if (queue.peek() == task) {
                newEarlierTaskSignal.signal();
            }
        } finally {
            lock.unlock(); // Critical Section End
        }
    }

    // The Main Loop (The "Heartbeat")
    private void runLoop() {
        while (isRunning) {
            lock.lock();
            try {
                // 1. Handle Empty Queue
                while (queue.isEmpty()) {
                    newEarlierTaskSignal.await(); // Releases lock, waits for signal [Source 960]
                }

                ScheduledTask head = queue.peek();
                long now = System.currentTimeMillis();
                long delay = head.getExecutionTime() - now;

                if (delay <= 0) {
                    // 2. Task is Due: Extract and Execute
                    ScheduledTask dueTask = queue.poll();

                    // Critical: Execute in Worker Pool, NOT in this loop
                    workerPool.submit(dueTask.getJob());

                    // Handle Recurring Tasks
                    if (dueTask.isRecurring()) {
                        dueTask.reschedule(); // This modifies dueTask.executionTime
                        queue.offer(dueTask);
                    }
                } else {
                    // 3. Task is Future: Wait efficiently
                    // "awaitNanos" automatically releases lock and re-acquires on wake
                    newEarlierTaskSignal.awaitNanos(TimeUnit.MILLISECONDS.toNanos(delay));
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void scheduleAtFixedRate(Runnable task, long initialDelay, long period) {
        long executionTime = System.currentTimeMillis() + initialDelay;
        ScheduledTask t = new ScheduledTask(task, executionTime, period);
        lock.lock();
        try {
            queue.offer(t);
            if (queue.peek() == t) newEarlierTaskSignal.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        isRunning = false;
        workerPool.shutdown();
    }
}

