package Tier0.ElevatorSystem.service;

import java.util.ArrayList;
import java.util.List;

import Tier0.ElevatorSystem.model.Direction;
import Tier0.ElevatorSystem.model.Elevator;
import Tier0.ElevatorSystem.strategy.Dispatcher;
import Tier0.ElevatorSystem.strategy.LookDispatcher;

// 4. The Controller: Elevator System
public class BuildingController {
    private final List<Elevator> elevators;
    private final Dispatcher dispatcher;

    public BuildingController(int numElevators) {
        this.elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(i));
        }
        this.dispatcher = new LookDispatcher();
    }

    // API 1: Request from Hall
    public void requestElevator(int floor, Direction direction) {
        Elevator selected = dispatcher.selectElevator(floor, direction, elevators);
        // Note: In a real system, if MAX_VALUE returned, we queue request.
        // Here we default to Elevator 0 for simplicity if all busy.
        if (selected == null) selected = elevators.get(0);

        System.out.println("Dispatching Elevator " + selected.id + " to Floor " + floor);
        selected.addStop(floor);
    }

    // API 2: Request from Car
    public void selectFloor(int elevatorId, int floor) {
        elevators.get(elevatorId).addStop(floor);
    }

    // Simulation Step
    public void step() {
        for (Elevator e : elevators) e.move();
    }
}

