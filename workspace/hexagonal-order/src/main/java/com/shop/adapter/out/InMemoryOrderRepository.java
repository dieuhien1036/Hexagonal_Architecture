package com.shop.adapter.out;

import com.shop.application.port.out.OrderRepository;
import com.shop.domain.Order;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * OUTPUT ADAPTER cho OrderRepository.
 *
 * AtomicLong o day dong vai tro AUTO_INCREMENT cua MySQL:
 * sinh id la viec cua ha tang, domain khong quan tam.
 */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<Long, Order> orders = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1000);

    @Override
    public Order save(Order order) {
        if (order.getId() == null) {
            order.assignId(sequence.incrementAndGet());
        }
        orders.put(order.getId(), order);
        return order;
    }

    public int count() {
        return orders.size();
    }
}
