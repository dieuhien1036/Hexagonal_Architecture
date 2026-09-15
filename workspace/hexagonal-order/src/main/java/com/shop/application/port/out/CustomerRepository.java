package com.shop.application.port.out;

import com.shop.domain.Customer;

import java.util.Optional;

/**
 * OUTPUT PORT (driven port).
 *
 * Cau hoi khi thiet ke port: "PlaceOrderService CAN GI tu ben ngoai?"
 * -> No chi can tra loi cau hoi: khach nay co that khong?
 *
 * Tra ve Optional thay vi null de bat buoc nguoi goi phai xu ly truong hop khong tim thay.
 * Interface nay nam trong application nhung do domain/application "so huu",
 * adapter la ben phai di theo (Dependency Inversion).
 */
public interface CustomerRepository {

    Optional<Customer> findById(Long customerId);
}
