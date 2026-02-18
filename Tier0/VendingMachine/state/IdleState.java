package Tier0.VendingMachine.state;

import Tier0.VendingMachine.model.Coin;
import Tier0.VendingMachine.model.Product;
import Tier0.VendingMachine.service.VendingMachine;

public class IdleState implements State {
    @Override
    public void insertCoin(VendingMachine machine, Coin coin) {
        System.out.println("Coin inserted: " + coin);
        machine.addBalance(coin.value);
        machine.setState(new HasMoneyState()); // Transition
    }

    @Override
    public void selectProduct(VendingMachine machine, Product product) {
        System.out.println("Please insert money first.");
    }

    @Override
    public void dispense(VendingMachine machine) {
        System.out.println("Payment required.");
    }

    @Override
    public void refund(VendingMachine machine) {
        System.out.println("No money to refund.");
    }
}

