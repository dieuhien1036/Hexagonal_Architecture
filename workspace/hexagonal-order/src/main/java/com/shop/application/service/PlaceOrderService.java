package com.shop.application.service;

import com.shop.application.exception.CustomerNotActiveException;
import com.shop.application.exception.CustomerNotFoundException;
import com.shop.application.exception.InsufficientInventoryException;
import com.shop.application.exception.ProductNotFoundException;
import com.shop.application.port.in.PlaceOrderCommand;
import com.shop.application.port.in.PlaceOrderResponse;
import com.shop.application.port.in.PlaceOrderUseCase;
import com.shop.application.port.out.CustomerRepository;
import com.shop.application.port.out.InventoryRepository;
import com.shop.application.port.out.OrderRepository;
import com.shop.application.port.out.ProductRepository;
import com.shop.domain.Customer;
import com.shop.domain.Order;
import com.shop.domain.OrderItem;
import com.shop.domain.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * APPLICATION SERVICE - trai tim cua use case Place Order.
 *
 * Class nay chi biet 4 interface port ra ngoai. No KHONG biet:
 * MySQL, JDBC, JPA, Hibernate, Spring Data, HTTP, JSON.
 *
 * Nhiem vu cua no la DIEU PHOI (orchestrate), khong phai tinh toan nghiep vu:
 * viec cong tien / giam gia da nam trong Order.
 *
 * Flow (dung theo sequence trong de bai):
 *   find customer -> validate customer -> find products -> validate products
 *   -> check inventory -> create Order -> calculateTotal -> applyDiscount
 *   -> save Order -> update inventory -> return response
 */
public class PlaceOrderService implements PlaceOrderUseCase {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;

    /**
     * Dependency Injection bang constructor.
     * Ai truyen implementation vao? -> bootstrap/Main (composition root),
     * hoac Spring neu sau nay ban gan @Service / @Bean.
     */
    public PlaceOrderService(CustomerRepository customerRepository,
                             ProductRepository productRepository,
                             OrderRepository orderRepository,
                             InventoryRepository inventoryRepository) {
        this.customerRepository = Objects.requireNonNull(customerRepository);
        this.productRepository = Objects.requireNonNull(productRepository);
        this.orderRepository = Objects.requireNonNull(orderRepository);
        this.inventoryRepository = Objects.requireNonNull(inventoryRepository);
    }

    @Override
    public PlaceOrderResponse execute(PlaceOrderCommand command) {
        Objects.requireNonNull(command, "command khong duoc null");

        // 1 + 2. Tim va kiem tra khach hang
        Customer customer = customerRepository.findById(command.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.getCustomerId()));
        if (!customer.isActive()) {
            throw new CustomerNotActiveException(customer.getId());
        }

        // 3 + 4 + 5. Tim san pham, kiem tra ton tai, kiem tra ton kho
        List<OrderItem> orderItems = new ArrayList<>();
        for (PlaceOrderCommand.Item requested : command.getItems()) {
            Long productId = requested.getProductId();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));

            int available = inventoryRepository.availableQuantity(productId);
            if (available < requested.getQuantity()) {
                throw new InsufficientInventoryException(productId, requested.getQuantity(), available);
            }

            orderItems.add(OrderItem.of(product, requested.getQuantity()));
        }

        // 6 + 7 + 8. Tao Order roi de DOMAIN tu tinh tien va giam gia
        Order order = Order.create(customer.getId(), orderItems);
        order.calculateTotal();
        order.applyDiscount();

        // 9. Luu don hang (adapter sinh id)
        Order savedOrder = orderRepository.save(order);

        // 10. Tru ton kho
        for (OrderItem item : savedOrder.getItems()) {
            inventoryRepository.decrease(item.getProductId(), item.getQuantity());
        }

        // 11. Tra ket qua ve cho adapter in
        return new PlaceOrderResponse(
                savedOrder.getId(),
                savedOrder.getTotalAmount(),
                savedOrder.getDiscountAmount(),
                savedOrder.getFinalAmount());
    }
}
