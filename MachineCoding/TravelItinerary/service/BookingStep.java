package MachineCoding.TravelItinerary.service;

import MachineCoding.TravelItinerary.exception.SagaCompensationException;
import MachineCoding.TravelItinerary.exception.SagaExecutionException;
import MachineCoding.TravelItinerary.model.ItineraryContext;

public interface BookingStep {
    String getName();
    boolean execute(ItineraryContext context) throws SagaExecutionException;
    boolean compensate(ItineraryContext context) throws SagaCompensationException;
}

