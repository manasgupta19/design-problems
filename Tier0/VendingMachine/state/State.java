package Tier0.VendingMachine.state;

import Tier0.VendingMachine.model.Coin;
import Tier0.VendingMachine.model.Product;
import Tier0.VendingMachine.service.VendingMachine;

public // ---------------------------------------------------------
// 3. STATE INTERFACE (The State Pattern Core)
// ---------------------------------------------------------
interface State {
    void insertCoin(VendingMachine machine, Coin coin);
    void selectProduct(VendingMachine machine, Product product);
    void dispense(VendingMachine machine);
    void refund(VendingMachine machine);
}
