package MachineCoding.TravelItinerary.dto;

public class TrainBookingResult {
    String bookingReference;
    String trainConfirmationNumber;

    public TrainBookingResult(String bookingReference, String trainConfirmationNumber) {
        this.bookingReference = bookingReference;
        this.trainConfirmationNumber = trainConfirmationNumber;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public String getTrainConfirmationNumber() {
        return trainConfirmationNumber;
    }
}
