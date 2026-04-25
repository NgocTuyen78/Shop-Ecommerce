package com.computershop.exception;

/**
 * Exception thrown when a business rule is violated.
 * Used for business logic errors like insufficient stock, invalid operation, etc.
 */
public class BusinessException extends RuntimeException {

    /**
     * Constructs a new BusinessException with the specified message.
     *
     * @param message the detail message
     */
    public BusinessException(String message) {
        super(message);
    }

    /**
     * Constructs a new BusinessException with a formatted message.
     *
     * @param message the message template
     * @param args arguments to format the message
     */
    public BusinessException(String message, Object... args) {
        super(String.format(message, args));
    }

    /**
     * Constructs a new BusinessException with a cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
