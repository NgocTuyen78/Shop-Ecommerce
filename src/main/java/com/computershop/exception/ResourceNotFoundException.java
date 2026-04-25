package com.computershop.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Used when entities like User, Product, Order, etc. are not found in the database.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message.
     *
     * @param resource the type of resource (e.g., "User", "Product")
     * @param field the field name used in the search
     * @param value the value that was searched for
     */
    public ResourceNotFoundException(String resource, String field, Object value) {
        super(String.format("%s not found with %s: '%s'", resource, field, value));
    }
}
