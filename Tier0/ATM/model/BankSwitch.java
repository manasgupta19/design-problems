package Tier0.ATM.model;

public interface BankSwitch {
    boolean verifyPin(String cardNum, int pin);
    // Returns txnId if success
    String debit(String cardNum, int amount);
    void reverse(String txnId);
}
