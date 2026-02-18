package Tier0.ParkingLot.strategy;

public interface FeeStrategy {
    double calculateFee(long durationInHours);
}
