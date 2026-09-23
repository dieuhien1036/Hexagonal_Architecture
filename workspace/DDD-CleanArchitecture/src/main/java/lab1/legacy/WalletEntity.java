package lab1.legacy;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * [LEGACY - Anemic Model] Giữ lại để so sánh. KHÔNG dùng trong code mới.
 * Mọi field public -> bất kỳ ai cũng có thể sửa balance/status tùy ý.
 */
@Deprecated
public class WalletEntity {
    public UUID id;
    public BigDecimal balance;
    public String status; // "ACTIVE", "LOCKED"
}
