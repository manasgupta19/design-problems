package Tier0.ElevatorSystem.strategy;

import java.util.List;

import Tier0.ElevatorSystem.model.Direction;
import Tier0.ElevatorSystem.model.Elevator;

public class LookDispatcher implements Dispatcher {
    @Override
    public Elevator selectElevator(int requestFloor, Direction requestDir, List<Elevator> elevators) {
        Elevator bestElevator = null;
        int minCost = Integer.MAX_VALUE;

        for (Elevator e : elevators) {
            int cost = calculateCost(e, requestFloor, requestDir);
            if (cost < minCost) {
                minCost = cost;
                bestElevator = e;
            }
        }
        return bestElevator;
    }

    // Heuristic Cost Function
    private int calculateCost(Elevator e, int reqFloor, Direction reqDir) {
        if (e.direction == Direction.IDLE)
            return Math.abs(e.currentFloor - reqFloor); // Closest idle wins

        // If moving in same direction and hasn't passed the floor yet -> Good Candidate
        if (e.direction == reqDir) {
            if (e.direction == Direction.UP && reqFloor >= e.currentFloor)
                return reqFloor - e.currentFloor;
            if (e.direction == Direction.DOWN && reqFloor <= e.currentFloor)
                return e.currentFloor - reqFloor;
        }

        // Penalty for moving away or wrong direction
        return Integer.MAX_VALUE;
    }
}

