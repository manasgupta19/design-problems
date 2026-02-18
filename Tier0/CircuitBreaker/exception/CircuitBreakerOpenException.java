package Tier0.CircuitBreaker.exception;

public class CircuitBreakerOpenException extends RuntimeException {
    public CircuitBreakerOpenException(String message) { super(message); }
}