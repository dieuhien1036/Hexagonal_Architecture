package com.shop.application.exception;

public class CustomerNotActiveException extends BusinessException {

    public CustomerNotActiveException(Long customerId) {
        super("Khach hang id = " + customerId + " dang bi khoa, khong the dat hang");
    }
}
