package Tier0.ElevatorSystem.model;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Elevator {
    public int id;
    public int currentFloor;
    public Direction direction;
    public State state;

    // LOOK Algorithm: Two queues for efficient traversal
    // Up stops: 3, 5, 7 (processed in ascending order)
    public PriorityQueue<Integer> upStops;
    // Down stops: 9, 4, 2 (processed in descending order)
    public PriorityQueue<Integer> downStops;

    private final Lock lock = new ReentrantLock();

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.state = State.IDLE;
        this.upStops = new PriorityQueue<>();
        this.downStops = new PriorityQueue<>(Collections.reverseOrder());
    }

    public void addStop(int floor) {
        lock.lock(); // Thread safety for concurrent requests [Source 1276, 1288]
        try {
            if (floor > currentFloor) upStops.add(floor);
            else if (floor < currentFloor) downStops.add(floor);

            // If IDLE, wake up and set initial direction
            if (direction == Direction.IDLE) {
                direction = (floor > currentFloor) ? Direction.UP : Direction.DOWN;
                state = State.MOVING;
            }
        } finally {
            lock.unlock();
        }
    }

    // The Logic Engine: Executes one step of movement
    public void move() {
        lock.lock();
        try {
            if (direction == Direction.IDLE) return;

            // 1. Determine Next Stop
            PriorityQueue<Integer> currentQueue = (direction == Direction.UP) ? upStops : downStops;

            if (currentQueue.isEmpty()) {
                // Change direction if needed
                direction = (direction == Direction.UP) ? Direction.DOWN : Direction.UP;
                currentQueue = (direction == Direction.UP) ? upStops : downStops;

                // If both empty, go IDLE
                if (currentQueue.isEmpty()) {
                    direction = Direction.IDLE;
                    state = State.IDLE;
                    return;
                }
            }

            int nextStop = currentQueue.peek();

            // 2. Move Logic
            if (currentFloor == nextStop) {
                // Open Doors
                System.out.println("Elevator " + id + " OPEN at Floor " + currentFloor);
                currentQueue.poll(); // Remove stop
                // Simulate door open wait time here
            } else {
                // Move one floor
                if (direction == Direction.UP) currentFloor++;
                else currentFloor--;
                state = State.MOVING;
                System.out.println("Elevator " + id + " moving " + direction + " to " + currentFloor);
            }
        } finally {
            lock.unlock();
        }
    }
}

