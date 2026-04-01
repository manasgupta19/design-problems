package MachineCoding.SeatBooking.service;

import java.util.List;

import MachineCoding.SeatBooking.model.Seat;

public interface SeatLockProvider {
    boolean lockSeats(List<Seat> seats);
    void unlockSeats(List<Seat> seats);
}
