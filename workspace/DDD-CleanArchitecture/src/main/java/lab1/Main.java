package lab1;

import lab1.application.WalletApplicationService;
import lab1.domain.exception.DomainException;
import lab1.domain.model.Wallet;
import lab1.infrastructure.InMemoryWalletRepository;
import lab1.legacy.WalletEntity;
import lab1.legacy.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("===== LEGACY (Anemic) =====");
        WalletEntity legacy = new WalletEntity();
        legacy.id = UUID.randomUUID();
        legacy.balance = new BigDecimal("100");
        legacy.status = "ACTIVE";
        new WalletService().withdraw(legacy, new BigDecimal("500"));
        System.out.println("Số dư sau khi rút 500 từ 100: " + legacy.balance + "  <-- BỊ ÂM!");

        System.out.println();
        System.out.println("===== RICH DOMAIN MODEL =====");
        InMemoryWalletRepository repo = new InMemoryWalletRepository();
        WalletApplicationService service = new WalletApplicationService(repo);

        Wallet wallet = Wallet.open(new BigDecimal("100"));
        repo.save(wallet);
        UUID id = wallet.getId();

        service.withdraw(id, new BigDecimal("30"));
        System.out.println("Rút 30 -> " + repo.findById(id).orElseThrow());

        tryRun("Rút 500 (vượt số dư)", () -> service.withdraw(id, new BigDecimal("500")));
        tryRun("Rút -10 (số âm)", () -> service.withdraw(id, new BigDecimal("-10")));

        service.lock(id);
        System.out.println("Khóa ví -> " + repo.findById(id).orElseThrow());
        tryRun("Rút 10 khi ví bị khóa", () -> service.withdraw(id, new BigDecimal("10")));
    }

    private static void tryRun(String label, Runnable action) {
        try {
            action.run();
            System.out.println(label + " -> OK");
        } catch (DomainException e) {
            System.out.println(label + " -> DomainException: " + e.getMessage());
        }
    }
}
