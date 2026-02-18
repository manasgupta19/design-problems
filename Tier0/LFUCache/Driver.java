package Tier0.LFUCache;

import Tier0.LFUCache.service.LFUCache;

// ---------------------------------------------------------
// 4. DRIVER CLASS
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) {
        System.out.println("--- Scenario 1: Filling Cache ---");
        LFUCache cache = new LFUCache(2);

        cache.put(1, 1); // Freq: 1
        cache.put(2, 2); // Freq: 1. MinFreq: 1
        cache.printState();
        // State: {1=1, 2=1}, MinFreq=1

        System.out.println("\n--- Scenario 2: Frequency Promotion ---");
        cache.get(1); // 1 promoted to Freq 2. MinFreq stays 1 (2 is still at 1)
        cache.printState();
        // State: Freq1:, Freq2:. MinFreq=1

        System.out.println("\n--- Scenario 3: Eviction (LFU) ---");
        cache.put(3, 3); // Must evict. MinFreq is 1. List has. Evict 2.
        // Insert 3 at Freq 1.
        cache.printState();
        // State: Freq1:, Freq2:. MinFreq=1. Node 2 gone.

        System.out.println("\n--- Scenario 4: MinFreq Update ---");
        cache.get(3); // 3 moves to Freq 2. Freq 1 is empty. MinFreq becomes 2.
        cache.printState();

        cache.put(4, 4); // Full. MinFreq is 2. Tie-breaker LRU needed.
        // Both 1 and 3 are freq 2. 1 was accessed earliest?
        // No, in Freq2 list: ->. 3 was added most recently. 1 is LRU.
        // Evicts 1.
        // Sets MinFreq = 1. Inserts 4 at Freq 1.
        cache.printState();
    }
}


