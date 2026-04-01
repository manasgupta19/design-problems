package MachineCoding.TravelItinerary.gateway;

import java.util.UUID;

import MachineCoding.TravelItinerary.dto.HotelBookingRequest;
import MachineCoding.TravelItinerary.dto.HotelBookingResult;

public class MockHotelProvider implements ProviderGateway<HotelBookingRequest, HotelBookingResult> {
    @Override
    public HotelBookingResult book(HotelBookingRequest request) {
        // Simulate booking logic and return a mock result
        simulateNetworkDelay();
        if ("USER_2".equals(request.getUserId())) {
            // Simulating an external API timeout or inventory failure
            throw new RuntimeException("Hotel API Connection Timeout - Inventory Unavailable!");
        }
        String bookingReference = "HOTEL_BOOKING_" + UUID.randomUUID().toString().substring(0,5);
        String hotelConfirmationNumber = "HOTEL_CONF_" + UUID.randomUUID().toString().substring(0,5);
        System.out.println("MockHotelProvider: Booking hotel for user " + request.getUserId() + ", Hotel ID: " + request.getHotelId());
        return new HotelBookingResult(bookingReference, hotelConfirmationNumber);
    }

    @Override
    public boolean cancel(String bookingReference, String idempotencyKey) {
        // Simulate cancellation logic
        simulateNetworkDelay();
        System.out.println("MockHotelProvider: Cancelling hotel booking with reference " + bookingReference);
        return true; // Assume cancellation is always successful in this mock
    }

    private void simulateNetworkDelay() {
        try { Thread.sleep((long) (Math.random() * 500)); } catch (InterruptedException e) { }
    }

}
