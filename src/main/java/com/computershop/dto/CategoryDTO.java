package com.computershop.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Category entity.
 * Used to transfer category data between layers.
 */
public class CategoryDTO {

    private Integer categoryId;
    private String categoryName;
    private String description;
    private LocalDateTime createdAt;
    private Integer productCount;

    // ==================== Constructors ====================

    public CategoryDTO() {
    }

    public CategoryDTO(Integer categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    // ==================== Getters and Setters ====================

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }
}
