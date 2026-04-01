package MachineCoding.SeatBooking.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import MachineCoding.SeatBooking.model.Seat;

public class InMemorySeatLockProvider implements SeatLockProvider {
    private static final long LOCK_TIMEOUT_MILLIS = 50;
    
    @Override
    public boolean lockSeats(List<Seat> seats) {
        List<Seat> sortedSeats = new ArrayList<>(seats);
        sortedSeats.sort(Comparator.comparing(Seat::getSeatId));
        
        List<Seat> acquiredLocks = new ArrayList<>();
        for(Seat seat: sortedSeats) {
            try {
                if(seat.lockSeat(LOCK_TIMEOUT_MILLIS, MILLISECONDS)) {
                    acquiredLocks.add(seat);
                } else {
                    // Failed to acquire lock, release all previously acquired locks
                    unlockSeats(acquiredLocks);
                    return false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // Release any locks acquired so far
                unlockSeats(acquiredLocks);
                return false;
            }
        }
        return true;
    }

    @Override
    public void unlockSeats(List<Seat> seats) {
        for(int i = seats.size() - 1; i >= 0; i--) {
            seats.get(i).unlockSeat();
        }
    }

}
