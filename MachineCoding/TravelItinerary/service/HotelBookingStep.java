package MachineCoding.TravelItinerary.service;

import MachineCoding.TravelItinerary.dto.HotelBookingRequest;
import MachineCoding.TravelItinerary.dto.HotelBookingResult;
import MachineCoding.TravelItinerary.exception.SagaCompensationException;
import MachineCoding.TravelItinerary.exception.SagaExecutionException;
import MachineCoding.TravelItinerary.gateway.ProviderGateway;
import MachineCoding.TravelItinerary.model.ItineraryContext;

public class HotelBookingStep implements BookingStep {
    private final ProviderGateway<HotelBookingRequest, HotelBookingResult> hotelProviderGateway;

    public HotelBookingStep(ProviderGateway<HotelBookingRequest, HotelBookingResult> hotelProviderGateway) {
        this.hotelProviderGateway = hotelProviderGateway;
    }

    @Override
    public String getName() {
        return "HotelBooking";
    }

    @Override
    public boolean execute(ItineraryContext context) throws SagaExecutionException {
        // Implementation for booking a hotel using a provider gateway
        try {
            System.out.println("Executing hotel booking step for itinerary: " + context.getItineraryId());
            HotelBookingRequest request = new HotelBookingRequest(
                    context.getUserId(),
                    "HOTEL123",
                    "2024-12-01",
                    "2024-12-05"
            );

            HotelBookingResult result = hotelProviderGateway.book(request);
            context.addBookingResult(getName()+"_HOTEL_CONF", result.getHotelConfirmationNumber());
            context.addBookingResult(getName()+"_BOOKING_REFERENCE", result.getBookingReference());
            System.out.println("Hotel booking successful for itinerary: " + context.getItineraryId() + ", Hotel Confirmation: " + result.getHotelConfirmationNumber()
                    + ", Booking Reference: " + result.getBookingReference());
            return true;
        } catch (Exception e) {
                System.out.println("Hotel booking failed for itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
                throw new SagaExecutionException("Failed to book hotel for itinerary: " + context.getItineraryId(), e);
        }
    }

    @Override
    public boolean compensate(ItineraryContext context) throws SagaCompensationException {
         // Implementation for compensating a hotel booking (cancellation) using a provider gateway
        String hotelConf = (String) context.getBookingResult(getName()+"_HOTEL_CONF");
        if(hotelConf == null) {
            System.out.println("No hotel booking found to compensate for itinerary: " + context.getItineraryId());
            return true; // Nothing to compensate
        }
        try {
            System.out.println("Compensating hotel booking for itinerary: " + context.getItineraryId() + ", Hotel Confirmation: " + hotelConf);
            boolean cancelResult = hotelProviderGateway.cancel(hotelConf, context.getItineraryId());
            return cancelResult;
        } catch (Exception e) {
            System.out.println("Failed to compensate hotel booking for itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
            throw new SagaCompensationException("Failed to compensate hotel booking for itinerary: " + context.getItineraryId(), e);
        }
    }

}
