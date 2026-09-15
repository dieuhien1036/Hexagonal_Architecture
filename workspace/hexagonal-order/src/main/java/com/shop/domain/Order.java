package com.shop.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Domain aggregate root: don hang.
 *
 * Toan bo quy tac tinh tien nam O DAY, khong nam trong Service, khong nam trong Controller.
 *  - calculateTotal() : cong thanh tien cac dong
 *  - applyDiscount()  : tong >= 1.000.000 thi giam 10%
 */
public class Order {

    /** Nguong duoc giam gia: 1.000.000 */
    public static final BigDecimal DISCOUNT_THRESHOLD = new BigDecimal("1000000");

    /** Ty le giam: 10% */
    public static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.10");

    private Long id;
    private final Long customerId;
    private final List<OrderItem> items;

    private BigDecimal totalAmount;      // tong truoc giam
    private BigDecimal discountAmount;   // so tien duoc giam
    private BigDecimal finalAmount;      // so tien phai tra

    private Order(Long id, Long customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Don hang phai co it nhat 1 san pham");
        }
        this.id = id;
        this.customerId = Objects.requireNonNull(customerId, "customerId khong duoc null");
        this.items = new ArrayList<>(items);
        this.totalAmount = BigDecimal.ZERO;
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = BigDecimal.ZERO;
    }

    /** Tao don hang moi (chua co id - id do OrderRepository sinh ra khi save). */
    public static Order create(Long customerId, List<OrderItem> items) {
        return new Order(null, customerId, items);
    }

    /** Dung khi adapter doc don hang da ton tai tu DB len. */
    public static Order rehydrate(Long id, Long customerId, List<OrderItem> items) {
        Order order = new Order(id, customerId, items);
        order.calculateTotal();
        order.applyDiscount();
        return order;
    }

    /**
     * Tinh tong tien truoc giam gia = sum(unitPrice x quantity).
     */
    public BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.lineTotal());
        }
        this.totalAmount = scale(total);
        // chua goi applyDiscount() thi so tien phai tra = tong
        this.discountAmount = BigDecimal.ZERO;
        this.finalAmount = this.totalAmount;
        return this.totalAmount;
    }

    /**
     * Ap dung chinh sach giam gia: tong >= 1.000.000 thi giam 10%.
     * Tra ve so tien cuoi cung phai tra.
     */
    public BigDecimal applyDiscount() {
        if (totalAmount.compareTo(DISCOUNT_THRESHOLD) >= 0) {
            this.discountAmount = scale(totalAmount.multiply(DISCOUNT_RATE));
        } else {
            this.discountAmount = BigDecimal.ZERO;
        }
        this.finalAmount = scale(totalAmount.subtract(discountAmount));
        return this.finalAmount;
    }

    public boolean isDiscounted() {
        return discountAmount.signum() > 0;
    }

    /** OrderRepository gan id sau khi luu xuong DB. */
    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Don hang da co id: " + this.id);
        }
        this.id = Objects.requireNonNull(id, "id khong duoc null");
    }

    private static BigDecimal scale(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    @Override
    public String toString() {
        return "Order{id=" + id
                + ", customerId=" + customerId
                + ", items=" + items.size()
                + ", total=" + totalAmount
                + ", discount=" + discountAmount
                + ", final=" + finalAmount + "}";
    }
}
