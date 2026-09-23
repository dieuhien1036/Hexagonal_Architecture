package lab1.infrastructure;

import lab1.domain.model.Wallet;
import lab1.domain.model.WalletStatus;
import lab1.domain.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter: lưu ví trong bộ nhớ. Lưu dạng "bản ghi" tách biệt với aggregate
 * (giống một bảng DB) để mô phỏng việc map giữa persistence model và domain model.
 */
public class InMemoryWalletRepository implements WalletRepository {

    private static final class WalletRecord {
        final UUID id;
        final BigDecimal balance;
        final String status;

        WalletRecord(UUID id, BigDecimal balance, String status) {
            this.id = id;
            this.balance = balance;
            this.status = status;
        }
    }

    private final Map<UUID, WalletRecord> table = new ConcurrentHashMap<>();

    @Override
    public Optional<Wallet> findById(UUID id) {
        WalletRecord r = table.get(id);
        if (r == null) return Optional.empty();
        return Optional.of(Wallet.reconstitute(r.id, r.balance, WalletStatus.valueOf(r.status)));
    }

    @Override
    public void save(Wallet wallet) {
        table.put(wallet.getId(),
                new WalletRecord(wallet.getId(), wallet.getBalance(), wallet.getStatus().name()));
    }
}
