package MachineCoding.TravelItinerary.dto;

public class HotelBookingResult {
    String bookingReference;
    String hotelConfirmationNumber;

    public HotelBookingResult(String bookingReference, String hotelConfirmationNumber) {
        this.bookingReference = bookingReference;
        this.hotelConfirmationNumber = hotelConfirmationNumber;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public String getHotelConfirmationNumber() {
        return hotelConfirmationNumber;
    }
}
