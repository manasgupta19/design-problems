package Tier0.BlockingQueue;

import Tier0.BlockingQueue.service.MyBlockingQueue;

// ---------------------------------------------------------
// DRIVER CLASS
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) {
        // Create a small queue to force blocking quickly
        MyBlockingQueue<Integer> queue = new MyBlockingQueue<>(2);

        // Producer Thread
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    queue.put(i);
                    Thread.sleep(100); // Simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        // Consumer Thread
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    // Simulate slow consumer to force Producer to block
                    Thread.sleep(300);
                    queue.take();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }
}


