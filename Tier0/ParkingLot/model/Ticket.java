package Tier0.ParkingLot.model;

import java.time.LocalDateTime;
import java.util.UUID;

public // ---------------------------------------------------------
// 7. SUPPORTING DTOs
// ---------------------------------------------------------
class Ticket {
    private String id;
    private LocalDateTime entryTime;
    private ParkingSlot slot;
    private Vehicle vehicle;

    public Ticket(Vehicle v, ParkingSlot s) {
        this.id = UUID.randomUUID().toString();
        this.entryTime = LocalDateTime.now().minusHours(2); // Mocking time for fee calc
        this.vehicle = v;
        this.slot = s;
    }
    public String getId() { return id; }
    public ParkingSlot getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
}
