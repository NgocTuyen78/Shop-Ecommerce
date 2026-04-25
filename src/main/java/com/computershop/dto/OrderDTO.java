package com.computershop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Order entity.
 * Used to transfer order data between layers.
 */
public class OrderDTO {

    private Integer orderId;
    private Integer userId;
    private String username;
    private String userEmail;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderDetailDTO> orderDetails;
    private BigDecimal totalAmount;

    // ==================== Constructors ====================

    public OrderDTO() {
    }

    public OrderDTO(Integer orderId, Integer userId, String username,
                    LocalDateTime orderDate, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.username = username;
        this.orderDate = orderDate;
        this.status = status;
    }

    // ==================== Getters and Setters ====================

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderDetailDTO> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetailDTO> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // ==================== Helper Methods ====================

    /**
     * Nested DTO for OrderDetail.
     */
    public static class OrderDetailDTO {
        private Integer orderDetailId;
        private Integer productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;

        public OrderDetailDTO() {
        }

        public OrderDetailDTO(Integer orderDetailId, Integer productId, String productName,
                              Integer quantity, BigDecimal price) {
            this.orderDetailId = orderDetailId;
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters
        public Integer getOrderDetailId() { return orderDetailId; }
        public void setOrderDetailId(Integer orderDetailId) { this.orderDetailId = orderDetailId; }
        public Integer getProductId() { return productId; }
        public void setProductId(Integer productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }

        /**
         * Calculates subtotal for this order detail.
         *
         * @return quantity * price
         */
        public BigDecimal getSubtotal() {
            if (price == null || quantity == null) {
                return BigDecimal.ZERO;
            }
            return price.multiply(BigDecimal.valueOf(quantity));
        }
    }

    /**
     * Calculates the total amount from order details.
     *
     * @return total amount
     */
    public BigDecimal calculateTotal() {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orderDetails.stream()
                .map(OrderDetailDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
