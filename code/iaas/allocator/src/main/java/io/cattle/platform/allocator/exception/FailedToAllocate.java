package io.cattle.platform.allocator.exception;

public class FailedToAllocate extends RuntimeException {

    private static final long serialVersionUID = -513567369353243755L;
    String message;

    public FailedToAllocate(String message) {
        this.message = message;
    }
}
