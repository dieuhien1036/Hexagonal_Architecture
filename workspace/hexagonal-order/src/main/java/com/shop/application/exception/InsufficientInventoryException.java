package com.shop.application.exception;

public class InsufficientInventoryException extends BusinessException {

    public InsufficientInventoryException(Long productId, int requested, int available) {
        super("San pham id = " + productId + " khong du hang: can " + requested
                + " nhung chi con " + available);
    }
}
