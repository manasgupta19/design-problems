package Tier0.ATM.model;

public interface CashDispenser {
    // Throws exception if hardware fails (e.g., Jam)
    void dispense(int amount) throws RuntimeException;
}
