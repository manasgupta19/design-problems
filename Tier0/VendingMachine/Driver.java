package Tier0.VendingMachine;

import Tier0.VendingMachine.model.Coin;
import Tier0.VendingMachine.model.Product;
import Tier0.VendingMachine.service.VendingMachine;

// ---------------------------------------------------------
// 6. DRIVER CLASS
// ---------------------------------------------------------
public class Driver {
    public static void main(String[] args) {
        VendingMachine vm = new VendingMachine();

        // Setup Inventory
        vm.addStock(Product.COKE, 2);
        vm.addStock(Product.PEPSI, 0); // Out of stock

        System.out.println("--- Scenario 1: Successful Purchase ---");
        vm.insertCoin(Coin.QUARTER);
        vm.insertCoin(Coin.QUARTER);
        vm.insertCoin(Coin.QUARTER);
        vm.insertCoin(Coin.QUARTER);
        vm.insertCoin(Coin.QUARTER);
        vm.insertCoin(Coin.QUARTER); // $1.50
        vm.selectProduct(Product.COKE);

        System.out.println("\n--- Scenario 2: Out of Stock ---");
        vm.insertCoin(Coin.QUARTER); // Insert to wake up
        vm.selectProduct(Product.PEPSI);

        System.out.println("\n--- Scenario 3: Insufficient Funds ---");
        vm.insertCoin(Coin.QUARTER);
        vm.selectProduct(Product.COKE); // Price 1.50, Balance 0.25

        System.out.println("\n--- Scenario 4: Refund ---");
        vm.insertCoin(Coin.DIME);
        vm.refund();
    }
}

