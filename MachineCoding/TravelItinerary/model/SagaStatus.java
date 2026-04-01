package MachineCoding.TravelItinerary.model;

public enum SagaStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,                     // A step failed, compensation is about to begin
    COMPENSATING,               // Currently rolling back previous steps
    COMPENSATED_SUCCESSFULLY,   // Rollback was successful, system is in a consistent state
    COMPENSATION_FAILED         // FATAL: A rollback failed. Requires manual/DLQ intervention.
}