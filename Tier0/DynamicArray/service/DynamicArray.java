package Tier0.DynamicArray.service;

// 1. Generic Class Definition
public class DynamicArray<T> implements DynamicList<T> {
    private Object[] data;
    private int size;
    private int capacity;

    // Default capacity
    private static final int INITIAL_CAPACITY = 2;

    public DynamicArray() {
        this.capacity = INITIAL_CAPACITY;
        // Java Generic Array Creation Hack
        this.data = new Object[capacity];
        this.size = 0;
    }

    @Override
    public void add(T element) {
        // 1. Check if resize is needed
        if (size == capacity) {
            resize(capacity * 2); // Geometric Growth (2x)
        }
        // 2. Insert and increment
        data[size++] = element;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(int index) {
        checkIndex(index);
        return (T) data[index];
    }

    @SuppressWarnings("unchecked")
    @Override
    public T removeAt(int index) {
        checkIndex(index);

        T removedElement = (T) data[index];

        // 3. Shift elements to the left: O(N) operation
        // Example: [A, B, C, D] remove(1) -> [A, C, D, null]
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }

        // 4. Memory Leak Prevention: Nullify the last active slot
        // Without this, the GC cannot reclaim the object.
        data[size - 1] = null;
        size--;

        // 5. Smart Shrinking (Hysteresis)
        // Only shrink if we are 1/4th full, but don't drop below min capacity
        if (size > 0 && size == capacity / 4) {
            resize(Math.max(INITIAL_CAPACITY, capacity / 2));
        }

        return removedElement;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    // ---------------------------------------------------------
    // INTERNAL HELPERS
    // ---------------------------------------------------------

    // The Core Resizing Logic: O(N)
    private void resize(int newCapacity) {
        System.out.println(">> Resizing: " + capacity + " -> " + newCapacity);
        Object[] newData = new Object[newCapacity];

        // Copy existing elements
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }

        this.data = newData;
        this.capacity = newCapacity;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}
