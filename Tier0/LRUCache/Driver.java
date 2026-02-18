package Tier0.LRUCache;

import Tier0.LRUCache.service.LRUCache;


// ---------------------------------------------------------
// 4. DRIVER CLASS
// ---------------------------------------------------------
class Driver {
    public static void main(String[] args) {
        System.out.println("--- Scenario 1: Basic Put/Get ---");
        LRUCache<Integer, String> cache = new LRUCache<>(3);

        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        cache.printCache(); // MRU -> [3:C] [2:B] [1:A] <- LRU

        System.out.println("\n--- Scenario 2: Access Update (Get) ---");
        // Access '1', making it MRU
        System.out.println("Get(1): " + cache.get(1));
        cache.printCache(); // MRU -> [1:A] [3:C] [2:B] <- LRU

        System.out.println("\n--- Scenario 3: Eviction (Put) ---");
        // Add '4'. Capacity is 3. LRU is '2'. '2' should be evicted.
        cache.put(4, "D");
        cache.printCache(); // MRU -> [4:D] [1:A] [3:C] <- LRU
        // Verify 2 is gone
        System.out.println("Get(2): " + cache.get(2)); // null

        System.out.println("\n--- Scenario 4: Update Existing Value ---");
        // Update '3'. Should move to MRU and change value.
        cache.put(3, "C-Updated");
        cache.printCache(); // MRU -> [3:C-Updated] [4:D] [1:A] <- LRU
    }
}


