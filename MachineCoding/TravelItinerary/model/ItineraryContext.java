package MachineCoding.TravelItinerary.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItineraryContext {
    private final String itineraryId;
    private final String userId;
    private volatile SagaStatus sagaStatus;
    private final Map<String, Object> bookingResults;

    public ItineraryContext(String itineraryId, String userId) {
        this.itineraryId = itineraryId;
        this.userId = userId;
        this.sagaStatus = SagaStatus.PENDING;
        this.bookingResults = new ConcurrentHashMap<>();
    }

    public String getItineraryId() {
        return itineraryId;
    }

    public String getUserId() {
        return userId;
    }

    public SagaStatus getSagaStatus() {
        return sagaStatus;
    }

    public void setSagaStatus(SagaStatus sagaStatus) {
        this.sagaStatus = sagaStatus;
    }

    public void addBookingResult(String service, Object result) {
        bookingResults.put(service, result);
    }

    public Object getBookingResult(String service) {
        return bookingResults.get(service);
    }
}
