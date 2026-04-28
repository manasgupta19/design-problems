package Tier1.ATM.state;

import Tier1.ATM.context.ATM;

public interface ATMState {
    void insertCard(ATM atm, String cardNum);
    void enterPin(ATM atm, int pin);
    void withdraw(ATM atm, int amount);
    void ejectCard(ATM atm);
}