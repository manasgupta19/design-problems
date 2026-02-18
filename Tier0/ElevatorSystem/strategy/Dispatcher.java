package Tier0.ElevatorSystem.strategy;

import java.util.List;

import Tier0.ElevatorSystem.model.Direction;
import Tier0.ElevatorSystem.model.Elevator;

public interface Dispatcher {
    Elevator selectElevator(int floor, Direction dir, List<Elevator> elevators);
}


