package com.computershop.util;

/**
 * Application-wide constants.
 * Contains role names, order statuses, error messages, and session attribute names.
 */
public final class Constants {

    // ==================== Role Names ====================
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_CUSTOMER = "customer";
    public static final String ROLE_STAFF = "staff";
    public static final String ROLE_SUPPLIER = "supplier";

    // ==================== Order Statuses ====================
    public static final String ORDER_STATUS_PENDING = "pending";
    public static final String ORDER_STATUS_PENDING_PAYMENT = "pending_payment";
    public static final String ORDER_STATUS_CONFIRMED = "confirmed";
    public static final String ORDER_STATUS_SHIPPING = "shipping";
    public static final String ORDER_STATUS_COMPLETED = "completed";
    public static final String ORDER_STATUS_CANCELLED = "cancelled";

    // ==================== Session Attribute Names ====================
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USERNAME = "username";
    public static final String SESSION_ROLE = "role";
    public static final String SESSION_CART = "cart";

    // ==================== Error Messages ====================
    public static final String ERROR_USER_NOT_FOUND = "User not found";
    public static final String ERROR_PRODUCT_NOT_FOUND = "Product not found";
    public static final String ERROR_ORDER_NOT_FOUND = "Order not found";
    public static final String ERROR_CATEGORY_NOT_FOUND = "Category not found";
    public static final String ERROR_INSUFFICIENT_STOCK = "Insufficient stock";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ERROR_UNAUTHORIZED = "Unauthorized access";
    public static final String ERROR_ACCESS_DENIED = "Access denied";

    // ==================== Success Messages ====================
    public static final String SUCCESS_PRODUCT_CREATED = "Product created successfully";
    public static final String SUCCESS_PRODUCT_UPDATED = "Product updated successfully";
    public static final String SUCCESS_PRODUCT_DELETED = "Product deleted successfully";
    public static final String SUCCESS_ORDER_CREATED = "Order created successfully";
    public static final String SUCCESS_ORDER_UPDATED = "Order updated successfully";

    // ==================== Pagination ====================
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== File Upload ====================
    public static final String UPLOAD_DIR = "uploads/";
    public static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // ==================== Payment ====================
    public static final String PAYMENT_METHOD_CASH = "cash";
    public static final String PAYMENT_METHOD_MOMO = "momo";
    public static final String PAYMENT_METHOD_BANK_TRANSFER = "bank_transfer";

    // ==================== Private Constructor ====================
    private Constants() {
        // Prevent instantiation
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }
}
