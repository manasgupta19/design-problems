package Tier0.HashMap;

import Tier0.HashMap.service.MyHashMap;

// ---------------------------------------------------------
// DRIVER CLASS
// ---------------------------------------------------------
public class HashMapDriver {
    public static void main(String[] args) {
        MyHashMap<String, Integer> map = new MyHashMap<>();

        System.out.println("--- Scenario 1: Basic Insert ---");
        map.put("Apple", 100);
        map.put("Banana", 200);
        System.out.println("Get Apple: " + map.get("Apple")); // 100

        System.out.println("\n--- Scenario 2: Collision Handling (Chain) ---");
        // These keys often collide or fall in same bucket depending on capacity
        // We force logic by knowing the map mechanics
        map.put("Aa", 1);
        map.put("BB", 2);
        // "Aa" and "BB" have same hashCode in Java strings (2112)
        System.out.println("Get Aa: " + map.get("Aa"));
        System.out.println("Get BB: " + map.get("BB"));

        System.out.println("\n--- Scenario 3: Update Existing ---");
        map.put("Apple", 500);
        System.out.println("New Apple: " + map.get("Apple")); // 500

        System.out.println("\n--- Scenario 4: Resizing Trigger ---");
        // Default Cap 16, Threshold 12. Let's add 12 more items.
        for(int i=0; i<12; i++) {
            map.put("Key" + i, i);
        }
        System.out.println("Size: " + map.size()); // 16 items
        System.out.println("Capacity (Should be 32): " + map.capacity());

        System.out.println("Get preserved item (Banana): " + map.get("Banana"));
    }
}

