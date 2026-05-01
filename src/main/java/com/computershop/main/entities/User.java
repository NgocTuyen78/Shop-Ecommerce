package com.computershop.main.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "username", nullable = false, length = 255)
    private String username;
    
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleid")
    private Role role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "address", length = 500)
    private String address;
    
    @Column(name = "phone", length = 255)
    private String phone;

    @Transient
    private String password;
    
    public User() {}
    
    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.enabled = true;
    }
    
    public Integer getUserId() {return userId;}
    public String getUsername() {return username;}
    public String getPasswordHash() {return passwordHash;}
    public Role getRole() {return role;}
    public String getPassword() {return password;}
    public boolean isEnabled() {return enabled;}
    public String getAddress() {return address;}
    public String getPhone() { return phone; }

    public void setUserId(Integer userId) {this.userId = userId;}
    public void setUsername(String username) {this.username = username;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}
    public void setRole(Role role) {this.role = role;}
    public void setPassword(String password) {this.password = password;}
    public void setEnabled(boolean enabled) {this.enabled = enabled;}
    public void setAddress(String address) {this.address = address;}
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role=" + (role != null ? role.getRoleName() : "null") +
                ", enabled=" + enabled +
                '}';
    }
}