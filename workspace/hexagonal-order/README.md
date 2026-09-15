# Place Order — Hexagonal Architecture (Java, không framework)

Bài tập triển khai use case **Place Order** theo Hexagonal Architecture (Ports & Adapters).
Project **không có dependency nào** — đó chính là điểm cần thấy: domain + application chạy được
độc lập, không cần Spring, không cần JPA, không cần DB.

## Chạy thử

Windows PowerShell:

```powershell
javac -d out (Get-ChildItem -Recurse -Filter *.java src/main/java | % FullName); java -cp out com.shop.bootstrap.Main
```

Git Bash / Linux / macOS:

```bash
javac -d out $(find src/main/java -name "*.java") && java -cp out com.shop.bootstrap.Main
```

Hoặc mở project bằng IntelliJ/Eclipse qua `pom.xml` rồi chạy `com.shop.bootstrap.Main`.

## Cấu trúc

```
src/main/java/com/shop/
├── domain/                          <- KHÔNG phụ thuộc vào bất cứ tầng nào
│   ├── Order.java                   <- calculateTotal(), applyDiscount()
│   ├── OrderItem.java               <- lineTotal(), snapshot giá lúc đặt hàng
│   ├── Product.java
│   └── Customer.java
│
├── application/
│   ├── port/in/                     <- INPUT PORT (driving)
│   │   ├── PlaceOrderUseCase.java
│   │   ├── PlaceOrderCommand.java
│   │   └── PlaceOrderResponse.java
│   ├── port/out/                    <- OUTPUT PORT (driven)
│   │   ├── CustomerRepository.java
│   │   ├── ProductRepository.java
│   │   ├── OrderRepository.java
│   │   └── InventoryRepository.java
│   ├── service/
│   │   └── PlaceOrderService.java   <- điều phối flow, chỉ biết 4 interface trên
│   └── exception/                   <- lỗi nghiệp vụ, không phải lỗi HTTP
│
├── adapter/
│   ├── in/                          <- INPUT ADAPTER
│   │   ├── OrderController.java     <- POST /orders
│   │   ├── PlaceOrderRequest.java   <- Request DTO
│   │   ├── PlaceOrderResponseDto.java
│   │   └── ApiResponse.java         <- thay cho ResponseEntity của Spring
│   └── out/                         <- OUTPUT ADAPTER
│       ├── InMemoryCustomerRepository.java
│       ├── InMemoryProductRepository.java
│       ├── InMemoryOrderRepository.java
│       ├── InMemoryInventoryRepository.java
│       └── MySqlProductRepository.java   <- bộ khung, để thấy tính thay thế được
│
└── bootstrap/
    └── Main.java                    <- composition root: nơi DUY NHẤT lắp ráp mọi thứ
```

## Chiều phụ thuộc (điều quan trọng nhất)

```
adapter.in  --> application.port.in <-- application.service --> application.port.out <-- adapter.out
                                              |
                                              +--> domain
```

Tất cả mũi tên đều **chĩa vào trong**. Không có mũi tên nào đi từ `application` hay `domain`
ra `adapter`. Kiểm chứng nhanh — liệt kê toàn bộ `import` của 2 tầng trong:

```bash
grep -rh "^import" src/main/java/com/shop/domain src/main/java/com/shop/application | sort -u
```

Kết quả chỉ có `java.*` và `com.shop.domain` / `com.shop.application`.
Không một dòng nào import `com.shop.adapter`, `javax.*`, `jdbc` hay `springframework`.

## Flow của use case (đúng theo sequence trong đề)

`OrderController.placeOrder()`
→ đổi Request DTO thành `PlaceOrderCommand`
→ `PlaceOrderService.execute()`
→ `customerRepository.findById()` + kiểm tra active
→ với từng item: `productRepository.findById()` + `inventoryRepository.availableQuantity()`
→ `Order.create()` → `order.calculateTotal()` → `order.applyDiscount()`
→ `orderRepository.save()` (adapter sinh id)
→ `inventoryRepository.decrease()`
→ trả `PlaceOrderResponse` → Controller đổi thành Response DTO.

## Kết quả chạy thật (8 kịch bản trong Main)

| # | Kịch bản | Kết quả |
|---|----------|---------|
| 1 | 500.000×2 + 300.000×1 = **1.300.000** → giảm 10% | `201` final = **1.170.000**, tồn kho trừ đúng |
| 2 | 300.000×2 = 600.000 (dưới ngưỡng) | `201` final = 600.000, không giảm |
| 3 | Đúng ngưỡng 1.000.000 (`>=`) | `201` final = 900.000 |
| 4 | Khách không tồn tại | `404` |
| 5 | Khách bị khoá | `400` |
| 6 | Sản phẩm không tồn tại | `404` |
| 7 | Không đủ tồn kho | `409` |
| 8 | quantity = 0 | `400` |

## Vì sao thiết kế port như vậy

Nguyên tắc: **"PlaceOrderService cần gì từ bên ngoài?"** — chỉ khai báo đúng từng ấy.

- `CustomerRepository.findById()` trả `Optional<Customer>` thay vì `null`, ép người gọi
  phải xử lý trường hợp không tìm thấy. Không có `save()` vì use case này không tạo khách hàng.
- `ProductRepository.findById()` — chỉ cần đọc tên + giá để dựng `OrderItem`.
  (Nâng cấp sau: thêm `findAllByIds()` để tránh N+1 query khi đơn có nhiều dòng.)
- `InventoryRepository` tách đúng 2 việc service cần: **hỏi** `availableQuantity()` và
  **trừ** `decrease()`. `hasEnough()` là `default method` suy ra từ `availableQuantity()`.
- `OrderRepository.save()` **trả về** `Order` đã có id, vì sinh id là việc của hạ tầng
  (`AUTO_INCREMENT`, sequence, UUID) chứ không phải của domain.

Các port nói bằng **ngôn ngữ nghiệp vụ** (`findById`, `decrease`), không nói bằng ngôn ngữ
hạ tầng (`executeQuery`, `getEntityManager`).

## Muốn đổi sang MySQL thật thì sửa ở đâu?

Đúng **một dòng** trong `bootstrap/Main.java`:

```java
// new InMemoryProductRepository()
new MySqlProductRepository(dataSource)
```

`PlaceOrderService`, `Order`, `OrderController` không đổi một ký tự nào.
Xem `adapter/out/MySqlProductRepository.java` để thấy bộ khung.

## Muốn gắn Spring Boot thì làm gì?

Chỉ thêm annotation, không sửa logic:

- `PlaceOrderService` → `@Service`
- Các `InMemory*Repository` → `@Repository` (hoặc khai báo `@Bean` trong `@Configuration` —
  chính là thứ thay thế cho `bootstrap/Main`)
- `OrderController` → `@RestController` + `@PostMapping`, đổi `ApiResponse<?>` thành
  `ResponseEntity<?>`, thêm `@RequestBody` vào tham số
- Chuyển `try/catch` trong controller sang `@RestControllerAdvice`

## Những điểm dễ sai mà project này tránh

- Tính tiền / giảm giá trong Service → phải nằm trong `Order` (domain).
- Controller gọi thẳng `PlaceOrderService` → phải gọi qua interface `PlaceOrderUseCase`.
- Dùng entity domain làm DTO JSON → tách `PlaceOrderRequest` / `PlaceOrderResponseDto`.
- Domain ném exception của Spring → ném `BusinessException`, adapter mới map ra HTTP status.
- Repository trả `null` → trả `Optional`.
