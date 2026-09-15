package com.shop.adapter.out;

import com.shop.application.port.out.InventoryRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryInventoryRepository implements InventoryRepository {

    private final Map<Long, Integer> stock = new HashMap<>();

    public void setStock(Long productId, int quantity) {
        stock.put(productId, quantity);
    }

    @Override
    public int availableQuantity(Long productId) {
        return stock.getOrDefault(productId, 0);
    }

    @Override
    public void decrease(Long productId, int quantity) {
        int current = availableQuantity(productId);
        if (current < quantity) {
            // Loi ky thuat cua adapter: khong duoc phep de ton kho am.
            throw new IllegalStateException(
                    "Ton kho am cho san pham " + productId + ": con " + current + ", tru " + quantity);
        }
        stock.put(productId, current - quantity);
    }
}
