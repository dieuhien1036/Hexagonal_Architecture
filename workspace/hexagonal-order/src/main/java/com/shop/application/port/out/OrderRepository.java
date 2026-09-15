package com.shop.application.port.out;

import com.shop.domain.Order;

/**
 * OUTPUT PORT: luu don hang.
 *
 * save() tra ve Order da co id, vi viec sinh id la trach nhiem cua ha tang
 * (AUTO_INCREMENT cua MySQL, sequence, UUID generator...), khong phai cua domain.
 *
 * Chi khai bao dung cai use case can (YAGNI). Chua can findById() thi chua them.
 */
public interface OrderRepository {

    Order save(Order order);
}
