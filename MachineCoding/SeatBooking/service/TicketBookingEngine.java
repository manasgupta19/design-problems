package MachineCoding.SeatBooking.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import MachineCoding.SeatBooking.exception.SeatUnavailableException;
import MachineCoding.SeatBooking.model.Booking;
import MachineCoding.SeatBooking.model.Seat;
import MachineCoding.SeatBooking.model.SeatStatus;

public class TicketBookingEngine {
    private final SeatLockProvider seatLockProvider;
    private final Map<String, Booking> activeBookings;
    private final MockPaymentService paymentService;

    private final ScheduledExecutorService cleanupExecutor;

    public TicketBookingEngine(SeatLockProvider seatLockProvider, MockPaymentService paymentService) {
        this.seatLockProvider = seatLockProvider;
        this.paymentService = paymentService;
        this.activeBookings = new ConcurrentHashMap<>();
        this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
        this.cleanupExecutor.scheduleAtFixedRate(this::cleanupExpiredBookings, 5, 5, TimeUnit.MINUTES);
    }

    private void cleanupExpiredBookings() {
        for (Map.Entry<String, Booking> en : activeBookings.entrySet()) {
            Booking booking = en.getValue();
            if (booking.isExpired()) {
                for (Seat seat : booking.getSeats()) {
                    seat.setSeatStatus(SeatStatus.AVAILABLE);
                }
                System.out.println("Cleaned up expired booking: " + booking.getBookingId());
                activeBookings.remove(en.getKey());
            }
        }
    }

    private void releaseSeats(List<Seat> seats) {
        for (Seat seat : seats) {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
        }
    }

    public Booking bookSeats(String userId, List<Seat> requestedSeats) {
        for(Seat seat : requestedSeats) {
            if(seat.getSeatStatus() != SeatStatus.AVAILABLE) {
                throw new SeatUnavailableException("Seat " + seat.getSeatId() + " is not available");
            }
        }

        if(!seatLockProvider.lockSeats(requestedSeats)) {
            throw new SeatUnavailableException("Failed to acquire locks on seats");
        }
        
        try {
            for(Seat seat : requestedSeats) {
                if(seat.getSeatStatus() != SeatStatus.AVAILABLE) {
                    throw new SeatUnavailableException("Seat " + seat.getSeatId() + " became unavailable");
                }
            }
            for(Seat seat : requestedSeats) {
                seat.setSeatStatus(SeatStatus.BLOCKED);
            }
            String bookingId = "BOOKING_" + UUID.randomUUID().toString().substring(0, 7);
            Booking booking = new Booking(bookingId, userId, requestedSeats, 5 * 60 * 1000); // 5 minutes expiry
            activeBookings.put(bookingId, booking);
            System.out.println("Booking successful: " + bookingId + " of " + requestedSeats.size() + " seats for user: " + userId);
            return booking;
        } finally {
            seatLockProvider.unlockSeats(requestedSeats);
        }
    }

    public boolean confirmBooking(String bookingId) {
        Booking booking = activeBookings.get(bookingId);
        if(booking == null) {
            throw new IllegalArgumentException("Booking " + bookingId + " is invalid");
        }
        if(booking.isExpired()) {
            activeBookings.remove(bookingId);
            releaseSeats(booking.getSeats());
            throw new IllegalStateException("Booking " + bookingId + " has expired");
        }

        double totalAmount = booking.getSeats().stream().mapToDouble(Seat::getPrice).sum();
        boolean paymentSuccess = paymentService.processPayment(booking.getUserId(), totalAmount);
        if(paymentSuccess) {
            for(Seat seat : booking.getSeats()) {
                seat.setSeatStatus(SeatStatus.BOOKED);
            }
            activeBookings.remove(bookingId);
            System.out.println("Booking confirmed: " + bookingId);
            return true;
        } else {
            activeBookings.remove(bookingId);
            releaseSeats(booking.getSeats());
            System.out.println("Payment failed for booking: " + bookingId);
            return false;
        }
    }

    public void shutdown() {
        cleanupExecutor.shutdown();
    }
}
