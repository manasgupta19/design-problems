package Tier0.VendingMachine.model;

public enum Product {
    COKE(1.50), PEPSI(1.25), SODA(1.00);
    public double price;
    Product(double p) { this.price = p; }
}
