package Tier0.ATM.state;

import Tier0.ATM.context.ATM;

public class HasCardState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardNum) { System.out.println("Error: Card already present."); }

    @Override
    public void enterPin(ATM atm, int pin) {
        System.out.println("Verifying PIN...");
        boolean success = atm.getBank().verifyPin(atm.getCurrentCard(), pin);
        if (success) {
            System.out.println("PIN Correct.");
            atm.setState(new AuthorizedState());
        } else {
            System.out.println("PIN Incorrect. Ejecting card.");
            atm.ejectCard();
        }
    }
    
    @Override
    public void withdraw(ATM atm, int amount) { System.out.println("Error: Enter PIN first."); }
    
    @Override
    public void ejectCard(ATM atm) {
        System.out.println("Card Ejected.");
        atm.setCurrentCard(null);
        atm.setState(new IdleState());
    }
}

