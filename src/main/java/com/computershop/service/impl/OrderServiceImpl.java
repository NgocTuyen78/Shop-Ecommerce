package com.computershop.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.computershop.main.entities.Order;
import com.computershop.main.entities.OrderDetail;
import com.computershop.main.entities.User;
import com.computershop.main.repositories.OrderDetailRepository;
import com.computershop.main.repositories.OrderRepository;
import com.computershop.service.api.OrderService;

/**
 * Implementation of OrderService.
 * Handles all order-related business logic.
 */
@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private OrderDetailServiceImpl orderDetailService;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findByIdWithDetails(orderId);
    }

    @Override
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserUserId(userId);
    }

    @Override
    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Order createOrder(Order order) {
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
        }
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Integer orderId, Order orderDetails) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (orderDetails.getUser() != null) {
            order.setUser(orderDetails.getUser());
        }
        if (orderDetails.getOrderDate() != null) {
            order.setOrderDate(orderDetails.getOrderDate());
        }
        if (orderDetails.getStatus() != null) {
            order.setStatus(orderDetails.getStatus());
        }

        return orderRepository.save(order);
    }

    @Override
    public void updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        
        // Kiểm tra nếu đơn hàng đang chờ thanh toán (PENDING_PAYMENT)
        if ("pending_payment".equalsIgnoreCase(order.getStatus())) {
            // Tính toán xem đã quá 24 giờ kể từ lúc tạo chưa
            // plusMinutes(1) để test nhanh, sau đó đổi lại thành plusHours(24)
            if (order.getOrderDate().plusHours(24).isBefore(LocalDateTime.now())) {
                order.setStatus("cancelled");
                String expiredNote = "This order was automatically cancelled because it was not paid within 24 hours.";
                order.setNotes(expiredNote);
                orderRepository.save(order);
                return;
            }
        }      

        order.setStatus(status.toLowerCase());
        orderRepository.save(order);
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    //@Scheduled(fixedRate = 60000) // Dùng 1 phút để test nhanh, sau đó đổi lại thành cron để chạy mỗi giờ
    @Transactional
        public void autoCancelOrders() {
            //LocalDateTime limit = LocalDateTime.now().minusMinutes(1); // Dùng 1 phút để test nhanh, sau đó đổi lại thành 24 giờ
            LocalDateTime limit = LocalDateTime.now().minusHours(24);
            List<Order> expiredOrders = orderRepository.findByStatusAndOrderDateBefore("pending_payment", limit);

            if (!expiredOrders.isEmpty()) {
                for (Order order : expiredOrders) {
                    order.setStatus("cancelled");
                    order.setNotes("System: Cancelled due to payment timeout (24h).");
                }
                orderRepository.saveAll(expiredOrders);
                System.out.println("Scheduled Task: Cancelled " + expiredOrders.size() + " expired orders.");
            }
        }

    @Override
    public void deleteOrder(Integer orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    @Override
    public long getTotalOrders() {
        return orderRepository.count();
    }

    @Override
    public double getTotalRevenue() {
        try {
            List<Order> orders = orderRepository.findAll();
            return orders.stream()
                    .mapToDouble(order -> {
                        return order.getOrderDetails().stream()
                                .mapToDouble(detail -> detail.getPrice().doubleValue() * detail.getQuantity())
                                .sum();
                    })
                    .sum();
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public List<Order> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findRecentOrdersForAdmin(pageable);
    }

    @Override
    public List<Order> getPendingOrders() {
        return orderRepository.findByStatus("pending");
    }

    @Override
    public List<Order> getCompletedOrders() {
        return orderRepository.findByStatus("completed");
    }

    @Override
    public List<Object[]> getOrderedProductsByUserId(Integer userId) {
        return orderDetailRepository.findOrderedProductsSummaryByUserId(userId);
    }

    // ==================== Additional Methods from Original Service ====================

    public List<Order> getUserOrdersRecent(Integer userId) {
        return orderRepository.findByUserUserIdWithDetailsFetched(userId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersWithDetailsForUser(Integer userId) {
        List<Order> orders = orderRepository.findByUserUserIdWithDetailsFetched(userId);
        // Force-initialize all lazy proxies inside the transaction
        for (Order order : orders) {
            Hibernate.initialize(order);
            if (order.getOrderDetails() != null) {
                Hibernate.initialize(order.getOrderDetails());
                for (var detail : order.getOrderDetails()) {
                    Hibernate.initialize(detail);
                    if (detail.getProduct() != null) {
                        Hibernate.initialize(detail.getProduct());
                        Hibernate.initialize(detail.getProduct().getCategory());
                        Hibernate.initialize(detail.getProduct().getImage());
                    }
                }
            }
        }
        return orders;
    }

    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate);
    }

    public List<Order> getTodayOrders() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return orderRepository.findTodayOrders(startOfDay, endOfDay);
    }

    public List<Order> getOrdersFromLastDays(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return orderRepository.findOrdersFromLastDays(startDate);
    }

    public Order createOrderForUser(Integer userId) {
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public Double calculateOrderTotal(Integer orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        return orderDetails.stream()
                .mapToDouble(od -> od.getPrice().doubleValue() * od.getQuantity())
                .sum();
    }

    public List<Order> getRecentOrdersByUserId(Integer userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findRecentOrdersByUserId(userId, pageable);
    }

    public double getTotalSpentByUserId(Integer userId) {
        return orderRepository.getTotalSpentByUserId(userId);
    }

    public List<Order> getOrdersByPeriod(LocalDate from, LocalDate to) {
        // Chuyển LocalDate sang LocalDateTime để quét từ đầu ngày From đến cuối ngày To
        LocalDateTime startDT = from.atStartOfDay(); 
        LocalDateTime endDT = to.atTime(LocalTime.MAX); 
        
        return orderRepository.findByOrderDateBetween(startDT, endDT);
    }
}
