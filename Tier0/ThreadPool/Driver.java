package Tier0.ThreadPool;

import Tier0.ThreadPool.service.SimpleThreadPool;

// DRIVER CLASS
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) throws InterruptedException {
        // Pool: 2 Threads, Queue Capacity 3
        SimpleThreadPool pool = new SimpleThreadPool(2, 3);

        // Scenario 1: Submit tasks (Happy Path)
        System.out.println("--- Submitting 5 tasks ---");
        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            pool.submit(() -> {
                System.out.println(Thread.currentThread().getName() + " executing Task " + taskId);
                try { Thread.sleep(100); } catch (InterruptedException e) {} // Simulate work
            });
        }

        // Scenario 2: Backpressure (Queue Full)
        // Threads (2) are busy. Queue (3) is full. Next submit blocks.
        // We simulate this by having a thread submit a 6th task.
        new Thread(() -> {
            try {
                System.out.println("Submitting Task 6 (Will Block)...");
                pool.submit(() -> System.out.println("Task 6 Executed"));
                System.out.println("Task 6 Submitted.");
            } catch (InterruptedException e) {}
        }).start();

        Thread.sleep(500); // Let tasks process

        // Scenario 3: Shutdown
        System.out.println("\n--- Initiating Shutdown ---");
        pool.shutdown();

        // Scenario 4: Submit after shutdown (Failure Case)
        try {
            pool.submit(() -> System.out.println("Should not run"));
        } catch (IllegalStateException e) {
            System.out.println("Expected Error: " + e.getMessage());
        }

        // Wait for workers to finish remaining tasks
        while (!pool.isTerminated()) {
            Thread.sleep(100);
        }
        System.out.println("All threads terminated.");
    }
}


