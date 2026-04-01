package MachineCoding.TravelItinerary.dto;

public class HotelBookingRequest {
    String userId;
    String hotelId;
    String checkInDate;
    String checkOutDate;
    public HotelBookingRequest(String userId, String hotelId, String checkInDate, String checkOutDate) {
        this.userId = userId;
        this.hotelId = hotelId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }
    public String getUserId() {
        return userId;
    }
    public String getHotelId() {
        return hotelId;
    }
    public String getCheckInDate() {
        return checkInDate;
    }
    public String getCheckOutDate() {
        return checkOutDate;
    }
}
