package com.computershop.main.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {

    public static final String PROVIDER_MOMO = "MOMO";

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_transaction_id")
    private Long paymentTransactionId;

    @Column(name = "provider", nullable = false, length = 32)
    private String provider;

    @Column(name = "status", nullable = false, length = 32)
    private String status = STATUS_PENDING;

    @Column(name = "receiver_account", nullable = false, length = 64)
    private String receiverAccount;

    @Column(name = "amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_code", nullable = false, length = 2048)
    private String paymentCode;

    @Column(name = "note", length = 512)
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PaymentTransaction() {}

    public Long getPaymentTransactionId() { return paymentTransactionId; }
    public String getProvider() { return provider; }
    public String getStatus() { return status; }
    public String getReceiverAccount() { return receiverAccount; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentCode() { return paymentCode; }
    public String getNote() { return note; }
    public Order getOrder() { return order; }
    public User getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setPaymentTransactionId(Long paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }
    public void setProvider(String provider) { this.provider = provider; }
    public void setStatus(String status) { this.status = status; }
    public void setReceiverAccount(String receiverAccount) { this.receiverAccount = receiverAccount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }
    public void setNote(String note) { this.note = note; }
    public void setOrder(Order order) { this.order = order; }
    public void setUser(User user) { this.user = user; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

