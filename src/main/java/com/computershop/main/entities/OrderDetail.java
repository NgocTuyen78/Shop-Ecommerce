package com.computershop.main.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
public class OrderDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "price", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;
    
    public OrderDetail() {}
    
    public OrderDetail(Order order, Product product, Integer quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
    
    public Integer getOrderDetailId() {return orderDetailId;}
    public Order getOrder() {return order;}
    public Product getProduct() {return product;}
    public Integer getQuantity() {return quantity;}
    public BigDecimal getPrice() {return price;}

    public void setOrderDetailId(Integer orderDetailId) {this.orderDetailId = orderDetailId;}
    public void setOrder(Order order) {this.order = order;}
    public void setProduct(Product product) {this.product = product;}
    public void setQuantity(Integer quantity) {this.quantity = quantity;}
    public void setPrice(BigDecimal price) {this.price = price;}

    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId=" + orderDetailId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", product=" + (product != null ? product.getProductName() : "null") +
                '}';
    }
}