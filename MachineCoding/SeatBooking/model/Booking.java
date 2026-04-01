package MachineCoding.SeatBooking.model;

import java.util.Collections;
import java.util.List;

public class Booking {
    private final String bookingId;
    private final String userId;
    private final List<Seat> seats;
    private final long expiryTimeMillis;

    public Booking(String bookingId, String userId, List<Seat> seats, long expiryTimeMillis) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.seats = Collections.unmodifiableList(seats);
        this.expiryTimeMillis = System.currentTimeMillis() + expiryTimeMillis;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTimeMillis;
    }

}
