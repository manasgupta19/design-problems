package Tier0.ParkingLot;

import Tier0.ParkingLot.model.Bike;
import Tier0.ParkingLot.model.Car;
import Tier0.ParkingLot.model.ParkingFloor;
import Tier0.ParkingLot.model.ParkingSlot;
import Tier0.ParkingLot.model.Receipt;
import Tier0.ParkingLot.model.SlotType;
import Tier0.ParkingLot.model.Ticket;
import Tier0.ParkingLot.model.Vehicle;
import Tier0.ParkingLot.service.ParkingLot;

// ---------------------------------------------------------
// 8. DRIVER CLASS
// ---------------------------------------------------------
public class ParkingDriver {
    public static void main(String[] args) {
        ParkingLot lot = ParkingLot.getInstance();

        // Setup: 1 Floor, 2 Slots (1 Car, 1 Bike)
        ParkingFloor floor1 = new ParkingFloor(1);
        floor1.addSlot(new ParkingSlot("F1-S1", SlotType.COMPACT)); // Car spot
        floor1.addSlot(new ParkingSlot("F1-S2", SlotType.BIKE_SLOT)); // Bike spot
        lot.addFloor(floor1);

        // Scenario 1: Park a Car (Success)
        Vehicle car = new Car("KA-01-1234");
        Ticket ticket1 = lot.park(car);

        // Scenario 2: Park a Bike (Success)
        Vehicle bike = new Bike("KA-01-5678");
        Ticket ticket2 = lot.park(bike);

        // Scenario 3: Park another Car (Fail - Full)
        try {
            lot.park(new Car("KA-01-9999"));
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Scenario 4: Unpark Car
        Receipt receipt = lot.unpark(ticket1.getId());
        System.out.println(receipt);

        // Scenario 5: Park Car again (Success - Slot freed)
        lot.park(new Car("KA-01-9999"));
    }
}

