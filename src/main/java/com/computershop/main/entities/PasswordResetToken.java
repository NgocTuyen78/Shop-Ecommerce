package com.computershop.main.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer tokenId;
    
    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
    
    @Column(name = "used", nullable = false)
    private Boolean used = false;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    public PasswordResetToken() {
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }
    
    public PasswordResetToken(String token, User user) {
        this();
        this.token = token;
        this.user = user;
        this.expiryDate = LocalDateTime.now().plusHours(24); // Token hết hạn sau 24 giờ
    }
    
    // Getters and Setters
    public Integer getTokenId() {
        return tokenId;
    }
    
    public void setTokenId(Integer tokenId) {
        this.tokenId = tokenId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Boolean getUsed() {
        return used;
    }
    
    public void setUsed(Boolean used) {
        this.used = used;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
