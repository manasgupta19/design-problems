package Tier0.GarbageCollector;

import Tier0.GarbageCollector.model.SimulatedObject;
import Tier0.GarbageCollector.service.SimpleVM;

public class GCDriver {
    public static void main(String[] args) {
        // Initialize VM with small size to force GC
        SimpleVM vm = new SimpleVM(5);

        // Scenario 1: Basic Allocation & Linking
        SimulatedObject a = vm.allocate("A");
        SimulatedObject b = vm.allocate("B");
        vm.addRoot(a); // 'a' is on stack
        vm.link(a, b); // A -> B (B is reachable via A)

        // Scenario 2: Cyclic Garbage (The Island of Isolation)
        SimulatedObject c = vm.allocate("C");
        SimulatedObject d = vm.allocate("D");
        vm.link(c, d);
        vm.link(d, c); // C <-> D cycle
        // Note: Neither C nor D are added to Roots. They are garbage immediately.

        vm.printHeap(); // Heap: [A, B, C, D], Size: 4/5

        // Scenario 3: Allocation triggering GC
        System.out.println(">> Attempting to allocate E (Heap near capacity)...");
        vm.allocate("E"); // Takes heap to 5/5

        System.out.println(">> Attempting to allocate F (Will trigger GC)...");
        // This should trigger GC.
        // Expected: A and B survive. C and D die.
        // Heap becomes [A, B, E], freeing space for F.
        vm.allocate("F");

        vm.printHeap();
    }
}

