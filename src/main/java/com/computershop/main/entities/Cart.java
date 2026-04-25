package com.computershop.main.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Integer cartId;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Cart() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Cart(User user) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getCartId() { return cartId; }
    public User getUser() { return user; }
    public List<CartItem> getCartItems() { return cartItems; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    public void setCartId(Integer cartId) { this.cartId = cartId; }
    public void setUser(User user) { this.user = user; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public void addCartItem(CartItem item) {
        cartItems.add(item);
        item.setCart(this);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeCartItem(CartItem item) {
        cartItems.remove(item);
        item.setCart(null);
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", itemCount=" + cartItems.size() +
                '}';
    }
}
