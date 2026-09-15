package com.shop.adapter.in;

/**
 * Thay the cho ResponseEntity<T> cua Spring, de project chay duoc ma khong can dependency nao.
 * Khi gan Spring vao, doi kieu tra ve thanh ResponseEntity la xong.
 */
public class ApiResponse<T> {

    private final int status;
    private final T body;

    private ApiResponse(int status, T body) {
        this.status = status;
        this.body = body;
    }

    public static <T> ApiResponse<T> created(T body) {
        return new ApiResponse<>(201, body);
    }

    public static <T> ApiResponse<T> badRequest(T body) {
        return new ApiResponse<>(400, body);
    }

    public static <T> ApiResponse<T> notFound(T body) {
        return new ApiResponse<>(404, body);
    }

    public static <T> ApiResponse<T> conflict(T body) {
        return new ApiResponse<>(409, body);
    }

    public int getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HTTP " + status + " " + body;
    }
}
