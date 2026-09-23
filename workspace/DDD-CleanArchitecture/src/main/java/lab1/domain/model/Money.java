package lab1.domain.model;

import lab1.domain.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Value Object: bất biến (immutable), so sánh theo giá trị, không bao giờ âm.
 */
public final class Money {
    private static final int SCALE = 2;

    private final BigDecimal amount;

    private Money(BigDecimal amount) {
        if (amount == null) {
            throw new DomainException("Số tiền không được null.");
        }
        if (amount.signum() < 0) {
            throw new DomainException("Số tiền không được âm: " + amount);
        }
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public Money add(Money other) {
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        if (this.isLessThan(other)) {
            throw new DomainException("Không thể trừ " + other + " khỏi " + this + ": kết quả bị âm.");
        }
        return new Money(this.amount.subtract(other.amount));
    }

    public boolean isLessThan(Money other) {
        return this.amount.compareTo(other.amount) < 0;
    }

    public BigDecimal toBigDecimal() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money)) return false;
        return amount.compareTo(((Money) o).amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
