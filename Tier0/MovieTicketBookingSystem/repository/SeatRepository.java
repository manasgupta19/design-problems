package Tier0.MovieTicketBookingSystem.repository;

import Tier0.MovieTicketBookingSystem.model.Seat;
import Tier0.MovieTicketBookingSystem.model.SeatStatus;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public // ---------------------------------------------------------
// 2. REPOSITORY (Simulating the Database Layer)
// ---------------------------------------------------------
class SeatRepository {
    // Simulating a DB Table: Map<SeatId, Seat>
    private final ConcurrentHashMap<String, Seat> seatTable = new ConcurrentHashMap<>();

    public SeatRepository(int seatCount) {
        for (int i = 1; i <= seatCount; i++) {
            String id = "S" + i;
            seatTable.put(id, new Seat(id));
        }
    }

    public Seat getSeat(String seatId) {
        // Simulates: SELECT * FROM seats WHERE id = ?
        Seat seat = seatTable.get(seatId);
        return seat != null ? new Seat(seat) : null;
    }

    /**
     * The Critical Section: Simulates "UPDATE ... WHERE version = ?"
     * Returns true only if ALL seats were updated atomically.
     */
    public boolean reserveSeats(List<Seat> seatsToUpdate, String userId) {
        // In a real DB, this would be a @Transactional method.
        // We use synchronized on the map to simulate DB row locking behavior for the simulation.
        synchronized (seatTable) {
            // 1. Re-Verify all versions match (The "Where Clause")
            for (Seat s : seatsToUpdate) {
                Seat currentDbSeat = seatTable.get(s.getSeatId());
                if (currentDbSeat.getVersion() != s.getVersion() || currentDbSeat.getStatus() != SeatStatus.AVAILABLE) {
                    return false; // Optimistic Lock Failure [Source 16]
                }
            }

            // 2. Commit Updates (The "Update Statement")
            for (Seat s : seatsToUpdate) {
                Seat currentDbSeat = seatTable.get(s.getSeatId());
                currentDbSeat.setStatus(SeatStatus.BOOKED);
                currentDbSeat.setReservedByUserId(userId);
                currentDbSeat.incrementVersion(); // Increment Version
            }
            return true;
        }
    }
}

