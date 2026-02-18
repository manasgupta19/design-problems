package Tier0.GarbageCollector.service;

import Tier0.GarbageCollector.model.SimulatedObject;

public interface GarbageCollector {
    // Allocates a new object. Triggers GC if max memory is exceeded.
    SimulatedObject allocate(String name);

    // Simulates a variable going onto the stack (Root)
    void addRoot(SimulatedObject obj);

    // Simulates a variable popping off the stack
    void removeRoot(SimulatedObject obj);

    // Creates a reference: parent.field = child
    void link(SimulatedObject parent, SimulatedObject child);

    // Manually trigger the Mark-and-Sweep process
    void collect();
}


