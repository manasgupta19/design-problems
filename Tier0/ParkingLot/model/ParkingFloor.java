package Tier0.ParkingLot.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public // ---------------------------------------------------------
// 4. THE FLOOR (Container)
// ---------------------------------------------------------
class ParkingFloor {
    private int floorId;
    private Map<SlotType, List<ParkingSlot>> slots; // Grouped by type for faster lookup

    public ParkingFloor(int floorId) {
        this.floorId = floorId;
        this.slots = new HashMap<>();
        for (SlotType type : SlotType.values()) {
            slots.put(type, new ArrayList<>());
        }
    }

    public void addSlot(ParkingSlot slot) {
        slots.get(slot.getType()).add(slot);
    }

    // Returns the first available slot of the required type
    public ParkingSlot findAvailableSlot(SlotType type) {
        for (ParkingSlot slot : slots.get(type)) {
            if (slot.isAvailable()) {
                return slot;
            }
        }
        return null;
    }
}
