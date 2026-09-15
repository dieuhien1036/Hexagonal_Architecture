package com.shop.adapter.out;

import com.shop.application.port.out.ProductRepository;
import com.shop.domain.Product;

import java.util.Optional;

/**
 * Bo khung cho adapter MySQL that (nhiem vu 5 cua de bai).
 *
 * Diem quan trong can thay: class nay va InMemoryProductRepository
 * implement CUNG MOT interface ProductRepository.
 * Khi nao ban noi duoc DB that, chi can doi mot dong trong bootstrap/Main:
 *
 *     new InMemoryProductRepository()  ->  new MySqlProductRepository(dataSource)
 *
 * Application va Domain khong doi mot ky tu nao. Do la gia tri cua Hexagonal.
 */
public class MySqlProductRepository implements ProductRepository {

    // private final DataSource dataSource;  // se can khi noi JDBC that

    @Override
    public Optional<Product> findById(Long productId) {
        // TODO: SELECT id, name, price FROM products WHERE id = ?
        //       roi map ResultSet -> new Product(id, name, price)
        throw new UnsupportedOperationException(
                "Chua noi MySQL. Dang dung InMemoryProductRepository trong bootstrap/Main.");
    }
}
