package Tier0.ATM.model;

public class MockDispenser implements CashDispenser {
    public boolean shouldFail = false;

    @Override
    public void dispense(int amount) {
        if (shouldFail) throw new RuntimeException("Dispenser Jammed!");
        System.out.println(">> HARDWARE: *Whirrr* Dispensed " + amount);
    }
}
