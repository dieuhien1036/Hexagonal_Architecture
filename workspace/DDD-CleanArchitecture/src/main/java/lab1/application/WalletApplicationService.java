package lab1.application;

import lab1.domain.exception.DomainException;
import lab1.domain.model.Wallet;
import lab1.domain.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application Service (Use Case): chỉ điều phối load -> gọi hành vi domain -> save.
 * KHÔNG chứa luật nghiệp vụ; luật nằm hết trong aggregate {@link Wallet}.
 */
public class WalletApplicationService {

    private final WalletRepository walletRepository;

    public WalletApplicationService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void withdraw(UUID walletId, BigDecimal amount) {
        Wallet wallet = load(walletId);
        wallet.withdrawMoney(amount);
        walletRepository.save(wallet);
    }

    public void lock(UUID walletId) {
        Wallet wallet = load(walletId);
        wallet.lockWallet();
        walletRepository.save(wallet);
    }

    private Wallet load(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new DomainException("Không tìm thấy ví: " + walletId));
    }
}
