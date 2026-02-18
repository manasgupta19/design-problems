package Tier0.VendingMachine.state;

import Tier0.VendingMachine.model.Coin;
import Tier0.VendingMachine.model.Product;
import Tier0.VendingMachine.service.VendingMachine;

public class DispensingState implements State {
    @Override
    public void insertCoin(VendingMachine machine, Coin coin) {
        System.out.println("Wait! Dispensing in progress.");
        // Usually return coin immediately physically
    }

    @Override
    public void selectProduct(VendingMachine machine, Product product) {
        System.out.println("Wait! Dispensing in progress.");
    }

    @Override
    public void dispense(VendingMachine machine) {
        Product p = machine.getSelectedProduct();
        double change = machine.getBalance() - p.price;

        // 1. Update Inventory
        machine.getInventory().deductProduct(p);

        // 2. Dispense
        System.out.println("DISPENSING: " + p);

        // 3. Return Change
        if (change > 0) {
            System.out.println("Returning change: " + change);
        }

        // 4. Reset & Transition
        machine.resetBalance();
        machine.setSelectedProduct(null);
        machine.setState(new IdleState());
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("Cannot refund during dispensing.");
    }
}

