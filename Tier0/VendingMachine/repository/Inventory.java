package Tier0.VendingMachine.repository;

import java.util.HashMap;
import java.util.Map;

import Tier0.VendingMachine.model.Product;

public // ---------------------------------------------------------
// 2. INVENTORY (Helper)
// ---------------------------------------------------------
class Inventory {
    private final Map<Product, Integer> stock = new HashMap<>();

    public void addProduct(Product p, int count) {
        stock.put(p, stock.getOrDefault(p, 0) + count);
    }

    public boolean hasProduct(Product p) {
        return stock.getOrDefault(p, 0) > 0;
    }

    public void deductProduct(Product p) {
        if (hasProduct(p)) {
            stock.put(p, stock.get(p) - 1);
        }
    }

    public int getCount(Product p) { return stock.getOrDefault(p, 0); }
}
