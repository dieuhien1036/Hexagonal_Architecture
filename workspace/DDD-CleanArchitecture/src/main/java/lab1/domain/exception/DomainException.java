package lab1.domain.exception;

/**
 * Exception nghiệp vụ tự định nghĩa: ném ra khi một luật bất biến (invariant) của domain bị vi phạm.
 * Là unchecked exception để không "làm bẩn" chữ ký phương thức nghiệp vụ bằng throws Exception.
 */
public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
