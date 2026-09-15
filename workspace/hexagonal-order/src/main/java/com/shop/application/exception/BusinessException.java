package com.shop.application.exception;

/**
 * Loi nghiep vu (khach hang lam sai), khac voi loi ky thuat (DB chet, mat mang).
 * Adapter in se map loai loi nay ra HTTP 4xx.
 */
public abstract class BusinessException extends RuntimeException {

    protected BusinessException(String message) {
        super(message);
    }
}
