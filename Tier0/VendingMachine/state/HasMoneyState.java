package Tier0.VendingMachine.state;

import Tier0.VendingMachine.model.Coin;
import Tier0.VendingMachine.model.Product;
import Tier0.VendingMachine.service.VendingMachine;

public class HasMoneyState implements State {
    @Override
    public void insertCoin(VendingMachine machine, Coin coin) {
        System.out.println("Coin inserted: " + coin);
        machine.addBalance(coin.value);
        System.out.println("Current Balance: " + machine.getBalance());
    }

    @Override
    public void selectProduct(VendingMachine machine, Product product) {
        // 1. Check Inventory
        if (!machine.getInventory().hasProduct(product)) {
            System.out.println("Error: " + product + " is out of stock.");
            refund(machine); // Auto-refund or stay in HasMoney
            return;
        }

        // 2. Check Balance
        if (machine.getBalance() < product.price) {
            System.out.println("Error: Insufficient funds. Price: " + product.price + ", Balance: " + machine.getBalance());
            return; // Stay in HasMoney state
        }

        // 3. Valid Selection -> Transition
        System.out.println("Product selected: " + product);
        machine.setSelectedProduct(product);
        machine.setState(new DispensingState());

        // Auto-trigger dispense logic
        machine.dispense();
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("Please select a product first.");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("Returning amount: " + machine.getBalance());
        machine.resetBalance();
        machine.setState(new IdleState());
    }
}

