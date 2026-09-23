package lab1.application;

import lab1.domain.exception.DomainException;
import lab1.domain.model.Wallet;
import lab1.domain.model.WalletStatus;
import lab1.infrastructure.InMemoryWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletApplicationServiceTest {

    private InMemoryWalletRepository repo;
    private WalletApplicationService service;
    private UUID walletId;

    @BeforeEach
    void setUp() {
        repo = new InMemoryWalletRepository();
        service = new WalletApplicationService(repo);
        Wallet wallet = Wallet.open(new BigDecimal("100"));
        repo.save(wallet);
        walletId = wallet.getId();
    }

    @Test
    void withdrawIsPersisted() {
        service.withdraw(walletId, new BigDecimal("40"));
        assertEquals(0, new BigDecimal("60").compareTo(repo.findById(walletId).orElseThrow().getBalance()));
    }

    @Test
    void failedWithdrawDoesNotChangeStoredState() {
        assertThrows(DomainException.class, () -> service.withdraw(walletId, new BigDecimal("1000")));
        assertEquals(0, new BigDecimal("100").compareTo(repo.findById(walletId).orElseThrow().getBalance()));
    }

    @Test
    void lockThenWithdrawFails() {
        service.lock(walletId);
        assertEquals(WalletStatus.LOCKED, repo.findById(walletId).orElseThrow().getStatus());
        assertThrows(DomainException.class, () -> service.withdraw(walletId, new BigDecimal("1")));
    }

    @Test
    void unknownWalletThrows() {
        assertThrows(DomainException.class, () -> service.withdraw(UUID.randomUUID(), BigDecimal.ONE));
    }
}
