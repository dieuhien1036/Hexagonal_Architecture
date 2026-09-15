package com.shop.adapter.in;

import java.util.List;

/**
 * REQUEST DTO - hinh dang cua JSON gui len, khong hon khong kem.
 *
 * {
 *   "customerId": 1,
 *   "items": [
 *     { "productId": 100, "quantity": 2 },
 *     { "productId": 200, "quantity": 1 }
 *   ]
 * }
 *
 * DTO co setter/field public vi thu vien JSON (Jackson) can no.
 * Do la ly do KHONG duoc dung domain object lam DTO.
 */
public class PlaceOrderRequest {

    private Long customerId;
    private List<ItemRequest> items;

    public PlaceOrderRequest() {
    }

    public PlaceOrderRequest(Long customerId, List<ItemRequest> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public List<ItemRequest> getItems() {
        return items;
    }

    public void setItems(List<ItemRequest> items) {
        this.items = items;
    }

    public static class ItemRequest {

        private Long productId;
        private Integer quantity;

        public ItemRequest() {
        }

        public ItemRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
}
