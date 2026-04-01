package MachineCoding.TravelItinerary.service;

import MachineCoding.TravelItinerary.dto.FlightBookingRequest;
import MachineCoding.TravelItinerary.dto.FlightBookingResult;
import MachineCoding.TravelItinerary.exception.SagaCompensationException;
import MachineCoding.TravelItinerary.exception.SagaExecutionException;
import MachineCoding.TravelItinerary.gateway.ProviderGateway;
import MachineCoding.TravelItinerary.model.ItineraryContext;

public class FlightBookingStep implements BookingStep {
    private final ProviderGateway<FlightBookingRequest, FlightBookingResult> flightProviderGateway;

    public FlightBookingStep(ProviderGateway<FlightBookingRequest, FlightBookingResult> flightProviderGateway) {
        this.flightProviderGateway = flightProviderGateway;
    }
    @Override
    public String getName() {
        return "FlightBooking";
    }

    @Override
    public boolean execute(ItineraryContext context) throws SagaExecutionException {
        // Implementation for booking a flight using a provider gateway
        try {
            System.out.println("Executing flight booking step for itinerary: " + context.getItineraryId());
            FlightBookingRequest request = new FlightBookingRequest(
                    context.getUserId(),
                    "FLIGHT123",
                    "2024-12-01",
                    "NYC",
                    "LAX"
            );

            FlightBookingResult result = flightProviderGateway.book(request);
            context.addBookingResult(getName()+"_PNR", result.getPnr());
            context.addBookingResult(getName()+"_BOOKING_REFERENCE", result.getBookingReference());
            System.out.println("Flight booking successful for itinerary: " + context.getItineraryId() + ", PNR: " + result.getPnr()
                    + ", Booking Reference: " + result.getBookingReference());
            return true;
        } catch (Exception e) {
                System.out.println("Flight booking failed for itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
                throw new SagaExecutionException("Failed to book flight for itinerary: " + context.getItineraryId(), e);
        }
    }

    @Override
    public boolean compensate(ItineraryContext context) throws SagaCompensationException {
        // Implementation for compensating a flight booking (cancellation) using a provider gateway
        String pnr = (String) context.getBookingResult(getName()+"_PNR");
        if(pnr == null) {
            System.out.println("No flight booking found to compensate for itinerary: " + context.getItineraryId());
            return true; // Nothing to compensate
        }
        try {
            System.out.println("Compensating flight booking for itinerary: " + context.getItineraryId() + ", PNR: " + pnr);
            boolean cancelResult = flightProviderGateway.cancel(pnr, context.getItineraryId());
            return cancelResult;
        } catch (Exception e) {
            System.out.println("Failed to compensate flight booking for itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
            throw new SagaCompensationException("Failed to compensate flight booking for itinerary: " + context.getItineraryId(), e);
        }
    }

}
