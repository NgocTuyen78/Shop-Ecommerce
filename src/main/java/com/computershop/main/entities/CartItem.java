package com.computershop.main.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Integer cartItemId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "added_at")
    private LocalDateTime addedAt;
    
    public CartItem() {
        this.quantity = 1;
        this.addedAt = LocalDateTime.now();
    }
    
    public CartItem(Cart cart, Product product, Integer quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.addedAt = LocalDateTime.now();
    }
    
    public Integer getCartItemId() { return cartItemId; }
    public Cart getCart() { return cart; }
    public Product getProduct() { return product; }
    public Integer getQuantity() { return quantity; }
    public LocalDateTime getAddedAt() { return addedAt; }
    
    public void setCartItemId(Integer cartItemId) { this.cartItemId = cartItemId; }
    public void setCart(Cart cart) { this.cart = cart; }
    public void setProduct(Product product) { this.product = product; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }
    
    public BigDecimal getSubtotal() {
        if (product != null && product.getPrice() != null) {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId=" + cartItemId +
                ", productId=" + (product != null ? product.getProductId() : null) +
                ", quantity=" + quantity +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}
