package MachineCoding.SeatBooking;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import MachineCoding.SeatBooking.exception.SeatUnavailableException;
import MachineCoding.SeatBooking.model.Booking;
import MachineCoding.SeatBooking.model.Seat;
import MachineCoding.SeatBooking.service.InMemorySeatLockProvider;
import MachineCoding.SeatBooking.service.MockPaymentService;
import MachineCoding.SeatBooking.service.SeatLockProvider;
import MachineCoding.SeatBooking.service.TicketBookingEngine;

public class BookingDriver {
    public static void main(String[] args) throws InterruptedException {
        // Initialize the booking system, create seats, and perform booking operations here.
        System.out.println("Booking system initialized. You can implement test cases here to demonstrate the functionality.");

        Seat seat1 = new Seat("A1", 100.0);
        Seat seat2 = new Seat("A2", 100.0);
        List<Seat> seatsToBook = List.of(seat1, seat2);

        SeatLockProvider seatLockProvider = new InMemorySeatLockProvider(); 
        MockPaymentService paymentService = new MockPaymentService();
        TicketBookingEngine bookingEngine = new TicketBookingEngine(seatLockProvider, paymentService);

        int totalUsers = 200;
        ExecutorService threadPool = Executors.newFixedThreadPool(50);

        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(totalUsers);

        AtomicInteger successfulBookings = new AtomicInteger(0);
        AtomicInteger failedBookings = new AtomicInteger(0);

        System.out.println("Starting booking simulation with " + totalUsers + " users...");

        for(int i=1;i<=totalUsers;i++) {
            final String userId = "User_" + i;
            threadPool.submit(() -> {
                try {
                    startGate.await(); // Wait for the signal to start
                    Booking booking = bookingEngine.bookSeats(userId, seatsToBook);
                    if(booking != null) {
                        System.out.println("Booking successful for " + userId + ": " + booking.getBookingId());
                        successfulBookings.incrementAndGet();
                    }
                } catch (SeatUnavailableException e) {
                    System.out.println("Booking failed for " + userId + ": " + e.getMessage());
                    failedBookings.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Booking interrupted for " + userId);
                } catch (Exception e) {
                    // THE FIX: Catching unexpected RuntimeExceptions (like NullPointerException)
                    // This prevents the ExecutorService from silently swallowing the crash.
                    System.err.println("Thread crashed for " + userId + " - Reason: " + e.toString());
                } finally {
                    endGate.countDown(); // Signal that this thread has finished
                }
            });
        }

        System.out.println("All booking threads are ready. Starting the booking process...");
        long startTime = System.currentTimeMillis();
        startGate.countDown(); // Signal all threads to start
        endGate.await(); // Wait for all threads to finish
        threadPool.shutdown(); // Shutdown the thread pool

        System.out.println("Booking simulation completed.");
        System.out.println("Total time taken: " + (System.currentTimeMillis() - startTime) + " ms");
        System.out.println("Successful bookings (exactly 1): " + successfulBookings.get());
        System.out.println("Failed bookings (exactly " + (totalUsers - 1) + "): " + failedBookings.get());
        System.out.println("Final seat status: " + seat1.getSeatStatus() + ", " + seat2.getSeatStatus());
    }
}
