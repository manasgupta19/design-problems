package Tier0.ATM.state;

import Tier0.ATM.context.ATM;

public class AuthorizedState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardNum) { System.out.println("Error: Card already present."); }
   
    @Override
    public void enterPin(ATM atm, int pin) { System.out.println("Error: Already authorized."); }

    // THE CRITICAL SECTION: Distributed Transaction Logic
    @Override
    public void withdraw(ATM atm, int amount) {
        System.out.println("Processing Withdrawal of $" + amount + "...");
        String txnId = null;

        try {
            // Step 1: Digital Debit (Remote)
            txnId = atm.getBank().debit(atm.getCurrentCard(), amount);
            System.out.println("Bank Debit Successful. TxnID: " + txnId);

            // Step 2: Physical Dispense (Hardware)
            atm.getDispenser().dispense(amount);
            System.out.println("Cash Dispensed Successfully. Please take your cash.");

            // Success Path
            atm.ejectCard();

        } catch (RuntimeException e) {
            System.out.println("HARDWARE FAILURE: " + e.getMessage());

            if (txnId != null) {
                // Step 3: Compensation / Reversal [Source 1507]
                System.out.println("Initiating Reversal for TxnID: " + txnId);
                atm.getBank().reverse(txnId);
                System.out.println("Reversal Complete. Account Credited.");
            }
            atm.ejectCard();
        }
    }

    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Card Ejected.");
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
    }
}


