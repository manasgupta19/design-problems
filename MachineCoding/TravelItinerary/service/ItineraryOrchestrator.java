package MachineCoding.TravelItinerary.service;

import java.util.List;

import MachineCoding.TravelItinerary.exception.SagaCompensationException;
import MachineCoding.TravelItinerary.exception.SagaExecutionException;
import MachineCoding.TravelItinerary.model.ItineraryContext;
import MachineCoding.TravelItinerary.model.SagaStatus;

public class ItineraryOrchestrator {
    private final List<BookingStep> bookingSteps;
    public ItineraryOrchestrator(List<BookingStep> bookingSteps) {
        this.bookingSteps = bookingSteps;
    }

    public SagaStatus bookItinerary(ItineraryContext context) {
        context.setSagaStatus(SagaStatus.PROCESSING);
        int completedSteps = 0;
        for (int i = 0; i < bookingSteps.size(); i++) {
            BookingStep step = bookingSteps.get(i);
            try {
                step.execute(context);
                completedSteps++;
            } catch (SagaExecutionException e) {
                System.err.println("Error executing step: " + step.getName() + " for itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
                context.setSagaStatus(SagaStatus.COMPENSATING);
                triggerCompensation(context, completedSteps - 1);
                return context.getSagaStatus();
            }
        }
        context.setSagaStatus(SagaStatus.COMPLETED);
        System.out.println("Itinerary booking completed successfully for itinerary: " + context.getItineraryId());
        return context.getSagaStatus();
    }

    private void triggerCompensation(ItineraryContext context, int lastSuccessfulStepIndex) {
        System.out.println("Starting compensation for itinerary: " + context.getItineraryId() + ", Last successful step index: " + lastSuccessfulStepIndex);
        for (int j = lastSuccessfulStepIndex; j >= 0; j--) {
            BookingStep step = bookingSteps.get(j);
            try {
                step.compensate(context);
            } catch (SagaCompensationException e) {
                System.err.println("Exception during compensation for step: " + step.getName() + " in itinerary: " + context.getItineraryId() + ", Error: " + e.getMessage());
                context.setSagaStatus(SagaStatus.COMPENSATION_FAILED);
                //publish to DLQ or alert for manual intervention
                return;
            }
        }
        context.setSagaStatus(SagaStatus.COMPENSATED_SUCCESSFULLY);
        System.out.println("Compensation completed successfully for itinerary: " + context.getItineraryId());
    }
}
