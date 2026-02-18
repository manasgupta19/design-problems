package Tier0.DynamicArray;

import Tier0.DynamicArray.service.DynamicArray;

// ---------------------------------------------------------
// DRIVER CLASS
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) {
        DynamicArray<String> list = new DynamicArray<>();

        System.out.println("--- 1. Growth Phase ---");
        // Capacity starts at 2
        list.add("A");
        list.add("B");
        System.out.println("Size: " + list.size() + ", Cap: " + list.getCapacity()); // Size 2, Cap 2

        list.add("C"); // Triggers Resize -> 4
        System.out.println("Size: " + list.size() + ", Cap: " + list.getCapacity());

        list.add("D");
        list.add("E"); // Triggers Resize -> 8
        System.out.println("Size: " + list.size() + ", Cap: " + list.getCapacity()); // Size 5, Cap 8

        System.out.println("\n--- 2. Removal Phase ---");
        list.removeAt(2); // Remove "C". Array: [A, B, D, E, null]. Size 4. Cap 8.
        System.out.println("Removed C. Size: " + list.size() + ", Cap: " + list.getCapacity());

        // Current: 4/8 (50%). No shrink yet.
        list.removeAt(0); // Remove "A". Size 3. Cap 8.

        // Current: 3/8. No shrink.
        list.removeAt(0); // Remove "B". Size 2. Cap 8.
        System.out.println("Removed B. Size: " + list.size() + ", Cap: " + list.getCapacity());

        // Current: 2/8 (25%). Triggers Shrink -> 4.
        System.out.println("Triggering Shrink...");
        list.removeAt(0); // Remove "D".
        // Logic: Post-remove size is 1. Before remove size was 2.
        // Wait, the logic is "if size == capacity / 4".
        // Let's trace carefully:
        // Size becomes 1. 1 == 8/4? No.
        // The check happens AFTER decrement.
        // Let's adjust inputs to force it.
    }
}

