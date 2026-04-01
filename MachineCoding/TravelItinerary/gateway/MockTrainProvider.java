package MachineCoding.TravelItinerary.gateway;

import java.util.UUID;

import MachineCoding.TravelItinerary.dto.TrainBookingRequest;
import MachineCoding.TravelItinerary.dto.TrainBookingResult;

public class MockTrainProvider implements ProviderGateway<TrainBookingRequest, TrainBookingResult>{
    @Override
    public TrainBookingResult book(TrainBookingRequest request) {
        // Simulate booking logic and return a mock result
        simulateNetworkDelay();
        String bookingReference = "TRAIN_BOOKING_" + UUID.randomUUID().toString().substring(0,5);
        String trainConfirmationNumber = "TRAIN_CONF_" + UUID.randomUUID().toString().substring(0,5);
        System.out.println("MockTrainProvider: Booking train for user " + request.getUserId() + ", Train ID: " + request.getTrainId());
        return new TrainBookingResult(bookingReference, trainConfirmationNumber);
    }

    @Override
    public boolean cancel(String bookingReference, String idempotencyKey) {
        // Simulate cancellation logic
        simulateNetworkDelay();
        System.out.println("MockTrainProvider: Cancelling train booking with reference " + bookingReference);
        return true; // Assume cancellation is always successful in this mock
    }

    private void simulateNetworkDelay() {
        try { Thread.sleep((long) (Math.random() * 500)); } catch (InterruptedException e) { }
    }

}
