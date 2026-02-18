package Tier0.VendingMachine.service;

import Tier0.VendingMachine.model.Coin;
import Tier0.VendingMachine.model.Product;
import Tier0.VendingMachine.repository.Inventory;
import Tier0.VendingMachine.state.IdleState;
import Tier0.VendingMachine.state.State;

public // ---------------------------------------------------------
// 4. CONTEXT (Vending Machine)
// ---------------------------------------------------------
class VendingMachine {
    private State currentState;
    private Inventory inventory;
    private double currentBalance;
    private Product selectedProduct;

    public VendingMachine() {
        this.inventory = new Inventory();
        this.currentState = new IdleState(); // Initial State
        this.currentBalance = 0.0;
    }

    // State Management
    public void setState(State state) { this.currentState = state; }
    public State getCurrentState() { return currentState; }

    // Inventory Management
    public Inventory getInventory() { return inventory; }
    public void addStock(Product p, int count) { inventory.addProduct(p, count); }

    // Balance Management
    public void addBalance(double amount) { this.currentBalance += amount; }
    public double getBalance() { return currentBalance; }
    public void deductBalance(double amount) { this.currentBalance -= amount; }
    public void resetBalance() { this.currentBalance = 0.0; }

    // Product Selection
    public void setSelectedProduct(Product p) { this.selectedProduct = p; }
    public Product getSelectedProduct() { return selectedProduct; }

    // ACTIONS (Delegated to State)
    public void insertCoin(Coin coin) { currentState.insertCoin(this, coin); }
    public void selectProduct(Product product) { currentState.selectProduct(this, product); }
    public void dispense() { currentState.dispense(this); }
    public void refund() { currentState.refund(this); }
}
