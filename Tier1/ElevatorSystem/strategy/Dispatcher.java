package Tier1.ElevatorSystem.strategy;

import java.util.List;

import Tier1.ElevatorSystem.model.Direction;
import Tier1.ElevatorSystem.model.Elevator;

public interface Dispatcher {
    Elevator selectElevator(int floor, Direction dir, List<Elevator> elevators);
}


