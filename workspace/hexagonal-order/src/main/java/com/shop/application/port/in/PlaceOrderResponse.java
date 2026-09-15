package com.shop.application.port.in;

import java.math.BigDecimal;

/**
 * Ket qua tra ve cua use case.
 *
 * De bai yeu cau toi thieu orderId + totalAmount; minh tra them so tien duoc giam
 * de adapter co du du lieu hien thi ma khong phai tinh lai (tranh logic ro ri ra ngoai).
 */
public class PlaceOrderResponse {

    private final Long orderId;
    private final BigDecimal totalAmount;      // tong truoc giam
    private final BigDecimal discountAmount;   // so tien duoc giam
    private final BigDecimal finalAmount;      // so tien phai tra

    public PlaceOrderResponse(Long orderId,
                              BigDecimal totalAmount,
                              BigDecimal discountAmount,
                              BigDecimal finalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }

    public Long getOrderId() {
        return orderId;
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
        return "PlaceOrderResponse{orderId=" + orderId
                + ", totalAmount=" + totalAmount
                + ", discountAmount=" + discountAmount
                + ", finalAmount=" + finalAmount + "}";
    }
}
