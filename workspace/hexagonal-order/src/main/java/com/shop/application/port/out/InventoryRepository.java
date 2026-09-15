package com.shop.application.port.out;

/**
 * OUTPUT PORT: ton kho.
 *
 * Service can 2 viec:
 *  1. Hoi con bao nhieu hang  -> availableQuantity()
 *  2. Tru kho sau khi dat hang -> decrease()
 */
public interface InventoryRepository {

    /** So luong con lai trong kho cua mot san pham. Tra ve 0 neu san pham chua co trong kho. */
    int availableQuantity(Long productId);

    /** Tru kho. Adapter phai bao loi neu tru xuong am. */
    void decrease(Long productId, int quantity);

    /** Tien ich suy ra tu availableQuantity - de service doc cho de hieu. */
    default boolean hasEnough(Long productId, int quantity) {
        return availableQuantity(productId) >= quantity;
    }
}
