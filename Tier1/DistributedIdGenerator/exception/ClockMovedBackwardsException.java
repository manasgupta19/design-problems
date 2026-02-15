package Tier1.DistributedIdGenerator.exception;

// 1. Custom Exception for Fail-Closed Scenario
public class ClockMovedBackwardsException extends RuntimeException {
    public ClockMovedBackwardsException(String message) { super(message); }
}
