package Tier0.MovieTicketBookingSystem.model;

public class Seat {
    String seatId;
    SeatStatus status;
    int version; // The Optimistic Lock [Source 14]
    String reservedByUserId;

    public Seat(String seatId) {
        this.seatId = seatId;
        this.status = SeatStatus.AVAILABLE;
        this.version = 1;
    }

    // Copy constructor for simulation safety
    public Seat(Seat other) {
        this.seatId = other.seatId;
        this.status = other.status;
        this.version = other.version;
        this.reservedByUserId = other.reservedByUserId;
    }

    public SeatStatus getStatus() { return status; }
    public String getSeatId() { return seatId; }

    public boolean isAvailable() { return status == SeatStatus.AVAILABLE; }
    public String getReservedByUserId() { return reservedByUserId; }

    public int getVersion() { return version; }

    public void setStatus(SeatStatus status) { this.status = status; }
    public void setReservedByUserId(String reservedByUserId) { this.reservedByUserId = reservedByUserId; }
    public void incrementVersion() { this.version++; }
}

