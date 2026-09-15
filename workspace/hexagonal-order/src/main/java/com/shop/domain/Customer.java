package com.shop.domain;

import java.util.Objects;

/**
 * Domain entity: khach hang.
 *
 * De bai chi liet ke Order / OrderItem / Product, nhung use case bat buoc phai
 * "kiem tra customer co ton tai" -> can mot kieu du lieu domain de CustomerRepository tra ve.
 */
public class Customer {

    private final Long id;
    private final String name;
    private final boolean active;

    public Customer(Long id, String name, boolean active) {
        this.id = Objects.requireNonNull(id, "customerId khong duoc null");
        this.name = Objects.requireNonNull(name, "ten khach hang khong duoc null");
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    /** Khach bi khoa thi khong duoc dat hang -> quy tac nghiep vu nam trong domain. */
    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name='" + name + "', active=" + active + "}";
    }
}
