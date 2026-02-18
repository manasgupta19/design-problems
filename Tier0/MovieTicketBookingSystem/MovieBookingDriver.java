package Tier0.MovieTicketBookingSystem;

import Tier0.MovieTicketBookingSystem.model.Booking;
import Tier0.MovieTicketBookingSystem.model.Seat;
import Tier0.MovieTicketBookingSystem.repository.SeatRepository;
import Tier0.MovieTicketBookingSystem.service.TicketBookingService;
import java.util.Arrays;

// ---------------------------------------------------------
// 4. DRIVER (Simulation)
// ---------------------------------------------------------
public class MovieBookingDriver {
    public static void main(String[] args) throws InterruptedException {
        SeatRepository repo = new SeatRepository(5); // Seats S1..S5
        TicketBookingService service = new TicketBookingService(repo);

        // Scenario: User A and User B try to book Seat S1 at the same time
        Runnable taskA = () -> {
            try {
                System.out.println("User A trying to book S1...");
                Booking b = service.bookSeats(Arrays.asList("S1"), "UserA");
                System.out.println("✅ User A Success: " + b);
            } catch (Exception e) {
                System.out.println("❌ User A Failed: " + e.getMessage());
            }
        };

        Runnable taskB = () -> {
            try {
                // Sleep tiny amount to ensure A likely reads first, but race occurs at write
                Thread.sleep(10);
                System.out.println("User B trying to book S1...");
                Booking b = service.bookSeats(Arrays.asList("S1"), "UserB");
                System.out.println("✅ User B Success: " + b);
            } catch (InterruptedException e) {
                System.out.println("❌ User B Failed: " + e.getMessage());
            }
        };

        Thread t1 = new Thread(taskA);
        Thread t2 = new Thread(taskB);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        // Verify final state
        Seat s1 = repo.getSeat("S1");
        System.out.println("\nFinal S1 Status: " + s1.getStatus() + ", Reserved By: " + s1.getReservedByUserId() + ", Version: " + s1.getVersion());
    }
}

