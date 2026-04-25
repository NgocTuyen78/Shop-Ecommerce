package com.computershop.main.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "category_name", nullable = false, unique = true, length = 100)
    private String categoryName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;
    
    public Category() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Category(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }
    
    public Integer getCategoryId() {return categoryId;}
    public String getCategoryName() {return categoryName;}
    public String getDescription() {return description;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public List<Product> getProducts() {return products;}

    public void setCategoryId(Integer categoryId) {this.categoryId = categoryId;}
    public void setCategoryName(String categoryName) {this.categoryName = categoryName;}
    public void setDescription(String description) {this.description = description;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setProducts(List<Product> products) {this.products = products;}

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}