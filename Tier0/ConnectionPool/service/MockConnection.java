package Tier0.ConnectionPool.service;

// 1. Mock Connection Class (The Resource)
public class MockConnection {
    private final int id;
    private boolean isOpen;

    public MockConnection(int id) {
        this.id = id;
        this.isOpen = true;
    }

    public boolean isValid() {
        // Simulate "SELECT 1" or heartbeat
        return isOpen;
    }

    public void close() {
        this.isOpen = false;
        System.out.println("Connection " + id + " closed physically.");
    }

    // Simulate failure
    public void simulateNetworkFailure() {
        this.isOpen = false;
    }

    @Override
    public String toString() { return "Conn-" + id; }
}
