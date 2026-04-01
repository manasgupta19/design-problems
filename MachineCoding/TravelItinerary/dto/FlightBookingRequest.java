package MachineCoding.TravelItinerary.dto;

public class FlightBookingRequest {
    private final String userId;
    private final String flightId;
    private final String departureDate;
    private final String source;
    private final String destination;

    public FlightBookingRequest(String userId, String flightId, String departureDate, String source, String destination) {
        this.userId = userId;
        this.flightId = flightId;
        this.departureDate = departureDate;
        this.source = source;
        this.destination = destination;
    }

    public String getUserId() {
        return userId;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }
}
