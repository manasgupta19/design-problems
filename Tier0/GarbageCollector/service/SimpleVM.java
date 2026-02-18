package Tier0.GarbageCollector.service;

import Tier0.GarbageCollector.model.SimulatedObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SimpleVM implements GarbageCollector {
      // 2. VM State
    private final List<SimulatedObject> heap = new ArrayList<>(); // All allocated memory
    private final Set<SimulatedObject> roots = new HashSet<>();   // The Stack
    private final int MAX_OBJECTS;

    public SimpleVM(int maxObjects) {
        this.MAX_OBJECTS = maxObjects;
    }

    // ---------------------------------------------------------
    // API IMPLEMENTATION
    // ---------------------------------------------------------

    @Override
    public SimulatedObject allocate(String name) {
        // Trigger GC if heap is full (Stop-the-world)
        if (heap.size() >= MAX_OBJECTS) {
            System.out.println("[Allocation Failed] Heap full. Triggering GC...");
            collect();

            // If still full after GC, throw OutOfMemoryError
            if (heap.size() >= MAX_OBJECTS) {
                throw new RuntimeException("OutOfMemoryError: Could not allocate " + name);
            }
        }

        SimulatedObject obj = new SimulatedObject(name);
        heap.add(obj);
        System.out.println("Allocated: " + obj);
        return obj;
    }

    @Override
    public void addRoot(SimulatedObject obj) {
        roots.add(obj);
        System.out.println("Added Root: " + obj);
    }

    @Override
    public void removeRoot(SimulatedObject obj) {
        roots.remove(obj);
        System.out.println("Removed Root: " + obj);
    }

    @Override
    public void link(SimulatedObject parent, SimulatedObject child) {
        parent.getReferences().add(child);
        System.out.println("Linked: " + parent + " -> " + child);
    }

    // ---------------------------------------------------------
    // CORE GC LOGIC (Mark-and-Sweep)
    // ---------------------------------------------------------

    @Override
    public void collect() {
        System.out.println("\n--- Starting GC ---");
        int initialSize = heap.size();

        // Step 1: MARK
        // Start trace from all active roots
        for (SimulatedObject root : roots) {
            markRecursively(root);
        }

        // Step 2: SWEEP
        // Identify garbage and survive logic
        Iterator<SimulatedObject> iterator = heap.iterator();
        while (iterator.hasNext()) {
            SimulatedObject obj = iterator.next();
            if (obj.isMarked()) {
                // It's alive. Reset flag for next GC cycle.
                obj.resetMark();
            } else {
                // It's garbage. Remove from heap.
                System.out.println("Collecting Garbage: " + obj);
                iterator.remove();
            }
        }

        int freed = initialSize - heap.size();
        System.out.println("--- GC Finished. Freed: " + freed + " objects ---\n");
    }

    // Recursive DFS for Graph Traversal [Source 308]
    private void markRecursively(SimulatedObject obj) {
        // Base case: If already marked, stop (prevents infinite loops in cycles)
        if (obj == null || obj.isMarked()) {
            return;
        }

        System.out.println("Marking: " + obj);
        obj.mark();

        for (SimulatedObject child : obj.getReferences()) {
            markRecursively(child);
        }
    }

    // Debug helper
    public void printHeap() {
        System.out.println("Current Heap: " + heap);
    }
}
