package MachineCoding.TravelItinerary.gateway;

import java.util.UUID;

import MachineCoding.TravelItinerary.dto.FlightBookingRequest;
import MachineCoding.TravelItinerary.dto.FlightBookingResult;

public class MockFlightProvider implements ProviderGateway<FlightBookingRequest, FlightBookingResult> {
    @Override
    public FlightBookingResult book(FlightBookingRequest request) {
        simulateNetworkDelay();
        return new FlightBookingResult("FLIGHT_PNR_" + UUID.randomUUID().toString().substring(0,5), "BOOKING_REF_" + UUID.randomUUID().toString().substring(0,5));
    }

    @Override
    public boolean cancel(String bookingReference, String idempotencyKey) {
        simulateNetworkDelay();
        System.out.println("Mock cancel flight booking with reference: " + bookingReference + ", idempotencyKey: " + idempotencyKey);
        return true;
    }

    private void simulateNetworkDelay() {
        try { Thread.sleep((long) (Math.random() * 500)); } catch (InterruptedException e) { }
    }

}
