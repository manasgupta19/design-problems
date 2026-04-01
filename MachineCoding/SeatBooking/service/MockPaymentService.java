package MachineCoding.SeatBooking.service;

import java.util.Random;

public class MockPaymentService {
    private final Random random = new Random();

    public boolean processPayment(String userId, double amount) {
        // Simulate payment processing time
        System.out.println("Processing payment for user: " + userId + " amount: $" + amount);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return random.nextDouble() < 0.8; // 80% success rate
    }
}
