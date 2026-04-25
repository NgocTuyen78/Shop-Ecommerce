package com.computershop.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Product entity.
 * Used to transfer product data between layers without exposing the entire entity.
 */
public class ProductDTO {

    private Integer productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer categoryId;
    private String categoryName;
    private Integer imageId;
    private String imageUrl;
    private LocalDateTime createdAt;

    // ==================== Constructors ====================

    public ProductDTO() {
    }

    public ProductDTO(Integer productId, String productName, String description,
                      BigDecimal price, Integer stockQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // ==================== Getters and Setters ====================

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ==================== Helper Methods ====================

    /**
     * Checks if the product is in stock.
     *
     * @return true if stock quantity > 0
     */
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }

    /**
     * Checks if the product has low stock (5 or less).
     *
     * @return true if stock is low
     */
    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity > 0 && stockQuantity <= 5;
    }

    /**
     * Checks if the product is out of stock.
     *
     * @return true if stock is 0
     */
    public boolean isOutOfStock() {
        return stockQuantity == null || stockQuantity <= 0;
    }
}
