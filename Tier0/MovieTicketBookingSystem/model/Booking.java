package Tier0.MovieTicketBookingSystem.model;

import java.util.List;
import java.util.stream.Collectors;

public class Booking {
    String bookingId;
    List<Seat> seats;
    double amount;

    public Booking(String bookingId, List<Seat> seats) {
        this.bookingId = bookingId;
        this.seats = seats;
    }
    @Override
    public String toString() { return "BookingID: " + bookingId + " Seats: " + seats.stream().map(s -> s.seatId).collect(Collectors.toList()); }
}
