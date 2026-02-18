package Tier0.ParkingLot.model;

public class Receipt {
    String ticketId;
    double fee;
    public Receipt(String id, double fee) {
        this.ticketId = id;
        this.fee = fee;
    }
    @Override
    public String toString() { return "Receipt[Ticket: " + ticketId + ", Fee: $" + fee + "]"; }
}
