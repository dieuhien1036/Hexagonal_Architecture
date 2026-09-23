package lab1.domain.model;

import lab1.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    private static BigDecimal bd(String v) {
        return new BigDecimal(v);
    }

    @Test
    @DisplayName("Ví mới mở ở trạng thái ACTIVE với đúng số dư ban đầu")
    void openWallet() {
        Wallet wallet = Wallet.open(bd("100"));
        assertEquals(WalletStatus.ACTIVE, wallet.getStatus());
        assertEquals(0, bd("100").compareTo(wallet.getBalance()));
        assertNotNull(wallet.getId());
    }

    @Test
    @DisplayName("Rút tiền hợp lệ -> số dư giảm tương ứng")
    void withdrawSuccess() {
        Wallet wallet = Wallet.open(bd("100"));
        wallet.withdrawMoney(bd("30.50"));
        assertEquals(0, bd("69.50").compareTo(wallet.getBalance()));
    }

    @Test
    @DisplayName("Rút đúng bằng số dư -> số dư về 0")
    void withdrawExactBalance() {
        Wallet wallet = Wallet.open(bd("100"));
        wallet.withdrawMoney(bd("100"));
        assertEquals(0, BigDecimal.ZERO.compareTo(wallet.getBalance()));
    }

    @Test
    @DisplayName("Invariant: không được rút quá số dư")
    void cannotOverdraw() {
        Wallet wallet = Wallet.open(bd("100"));
        DomainException ex = assertThrows(DomainException.class, () -> wallet.withdrawMoney(bd("100.01")));
        assertTrue(ex.getMessage().contains("Số dư không đủ"));
        assertEquals(0, bd("100").compareTo(wallet.getBalance()), "Số dư không được thay đổi khi lỗi");
    }

    @Test
    @DisplayName("Invariant: không được rút tiền khi ví bị khóa")
    void cannotWithdrawWhenLocked() {
        Wallet wallet = Wallet.open(bd("100"));
        wallet.lockWallet();
        DomainException ex = assertThrows(DomainException.class, () -> wallet.withdrawMoney(bd("10")));
        assertTrue(ex.getMessage().contains("khóa"));
        assertEquals(0, bd("100").compareTo(wallet.getBalance()));
    }

    @Test
    @DisplayName("Số tiền rút phải > 0 (chặn số âm / 0 / null lách luật)")
    void amountMustBePositive() {
        Wallet wallet = Wallet.open(bd("100"));
        assertThrows(DomainException.class, () -> wallet.withdrawMoney(bd("-50")));
        assertThrows(DomainException.class, () -> wallet.withdrawMoney(BigDecimal.ZERO));
        assertThrows(DomainException.class, () -> wallet.withdrawMoney(null));
        assertEquals(0, bd("100").compareTo(wallet.getBalance()));
    }

    @Test
    @DisplayName("lockWallet chuyển trạng thái sang LOCKED và idempotent")
    void lockWallet() {
        Wallet wallet = Wallet.open(bd("100"));
        wallet.lockWallet();
        wallet.lockWallet();
        assertEquals(WalletStatus.LOCKED, wallet.getStatus());
        assertTrue(wallet.isLocked());
    }

    @Test
    @DisplayName("Không thể mở ví với số dư âm")
    void cannotOpenWithNegativeBalance() {
        assertThrows(DomainException.class, () -> Wallet.open(bd("-1")));
    }

    @Test
    @DisplayName("Đóng gói: mọi field là private, không có setter công khai")
    void encapsulation() {
        for (Field f : Wallet.class.getDeclaredFields()) {
            assertTrue(Modifier.isPrivate(f.getModifiers()), "Field phải private: " + f.getName());
        }
        boolean hasSetter = Arrays.stream(Wallet.class.getMethods())
                .map(Method::getName)
                .anyMatch(n -> n.startsWith("set"));
        assertFalse(hasSetter, "Wallet không được có setter công khai");
    }
}
