package Tier0.ParkingLot.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Tier0.ParkingLot.model.ParkingFloor;
import Tier0.ParkingLot.model.ParkingSlot;
import Tier0.ParkingLot.model.Receipt;
import Tier0.ParkingLot.model.SlotType;
import Tier0.ParkingLot.model.Ticket;
import Tier0.ParkingLot.model.Vehicle;
import Tier0.ParkingLot.model.VehicleType;
import Tier0.ParkingLot.strategy.FeeStrategy;
import Tier0.ParkingLot.strategy.HourlyFeeStrategy;

public 
// ---------------------------------------------------------
// 6. THE PARKING LOT (Controller / Singleton)
// ---------------------------------------------------------
class ParkingLot {
    private static ParkingLot instance;
    private List<ParkingFloor> floors;
    private Map<String, Ticket> activeTickets; // Map Ticket ID to Ticket
    private FeeStrategy feeStrategy;
    private final Lock lock = new ReentrantLock(); // Simple concurrency control

    private ParkingLot() {
        floors = new ArrayList<>();
        activeTickets = new HashMap<>();
        feeStrategy = new HourlyFeeStrategy();
    }

    public static ParkingLot getInstance() {
        if (instance == null) instance = new ParkingLot();
        return instance;
    }

    public void addFloor(ParkingFloor floor) {
        floors.add(floor);
    }

    // MAPPING LOGIC: Which vehicle goes to which slot type?
    private SlotType getPreferredSlotType(VehicleType vType) {
        switch (vType) {
            case BIKE: return SlotType.BIKE_SLOT;
            case CAR: return SlotType.COMPACT;
            case TRUCK: return SlotType.LARGE;
            default: return SlotType.LARGE;
        }
    }

    // API 1: PARK
    public Ticket park(Vehicle vehicle) {
        lock.lock(); // Critical section start
        try {
            SlotType requiredType = getPreferredSlotType(vehicle.getType());

            // Strategy: Lowest Floor First
            for (ParkingFloor floor : floors) {
                ParkingSlot slot = floor.findAvailableSlot(requiredType);
                if (slot != null) {
                    slot.park(vehicle);

                    Ticket ticket = new Ticket(vehicle, slot);
                    activeTickets.put(ticket.getId(), ticket);
                    System.out.println("Parked " + vehicle.getType() + " [" + vehicle.getLicensePlate() + "] at " + slot.getSlotId());
                    return ticket;
                }
            }
            throw new RuntimeException("Parking Full for " + vehicle.getType());
        } finally {
            lock.unlock(); // Critical section end
        }
    }

    // API 2: UNPARK
    public Receipt unpark(String ticketId) {
        lock.lock();
        try {
            if (!activeTickets.containsKey(ticketId)) {
                throw new RuntimeException("Invalid Ticket");
            }
            Ticket ticket = activeTickets.get(ticketId);

            // Find slot logic (In real DB, ticket has slotID)
            // Here we assume ticket holds slot reference for simplicity or lookup
            ticket.getSlot().unpark();
            activeTickets.remove(ticketId);

            // Calculate Fee
            long duration = Duration.between(ticket.getEntryTime(), LocalDateTime.now()).toHours();
            double fee = feeStrategy.calculateFee(duration);

            return new Receipt(ticketId, fee);
        } finally {
            lock.unlock();
        }
    }
}
