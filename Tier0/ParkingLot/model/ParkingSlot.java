package Tier0.ParkingLot.model;

public class ParkingSlot {
    private String slotId;
    private SlotType type;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSlot(String id, SlotType type) {
        this.slotId = id;
        this.type = type;
    }

    public boolean isAvailable() { return !isOccupied; }
    public SlotType getType() { return type; }
    public String getSlotId() { return slotId; }

    public void park(Vehicle v) {
        this.parkedVehicle = v;
        this.isOccupied = true;
    }

    public void unpark() {
        this.parkedVehicle = null;
        this.isOccupied = false;
    }
}

