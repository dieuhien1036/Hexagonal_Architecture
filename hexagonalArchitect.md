bài tập: triển khai Place order cho hexagonal theo sequence sample (Dùng Java)

1. Business requirement
Hệ thống bán hàng có use case:
Customer đặt hàng → Place Order
Khi customer đặt hàng:
Nhận customerId và danh sách sản phẩm.
Kiểm tra customer có tồn tại.
Kiểm tra từng product có tồn tại.
Kiểm tra số lượng tồn kho đủ.
Tạo Order.
Tính tổng tiền.
Nếu tổng tiền >= 1,000,000 → giảm 10%.
Lưu Order.
Trừ inventory.
Trả về orderId và totalAmount.

2. Sequence sample
Hãy coi đây là sequence diagram mà bạn cần triển khai:
Customer
   |
   | POST /orders
   ↓
OrderController
   |
   | placeOrder(command)
   ↓
PlaceOrderUseCase
   |
   | findCustomer()
   ↓
CustomerRepository
   |
   |--------------------→ Customer DB
   |
   | findProduct()
   ↓
ProductRepository
   |
   |--------------------→ Product DB
   |
   ↓
Order
   |
   | calculateTotal()
   | applyDiscount()
   |
   ↓
OrderRepository
   |
   |--------------------→ Order DB
   |
   ↓
InventoryRepository
   |
   |--------------------→ Inventory DB
   |
   ↓
OrderController
   |
   ↓
Customer

3. Architecture bạn phải dùng
Tối thiểu tổ chức như sau:
src/main/java/

├── domain/
│   ├── Order.java
│   ├── OrderItem.java
│   └── Product.java
│
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   └── PlaceOrderUseCase.java
│   │   │
│   │   └── out/
│   │       ├── CustomerRepository.java
│   │       ├── ProductRepository.java
│   │       ├── OrderRepository.java
│   │       └── InventoryRepository.java
│   │
│   └── service/
│       └── PlaceOrderService.java
│
└── adapter/
    ├── in/
    │   └── OrderController.java
    │
    └── out/
        ├── MySqlCustomerRepository.java
        ├── MySqlProductRepository.java
        ├── MySqlOrderRepository.java
        └── MySqlInventoryRepository.java

Không cần Spring Data/JPA ngay. Có thể implement bằng fake/in-memory repository trước.
Mục tiêu hiện tại là hiểu architecture.

4. Nhiệm vụ 1 — Domain
Class: 
	Order
	OrderItem
	Product
Order cần có:
	id
	customerId
	items
Và các behavior:
	calculateTotal()
	applyDiscount()
Ví dụ:
	Product A: 500,000 × 2
	Product B: 300,000 × 1
	Total = 1,300,000
	Discount = 10%
	Final = 1,170,000

5. Nhiệm vụ 2 — Input Port
	public interface PlaceOrderUseCase {

		PlaceOrderResponse execute(
			PlaceOrderCommand command
		);
	}
	
Thiết kế
	PlaceOrderCommand
	PlaceOrderResponse
	
6. Nhiệm vụ 3 — Output Ports
PlaceOrderService cần:
tìm Customer
tìm Product
lưu Order
trừ Inventory

tạo các interface:
CustomerRepository
ProductRepository
OrderRepository
InventoryRepository

Ví dụ:
public interface ProductRepository {

    Product findById(Long productId);
}

Nhưng tự thiết kế method cho các interface còn lại.
Đừng copy y chang một mẫu có sẵn.
Hãy suy nghĩ:
"PlaceOrderService cần gì từ bên ngoài?"
rồi mới thiết kế Port.
	
7. Nhiệm vụ 4 — Application
Đây là class chính:
public class PlaceOrderService
        implements PlaceOrderUseCase {
}
Bạn phải implement flow:
execute()
   ↓
find customer
   ↓
validate customer
   ↓
find products
   ↓
validate products
   ↓
check inventory
   ↓
create Order
   ↓
calculate total
   ↓
apply discount
   ↓
save Order
   ↓
update inventory
   ↓
return response

Chú ý
	PlaceOrderService không được biết:
	MySQL
	JPA
	Hibernate
	SQL
	Spring Data

Nó chỉ biết:
	CustomerRepository
	ProductRepository
	OrderRepository
	InventoryRepository

8. Nhiệm vụ 5 — Adapter
Sau khi Application xong, tạo implementation:
	MySqlCustomerRepository
	MySqlProductRepository
	MySqlOrderRepository
	MySqlInventoryRepository

Ví dụ:
public class MySqlProductRepository
        implements ProductRepository {

    @Override
    public Product findById(Long productId) {
        // TODO
    }
}

Không cần kết nối MySQL thật ở bước đầu.
Bạn có thể dùng:
Map<Long, Product> để fake database.

Ví dụ:
public class InMemoryProductRepository
        implements ProductRepository {

    private Map<Long, Product> products;
}

9. Nhiệm vụ 6 — Input Adapter
Tạo:
@RestController
public class OrderController {
}

Endpoint: POST /orders
Request:
{
  "customerId": 1,
  "items": [
    {
      "productId": 100,
      "quantity": 2
    },
    {
      "productId": 200,
      "quantity": 1
    }
  ]
}

Controller chỉ nên làm:
HTTP Request
     ↓
Request DTO
     ↓
Command
     ↓
PlaceOrderUseCase
     ↓
Response

Không viết business logic trong Controller.