package com.shop.application.port.in;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Command = du lieu dau vao cua use case, da duoc lam sach khoi HTTP/JSON.
 *
 * Adapter in (Controller) chiu trach nhiem dich Request DTO -> Command.
 * Nho vay Application khong he biet request den tu REST, gRPC hay CLI.
 */
public class PlaceOrderCommand {

    private final Long customerId;
    private final List<Item> items;

    public PlaceOrderCommand(Long customerId, List<Item> items) {
        if (customerId == null) {
            throw new IllegalArgumentException("customerId la bat buoc");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Danh sach san pham khong duoc rong");
        }
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
    }

    public Long getCustomerId() {
        return customerId;
    }

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    /** Mot dong hang muon dat: mua san pham nao, bao nhieu cai. */
    public static class Item {

        private final Long productId;
        private final int quantity;

        public Item(Long productId, int quantity) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("So luong phai > 0, nhan duoc: " + quantity);
            }
            this.productId = Objects.requireNonNull(productId, "productId la bat buoc");
            this.quantity = quantity;
        }

        public Long getProductId() {
            return productId;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
