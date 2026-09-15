package com.shop.domain;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Domain entity: mot dong trong don hang.
 *
 * Luu lai unitPrice TAI THOI DIEM DAT HANG (snapshot).
 * Neu sau nay Product doi gia, don hang cu khong bi thay doi theo.
 */
public class OrderItem {

    private final Long productId;
    private final String productName;
    private final BigDecimal unitPrice;
    private final int quantity;

    public OrderItem(Long productId, String productName, BigDecimal unitPrice, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("So luong phai > 0, nhan duoc: " + quantity);
        }
        this.productId = Objects.requireNonNull(productId, "productId khong duoc null");
        this.productName = Objects.requireNonNull(productName, "productName khong duoc null");
        this.unitPrice = Objects.requireNonNull(unitPrice, "unitPrice khong duoc null");
        this.quantity = quantity;
    }

    /** Tao OrderItem tu Product + so luong. */
    public static OrderItem of(Product product, int quantity) {
        return new OrderItem(product.getId(), product.getName(), product.getPrice(), quantity);
    }

    /** Thanh tien cua dong nay = don gia x so luong. */
    public BigDecimal lineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{product=" + productName + " (#" + productId + ")"
                + ", unitPrice=" + unitPrice
                + ", quantity=" + quantity
                + ", lineTotal=" + lineTotal() + "}";
    }
}
