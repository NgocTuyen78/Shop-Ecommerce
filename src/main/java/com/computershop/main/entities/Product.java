package com.computershop.main.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
    
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    private Image image;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public Product() {}
    
    public Product(String productName, String description, BigDecimal price, Integer stockQuantity, Category category, Image image) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.image = image;
        this.createdAt = LocalDateTime.now();
    }
    
    public Integer getProductId() {return productId;}
    public String getProductName() {return productName;}
    public String getDescription() {return description;}
    public BigDecimal getPrice() {return price;}
    public Integer getStockQuantity() {return stockQuantity;}
    public Category getCategory() {return category;}
    public LocalDateTime getCreatedAt() {return createdAt;}
    public Image getImage() {return image;} 

    public void setProductId(Integer productId) {this.productId = productId;}
    public void setProductName(String productName) {this.productName = productName;}
    public void setDescription(String description) {this.description = description;}
    public void setPrice(BigDecimal price) {this.price = price;}
    public void setStockQuantity(Integer stockQuantity) {this.stockQuantity = stockQuantity;}
    public void setCategory(Category category) {this.category = category;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
    public void setImage(Image image) {this.image = image;}
    
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}