package Tier0.ParkingLot.strategy;

public class HourlyFeeStrategy implements FeeStrategy {
    public double calculateFee(long hours) {
        return Math.max(1, hours) * 10.0; // $10 per hour
    }
}
