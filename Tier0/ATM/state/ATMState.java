package Tier0.ATM.state;

import Tier0.ATM.context.ATM;

public interface ATMState {
    void insertCard(ATM atm, String cardNum);
    void enterPin(ATM atm, int pin);
    void withdraw(ATM atm, int amount);
    void ejectCard(ATM atm);
}