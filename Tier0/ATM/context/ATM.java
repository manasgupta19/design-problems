package Tier0.ATM.context;

import Tier0.ATM.model.BankSwitch;
import Tier0.ATM.model.CashDispenser;
import Tier0.ATM.state.ATMState;
import Tier0.VendingMachine.state.IdleState;

public // ---------------------------------------------------------
// 3. THE CONTEXT (The ATM Machine)
// ---------------------------------------------------------
class ATM {
    private ATMState currentState;

    // Hardware & Services
    private final CashDispenser dispenser;
    private final BankSwitch bankSwitch;

    // Session Data
    private String currentCardNum;

    public ATM(CashDispenser dispenser, BankSwitch bankSwitch) {
        this.dispenser = dispenser;
        this.bankSwitch = bankSwitch;
        this.currentState = new IdleState(); // Initial State
    }

    // State Transitions
    public void setState(ATMState newState) {
        this.currentState = newState;
        System.out.println("State transition: -> " + newState.getClass().getSimpleName());
    }

    public void setCurrentCard(String card) { this.currentCardNum = card; }
    public String getCurrentCard() { return currentCardNum; }

    public BankSwitch getBank() { return bankSwitch; }
    public CashDispenser getDispenser() { return dispenser; }

    // API Delegation
    public void insertCard(String cardNum) { currentState.insertCard(this, cardNum); }
    public void enterPin(int pin) { currentState.enterPin(this, pin); }
    public void withdraw(int amount) { currentState.withdraw(this, amount); }
    public void ejectCard() { currentState.ejectCard(this); }
}

