package Tier0.MovieTicketBookingSystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Tier0.MovieTicketBookingSystem.model.Booking;
import Tier0.MovieTicketBookingSystem.model.Seat;
import Tier0.MovieTicketBookingSystem.model.SeatStatus;
import Tier0.MovieTicketBookingSystem.repository.SeatRepository;

public // ---------------------------------------------------------
// 3. SERVICE LAYER (Business Logic)
// ---------------------------------------------------------
class TicketBookingService {
    private final SeatRepository seatRepo;

    public TicketBookingService(SeatRepository seatRepo) {
        this.seatRepo = seatRepo;
    }

    public Booking bookSeats(List<String> seatIds, String userId) {
        List<Seat> seatsToBook = new ArrayList<>();

        // Step 1: Fetch current state (Snapshot read)
        for (String id : seatIds) {
            Seat s = seatRepo.getSeat(id);
            if (s == null) throw new RuntimeException("Invalid Seat: " + id);
            if (s.getStatus() == SeatStatus.BOOKED) {
                throw new RuntimeException("Seat " + id + " is already booked.");
            }
            seatsToBook.add(s);
        }

        // Step 2: Attempt Atomic Reservation (Conditional Update)
        // Pass the *original read versions* to the repo
        boolean success = seatRepo.reserveSeats(seatsToBook, userId);

        if (success) {
            return new Booking(UUID.randomUUID().toString(), seatsToBook);
        } else {
            // Step 3: Handle Contention
            throw new RuntimeException("Booking Failed: Seats were modified by another user.");
        }
    }
}

