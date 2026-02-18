package Tier0.ATM.model;

public class MockBank implements BankSwitch {

    @Override
    public boolean verifyPin(String card, int pin) { return pin == 1234; }

    @Override
    public String debit(String card, int amount) {
        if (amount > 1000) throw new RuntimeException("Insufficient Funds");
        return "TXN-" + System.currentTimeMillis();
    }

    @Override
    public void reverse(String txnId) { System.out.println(">> BANK: Reversing " + txnId); }
}
