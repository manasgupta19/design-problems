package Tier1.CircuitBreaker.exception;

public class CircuitBreakerOpenException extends RuntimeException {
    public CircuitBreakerOpenException(String message) { super(message); }
}