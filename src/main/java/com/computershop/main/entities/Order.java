package com.computershop.main.entities;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
    
    @Column(name = "status")
    private String status = "pending";

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "notes")
    private String notes;
    
    public Order() {}
    
    public Order(User user, LocalDateTime orderDate) {
        this.user = user;
        this.orderDate = orderDate;
    }
    
    public Integer getOrderId() {return orderId;}
    public User getUser() {return user;}
    public LocalDateTime getOrderDate() {return orderDate;}
    public List<OrderDetail> getOrderDetails() {return orderDetails;}
    public String getStatus() {return status;}
    public String getShippingAddress() {return shippingAddress;}
    public String getPaymentMethod() {return paymentMethod;}
    public String getNotes() {return notes;}

    public void setOrderId(Integer orderId) {this.orderId = orderId;}
    public void setUser(User user) {this.user = user;}
    public void setOrderDate(LocalDateTime orderDate) {this.orderDate = orderDate;}
    public void setOrderDetails(List<OrderDetail> orderDetails) {this.orderDetails = orderDetails;}
    public void setStatus(String status) {this.status = status;}
    public void setShippingAddress(String shippingAddress) {this.shippingAddress = shippingAddress;}
    public void setPaymentMethod(String paymentMethod) {this.paymentMethod = paymentMethod;}
    public void setNotes(String notes) {this.notes = notes;}
    
    public double getTotalAmount() {
        if (orderDetails == null || orderDetails.isEmpty()) {
            return 0.0;
        }
        return orderDetails.stream()
                .mapToDouble(detail -> detail.getPrice().doubleValue() * detail.getQuantity())
                .sum();
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", orderDate=" + orderDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId != null && orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}