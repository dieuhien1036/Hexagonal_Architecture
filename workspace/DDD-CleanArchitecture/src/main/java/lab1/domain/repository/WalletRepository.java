package lab1.domain.repository;

import lab1.domain.model.Wallet;

import java.util.Optional;
import java.util.UUID;

/**
 * Port (interface) do domain định nghĩa. Tầng infrastructure sẽ implement
 * -> Dependency Rule: phụ thuộc hướng VÀO domain, domain không biết DB là gì.
 */
public interface WalletRepository {
    Optional<Wallet> findById(UUID id);

    void save(Wallet wallet);
}
