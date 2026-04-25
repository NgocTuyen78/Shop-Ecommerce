package com.computershop.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Utility class for common operations.
 * Provides validation and formatting methods used throughout the application.
 */
public final class CommonUtils {

    // ==================== Date Formatters ====================
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ==================== Validation Patterns ====================
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,50}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");

    // ==================== Private Constructor ====================
    private CommonUtils() {
        throw new UnsupportedOperationException("CommonUtils class cannot be instantiated");
    }

    // ==================== Validation Methods ====================

    /**
     * Validates an email address.
     *
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a username.
     * Username must be 3-50 characters and contain only letters, numbers, and underscores.
     *
     * @param username the username to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }

    /**
     * Validates a password.
     * Password must be at least 6 characters long.
     *
     * @param password the password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates that a string is not null or empty.
     *
     * @param value the string to validate
     * @return true if valid (not null/empty), false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // ==================== Formatting Methods ====================

    /**
     * Formats a LocalDateTime to a string.
     *
     * @param dateTime the date time to format
     * @return formatted string in "yyyy-MM-dd HH:mm:ss" format
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * Formats a LocalDateTime to a date string (without time).
     *
     * @param dateTime the date time to format
     * @return formatted string in "yyyy-MM-dd" format
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_FORMATTER);
    }

    /**
     * Formats a price with Vietnamese currency symbol.
     *
     * @param price the price to format
     * @return formatted price string (e.g., "1.000.000 VND")
     */
    public static String formatPrice(Number price) {
        if (price == null) {
            return "0 VND";
        }
        return String.format("%,.0f VND", price);
    }

    // ==================== String Methods ====================

    /**
     * Truncates a string to a maximum length.
     *
     * @param text the text to truncate
     * @param maxLength the maximum length
     * @return truncated string with "..." suffix if truncated
     */
    public static String truncate(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    /**
     * Converts a string to a slug (URL-friendly format).
     *
     * @param text the text to convert
     * @return slug string
     */
    public static String toSlug(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        return text.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("[\\s-]+", "-");
    }
}
