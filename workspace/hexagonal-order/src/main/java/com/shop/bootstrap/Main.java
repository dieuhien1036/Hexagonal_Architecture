package com.shop.bootstrap;

import com.shop.adapter.in.ApiResponse;
import com.shop.adapter.in.OrderController;
import com.shop.adapter.in.PlaceOrderRequest;
import com.shop.adapter.out.InMemoryCustomerRepository;
import com.shop.adapter.out.InMemoryInventoryRepository;
import com.shop.adapter.out.InMemoryOrderRepository;
import com.shop.adapter.out.InMemoryProductRepository;
import com.shop.application.port.in.PlaceOrderUseCase;
import com.shop.application.service.PlaceOrderService;
import com.shop.domain.Customer;
import com.shop.domain.Product;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * COMPOSITION ROOT - noi duy nhat trong ung dung duoc phep biet ca adapter lan application.
 *
 * Day chinh la viec ma Spring lam ho ban khi ban dung @Service / @Repository / @Bean.
 * Lam tay mot lan de thay ro: ai lap rap cai gi vao cai gi.
 *
 * Chay: java -cp out com.shop.bootstrap.Main
 */
public class Main {

    public static void main(String[] args) {
        // ---------- 1. Lap rap cac adapter out (driven side) ----------
        InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();
        InMemoryProductRepository productRepository = new InMemoryProductRepository();
        InMemoryOrderRepository orderRepository = new InMemoryOrderRepository();
        InMemoryInventoryRepository inventoryRepository = new InMemoryInventoryRepository();

        // ---------- 2. Do du lieu mau vao "DB" ----------
        customerRepository.save(new Customer(1L, "Nguyen Van A", true));
        customerRepository.save(new Customer(2L, "Tran Thi B (bi khoa)", false));

        productRepository.save(new Product(100L, "Product A", new BigDecimal("500000")));
        productRepository.save(new Product(200L, "Product B", new BigDecimal("300000")));
        productRepository.save(new Product(300L, "Product C", new BigDecimal("150000")));

        inventoryRepository.setStock(100L, 10);
        inventoryRepository.setStock(200L, 5);
        inventoryRepository.setStock(300L, 1);

        // ---------- 3. Tiem port vao application service ----------
        PlaceOrderUseCase placeOrderUseCase = new PlaceOrderService(
                customerRepository,
                productRepository,
                orderRepository,
                inventoryRepository);

        // ---------- 4. Tiem use case vao adapter in ----------
        OrderController orderController = new OrderController(placeOrderUseCase);

        // ---------- 5. Gia lap cac request POST /orders ----------

        scenario("1. Dat hang duoc giam gia (500.000 x 2 + 300.000 x 1 = 1.300.000 -> 1.170.000)");
        call(orderController, new PlaceOrderRequest(1L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(100L, 2),
                new PlaceOrderRequest.ItemRequest(200L, 1))));
        showStock(inventoryRepository, Arrays.asList(100L, 200L));

        scenario("2. Duoi nguong, khong giam gia (300.000 x 2 = 600.000)");
        call(orderController, new PlaceOrderRequest(1L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(200L, 2))));

        scenario("3. Dung dung nguong 1.000.000 -> van duoc giam (>=)");
        call(orderController, new PlaceOrderRequest(1L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(100L, 2))));

        scenario("4. Khach khong ton tai -> 404");
        call(orderController, new PlaceOrderRequest(99L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(100L, 1))));

        scenario("5. Khach bi khoa -> 400");
        call(orderController, new PlaceOrderRequest(2L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(100L, 1))));

        scenario("6. San pham khong ton tai -> 404");
        call(orderController, new PlaceOrderRequest(1L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(999L, 1))));

        scenario("7. Khong du ton kho (Product C chi con 1) -> 409");
        call(orderController, new PlaceOrderRequest(1L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(300L, 5))));

        scenario("8. Quantity khong hop le -> 400");
        call(orderController, new PlaceOrderRequest(1L, Arrays.asList(
                new PlaceOrderRequest.ItemRequest(100L, 0))));

        System.out.println();
        System.out.println("Tong so don da luu trong OrderRepository: " + orderRepository.count());
    }

    private static void scenario(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }

    private static void call(OrderController controller, PlaceOrderRequest request) {
        System.out.println("--> POST /orders " + toJson(request));
        ApiResponse<?> response = controller.placeOrder(request);
        System.out.println("<-- " + response);
    }

    private static void showStock(InMemoryInventoryRepository inventory, List<Long> productIds) {
        StringBuilder sb = new StringBuilder("    ton kho sau khi tru:");
        for (Long id : productIds) {
            sb.append(" #").append(id).append("=").append(inventory.availableQuantity(id));
        }
        System.out.println(sb);
    }

    private static String toJson(PlaceOrderRequest request) {
        StringBuilder sb = new StringBuilder("{\"customerId\": ")
                .append(request.getCustomerId())
                .append(", \"items\": [");
        for (int i = 0; i < request.getItems().size(); i++) {
            PlaceOrderRequest.ItemRequest item = request.getItems().get(i);
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("{\"productId\": ").append(item.getProductId())
              .append(", \"quantity\": ").append(item.getQuantity()).append("}");
        }
        return sb.append("]}").toString();
    }
}
