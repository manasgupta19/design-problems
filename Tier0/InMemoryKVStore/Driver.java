package Tier0.InMemoryKVStore;

import Tier0.InMemoryKVStore.service.ConcurrentLRUCache;

// ---------------------------------------------------------
// 4. DRIVER CLASS
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) throws InterruptedException {
        // Create Cache with capacity 32 (2 items per segment approx)
        ConcurrentLRUCache<String, String> cache = new ConcurrentLRUCache<>(32);

        System.out.println("--- Scenario 1: Basic Put/Get ---");
        cache.put("A", "Alpha");
        System.out.println("Get A: " + cache.get("A")); // Alpha

        System.out.println("\n--- Scenario 2: Atomicity (PutIfAbsent) ---");
        cache.put("B", "Beta");
        String res1 = cache.putIfAbsent("B", "BetaNew");
        System.out.println("PutIfAbsent B: " + res1); // Should return "Beta" (existing)
        System.out.println("Get B: " + cache.get("B")); // Still "Beta"

        String res2 = cache.putIfAbsent("C", "Gamma");
        System.out.println("PutIfAbsent C: " + res2); // Should return null (success)

        System.out.println("\n--- Scenario 3: Concurrency Test ---");
        // Spawn 10 threads hitting different keys
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            final int id = i;
            threads[i] = new Thread(() -> {
                cache.put("Key" + id, "Val" + id);
                System.out.println("Thread " + id + " wrote Key" + id);
            });
            threads[i].start();
        }

        for (Thread t : threads) t.join();
        System.out.println("Final Size: " + cache.size());
    }
}


