package Tier0.GarbageCollector.model;

import java.util.ArrayList;
import java.util.List;

// 1. The Object Representation (Nodes in the Graph)
public class SimulatedObject {
    String name;
    boolean marked = false; // The GC Flag
    List<SimulatedObject> references = new ArrayList<>();

    public SimulatedObject(String name) {
        this.name = name;
    }

    @Override
    public String toString() { return name; }

    public void resetMark() { this.marked = false; }

    public void mark() { this.marked = true; }

    public List<SimulatedObject> getReferences() { return references; }

    public boolean isMarked() { return marked; }
}
