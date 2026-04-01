package MachineCoding.TravelItinerary.dto;

public class TrainBookingRequest {
    String userId;
    String trainId;
    String departureDate;

    public TrainBookingRequest(String userId, String trainId, String departureDate) {
        this.userId = userId;
        this.trainId = trainId;
        this.departureDate = departureDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getTrainId() {
        return trainId;
    }

    public String getDepartureDate() {
        return departureDate;
    }
}
