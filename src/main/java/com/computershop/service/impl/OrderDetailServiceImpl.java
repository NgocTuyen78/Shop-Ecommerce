package com.computershop.service.impl;

import com.computershop.main.entities.OrderDetail;
import com.computershop.main.entities.Order;
import com.computershop.main.entities.Product;
import com.computershop.main.repositories.OrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OrderDetailService.
 * Handles all order detail-related business logic.
 */
@Service
public class OrderDetailServiceImpl {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductServiceImpl productService;

    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    public Optional<OrderDetail> getOrderDetailById(Integer orderDetailId) {
        return orderDetailRepository.findById(orderDetailId);
    }

    public List<OrderDetail> getOrderDetailsByOrder(Order order) {
        return orderDetailRepository.findByOrder(order);
    }

    public List<OrderDetail> getOrderDetailsByOrderId(Integer orderId) {
        return orderDetailRepository.findByOrderOrderId(orderId);
    }

    public List<OrderDetail> getOrderDetailsByProduct(Product product) {
        return orderDetailRepository.findByProduct(product);
    }

    public List<OrderDetail> getOrderDetailsByProductId(Integer productId) {
        return orderDetailRepository.findByProductProductId(productId);
    }

    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        Product product = orderDetail.getProduct();
        if (!productService.hasSufficientStock(product.getProductId(), orderDetail.getQuantity())) {
            throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
        }

        productService.decreaseStock(product.getProductId(), orderDetail.getQuantity());

        return orderDetailRepository.save(orderDetail);
    }

    public OrderDetail createOrderDetail(Order order, Product product, Integer quantity, BigDecimal price) {
        if (!productService.hasSufficientStock(product.getProductId(), quantity)) {
            throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
        }

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setQuantity(quantity);
        orderDetail.setPrice(price);

        productService.decreaseStock(product.getProductId(), quantity);

        return orderDetailRepository.save(orderDetail);
    }

    public OrderDetail updateOrderDetail(Integer orderDetailId, OrderDetail orderDetailDetails) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Order detail not found with id: " + orderDetailId));

        Integer oldQuantity = orderDetail.getQuantity();
        Integer newQuantity = orderDetailDetails.getQuantity();

        if (!oldQuantity.equals(newQuantity)) {
            Product product = orderDetail.getProduct();
            Integer quantityDifference = newQuantity - oldQuantity;

            if (quantityDifference > 0) {
                if (!productService.hasSufficientStock(product.getProductId(), quantityDifference)) {
                    throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
                }
                productService.decreaseStock(product.getProductId(), quantityDifference);
            } else {
                productService.increaseStock(product.getProductId(), Math.abs(quantityDifference));
            }
        }

        orderDetail.setQuantity(orderDetailDetails.getQuantity());
        orderDetail.setPrice(orderDetailDetails.getPrice());

        return orderDetailRepository.save(orderDetail);
    }

    public void deleteOrderDetail(Integer orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new RuntimeException("Order detail not found with id: " + orderDetailId));

        productService.increaseStock(orderDetail.getProduct().getProductId(), orderDetail.getQuantity());

        orderDetailRepository.deleteById(orderDetailId);
    }

    public BigDecimal calculateOrderTotal(Integer orderId) {
        BigDecimal total = orderDetailRepository.calculateOrderTotal(orderId);
        return total != null ? total : BigDecimal.ZERO;
    }

    public long countItemsInOrder(Integer orderId) {
        return orderDetailRepository.countItemsInOrder(orderId);
    }
}
