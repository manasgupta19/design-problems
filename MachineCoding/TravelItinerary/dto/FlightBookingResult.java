package MachineCoding.TravelItinerary.dto;

public class FlightBookingResult {
    String pnr;
    String bookingReference;

    public FlightBookingResult(String pnr, String bookingReference) {
        this.pnr = pnr;
        this.bookingReference = bookingReference;
    }

    public String getPnr() {
        return pnr;
    }

    public String getBookingReference() {
        return bookingReference;
    }
}
