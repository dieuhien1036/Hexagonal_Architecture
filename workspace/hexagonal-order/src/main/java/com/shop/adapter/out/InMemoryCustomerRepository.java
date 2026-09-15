package com.shop.adapter.out;

import com.shop.application.port.out.CustomerRepository;
import com.shop.domain.Customer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * OUTPUT ADAPTER: fake DB bang Map.
 *
 * Doi sang MySQL that = viet class khac implement cung interface,
 * KHONG phai sua mot dong nao trong PlaceOrderService.
 */
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<Long, Customer> customers = new HashMap<>();

    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public Optional<Customer> findById(Long customerId) {
        return Optional.ofNullable(customers.get(customerId));
    }
}
