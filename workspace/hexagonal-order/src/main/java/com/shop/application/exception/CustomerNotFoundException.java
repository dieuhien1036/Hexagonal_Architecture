package com.shop.application.exception;

public class CustomerNotFoundException extends BusinessException {

    public CustomerNotFoundException(Long customerId) {
        super("Khong tim thay khach hang co id = " + customerId);
    }
}
