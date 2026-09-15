package com.shop.application.exception;

public class ProductNotFoundException extends BusinessException {

    public ProductNotFoundException(Long productId) {
        super("Khong tim thay san pham co id = " + productId);
    }
}
