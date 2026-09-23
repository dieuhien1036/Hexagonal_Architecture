package lab1.legacy;

import java.math.BigDecimal;

/**
 * [LEGACY - Anemic Model] Logic nghiệp vụ nằm ngoài entity.
 */
@Deprecated
public class WalletService {
    public void withdraw(WalletEntity wallet, BigDecimal amount) throws Exception {
        if ("LOCKED".equals(wallet.status)) throw new Exception("Ví điện tử hiện đang bị khóa!");
        // LỖI CHÍ MẠNG: Thiếu kiểm tra điều kiện số dư, cho phép balance bị trừ âm tự do
        wallet.balance = wallet.balance.subtract(amount);
    }
}
