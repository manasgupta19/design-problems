package MachineCoding.TravelItinerary.service;

import MachineCoding.TravelItinerary.dto.TrainBookingRequest;
import MachineCoding.TravelItinerary.dto.TrainBookingResult;
import MachineCoding.TravelItinerary.exception.SagaCompensationException;
import MachineCoding.TravelItinerary.exception.SagaExecutionException;
import MachineCoding.TravelItinerary.gateway.ProviderGateway;
import MachineCoding.TravelItinerary.model.ItineraryContext;

public class TrainBookingStep implements BookingStep {
    private final ProviderGateway<TrainBookingRequest, TrainBookingResult> trainProviderGateway;

    public TrainBookingStep(ProviderGateway<TrainBookingRequest, TrainBookingResult> trainProviderGateway) {
        this.trainProviderGateway = trainProviderGateway;
    }

    @Override
    public String getName() {
        return "TrainBooking";
    }

    @Override
    public boolean execute(ItineraryContext context) throws SagaExecutionException {
        // Implementation for booking a train using a provider gateway
        try {
            System.out.println("Executing train booking step for itinerary: " + context.getItineraryId());
            TrainBookingRequest request = new TrainBookingRequest(
                    context.getUserId(),
                    "TRAIN123",
                    "2024-12-01"
            );

            TrainBookingResult result = trainProviderGateway.book(request);
            context.addBookingResult(getName()+"_TRAIN_CONF", result.getTrainConfirmationNumber());
            context.addBookingResult(getName()+"_BOOKING_REFERENCE", result.getBookingReference());
            System.out.println("Train booking successful for itinerary: " + context.getItineraryId() + ", Train Confirmation: " + result.getTrainConfirmationNumber()
                    + ", Booking Reference: " + result.getBookingReference());
            return true;
        } catch (Exception e) {
                System.out.println("Train booking failed for itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
                throw new SagaExecutionException("Failed to book train for itinerary: " + context.getItineraryId(), e);
        }
    }

    @Override
    public boolean compensate(ItineraryContext context) throws SagaCompensationException {
         // Implementation for compensating a train booking (cancellation) using a provider gateway
        String trainConf = (String) context.getBookingResult(getName()+"_TRAIN_CONF");
        if(trainConf == null) {
            System.out.println("No train booking found to compensate for itinerary: " + context.getItineraryId());
            return true; // Nothing to compensate
        }
        try {
            System.out.println("Compensating train booking for itinerary: " + context.getItineraryId() + ", Train Confirmation: " + trainConf);
            boolean cancelResult = trainProviderGateway.cancel(trainConf, context.getItineraryId());
            return cancelResult;
        } catch (Exception e) {
            System.out.println("Failed to compensate train booking for itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
            throw new SagaCompensationException("Failed to compensate train booking for itinerary: " + context.getItineraryId(), e);
        }
    }

}
