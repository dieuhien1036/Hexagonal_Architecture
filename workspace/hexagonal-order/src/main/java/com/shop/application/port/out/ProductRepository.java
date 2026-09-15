package com.shop.application.port.out;

import com.shop.domain.Product;

import java.util.Optional;

/**
 * OUTPUT PORT: lay thong tin san pham (ten, gia) de dung khi tao OrderItem.
 *
 * Luu y: port KHONG co method kieu "executeSql(...)" hay "getEntity(...)".
 * Port noi bang ngon ngu nghiep vu, khong noi bang ngon ngu ha tang.
 */
public interface ProductRepository {

    Optional<Product> findById(Long productId);
}
