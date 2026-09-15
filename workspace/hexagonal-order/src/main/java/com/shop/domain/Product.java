package com.shop.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain entity: san pham.
 *
 * Domain KHONG biet gi ve DB, HTTP, framework.
 * No chi mo ta du lieu + quy tac nghiep vu cua chinh no.
 */
public class Product {

    private final Long id;
    private final String name;
    private final BigDecimal price;

    public Product(Long id, String name, BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Gia san pham khong hop le: " + price);
        }
        this.id = Objects.requireNonNull(id, "productId khong duoc null");
        this.name = Objects.requireNonNull(name, "ten san pham khong duoc null");
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
    }
}
