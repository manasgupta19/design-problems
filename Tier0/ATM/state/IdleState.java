package Tier0.ATM.state;

import Tier0.ATM.context.ATM;

public class IdleState implements ATMState {
    @Override
    public void insertCard(ATM atm, String cardNum) {
        System.out.println("Card Inserted: " + cardNum);
        atm.setCurrentCard(cardNum);
        atm.setState(new HasCardState());
    }
    
    @Override
    public void enterPin(ATM atm, int pin) { System.out.println("Error: No card inserted."); }
    
    @Override
    public void withdraw(ATM atm, int amount) { System.out.println("Error: No card inserted."); }
    
    @Override
    public void ejectCard(ATM atm) { System.out.println("Error: No card to eject."); }
}
