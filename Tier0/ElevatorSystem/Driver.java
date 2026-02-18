package Tier0.ElevatorSystem;

import Tier0.ElevatorSystem.model.Direction;
import Tier0.ElevatorSystem.service.BuildingController;

// 5. Driver / Test Scenarios
public class Driver {
    public static void main(String[] args) {
        BuildingController building = new BuildingController(2); // 2 Elevators

        System.out.println("--- Scenario 1: Idle Assign ---");
        // Elevator 0 is at 0. Request at 5 UP. Should assign Elevator 0.
        building.requestElevator(5, Direction.UP);

        // Simulation steps to move elevator
        for(int i=0; i<6; i++) building.step();

        System.out.println("\n--- Scenario 2: En-route Pickup (LOOK Algorithm) ---");
        // Elevator 0 is at 5. Request at 7 UP.
        // Elevator 0 should pick this up as it continues UP.
        building.requestElevator(7, Direction.UP);

        System.out.println("\n--- Scenario 3: Opposite Direction Request ---");
        // Request at 2 DOWN. Elevator 0 is moving UP to 7.
        // Dispatcher should ideally pick Elevator 1 (Idle at 0) or penalty calc prevents E0.
        building.requestElevator(2, Direction.DOWN);

        // Advance simulation
        building.step();
    }
}

