package Tier0.BlockingQueue.service;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

// 1. Generic Class Definition
public class MyBlockingQueue<T> {

    // Core Data Structure: Circular Array
    private final Object[] items;
    private int head;
    private int tail;
    private int count;

    // Concurrency Primitives [Source 877]
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    public MyBlockingQueue(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException();
        this.items = new Object[capacity];
        this.head = 0;
        this.tail = 0;
        this.count = 0;

        // Fair lock ensures FIFO ordering of waiting threads
        this.lock = new ReentrantLock(true);
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    // ---------------------------------------------------------
    // PRODUCER: PUT (Blocking)
    // ---------------------------------------------------------
    public void put(T item) throws InterruptedException {
        lock.lock(); // Enter Critical Section
        try {
            // Guarded Block: Wait while queue is full
            while (count == items.length) {
                System.out.println(Thread.currentThread().getName() + " is waiting (Queue Full)");
                notFull.await(); // Atomically releases lock & sleeps
            }

            // Critical Section: Modification
            items[tail] = item;

            // Circular increment: 0 -> 1 -> ... -> (cap-1) -> 0
            tail = (tail + 1) % items.length;
            count++;

            System.out.println(Thread.currentThread().getName() + " produced: " + item);

            // Signal logic: Wake up a consumer
            notEmpty.signal();

        } finally {
            lock.unlock(); // Ensure lock release even if exception occurs
        }
    }

    // ---------------------------------------------------------
    // CONSUMER: TAKE (Blocking)
    // ---------------------------------------------------------
    public T take() throws InterruptedException {
        lock.lock(); // Enter Critical Section
        try {
            // Guarded Block: Wait while queue is empty
            while (count == 0) {
                System.out.println(Thread.currentThread().getName() + " is waiting (Queue Empty)");
                notEmpty.await(); // Atomically releases lock & sleeps
            }

            // Critical Section: Modification
            @SuppressWarnings("unchecked")
            T item = (T) items[head];
            items[head] = null; // Help GC [Source 216]

            // Circular increment
            head = (head + 1) % items.length;
            count--;

            System.out.println(Thread.currentThread().getName() + " consumed: " + item);

            // Signal logic: Wake up a producer
            notFull.signal();

            return item;

        } finally {
            lock.unlock();
        }
    }
}
