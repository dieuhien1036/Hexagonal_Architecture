package lab1.domain.model;

import lab1.domain.exception.DomainException;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root: Ví điện tử (Rich Domain Model).
 *
 * <ul>
 *   <li>Mọi thuộc tính là private, KHÔNG có setter công khai.</li>
 *   <li>Trạng thái chỉ thay đổi qua các phương thức nghiệp vụ giàu hành vi
 *       ({@link #withdrawMoney(BigDecimal)}, {@link #lockWallet()}).</li>
 *   <li>Luật bất biến được kiểm tra bên trong aggregate nên không thể bị bỏ qua:
 *       <ol>
 *         <li>Không được rút tiền khi ví bị khóa.</li>
 *         <li>Không được rút quá số dư hiện có (balance luôn &gt;= 0).</li>
 *       </ol>
 *   </li>
 * </ul>
 */
public class Wallet {

    private final UUID id;
    private Money balance;
    private WalletStatus status;

    private Wallet(UUID id, Money balance, WalletStatus status) {
        this.id = Objects.requireNonNull(id, "id");
        this.balance = Objects.requireNonNull(balance, "balance");
        this.status = Objects.requireNonNull(status, "status");
    }

    /** Factory method: mở ví mới, luôn ở trạng thái ACTIVE. */
    public static Wallet open(BigDecimal initialBalance) {
        return new Wallet(UUID.randomUUID(), Money.of(initialBalance), WalletStatus.ACTIVE);
    }

    /** Dựng lại aggregate từ dữ liệu đã lưu (chỉ dành cho tầng persistence). */
    public static Wallet reconstitute(UUID id, BigDecimal balance, WalletStatus status) {
        return new Wallet(id, Money.of(balance), status);
    }

    // ===================== Hành vi nghiệp vụ =====================

    /**
     * Rút tiền khỏi ví.
     *
     * @throws DomainException nếu ví bị khóa, số tiền không hợp lệ, hoặc vượt quá số dư.
     */
    public void withdrawMoney(BigDecimal amount) {
        ensureActive();
        Money toWithdraw = requirePositive(amount);
        if (balance.isLessThan(toWithdraw)) {
            throw new DomainException(
                    "Số dư không đủ: hiện có " + balance + ", yêu cầu rút " + toWithdraw + ".");
        }
        this.balance = balance.subtract(toWithdraw);
    }

    /** Khóa ví. Idempotent: khóa ví đã khóa không gây lỗi. */
    public void lockWallet() {
        this.status = WalletStatus.LOCKED;
    }

    public boolean isLocked() {
        return status == WalletStatus.LOCKED;
    }

    // ===================== Guard (invariant) =====================

    private void ensureActive() {
        if (isLocked()) {
            throw new DomainException("Ví điện tử hiện đang bị khóa!");
        }
    }

    private static Money requirePositive(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new DomainException("Số tiền giao dịch phải lớn hơn 0.");
        }
        return Money.of(amount);
    }

    // ===================== Getter chỉ đọc =====================

    public UUID getId() {
        return id;
    }

    /** Trả về BigDecimal (immutable) nên bên ngoài không thể sửa ngược vào aggregate. */
    public BigDecimal getBalance() {
        return balance.toBigDecimal();
    }

    public WalletStatus getStatus() {
        return status;
    }

    // Entity: định danh theo id, không theo giá trị thuộc tính.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wallet)) return false;
        return id.equals(((Wallet) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Wallet{id=" + id + ", balance=" + balance + ", status=" + status + '}';
    }
}
