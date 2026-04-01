package MachineCoding.TravelItinerary.gateway;

public interface ProviderGateway<T, R> {
    R book(T request);
    boolean cancel(String bookingReference, String idempotencyKey);
}
