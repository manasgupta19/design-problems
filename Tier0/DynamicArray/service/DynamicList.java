package Tier0.DynamicArray.service;

public interface DynamicList<T> {
    // Appends element to the end. Triggers resize if full.
    void add(T element);

    // Returns element at index. O(1).
    T get(int index);

    // Removes element at index, shifts subsequent elements left.
    // Triggers shrink if usage < 25%.
    T removeAt(int index);

    // Returns current number of elements.
    int size();

    // Returns current physical capacity (for testing internals).
    int getCapacity();
}


