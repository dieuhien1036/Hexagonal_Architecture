package com.shop.adapter.in;

import com.shop.application.exception.CustomerNotFoundException;
import com.shop.application.exception.InsufficientInventoryException;
import com.shop.application.exception.ProductNotFoundException;
import com.shop.application.exception.BusinessException;
import com.shop.application.port.in.PlaceOrderCommand;
import com.shop.application.port.in.PlaceOrderResponse;
import com.shop.application.port.in.PlaceOrderUseCase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * INPUT ADAPTER (driving adapter): POST /orders
 *
 * Controller chi lam 4 viec, TUYET DOI khong co business logic:
 *   HTTP Request -> Request DTO -> Command -> goi UseCase -> Response DTO
 *
 * Khi gan Spring Boot vao, chi can them annotation (khong sua logic):
 *
 *   @RestController
 *   @RequestMapping("/orders")
 *   public class OrderController {
 *       @PostMapping
 *       public ResponseEntity<PlaceOrderResponseDto> placeOrder(@RequestBody PlaceOrderRequest request) { ... }
 *   }
 *
 * Chu y: Controller phu thuoc vao INTERFACE PlaceOrderUseCase,
 * khong phu thuoc vao class PlaceOrderService.
 */
public class OrderController {

    private final PlaceOrderUseCase placeOrderUseCase;

    public OrderController(PlaceOrderUseCase placeOrderUseCase) {
        this.placeOrderUseCase = Objects.requireNonNull(placeOrderUseCase);
    }

    /** POST /orders */
    public ApiResponse<?> placeOrder(PlaceOrderRequest request) {
        try {
            PlaceOrderCommand command = toCommand(request);       // DTO -> Command
            PlaceOrderResponse response = placeOrderUseCase.execute(command);  // goi vao hexagon
            return ApiResponse.created(PlaceOrderResponseDto.from(response));  // -> Response DTO
        } catch (CustomerNotFoundException | ProductNotFoundException e) {
            return ApiResponse.notFound(error(e.getMessage()));            // 404
        } catch (InsufficientInventoryException e) {
            return ApiResponse.conflict(error(e.getMessage()));            // 409
        } catch (BusinessException | IllegalArgumentException e) {
            return ApiResponse.badRequest(error(e.getMessage()));          // 400
        }
    }

    /**
     * Dich DTO cua tang HTTP sang Command cua tang Application.
     * Day la "chot chan": tu day tro vao trong khong ai biet HTTP la gi nua.
     */
    private PlaceOrderCommand toCommand(PlaceOrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body rong");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Danh sach san pham khong duoc rong");
        }

        List<PlaceOrderCommand.Item> items = new ArrayList<>();
        for (PlaceOrderRequest.ItemRequest item : request.getItems()) {
            if (item.getQuantity() == null) {
                throw new IllegalArgumentException("Thieu quantity cho san pham " + item.getProductId());
            }
            items.add(new PlaceOrderCommand.Item(item.getProductId(), item.getQuantity()));
        }
        return new PlaceOrderCommand(request.getCustomerId(), items);
    }

    private String error(String message) {
        return "{\"error\": \"" + message + "\"}";
    }
}
