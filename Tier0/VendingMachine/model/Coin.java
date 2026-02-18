package Tier0.VendingMachine.model;

public enum Coin {
    PENNY(0.01), NICKEL(0.05), DIME(0.1), QUARTER(0.25);
    public double value;
    Coin(double v) { this.value = v; }
}
