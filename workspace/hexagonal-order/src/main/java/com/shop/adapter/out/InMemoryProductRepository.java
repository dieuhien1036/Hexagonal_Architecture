package com.shop.adapter.out;

import com.shop.application.port.out.ProductRepository;
import com.shop.domain.Product;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryProductRepository implements ProductRepository {

    private final Map<Long, Product> products = new HashMap<>();

    public void save(Product product) {
        products.put(product.getId(), product);
    }

    @Override
    public Optional<Product> findById(Long productId) {
        return Optional.ofNullable(products.get(productId));
    }
}
