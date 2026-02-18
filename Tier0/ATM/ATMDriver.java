package Tier0.ATM;

import Tier0.ATM.context.ATM;
import Tier0.ATM.model.MockBank;
import Tier0.ATM.model.MockDispenser;

public class ATMDriver {
    public static void main(String[] args) {
        MockDispenser dispenser = new MockDispenser();
        ATM atm = new ATM(dispenser, new MockBank());

        System.out.println("--- Scenario 1: Happy Path ---");
        atm.insertCard("1234-5678");
        atm.enterPin(1234);
        atm.withdraw(100); // Should succeed

        System.out.println("\n--- Scenario 2: Hardware Failure (The Reversal) ---");
        dispenser.shouldFail = true; // Simulate Jam

        atm.insertCard("9876-5432");
        atm.enterPin(1234);
        atm.withdraw(200); // Should debit, fail dispense, then reverse

        System.out.println("\n--- Scenario 3: State Enforcement ---");
        atm.withdraw(500); // Should fail (State is Idle)
    }
}
