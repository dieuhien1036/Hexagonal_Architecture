package com.shop.adapter.in;

import com.shop.application.port.in.PlaceOrderResponse;

import java.math.BigDecimal;

/**
 * RESPONSE DTO - hinh dang JSON tra ve cho client.
 *
 * Tach rieng khoi PlaceOrderResponse cua application de sau nay doi format JSON
 * (them field, doi ten) ma khong dung den application.
 */
public class PlaceOrderResponseDto {

    private final Long orderId;
    private final BigDecimal totalAmount;
    private final BigDecimal discountAmount;
    private final BigDecimal finalAmount;

    private PlaceOrderResponseDto(Long orderId,
                                  BigDecimal totalAmount,
                                  BigDecimal discountAmount,
                                  BigDecimal finalAmount) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
    }

    public static PlaceOrderResponseDto from(PlaceOrderResponse response) {
        return new PlaceOrderResponseDto(
                response.getOrderId(),
                response.getTotalAmount(),
                response.getDiscountAmount(),
                response.getFinalAmount());
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

    /** Khong co Jackson nen tu in ra cho giong JSON de xem ket qua. */
    @Override
    public String toString() {
        return "{"
                + "\"orderId\": " + orderId + ", "
                + "\"totalAmount\": " + totalAmount + ", "
                + "\"discountAmount\": " + discountAmount + ", "
                + "\"finalAmount\": " + finalAmount
                + "}";
    }
}
